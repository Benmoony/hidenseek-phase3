package com.cascadia.hidenseek.active;

import android.os.Handler;
import android.os.Message;

import com.cascadia.hidenseek.utilities.ConnectionChecks;
import com.cascadia.hidenseek.model.Match;
import com.cascadia.hidenseek.model.Player;
import com.cascadia.hidenseek.model.PlayerList;

/**
 * Created by deb on 11/8/16.
 */
public class HiderTask extends GameTask {

    public HiderTask(Handler handler, Player player, ConnectionChecks connectionChecks) {
        super(handler, player, connectionChecks);
    }

    // Process the new status for the match and players.
    // The player objects are not updated until after this method runs,
    // so checks of these are for the last players and player
    @Override
    protected void processPlayers(PlayerList newPlayers) {
        // See if one of the other players has been found
        for (final Player hider : newPlayers.values()) {
            boolean thisPlayer = (hider.getId() == player.getId());

            Player.Status status = hider.getStatus();

            Message message;
            if (thisPlayer) {
                if (status == Player.Status.Hiding) {
                    message = createMessage("hiding", player);
                    handler.sendMessage(message);
                }
                // make sure the player status has changed to spotted this time so the
                // verification only shows up once
                else if ((status == Player.Status.Spotted) &&
                        (player.getStatus() != Player.Status.Spotted)) {
                    message = createMessage("spotted", hider);
                    handler.sendMessage(message);
                }
            }
            else if (match.getType() == Match.MatchType.HideNSeek) {
                if ((status == Player.Status.Found) &&
                        (newPlayers.get(new Integer(hider.getId())).getStatus() != Player.Status.Found)) {
                    message = createMessage("show-other-players", hider);
                    handler.sendMessage(message);
                }
            }
        }

        if (match.getStatus() == Match.Status.Complete) {
            Message message = createMessage("game-over", player);
            handler.sendMessage(message);
        }

    }
}
