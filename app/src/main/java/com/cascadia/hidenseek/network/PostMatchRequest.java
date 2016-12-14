package com.cascadia.hidenseek.network;

import com.cascadia.hidenseek.model.Match;
import com.cascadia.hidenseek.network.NetworkBase.RequestType;
/* Send a POST to the API to create a match */
public abstract class PostMatchRequest extends NetworkRequest {

	public void doRequest(Match toPost) {
		m = toPost;
		Request r = new Request();
		r.url = baseUrl + "matches/";
		r.type = RequestType.POST;
		r.jsonArgs = m.toJSONPost();
		doRequest(r);
	}
	
	//To be overwritten
	protected void onComplete(Match match) { }
	
	@Override
	protected final void processPostExecute(String s) {
		try {
			m.processPostResponse(s);
		} catch (NullPointerException e) {
			onException(e);
			return;
		}
		onComplete(m);
	}
	
	private Match m;
}
