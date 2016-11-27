package com.cascadia.hidenseek;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.cascadia.hidenseek.network.PutStopRequest;

import java.util.Calendar;

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
    protected void processPlayers() {
        int numPlayers = match.players.size();
        Message message = handler.obtainMessage();
        Bundle bundle = new Bundle();
        message.obj = match;

        for (final Player hider : match.players.values()) {
            message.arg1 = hider.getId();
            Player.Status status = hider.getStatus();

            if (hider.getRole() == Player.Role.Seeker) {
                numPlayers--;
                continue;
            }

            if (match.getType() == Match.MatchType.HideNSeek) {
                switch (status) {
                    case Hiding:
                        bundle.putString("event", "hiding");
                        message.setData(bundle);
                        handler.sendMessage(message);
                        break;
                    case Spotted:
                        if (players.get(new Integer(hider.getId())).getStatus() != Player.Status.Spotted) {
                            bundle.putString("event", "spotted");
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                       break;
                    case Found:
                        if (players.get(new Integer(hider.getId())).getStatus() != Player.Status.Found) {
                            bundle.putString("event", "found");
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                        numPlayers--;
                        break;
                }
            }
        }

        if ((numPlayers == 0) || Calendar.getInstance().getTime().after(match.getEndTime())) {
            PutStopRequest putStopRequest = new PutStopRequest();
            putStopRequest.DoRequest(match);
        }
    }
}