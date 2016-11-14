package com.cascadia.hidenseek;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by deb on 11/8/16.
 */
public class HiderTask extends GameTask {

    public HiderTask(Handler handler, Player player) {
        super(handler, player);
    }

    // Process the new status for the match and players.
    // The player objects are not updated until after this method runs,
    // so checks of these are for the last players and player
    @Override
    protected void processStatus() {
        int numPlayers = match.players.size();
        Message message = handler.obtainMessage();
        Bundle bundle = new Bundle();
        message.obj = match;

        //googleMap.clear();  TODO update Google API
        int idx = 0;

        // We need to identify which player this is before anything else
        for (idx = 0; idx < match.players.size(); idx++) {

            Player hider = match.players.get(idx);
            if (hider.GetId() == player.GetId()) {
                Player.Status status = hider.GetStatus();
                message.arg1 = idx; // let the handler know which player is the current player
                if (status == Player.Status.Hiding) {
                    bundle.putString("event", "hiding");
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
                // make sure the player status has changed to spotted this time so the
                // verification only shows up once
                else if ((status == Player.Status.Spotted) &&
                        (player.GetStatus() != Player.Status.Spotted)) {
                    bundle.putString("event", "spotted");
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }
        }
        // See if one of the other players has been found
        idx = 0;
        for (final Player hider : match.players) {
            boolean thisPlayer = (hider.GetId() == player.GetId());

            Player.Status status = hider.GetStatus();

            if (match.GetType() == Match.MatchType.HideNSeek) {
                if (!thisPlayer && (status == Player.Status.Found) &&
                        (players.get(new Integer(hider.GetId())).GetStatus() != Player.Status.Found)) {
                    bundle.putString("event", "show-other-players");
                    message.setData(bundle);
                    message.arg2 = idx; // let the handler know which player was found
                    handler.sendMessage(message);
                }
            }
            idx++;
        }

        if (match.GetStatus() == Match.Status.Complete) {
            bundle.putString("event", "game-over");
            message.setData(bundle);
            handler.sendMessage(message);
        }

    }
}
