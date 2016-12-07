package com.cascadia.hidenseek;

import android.os.Bundle;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.widget.TextView;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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

    public String secondsLeft(){

       /*public void Date (long millisUntilFinished){
            long secondUntilFinished = (long) (millisUntilFinished/1000);
            long secondsLeft = (endTime - secondUntilFinished);
            long minutesPassed = (long) (secondsLeft/60);
            secondsLeft = secondsLeft%60;

        return secondsLeft();*/



        long finalTime;
        SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss");

        //subtracting endTime from current Date time from calender

        //finalTime = new Date (Calendar.getInstance().getTime().getTime() -endTime.getTime());
        //get getEndTime from match
        finalTime  = endTime.getTime() - (Calendar.getInstance().getTime().getTime());

        String countTime = timeformat.format(finalTime);
        //return countTime;

        /*int difference = 0;
        int hoursLeft = (difference / (3600));
        int minutesLeft = ((difference - (hoursLeft * 3600)) / 60);
        int secondsLeft = (difference - (hoursLeft * 3600) - minutesLeft * 60);*/

        long seconds = finalTime / 1000L;

        int secondsLeft = (int) (seconds % 60);
        int minutesLeft = (int)(seconds / 60) % 60;
        int hoursLeft = (int)(seconds / (60 * 60) ) % 24;

        return String.format("%02d:%02d:%02d", hoursLeft, minutesLeft, secondsLeft );

    }
}