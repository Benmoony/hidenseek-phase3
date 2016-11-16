package com.cascadia.hidenseek.network;
/* Create the POST message to add a player to a match */
import org.json.JSONException;

import com.cascadia.hidenseek.Player;
import com.cascadia.hidenseek.network.NetworkBase.RequestType;

public abstract class PostPlayerRequest extends NetworkRequest {

	public void DoRequest(Player toPost, String password) {
		p = toPost;
		Request r = new Request();
		r.url = baseUrl + "matches/" + toPost.getAssociatedMatch().getId() + "/players/";
		r.type = RequestType.POST;
		try {
			r.jsonArgs = p.toJSONPost(password);
		} catch (JSONException e) {
			onException(e);
		}
		doRequest(r);
	}
	
	//To be overwritten
	protected void onComplete(Player player) { }
	
	@Override
	protected final void processPostExecute(String s) {
		p.processPostResponse(s);
		onComplete(p);
	}
	
	private Player p;

}
