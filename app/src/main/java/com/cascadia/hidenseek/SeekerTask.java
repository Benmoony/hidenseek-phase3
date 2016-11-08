package com.cascadia.hidenseek;

import com.google.android.gms.vision.barcode.Barcode;

import java.util.Calendar;
import java.util.logging.Handler;

/**
 * Created by deb on 11/7/16.
 */

public class SeekerTask extends GameTask {

    public SeekerTask(Handler handler, Player player) {
        super(handler, player);
    }

    // Update the match based on the new status
    @Override
    protected void processStatus() {
        int numPlayers = match.players.size();

        //googleMap.clear();  TODO update Google API
        for (final Player hider : match.players) {
            Player.Status status = hider.GetStatus();

            if (match.GetType() == Match.MatchType.HideNSeek) {
                switch (hider.GetStatus()) {
                    case Hiding:
                        showDistance();
                        break;
                    case Spotted:
                        showSpotted();
                        break;
                    case Found:
                        showFound();
                        numPlayers--;
                        break;
                }
                if (hider.GetRole() == Player.Role.Seeker) {
                    numPlayers--;
                }
            }
        }

        if ((numPlayers == 0) || Calendar.getInstance().getTime().after(match.getEndTime())) {
            endGame();
        }
    }
    // Send a message to the handler with information it needs to update the distance indication
    // from the Seeker to the hider
    private void showDistance() {
    }
    // Send a message to the handler with information it needs to update the player to show spotted
    private void showSpotted() {
    }
    // Send a message to the handler with information it needs to update the player to show found
    private void showFound() {
    }
    // Send a message to the handler with information it needs to end the game
    private void endGame() {
    }

}
