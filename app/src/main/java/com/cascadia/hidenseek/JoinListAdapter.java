package com.cascadia.hidenseek;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class JoinListAdapter extends RecyclerView.Adapter<JoinListAdapter.ViewHolder>  {

    private PlayerList players;

    public JoinListAdapter() {
    } // empty base constructor


    public static JoinListAdapter newInstance(Activity context, PlayerList players) {
        JoinListAdapter joinListAdapter = new JoinListAdapter();
        //joinListAdapter.context = context;
        joinListAdapter.players = players;
        return joinListAdapter;
    }

    //Add roles here
    /*String[] radio = {
            "Seek", "Hide", "Supervise"
    };*/

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_single, null, true);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.player = players.get(position);
        holder.txtTitle.setText(holder.player.getName());

        /* Commented out for now.  Not sure what these are for
        Match.MatchType matchType = holder.player.getAssociatedMatch().getType();

        // Code to add radio buttons for hide'n'seek game
        if (matchType == Match.MatchType.HideNSeek) {
            final RadioButton[] rb = new RadioButton[radio.length];
            RadioGroup rg = (RadioGroup) holder.mView.findViewById(R.id.radioGroup1);
            rg.setOrientation(RadioGroup.HORIZONTAL);
            for (int i = 0; i < radio.length; i++) {
                rb[i] = new RadioButton(holder.mView.getContext());
                rg.addView(rb[i]);
                rb[i].setText(radio[i]);
            }
        }*/
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Player player;
        public final View mView;
        public final TextView txtTitle;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            txtTitle =  (TextView) view.findViewById(R.id.txt);
        }
    }
}




