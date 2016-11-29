package com.cascadia.hidenseek;

import java.util.Hashtable;


import com.cascadia.hidenseek.Player.Status;
import com.cascadia.hidenseek.network.PutStatusRequest;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class CustomPlayersList extends ArrayAdapter<String>{

	private final Activity context;
	private final String[] web;
	
	public CustomPlayersList(Activity context, String[] web) {
		super(context, R.layout.list_current_player, web);
		this.context = context;
		this.web = web;
	}
	
	//Add roles here
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		//Create listview
		
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView= inflater.inflate(R.layout.list_current_player, null, true);
		final TextView txtTitle = (TextView) rowView.findViewById(R.id.player_name);
		Button btn=(Button) rowView.findViewById(R.id.btn_found);
		txtTitle.setText(web[position]);

		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String playerStr = txtTitle.getText().toString();
				int sIndex = playerStr.indexOf(',');
				String playerID = playerStr.substring(0, sIndex);
				Player player = null;
				Hashtable<Integer, Player> players = LoginManager.getMatch().players;
				player = players.get(new Integer(playerID));

				if (player != null && player.getStatus() == Status.Hiding) {
					Dialog dialog = new Dialog(context);
					dialog.setTitle("Player " + player.getName() + " Found!");
					dialog.show();
				} else
					return;

				player.setStatus(Status.Spotted);
				PutStatusRequest putStatusRequest = new PutStatusRequest() {

					@Override
					protected void onException(Exception e) {
						e.printStackTrace();
					}

					@SuppressWarnings("unused")
					protected void onComplete(Player p) {
						Dialog dialog = new Dialog(context);
						dialog.setTitle("Player " + p.getName() + " Found!");
						dialog.show();
					}

				};
				putStatusRequest.DoRequest(player);

			}
		});

		return rowView;
	}
}
	
	
	