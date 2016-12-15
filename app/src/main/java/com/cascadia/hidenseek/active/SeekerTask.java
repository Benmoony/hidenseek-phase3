package com.cascadia.hidenseek.active;

import android.os.Handler;
import android.os.Message;

import com.cascadia.hidenseek.utilities.ConnectionChecks;
import com.cascadia.hidenseek.model.Match;
import com.cascadia.hidenseek.model.Player;
import com.cascadia.hidenseek.model.PlayerList;

/**
 * Created by deb on 11/7/16.
 * This task checks for events related to the seeker.
 */

public class SeekerTask extends GameTask {

    public SeekerTask(Handler handler, Player player, ConnectionChecks connectionChecks) {
        super(handler, player, connectionChecks);
    }

    // Process the new status for the match and players.
    // The player objects are not updated until after this method runs,
    // so checks of these are for the last players and player
    @Override
    protected void processPlayers(PlayerList newPlayers) {

        for (final Player hider : newPlayers.values()) {
            Player.Status status = hider.getStatus();

            if (status == null) continue; // check if the status is filled in.  It will not be for the Seeker.

            Message message;
            if (match.getType() == Match.MatchType.HideNSeek) {
                switch (status) {
                    case Hiding:
                        //If statement checking if previous status of hider was spotted
                        if (match.players.get(new Integer(hider.getId())).getStatus() == Player.Status.Spotted){
                            //match.players is a reference to the match which has yet to be updated with the new status
                            message = createMessage("notFound", hider);
                            handler.sendMessage(message);
                            break;
                        }
                        message = createMessage("showDistance", hider);
                        handler.sendMessage(message);
                        break;
                    case Spotted:
                        if (newPlayers.get(new Integer(hider.getId())).getStatus() != Player.Status.Spotted) {
                            message = createMessage("showSpotted", hider);
                            handler.sendMessage(message);
                        }
                       break;
                    case Found:
                        if (newPlayers.get(new Integer(hider.getId())).getStatus() != Player.Status.Found) {
                            message = createMessage("showFound", hider);
                            handler.sendMessage(message);
                        }
                        break;

                }
            }
        }
    }

}
