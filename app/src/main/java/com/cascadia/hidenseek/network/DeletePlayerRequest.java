package com.cascadia.hidenseek.network;

import com.cascadia.hidenseek.Player;

/**
 * Created by deb on 12/12/16.
 * Send the API message to delete the player from the match
 */

public abstract class DeletePlayerRequest extends NetworkRequest {

    public void doRequest(Player toPost) {
        player = toPost;
        Request request = new Request();
        request.url = baseUrl + "matches/" + toPost.getAssociatedMatch().getId() + "/players/" + toPost.getId();
        request.type = NetworkBase.RequestType.DELETE;
        doRequest(request);
    }

    //To be overwritten
    protected abstract void onComplete(Player player);

    @Override
    protected final void processPostExecute(String s) {
        if (!player.processPostResponse(s)) {
            player.setID(0);
        }
        onComplete(player);
    }

    private Player player;
}
