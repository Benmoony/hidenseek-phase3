package com.cascadia.hidenseek.network;

import com.cascadia.hidenseek.Match;
import com.cascadia.hidenseek.network.NetworkBase.RequestType;

public class PutStopRequest extends NetworkRequest{
	public void doRequest(Match toStop) {
		m = toStop;
		Request r = new Request();
		r.url = baseUrl + "matches/" + toStop.getId() + "/stop/";
		r.type = RequestType.PUT;
		r.jsonArgs = m.toJSONStart();
		doRequest(r);
	}
	protected void onComplete(Match match) { }
	@Override
	protected void onException(Exception e) {
	}

	@Override
	protected void processPostExecute(String s) {
		m.stopMatch();
		onComplete(m);
		
	}
	private Match m;

}
