package com.cascadia.hidenseek.network;

import com.cascadia.hidenseek.model.Player;
import com.cascadia.hidenseek.network.NetworkBase.RequestType;

import org.json.JSONException;

public abstract class PutRoleRequest extends NetworkRequest {

	public void doRequest(Player p) {
		Request r = new Request();
		r.url = baseUrl + "players/" + p.getId() + "/role/";
		r.type = RequestType.PUT;
		try {
			r.jsonArgs = p.roleToJSON();
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
