package com.cascadia.hidenseek.network;


import android.location.Location;

import java.text.ParseException;
import java.util.Locale;

public class LocationParser {

	public static String getString(Location l) {
		if(l == null) {
			throw new NullPointerException("Null Location in LocationParser.getString");
		}
		return String.format(Locale.ENGLISH, "%f\n%f\n%f\n%f\n%f",
				l.getLatitude(),
				l.getLongitude(),
				l.getAccuracy(),
				l.getBearing(),
				l.getSpeed());
	}
	
	public static Location parse(String s) throws ParseException {
		Location toReturn = new Location("server");
		String[] values = s.split("\n");
		if(values.length < 5) {
			throw new ParseException("Invalid Location string: wrong number of values", -1);
		}
		try {
		toReturn.setLatitude(Double.parseDouble(values[0]));
		toReturn.setLongitude(Double.parseDouble(values[1]));
		toReturn.setAccuracy(Float.parseFloat(values[2]));
		toReturn.setBearing(Float.parseFloat(values[3]));
		toReturn.setSpeed(Float.parseFloat(values[4]));
		} catch(NumberFormatException e) {
			throw new ParseException("Invalid Location string: parse error",-1);

		}
		return toReturn;
	}

}
