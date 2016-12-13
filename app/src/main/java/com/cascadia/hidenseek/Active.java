package com.cascadia.hidenseek;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cascadia.hidenseek.Player.Role;
import com.cascadia.hidenseek.Player.Status;
import com.cascadia.hidenseek.network.DeletePlayerRequest;
import com.cascadia.hidenseek.network.DeletePlayingRequest;
import com.cascadia.hidenseek.network.PlayerListFragment;
import com.cascadia.hidenseek.network.PutGpsRequest;
import com.cascadia.hidenseek.network.PutStatusRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class Active extends FragmentActivity implements OnMapReadyCallback,
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener, PlayerListFragment.OnListFragmentInteractionListener {
    private GoogleMap googleMap;
    private Match match;
    private Player player;
    private ArrayList<Player> playerArray = new ArrayList<>();
    final Context context = this;
    boolean tagged = true;
    private ShowHider sh;
    protected GoogleApiClient googleApiClient;
    TextView roleView;
    TextView timerView;
    Timer hidetime;
    Timer seektime;
    private PlayerListFragment playerList;
    private static ConnectionChecks connectionChecks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active);
        connectionChecks = new ConnectionChecks(this);

        roleView = (TextView) findViewById(R.id.textrole);
        timerView = (TextView) findViewById(R.id.timer);

        match = LoginManager.getMatch();
        player = LoginManager.playerMe;
        FrameLayout roleLayout;
        //isActive = true;

        if (match == null || player == null) {
            Dialog d = new Dialog(this);
            d.setTitle("Error: null match.");
            d.show();
            finish();
        }

        getActionBar().hide();

        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();

        if (savedInstanceState == null) {

            if (player.getRole() == Player.Role.Seeker) {

                playerList = PlayerListFragment.newInstance(match.players);

                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.Context_Player_UI, playerList, "playList")
                        .commit();
            } else {
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.Context_Player_UI, supportMapFragment, "map")
                        .commit();
            }

        }

		/* Show user's position on map */
        supportMapFragment.getMapAsync(this);

        // User clicked Leave Match button
        ImageButton btnLeave = (ImageButton) findViewById(R.id.btnLeaveGame);
        btnLeave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // send a message to the API that the player is leaving, but don't worry if it works
                DeletePlayerRequest deletePlayerRequest = new DeletePlayerRequest() {
                    @Override
                    protected void onComplete(Player player) {}
                    @Override
                    protected void onException(Exception e) { }
                };
                deletePlayerRequest.doRequest(player);
                Intent intent = new Intent(Active.this, Home.class);
                startActivity(intent);
            }
        });

        if (player.getRole() == Role.Seeker) {
            new Thread(new SeekerTask(seekerHandler, player, connectionChecks)).start();
        } else {
            new Thread(new HiderTask(hiderHandler, player, connectionChecks)).start();
        }

        // Create an instance of GoogleAPIClient.
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_MAPS_RECEIVE);
        } else {
            createLocationRequest();
        }
    }

    private final int MY_PERMISSIONS_REQUEST_MAPS_RECEIVE = 1;
    private Boolean locationAnswered = false;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            createLocationRequest();
        }
        // Add a marker in Cascadia College, and move the camera.
        LatLng cascadia = new LatLng(47.760641, -122.191283);
        googleMap.addMarker(new MarkerOptions().position(cascadia).title("Cascadia College"));

        final CameraPosition cameraPosition =
                new CameraPosition.Builder().target(cascadia)
                        .zoom(20.0f)
                        .build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_MAPS_RECEIVE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (googleMap != null) {
                        googleMap.setMyLocationEnabled(true);
                    }
                    createLocationRequest();
                }
                //			return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private LocationRequest locationRequest;

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private Handler seekerHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            Bundle bundle = message.getData();

            String event = bundle.getString("event");
            match = (Match) message.obj;
            player = match.players.get(new Integer(player.getId()));

            //create the timer
            //update the UI
            hidetime = new Timer(match);
            hidetime.setTimername("Hiding Now!");

            roleView.setText(hidetime.timername);
            timerView.setText(hidetime.SecondsLeft());

            /*seektime = new Timer(match);
            seektime.setTimername("Start Seeking");

            roleView.setText(seektime.timername);
            timerView.setText(seektime.SecondsLeft_2());*/



            // handle the event
            switch (event) {
                case "showDistance":
                    showDistance();
                    break;
                case "showSpotted":
                    showSpotted();
                    break;
                case "showFound":
                    showFound();
                    break;
                case "game-over":
                    gameOver();
                    break;
            }
        }
    };


    // Update the distance indication
    // from the Seeker to the hider
    private void showDistance() {
    }

    // Update the player to show spotted
    private void showSpotted() {
    }

    // Update the player to show found
    private void showFound() {
    }

    // Go back to the login screen when the game has ended
    private void gameOver() {
        Intent end = new Intent(context, TempToHome.class);
        startActivity(end);
    }

    // Handle events from the hider task
    private Handler hiderHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            Bundle bundle = message.getData();

            Location location = player.getLocation();
            if (location != null) {
            }

            String event = bundle.getString("event");

            switch (event) {
                case "spotted":
                    showFoundAlert();
                    break;
                case "show-other-players":
                    showOtherPlayers(message.arg1);
                    break;
                case "game-over":
                    gameOver();
                    break;
            }
        }
    };

    private void showFoundAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set title
        alertDialogBuilder.setTitle("Found You");

        alertDialogBuilder
                .setMessage("The seeker just said he found you, is this correct?")
                .setCancelable(false)
                .setPositiveButton("Yes", foundClickListener)
                .setNegativeButton("No", notFoundClickListener);

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    // Update the player status to found when acknowledged
    private DialogInterface.OnClickListener foundClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {

            player.setStatus(Status.Found);
            player.setLocation(null);
            PutStatusRequest pp = new PutStatusRequest() {

                @Override
                protected void onException(Exception e) {
                    e.printStackTrace();
                }

            };

            pp.doRequest(player);

            ShowSeeker();
            tagged = true;
            Intent intent = new Intent(context, TempToHome.class);
            startActivity(intent);
        }
    };
    // Reset the player status to hiding if he/she denies being found
    private DialogInterface.OnClickListener notFoundClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
            // if this button is clicked, just close the dialog box
            // and do nothing
            player.setStatus(Status.Hiding);
            PutStatusRequest pp = new PutStatusRequest() {

                @Override
                protected void onException(Exception e) {
                    e.printStackTrace();
                }

            };
            pp.doRequest(player);
            tagged = true;
        }
    };

    // Show the other hiders and the Seeker for a period of time
    // when another hider has been found
    private void showOtherPlayers(int foundID) {
    }


    public void ShowSeeker() {
/*
        sh = new ShowHider(showTime, 1000, temp);
		sh.startCountDown1();
*/
    }

    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        final String TAG_ERROR_DIALOG_FRAGMENT = "errorDialog";
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (status == ConnectionResult.SUCCESS) {
            // no problems just work
        } else if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
            ErrorDialogFragment.newInstance(status).show(
                    getSupportFragmentManager(), TAG_ERROR_DIALOG_FRAGMENT);
        } else {
            Toast.makeText(this, "Google Maps V2 is not available!",
                    Toast.LENGTH_LONG).show();
            finish();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DeletePlayingRequest dpRequest = new DeletePlayingRequest() {

            @Override
            protected void onException(Exception e) {
                e.printStackTrace();
            }
        };
        dpRequest.doRequest(player);
    }

    @Override
    public void onLocationChanged(Location location) {
        player.setLocation(location);

        // Update the player's location at the API
        PutGpsRequest putGpsRequest = new PutGpsRequest() {

            @Override
            protected void onException(Exception e) {
                e.printStackTrace();
            }

        };
        // Do the request
        putGpsRequest.doRequest(player);

        // Update the center of the map for a hider
        if (player.getRole() != Role.Seeker) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to the player location
                    .zoom(20.0f)                // Sets the zoom
                    .build();                   // Creates a CameraPosition from the builder
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    //This is where Active communicates with PlayerListFragment.java i.e. If something changes in
    // PlayerListFragment in order to communicate with app, this manages that.
    public void onListFragmentInteraction(Player player) {

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ErrorDialogFragment extends DialogFragment {
        static final String ARG_STATUS = "status";

        static ErrorDialogFragment newInstance(int status) {
            Bundle args = new Bundle();
            args.putInt(ARG_STATUS, status);
            ErrorDialogFragment result = new ErrorDialogFragment();
            result.setArguments(args);
            return (result);
        }

        public void show(FragmentManager supportFragmentManager, String TAG_ERROR_DIALOG_FRAGMENT) {

        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle args = getArguments();
            return GooglePlayServicesUtil.getErrorDialog(
                    args.getInt(ARG_STATUS), getActivity(), 0);
        }

        @Override
        public void onDismiss(DialogInterface dlg) {
            if (getActivity() != null) {
                getActivity().finish();
            }
        }
    }

    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        googleApiClient.disconnect();
        super.onStop();
        //isActive = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.players, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.players_list) {
            Intent intent = new Intent(Active.this, CurrentPlayers.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

