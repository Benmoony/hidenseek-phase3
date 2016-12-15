package com.cascadia.hidenseek.utilities;

import com.cascadia.hidenseek.model.Match;
import com.cascadia.hidenseek.model.Match.MatchType;
import com.cascadia.hidenseek.model.Player;

public class LoginManager {
	

	public LoginManager() {	} 
	
	public static Match validateHostLogin(String matchName, String password, int matchType ) {
		if(matchType == 0) {
			match = new Match(matchName, password, MatchType.HideNSeek);
		} else {
			match = new Match(matchName, password, MatchType.Sandbox);
		}
		isHost = true;
		return match;
	}
	 
	public static void validateJoinLogin(Player player) {
		playerMe = player;
		match = player.getAssociatedMatch();
		isHost = false;
	}
	/*  Should not allow to change match, it is related to player
	public static void SetMatch(Match match) {
		match = match;
	} */
	
	public static Match getMatch() {
		return match;
	}

	public static void resetLoginManger() {
		playerMe = null;
		match = null;

	}
	public static Player playerMe;
	private static Match match;
	public static boolean isHost;
}
