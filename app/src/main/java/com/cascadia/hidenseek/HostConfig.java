package com.cascadia.hidenseek;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.cascadia.hidenseek.Match.MatchType;
import com.cascadia.hidenseek.Match.Status;
import com.cascadia.hidenseek.network.DeletePlayerRequest;
import com.cascadia.hidenseek.network.GetMatchRequest;
import com.cascadia.hidenseek.network.GetPlayerListRequest;
import com.cascadia.hidenseek.network.PutStartRequest;


public class HostConfig extends Activity {

    String username, counttime, seektime;
    RecyclerView list;
    boolean isActive;
    SharedPreferences sh_Pref;
    Editor toEdit;

    //Used for periodic callback.
    private Handler h2 = new Handler();
    //Millisecond delay between callbacks
    private final int callbackDelay = 500;
    private static ConnectionChecks connectionChecks;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_config);
        connectionChecks = new ConnectionChecks(this);

        if (LoginManager.getMatch() == null) {
            Dialog d = new Dialog(this);
            d.setTitle("Error: null match.");
            d.show();
            finish();
        }
        Toast.makeText(this, "Id Changed to" + Integer.toString(LoginManager.playerMe.getId()), Toast.LENGTH_LONG).show();
        initSettings();

        list = (RecyclerView) findViewById(R.id.configPlayerList);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(joinedListAdapter);
        isActive = true;

        ImageView countHelp = (ImageView) findViewById(R.id.configCountTimeHelp);
        countHelp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                HelpDialog helpDialog = new HelpDialog("Enter the count time to begin the match.", "Count Time");
                helpDialog.show(getFragmentManager(), "Help");
            }
        });
        ImageView searchHelp = (ImageView) findViewById(R.id.configSeekTimeHelp);
        searchHelp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                HelpDialog helpDialog = new HelpDialog("Enter the time that would last for the game.", "Search Time");
                helpDialog.show(getFragmentManager(), "Help");
            }
        });
        ImageButton btnBeginMatch = (ImageButton) findViewById(R.id.btnConfigBegin);
        ImageButton btnCancelMatch = (ImageButton) findViewById(R.id.btnConfigCancel);

        btnBeginMatch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!connectionChecks.isConnected()) {
                    connectionChecks.showAlert();
                    return;
                }
                //Set the match count time and seek time as specified, etc.
                EditText countTime = (EditText) findViewById(R.id.configCountTimeInput);
                EditText seekTime = (EditText) findViewById(R.id.configSeekTimeInput);
                Match m = LoginManager.getMatch();

                //Validate countTime and searchTime input unless this is a sandbox
                if (LoginManager.getMatch().getType() != MatchType.Sandbox) {
                    String sCountTime = countTime.getText().toString();
                    String sSeekTime = seekTime.getText().toString();

                    if (sSeekTime.length() == 0 || sCountTime.length() == 0) {
                        HelpDialog helpDialog = new HelpDialog("Please enter the count time and search time.", "Enter times");
                        helpDialog.show(getFragmentManager(), "Help");
                        return;
                    }
                    try {
                        m.setCountTime(Integer.parseInt(countTime.getText().toString()));
                        m.setSeekTime(Integer.parseInt(seekTime.getText().toString()));
                        // Save if we go through
                        sh_Pref = getSharedPreferences("HideNSeek_shared_pref", MODE_PRIVATE);
                        toEdit = sh_Pref.edit();
                        toEdit.putString("Counttime", sCountTime);
                        toEdit.putString("Seektime", sSeekTime);
                        toEdit.commit();
                    } catch (NumberFormatException e) {

                        Dialog d = new Dialog(HostConfig.this);
                        d.setTitle("Error: invalid value for count time and/or seek time");
                        d.show();
                        return;
                    }
                }
                PutStartRequest request = new PutStartRequest() {
                    @Override
                    protected void onException(Exception e) {

                    }

                    @Override
                    protected void onComplete(Match m) {
                        Intent intent = new Intent(HostConfig.this, Active.class);
                        startActivity(intent);
                        isActive = false;
                    }

                };
                request.doRequest(m);
            }
        });

        btnCancelMatch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                confirmCancel();
            }
        });

        //Remove count time and search time things if this is a sandbox
        if (LoginManager.getMatch().getType() == MatchType.Sandbox || !LoginManager.isHost) {
            findViewById(R.id.configTimeContainer).setVisibility(View.GONE);
        }

        //Change the buttons for joiners.
        if (!LoginManager.isHost) {
            btnBeginMatch.setEnabled(false);
            btnBeginMatch.setAlpha(100);
            btnCancelMatch.setImageDrawable(
                    getResources().getDrawable(R.drawable.btn_leave_set));
        }
        Runnable callback = new Runnable() {

            //This function gets called twice per second until the app is stopped.
            @Override
            public void run() {
                if (connectionChecks.isConnected()) {
                    setPlayerList();

                    //If joiner, then check if host has started yet.
                    if (!LoginManager.isHost) {
                        GetMatchRequest gmRequest = new GetMatchRequest() {
                            @Override
                            protected void onException(Exception e) {
                            }

                            @Override
                            protected void onComplete(Match match) {
                                if (match == null) return;
                                if (match.getStatus() == Status.Active) {
                                    isActive = false;
                                    Intent intent = new Intent(HostConfig.this, Active.class);
                                    startActivity(intent);
                                }
                            }
                        };
                        gmRequest.doRequest(LoginManager.getMatch().getId());
                    }
                }

                if (isActive) {
                    h2.postDelayed(this, callbackDelay);
                }
            }
        };
        callback.run(); //Begin periodic updating!

    }

    private PlayerList joinedPlayers = new PlayerList();
    private JoinListAdapter joinedListAdapter = JoinListAdapter.newInstance(HostConfig.this, joinedPlayers);

    private void setPlayerList() {
        if (LoginManager.getMatch() == null) {
            String[] titles = {"Failed to update match list.", "(null match)"};
            joinedPlayers.clear();
            joinedListAdapter.notifyDataSetChanged();
            return;
        }
        GetPlayerListRequest request = new GetPlayerListRequest() {

            @Override
            protected void onException(Exception e) {
                String[] titles = {"Failed to update match list."};
                joinedPlayers.clear();
                joinedListAdapter.notifyDataSetChanged();
            }

            @Override
            protected void onComplete(Match match) {
                // refill the player list
                joinedPlayers.clear();
                for (Player p : match.players.values()) {
                    joinedPlayers.put(new Integer(p.getId()), p);
                }
                joinedListAdapter.notifyDataSetChanged();
            }
        };
        request.doRequest(LoginManager.getMatch());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.host_config, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Shows a dialog to confirm whether the user wants to cancel/leave match
     */
    private void confirmCancel() {
        final Dialog dialog;
        String message = LoginManager.isHost ? "Cancel this match?" : "Leave this match?";

        dialog = new AlertDialog.Builder(this).setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                DeletePlayerRequest deletePlayerRequest = new DeletePlayerRequest() {
                                    @Override
                                    protected void onComplete(Player player) {}
                                    @Override
                                    protected void onException(Exception e) { }
                                };
                                deletePlayerRequest.doRequest(LoginManager.playerMe);
                                finish();
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }).create();
        dialog.show();
    }

    /**
     * Get any stored preferences and put them in the fields when form is loaded
     */
    private void initSettings() {
        counttime = getSharedPreferences("HideNSeek_shared_pref", MODE_PRIVATE).getString("Counttime", "");
        EditText cTime = (EditText) findViewById(R.id.configCountTimeInput);
        cTime.setText(counttime);

        seektime = getSharedPreferences("HideNSeek_shared_pref", MODE_PRIVATE).getString("Seektime", "");
        EditText sTime = (EditText) findViewById(R.id.configSeekTimeInput);
        sTime.setText(seektime);
    }
}
