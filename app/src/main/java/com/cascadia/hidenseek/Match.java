package com.cascadia.hidenseek;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
/* The Match object contains the definition for the match, its status,
*  and a Hashtable of the players */
public class Match {

	public enum Status {
		Active,
		Pending,
		Complete;

		public static Status parse(String s) {
			if(s.equalsIgnoreCase("active")){
				return Active;
			} else if (s.equalsIgnoreCase("pending")) {
				return Pending;
			} else return Complete;
		}
		
		public String getApiString() {
			switch(this) {
			case Active:
				return "active";
			case Pending:
				return "pending";
			default:
				return "complete";
			}
		}
	}
	
	public enum MatchType {
		HideNSeek,
		Sandbox;
		
		/**
		 * Returns Sandbox if string is equal to "sandbox" (ignoring case),
		 * else returns HideNSeek
		 */
		public static MatchType Parse(String s) {
			if(s.equalsIgnoreCase("sandbox")){
				return Sandbox;
			}
			else return HideNSeek;
		}
		
		public String GetApiString() {
			switch(this) {
			case Sandbox:
				return "sandbox";
			default:
				return "hide-n-seek";
			}
		}
		
	}
	
	public Match() {
	}
	public Match(String name, String password, MatchType type) {
		this.name = name;
		this.password = password;
		this.type = type;

	}
	
	/**
	 * Parses the given JSON string
	 * @param jsonStr A string formatted in JSON as prescribed by the 
	 * Hide-n-Seek API for the /matches/ request
	 * @return A list of Matches. Returns null in case of parsing error.
	 */
	public static List<Match> parseToList(String jsonStr) {
		List<Match> toReturn = new ArrayList<Match>();
		JSONArray jArray;
		
		try {
			jArray = new JSONObject(jsonStr).getJSONArray("matches");
			for(int i = 0; i < jArray.length(); i++) {
				toReturn.add(parse(jArray.getJSONObject(i)));
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return toReturn;
	}
	
	/**
	 * Parses the given JSON string
	 * @param jsonStr A string formatted in JSON as prescribed by the 
	 * Hide-n-Seek API for a /matches/match id request
	 * @return A Match. Returns null in case of parsing error.
	 */
	public static Match parse(String jsonStr) {
		try {
			return parse(
					new JSONObject(jsonStr)
						.getJSONArray("match")
						.getJSONObject(0)
						);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String toJSONPost() {
		JSONObject jObject = new JSONObject();
		try {
			jObject.put("name", name);
			jObject.put("password", password);
			jObject.put("type", type.GetApiString());
			return jObject.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void processPostResponse(String jsonStr) {
		try {
			matchId = new JSONObject(jsonStr).getInt("id");
		} catch (JSONException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
	}
	
	//Parameters needed to start the match
	public String toJSONStart() {
		JSONObject jObject = new JSONObject();
		try {
			jObject.put("countTime", countTime);
			jObject.put("seekTime", seekTime);
			return jObject.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	

	/* Parse a match object from a JSONObject */
	private static Match parse(JSONObject jObject) throws JSONException 
	{
		Match toReturn = new Match(jObject.getString("name"),
					  jObject.getString("password"),
					  MatchType.Parse(jObject.getString("type")));
		//For the following values, set the value to -1 if no valid value is received
		try {
			toReturn.matchId = jObject.getInt("id");
		} catch(Exception e) {
			toReturn.matchId = -1;
		}
		try {
			toReturn.countTime = jObject.getInt("countTime");
		} catch(Exception e) {
			toReturn.countTime = -1;
		}
		try {
			toReturn.seekTime = jObject.getInt("seekTime");
		} catch(Exception e) {
			toReturn.seekTime = -1;
		}
		try {
			toReturn.status = Status.parse(jObject.getString("status"));
		} catch(Exception e) {
			//Leave it null
		}
		try {
			toReturn.startTime = dateTimeFormat.parse(jObject.getString("startTime"));
		} catch(JSONException e) {
            toReturn.startTime = null;
		} catch (Exception e) {
			//Assume that the exception means there is no startTime set.
		}
		try {
			toReturn.timestamp = jObject.getInt("timestamp");
		} catch (Exception e) {
            toReturn.timestamp = -1;
        }

		return toReturn;
	}
	
	public int getId() {
		return matchId;
	}
	public String getName() {
		return name;
	}
	public String getPassword() {
		return password;
	}
	public int getCountTime() {
		return countTime;
	}
	public void setCountTime(int time) {
		countTime = time;
	}
	public int getSeekTime() {
		return seekTime;
	}
	public void setSeekTime(int time) {
		seekTime = time;
	}
	public MatchType getType() {
		return type;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Match.Status s){
		status= s;
	}

	public Date getStartTime() {
		return startTime;
	}
	// Provide the expected end time
	public Date getEndTime() {
		Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
		calendar.add(Calendar.SECOND, countTime);
		calendar.add(Calendar.MINUTE, seekTime);
		return calendar.getTime();
	}
    public Date getTimeStamp() {
        return new Date((long)timestamp * 1000);
    }

	private int matchId = -1; //Does not get set by constructor; set by parse
	private String name;
	private String password;
	private int countTime = -1; // time in seconds for the hiders to hide
	private int seekTime = -1; // game time in minutes
	private Status status;
	private MatchType type; // Hide N Seek or Sandbox
	private Date startTime; // time at the server that the game startd
	private int timestamp; // seconds past the epoch 01/01/70 12:00am
	private Status stat;

	
	private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
	
	public PlayerList players = new PlayerList();

	public void startMatch() {
		startTime = new Date();
		status = Status.Active;
	}

    public void stopMatch() {
        stat = Status.Complete;
    }
}
