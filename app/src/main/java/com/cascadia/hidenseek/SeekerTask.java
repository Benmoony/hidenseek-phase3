package com.cascadia.hidenseek;

import android.os.Handler;
import android.os.Message;

import java.util.Hashtable;

/**
 * Created by deb on 11/7/16.
 */

public class SeekerTask extends GameTask {

    public SeekerTask(Handler handler, Player player) {
        super(handler, player);
    }

    // Process the new status for the match and players.
    // The player objects are not updated until after this method runs,
    // so checks of these are for the last players and player
    @Override
    protected void processPlayers(PlayerList newPlayers) {

        for (final Player hider : newPlayers.values()) {
            // skip the Seeker.  The Seeker's status is null
            if (hider.getRole() == Player.Role.Seeker)
                continue;

            Player.Status status = hider.getStatus();

            Message message;
            if (match.getType() == Match.MatchType.HideNSeek) {
                switch (status) {
                    case Hiding:
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
