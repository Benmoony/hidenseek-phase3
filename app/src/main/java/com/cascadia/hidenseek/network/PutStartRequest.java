package com.cascadia.hidenseek.network;

import com.cascadia.hidenseek.Match;
import com.cascadia.hidenseek.network.NetworkBase.RequestType;

public abstract class PutStartRequest extends NetworkRequest {

	public void doRequest(Match toStart) {
		m = toStart;
		Request r = new Request();
		r.url = baseUrl + "matches/" + toStart.getId() + "/start/";
		r.type = RequestType.PUT;
		r.jsonArgs = m.toJSONStart();
		doRequest(r);
	}
	
	//To be overwritten
	protected void onComplete(Match match) { }
	
	@Override
	protected final void processPostExecute(String s) {
		m.startMatch();
		onComplete(m);
	}
	
	private Match m;

}
