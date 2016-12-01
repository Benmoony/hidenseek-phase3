package com.cascadia.hidenseek.network;

import com.cascadia.hidenseek.Match;
import com.cascadia.hidenseek.network.NetworkBase.RequestType;

import java.util.List;


public abstract class GetMatchListRequest extends NetworkRequest {
	
	public void doRequest() {
		Request r = new Request();
		r.url = baseUrl + "matches/";
		r.type = RequestType.GET;
		doRequest(r);
	}
	
	protected abstract void onComplete(List<Match> matches);
	
	@Override
	protected final void processPostExecute(String s) {
		onComplete(Match.parseToList(s));
	}

}
