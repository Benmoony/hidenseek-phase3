package com.cascadia.hidenseek.network;

import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cascadia.hidenseek.Player;
import com.cascadia.hidenseek.PlayerList;
import com.cascadia.hidenseek.R;
import com.cascadia.hidenseek.network.PlayerListFragment.OnListFragmentInteractionListener;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlayerList} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class PlayerRecyclerViewAdapter extends RecyclerView.Adapter<PlayerRecyclerViewAdapter.ViewHolder>{

    private final PlayerList players;
    private final OnListFragmentInteractionListener listener;

    public PlayerRecyclerViewAdapter(PlayerList players, OnListFragmentInteractionListener listener) {
        this.players = players;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_player, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.player = players.get(position);
        holder.mContentView.setText(holder.player.getName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    listener.onListFragmentInteraction(holder.player);
                }
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Found Player")
                        .setMessage("Have you found this player?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which) {
                                holder.player.setStatus(Player.Status.Spotted);
                                PutStatusRequest stat = new PutStatusRequest() {
                                    @Override
                                    protected void onException(Exception e) {
                                        e.printStackTrace();
                                    }
                                };
                                stat.doRequest(holder.player);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which){
                                return;
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return players.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Player player;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.playerName);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }

    }
}
