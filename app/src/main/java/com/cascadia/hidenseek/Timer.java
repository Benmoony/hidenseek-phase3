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

            return secondsLeft();

          (String.format("%2d")), minutesPassed + ":" + String.format("2%d", secondsLeft));*/
            //return   "Bothell";
            //start = System.currentTimeMillis();

            Date finalTime;
            SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss");

            finalTime = new Date (Calendar.getInstance().getTime().getTime() -endTime.getTime());


            //alendar.add(Calendar.MINUTE, -endTime);

            String countTime = timeformat.format(finalTime);

            return countTime;
        }
}