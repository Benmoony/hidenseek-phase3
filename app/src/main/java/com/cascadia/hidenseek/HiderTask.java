package com.cascadia.hidenseek;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Created by deb on 11/8/16.
 */
public class HiderTask extends GameTask {

    public HiderTask(Context context, Handler handler, Player player) {
        super(context, handler, player);
    }

    // Process the new status for the match and players.
    // The player objects are not updated until after this method runs,
    // so checks of these are for the last players and player
    @Override
    protected void processPlayers() {
        int numPlayers = match.players.size();
        Message message = handler.obtainMessage();
        Bundle bundle = new Bundle();
        message.obj = match;

        //googleMap.clear();  TODO update Google API

        // See if one of the other players has been found
        for (final Player hider : match.players.values()) {
            boolean thisPlayer = (hider.getId() == player.getId());

            Player.Status status = hider.getStatus();

            if (thisPlayer) {
                if (status == Player.Status.Hiding) {
                    bundle.putString("event", "hiding");
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
                // make sure the player status has changed to spotted this time so the
                // verification only shows up once
                else if ((status == Player.Status.Spotted) &&
                        (player.getStatus() != Player.Status.Spotted)) {
                    bundle.putString("event", "spotted");
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }
            else if (match.getType() == Match.MatchType.HideNSeek) {
                if ((status == Player.Status.Found) &&
                        (players.get(new Integer(hider.getId())).getStatus() != Player.Status.Found)) {
                    bundle.putString("event", "show-other-players");
                    message.setData(bundle);
                    message.arg1 = hider.getId(); // let the handler know which player was found
                    handler.sendMessage(message);
                }
            }
        }

        if (match.getStatus() == Match.Status.Complete) {
            bundle.putString("event", "game-over");
            message.setData(bundle);
            handler.sendMessage(message);
        }
    }
}
