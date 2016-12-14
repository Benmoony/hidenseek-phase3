package com.cascadia.hidenseek.network;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cascadia.hidenseek.Player;
import com.cascadia.hidenseek.PlayerList;
import com.cascadia.hidenseek.R;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class PlayerListFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;
    private PlayerList players;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlayerListFragment() {
    }

    @SuppressWarnings("unused")
    public static PlayerListFragment newInstance(PlayerList players) {
        PlayerListFragment fragment = new PlayerListFragment();
        fragment.players = players;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new PlayerRecyclerViewAdapter(players, mListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    public void playerFound(Player player){
        //TODO:Change background of the player passed to the method to Green
       // Toast.makeText(this, "The player has been found!", Toast.LENGTH_LONG).show();)

    }

    public void playerNotFound(Player player){
        //Build Alert Dialogue indicating that Player has denied being found
       // Toast.makeText(this, "You have not found the player", Toast.LENGTH_LONG).show();)
        String playname = player.getName();
        new AlertDialog.Builder(this.getContext())
                .setTitle("Denied")
                .setMessage(playname + " denies being found")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Player player);
    }
}
