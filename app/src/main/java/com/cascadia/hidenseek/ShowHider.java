package com.cascadia.hidenseek;

import android.os.CountDownTimer;

import com.cascadia.hidenseek.Player.Role;
import com.cascadia.hidenseek.network.PutRoleRequest;

public class ShowHider extends CountDownTimer {
	private boolean isrunning = false;
	Player isSeeker;

	public ShowHider(long startTime, long interval) {
			super(startTime, interval);
		
	}
	public ShowHider(long StartTime,long interval, Player p){
		super(StartTime, interval);
		isSeeker=p;
	}

	public void startCountDown1() {
		isrunning = true;
		isSeeker.setRole(Role.Supervisor);
	    PutRoleRequest pp = new PutRoleRequest() {
			
			@Override
			protected void onException(Exception e) {
				e.printStackTrace();
			}
			
								
		};
		pp.doRequest(isSeeker);
		start();
	}
	
	@Override
	public void onFinish() {
		isrunning = true;
		isSeeker.setRole(Role.Seeker);
	    PutRoleRequest pp = new PutRoleRequest() {
			
			@Override
			protected void onException(Exception e) {
				e.printStackTrace();
			}
			
								
		};
		pp.doRequest(isSeeker);
		start();
		isrunning = false;
	}

	@Override
	public void onTick(long millisUntilFinished) {
		
	}

	public boolean isRunning() {
		return isrunning;
	}
}
