package com.cascadia.hidenseek.network;

import com.cascadia.hidenseek.Player;
import com.cascadia.hidenseek.network.NetworkBase.RequestType;

import org.json.JSONException;

public abstract class PutStatusRequest extends NetworkRequest {

	public void doRequest(Player p) {
		Request r = new Request();
		r.url = baseUrl + "players/" + p.getId() + "/status/";
		r.type = RequestType.PUT;
		try {
			r.jsonArgs = p.statusToJSON();
		} catch(JSONException e) {
			onException(e);
		}
		doRequest(r);
	}
	
	//To be overwritten
	protected void onComplete() { }
	
	@Override
	protected final void processPostExecute(String s) {
		onComplete();
	}
}
