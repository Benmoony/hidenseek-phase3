package com.cascadia.hidenseek;

import com.google.android.gms.vision.barcode.Barcode;

import java.util.Calendar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

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
    protected void processStatus() {
        int numPlayers = match.players.size();
        Message message = handler.obtainMessage();
        Bundle bundle = new Bundle();
        message.obj = match;

        int idx = 0;
        for (final Player hider : match.players) {
            // pass the index of the current player to the handler
            if (hider.GetId() == player.GetId()) {
                message.arg1 = idx;
            }

            Player.Status status = hider.GetStatus();

            if (match.GetType() == Match.MatchType.HideNSeek) {
                switch (hider.GetStatus()) {
                    case Hiding:
                        bundle.putString("event", "hiding");
                        message.setData(bundle);
                        handler.sendMessage(message);
                        break;
                    case Spotted:
                        if (players.get(new Integer(hider.GetId())).GetStatus() != Player.Status.Spotted) {
                            bundle.putString("event", "spotted");
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                       break;
                    case Found:
                        if (players.get(new Integer(hider.GetId())).GetStatus() != Player.Status.Found) {
                            bundle.putString("event", "found");
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                        numPlayers--;
                        break;
                }
                if (hider.GetRole() == Player.Role.Seeker) {
                    numPlayers--;
                }
            }
            idx++;
        }

        if ((numPlayers == 0) || Calendar.getInstance().getTime().after(match.getEndTime())) {
            bundle.putString("event", "game-over");
            message.setData(bundle);
            handler.sendMessage(message);
        }
    }

}
