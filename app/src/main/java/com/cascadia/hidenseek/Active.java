package com.cascadia.hidenseek;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cascadia.hidenseek.Player.Role;
import com.cascadia.hidenseek.Player.Status;
import com.cascadia.hidenseek.network.DeletePlayingRequest;
import com.cascadia.hidenseek.network.PutStatusRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;

public class Active extends FragmentActivity {
	GoogleMap googleMap;
	Match match;
	Player player;
	boolean isActive;
	Status pend;
	Role playerRole;
	String Timer;
	final Context context = this;
	boolean tagged = true;
	private ShowHider sh;
	Long showTime = (long) 30000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_active);

		match = LoginManager.GetMatch();
		player = LoginManager.playerMe;
		isActive = true;
		
		if (match == null || player == null) {
			Dialog d = new Dialog(this);
			d.setTitle("Error: null match.");
			d.show();
			finish();

		}
		
		ActionBar ab = getActionBar();
		if (player.getRole() != Player.Role.Seeker) {
			ab.hide();
		}

		/* Show user's position on map
		googleMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.mapview)).getMap();
		googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		googleMap.setMyLocationEnabled(true);
		googleMap
				.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
					@Override
					public void onMyLocationChange(Location location) {
						LatLng point = new LatLng(location.getLatitude(),
								location.getLongitude());
						player.SetLocation(location);
						googleMap.animateCamera(CameraUpdateFactory
								.newLatLngZoom(point, 17));
					}
				});
		*/
		// User clicked Leave Match button
		ImageButton btnLeave = (ImageButton) findViewById(R.id.btnLeaveGame);
		btnLeave.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Active.this, Home.class);
				startActivity(intent);
			}
		});

		if (player.getRole() == Role.Seeker) {
			new Thread(new SeekerTask(seekerHandler, player)).start();
		}
		else {
			new Thread(new HiderTask(hiderHandler, player)).start();
		}

	}

	private Handler seekerHandler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			Bundle bundle = message.getData();

			String event = bundle.getString("event");
			match = (Match) message.obj;
			player = match.players.get(new Integer(message.arg1));

			// handle the event
			switch (event) {
				case "showDistance":
					showDistance();
					break;
				case "showSpotted":
					showSpotted();
					break;
				case "showFound":
					showFound();
					break;
				case "game-over":
					gameOver();
					break;
			}
		}
	};



	// Update the distance indication
	// from the Seeker to the hider
	private void showDistance() {
	}
	// Update the player to show spotted
	private void showSpotted() {
	}
	// Update the player to show found
	private void showFound() {
	}
	// Go back to the login screen when the game has ended
	private void gameOver() {
		Intent end = new Intent(context, TempToHome.class);
		startActivity(end);
	}

	// Handle events from the hider task
	private Handler hiderHandler= new Handler() {
		@Override
		public void handleMessage(Message message) {
			Bundle bundle = message.getData();

			String event = bundle.getString("event");

			switch (event) {
				case "spotted":
					showFoundAlert();
					break;
				case "show-other-players":
					showOtherPlayers(message.arg1);
					break;
			}
		}
	};

	private void showFoundAlert() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		// set title
		alertDialogBuilder.setTitle("Found You");

		alertDialogBuilder
                .setMessage("The seeker just said he found you, is this correct?")
                .setCancelable(false)
                .setPositiveButton("Yes", foundClickListener)
                .setNegativeButton("No", notFoundClickListener);

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	private DialogInterface.OnClickListener foundClickListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int id) {

			player.setStatus(Status.Found);
			player.setLocation(null);
			PutStatusRequest pp = new PutStatusRequest() {

				@Override
				protected void onException(Exception e) {
					e.printStackTrace();
				}

			};

			pp.DoRequest(player);

			ShowSeeker();
			tagged = true;
			Intent intent = new Intent(context, TempToHome.class);
			startActivity(intent);
		}
	};
	private DialogInterface.OnClickListener notFoundClickListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int id) {
			// if this button is clicked, just close the dialog box
			// and do nothing
			player.setStatus(Status.Hiding);
			PutStatusRequest pp = new PutStatusRequest() {

				@Override
				protected void onException(Exception e) {
					e.printStackTrace();
				}

			};
			pp.DoRequest(player);
			tagged = true;
		}
	};
	// Show the other hiders and the Seeker for a period of time
	// when another hider has been found
	private void showOtherPlayers(int foundID) {
	}


	public void ShowSeeker() {
/*
		sh = new ShowHider(showTime, 1000, temp);
		sh.startCountDown1();
*/
	}

	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		
		final String TAG_ERROR_DIALOG_FRAGMENT = "errorDialog";
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (status == ConnectionResult.SUCCESS) {
			// no problems just work
		} else if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
			ErrorDialogFragment.newInstance(status).show(
					getSupportFragmentManager(), TAG_ERROR_DIALOG_FRAGMENT);
		} else {
			Toast.makeText(this, "Google Maps V2 is not available!",
					Toast.LENGTH_LONG).show();
			finish();
		}
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		DeletePlayingRequest dpRequest = new DeletePlayingRequest() {

			@Override
			protected void onException(Exception e) {
				e.printStackTrace();
			}
		};
		dpRequest.DoRequest(player);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class ErrorDialogFragment extends DialogFragment {
		static final String ARG_STATUS = "status";

		static ErrorDialogFragment newInstance(int status) {
			Bundle args = new Bundle();
			args.putInt(ARG_STATUS, status);
			ErrorDialogFragment result = new ErrorDialogFragment();
			result.setArguments(args);
			return (result);
		}

		public void show(FragmentManager supportFragmentManager,
				String TAG_ERROR_DIALOG_FRAGMENT) {
			// TODO Auto-generated method stub

		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Bundle args = getArguments();
			return GooglePlayServicesUtil.getErrorDialog(
					args.getInt(ARG_STATUS), getActivity(), 0);
		}

		@Override
		public void onDismiss(DialogInterface dlg) {
			if (getActivity() != null) {
				getActivity().finish();
			}
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		isActive = true;
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.players, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.players_list) {
			Intent intent = new Intent(Active.this, CurrentPlayers.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

