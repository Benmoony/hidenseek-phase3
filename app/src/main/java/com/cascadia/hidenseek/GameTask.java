package com.cascadia.hidenseek;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.cascadia.hidenseek.network.GetMatchRequest;
import com.cascadia.hidenseek.network.GetPlayerListRequest;

import java.util.Hashtable;

/**
 * Created by deb on 11/7/16.
 *
 * GameTask.java
 * This sets up a Runnable that checks the player list twice a second and has an
 * abstract method to process the results.  The abstract method sends a message to the handler
 * passed at construction to update the GUI.
 */

public abstract class GameTask implements Runnable {

    protected Context context;
    protected Handler handler; // message handler
    protected Match match;
    protected Player player;
    protected final int DELAY = 500; // delay between checks of player status
    // Keep track of the last status for all the players
    protected Hashtable<Integer, Player> players = new Hashtable<>();

    // Create the GameTask and provide it with a message handler in the
    // GUI task
    public GameTask(Handler handler, Player player) {
        this.handler = handler;
        this.match = player.getAssociatedMatch();
        this.player = player;
    }

    // Run the task
    public void run() {
        // loop until the user has left the game or it is over
        while (true) {
            // Break out of the loop if the match is over or the player is found
            if ((match.getStatus() == Match.Status.Complete) ||
                    (player.getStatus() == Player.Status.Found)) {
                break;
            }
            // Do request and update values in match. No callback needed.
            GetPlayerListRequest gplRequest = new GetPlayerListRequest() {

                @Override
                protected void onException(Exception e) {
                }

                @Override
                protected void onComplete(Match newMatch) {
//                    match.players = newMatch.players;
                    processPlayers();

                    // Update the status for each player, and the current player
                    players.clear();
                    for (Player player : newMatch.players.values()) {
                        players.put(new Integer(player.getId()), player);
                    }
                    player = players.get(new Integer(player.getId()));
                }
            };
            gplRequest.doRequest(match);

            GetMatchRequest gmRequest = new GetMatchRequest() {
                @Override
                protected void onException(Exception e) {
                }

                @Override
                protected void onComplete(Match matchUpdate) {
                    Match.Status status = matchUpdate.getStatus();
                    if ((status == Match.Status.Complete) /*&& (match.getStatus() != Match.Status.Complete)*/) {
                        Message message = handler.obtainMessage();
                        Bundle bundle = new Bundle();
                        message.obj = match;
                        bundle.putString("event", "game-over");
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }
                    PlayerList players = match.players;
                    match = matchUpdate;
                    match.players = players;
                }
            };

            gmRequest.doRequest(LoginManager.getMatch().getId());

            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    // Process the returned
    // status for the match
    protected abstract void processPlayers();

}
