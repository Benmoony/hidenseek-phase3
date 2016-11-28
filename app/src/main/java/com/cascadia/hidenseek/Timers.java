package com.cascadia.hidenseek;

import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by zinmaung on 11/27/16.
 */


public class Timers extends Active {

    String timername;
    int EndTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active);

        TextView timerTextView = (TextView) findViewById(R.id.timer);

        timerTextView.setText(timername);
    }

        public void showTimer(){

    }

    public void onTick (long millisUntilFinished){
        long secondUntilFinished = (long) (millisUntilFinished/1000);
        long secondsPassed = (EndTime - secondUntilFinished);
        long minutesPassed = (long) (secondsPassed/60);
        secondsPassed = secondsPassed%60;

        EndTime.setText(String.format("%2d"), minutesPassed + ":" + String.format("2%d", secondsPassed));
    }

    public void onFinish(){

    }
}
