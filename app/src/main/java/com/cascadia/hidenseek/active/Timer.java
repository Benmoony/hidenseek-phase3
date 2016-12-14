package com.cascadia.hidenseek.active;

import com.cascadia.hidenseek.model.Match;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by zinmaung on 11/27/16.
 */

public class Timer {

    String timername;
    //int endTime = 10;
    Date endTime;

    public String getTimername() {
        return timername;
    }

    public void setTimername(String timername) {
        this.timername = timername;
    }

    public Timer(Match match){
        this.endTime = match.getEndTime();

    }

    public String SecondsLeft(){

        long finalTime;
        SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss");

        //subtracting endTime from current Date time from calender

        //get getEndTime from match
        finalTime  = endTime.getTime() - (Calendar.getInstance().getTime().getTime());

        String countTime = timeformat.format(finalTime);

        long seconds = finalTime / 1000L;

        int SecondsLeft = (int) (seconds % 60);
        int minutesLeft = (int)(seconds / 60) % 60;
        int hoursLeft = (int)(seconds / (60 * 60) ) % 24;

        if (finalTime <= 0){
            return "Times Up!";
            //return "00:00:00";
        }
        return String.format("%02d:%02d:%02d", hoursLeft, minutesLeft, SecondsLeft );
    }
}