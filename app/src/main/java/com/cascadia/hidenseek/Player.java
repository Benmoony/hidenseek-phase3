package com.cascadia.hidenseek;

import android.location.Location;

import com.cascadia.hidenseek.network.LocationParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.cascadia.hidenseek.Player.Temperature.Cold;
import static com.cascadia.hidenseek.Player.Temperature.Hot;
import static com.cascadia.hidenseek.Player.Temperature.Warm;

public class Player {

public Temperature getTemperature() {
return temperature;
}

    public enum Role {
		Hider,
		Seeker,
		Supervisor;
		
		public static Role parse(String s) {
			if(s.equalsIgnoreCase("hider")){
				return Hider;
			} else if (s.equalsIgnoreCase("seeker")) {
				return Seeker;
			} else return Supervisor;
		}
		
		public String getApiString() {
			switch(this) {
			case Hider:
				return "hider";
			case Seeker:
				return "seeker";
			default:
				return "admin";
			}
		}
	}

	public enum Temperature {
        Hot,
        Warm,
		Cold
    }

        // Show hot or cold location from seeker to hiders
        public Temperature hotOrCold(Player seeker) {

            if (this.getLocation().distanceTo(seeker.getLocation()) <= 10f) {
                return Hot;
            }

            else if (this.getLocation().distanceTo(seeker.getLocation()) < 20f) {
                return Warm;
            }

            else {
                return Cold;
            }
        }


	
	public enum Status {
		Hiding,
		Spotted,
		Found;
		
		public static Status parse(String s) {
			if(s.equalsIgnoreCase("hiding")){
				return Hiding;
			} else if (s.equalsIgnoreCase("spotted")) {
				return Spotted;
			} else if (s.equalsIgnoreCase("found")) {
				return Found;
			} else return null;
		}
		
		public String getApiString() {
			switch(this) {
			case Hiding:
				return "hiding";
			case Spotted:
				return "spotted";
			default:
				return "found";
			}
		}
	}
	
	public Player(String name, Match match) {
		this.name = name;
		this.associatedMatch = match;
		
	}

	
	// parse the match/#/player json and return a list of players
	public static PlayerList parseToList(String jsonStr, Match associatedMatch)
			throws JSONException {
		PlayerList toReturn = new PlayerList();
		JSONArray jArray = new JSONObject(jsonStr).getJSONArray("players");
		for(int i = 0; i < jArray.length(); i++) {
			Player player = parse(jArray.getJSONObject(i), associatedMatch);
			toReturn.put(new Integer(player.getId()), player);
		}
		return toReturn;
	}
	
	public String toJSONPost(String password) throws JSONException {
		JSONObject jObject = new JSONObject();
		jObject.put("name", name);
		jObject.put("password", password);
		return jObject.toString();
	}

	// Prepare the role data for the API request PUT .../players/playerid/role/
	public String roleToJSON() throws JSONException {
		JSONObject jObject = new JSONObject();
		jObject.put("role", role.getApiString());
		return jObject.toString();
	}

	// Prepare the status data for the API request PUT .../players/playerid/status/
	public String statusToJSON() throws JSONException {
		JSONObject jObject = new JSONObject();
		jObject.put("status", status.getApiString());
		return jObject.toString();
	}
	
	public String locationToJSON() throws JSONException {
		JSONObject jObject = new JSONObject();
		jObject.put("gps", LocationParser.getString(location));
		return jObject.toString();
	}
	
	public String playingToJSON() throws JSONException {
		JSONObject jObject = new JSONObject();
		jObject.put("playing", Boolean.toString(isPlaying));
		return jObject.toString();
	}
	
	public boolean processPostResponse(String jsonStr) {
		try {
			playerId = new JSONObject(jsonStr).getInt("playerID");
			if(playerId == 0) {
				return false;
			}
			else return true;
		} catch (JSONException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	private static Player parse(JSONObject jObject, Match associatedMatch)
			throws JSONException {
		Player toReturn = new Player(jObject.getString("name"), associatedMatch);
		toReturn.playerId = jObject.getInt("id");
		toReturn.role = Role.parse(jObject.getString("role"));
		toReturn.status = Status.parse(jObject.getString("hiderStatus"));
		toReturn.isPlaying = jObject.getInt("playing") == 1;
		try {
			toReturn.location = LocationParser.parse(jObject.getString("GPSLocation"));
			toReturn.lastUpdatedLocation = dateTimeFormat.parse(jObject.getString("lastUpdated"));
		} catch(JSONException e) {
		} catch(ParseException e) {
			//Assume that the exception means to leave the current values alone
		}
		return toReturn;
	}
	
	public String getName() {
		return name;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location l) {
		location = l;
	}
	
	public Date getLastUpdatedLocation() {
		return lastUpdatedLocation;
	}
	
	public Role getRole() {
		return role;
	}
	
	public void setRole(Role r) {
		role = r;
	}

	public Status getStatus() {
		return status;
	}
	
	public void setStatus(Status s) {
		status = s;
	}
	
	public int getId() {
		return playerId;
	}
	
	public void setID(int ID){
		playerId=ID;
	}
	
	public Boolean isPlaying() {
		return isPlaying;
	}
	
	public void setPlaying(Boolean playing) {
		isPlaying = playing;
	}
	public Match getAssociatedMatch() {
		return associatedMatch;
	}
	
	private Location location;
	private Date lastUpdatedLocation;
	private Match associatedMatch;
	private String name;
	private Role role;
	private Status status;
	private boolean isPlaying = true;
	private int playerId = -1;
    private Temperature temperature;
	
	private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);

	public void resetPlayer() {
		associatedMatch=null;
		role=null;
		status=null;
		playerId=-1;
		
	}
}
