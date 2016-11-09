package com.cascadia.hidenseek;


import com.cascadia.hidenseek.network.GetPlayerListRequest;

import android.os.Handler;

import java.util.HashMap;

/**
 * Created by deb on 11/7/16.
 *
 * GameTask.java
 * This sets up a Runnable that checks the player list twice a second and has an
 * abstract method to process the results.  The abstract method sends a message to the handler
 * passed at construction to update the GUI.
 */

public abstract class GameTask implements Runnable {

    protected Handler handler; // message handler
    protected Match match;
    protected Player player;
    protected final int DELAY = 500; // delay between checks of player status
    // Keep track of the last status for all the players
    protected HashMap<Integer, Player> players = new HashMap<>();

    // Create the GameTask and provide it with a message handler in the
    // GUI task
    public GameTask(Handler handler, Player player) {
        this.handler = handler;
        this.match = player.GetAssociatedMatch();
        this.player = player;
    }
    // Run the task
    public void run() {
        // loop until the user has left the game or it is over
        while (true) {
            // Break out of the loop if the match is over or the player is found
            if ((match.GetStatus() == Match.Status.Complete) ||
                    (player.GetStatus() == Player.Status.Found)) {
                break;
            }
            // Do request and update values in match. No callback needed.
            GetPlayerListRequest gplRequest = new GetPlayerListRequest() {

                @Override
                protected void onException(Exception e) {
                }

                @Override
                protected void onComplete(Match newMatch) {
                    match = newMatch;
                    processStatus();

                    // Update the status for each player, and the current player
                    for (Player hider : match.players) {
                        players.put(new Integer(hider.GetId()), hider);
                        if (hider.GetId() == player.GetId())
                            player = hider;
                    }
                }
            };
            gplRequest.DoRequest(match);

            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }

    }
    // Process the returned status for the match
    protected abstract void processStatus();
}
