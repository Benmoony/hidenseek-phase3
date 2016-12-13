package com.cascadia.hidenseek.network;

import com.cascadia.hidenseek.model.Match;
import com.cascadia.hidenseek.network.NetworkBase.RequestType;

import java.util.List;


public abstract class GetMatchListRequest extends NetworkRequest {

	private Match.Status status = null; // optional filter for matches

	public GetMatchListRequest(Match.Status status) {
		this.status = status;
	}

	public void doRequest() {
		Request request = new Request();
		request.url = baseUrl + "matches";
		if (status != null) {
			request.url = String.format("%1$s?status=%2$s", request.url, status.getApiString());
		}
		request.type = RequestType.GET;
		doRequest(request);
	}
	
	protected abstract void onComplete(List<Match> matches);
	
	@Override
	protected final void processPostExecute(String s) {
		onComplete(Match.parseToList(s));
	}

}
