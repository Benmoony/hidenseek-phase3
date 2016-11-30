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

        for (final Player hider : match.players.values()) {

            Player.Status status = hider.getStatus();

            if (hider.getRole() == Player.Role.Seeker) {
                numPlayers--;
                continue;
            }

            Message message;
            if (match.getType() == Match.MatchType.HideNSeek) {
                switch (status) {
                    case Hiding:
                        message = createMessage("showDistance", hider);
                        handler.sendMessage(message);
                        break;
                    case Spotted:
                        if (players.get(new Integer(hider.getId())).getStatus() != Player.Status.Spotted) {
                            message = createMessage("showSpotted", hider);
                            handler.sendMessage(message);
                        }
                       break;
                    case Found:
                        if (players.get(new Integer(hider.getId())).getStatus() != Player.Status.Found) {
                            message = createMessage("showFound", hider);
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
    /* Create a message to send to the Active Activity */
    protected Message createMessage(String event, Player hider) {
        Message message = handler.obtainMessage();
        message.obj = match;
        message.arg1 = hider.getId();
        Bundle bundle = new Bundle();
        bundle.putString("event", event);
        message.setData(bundle);
        return message;
    }

}
