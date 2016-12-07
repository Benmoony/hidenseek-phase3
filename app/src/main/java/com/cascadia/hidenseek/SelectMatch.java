package com.cascadia.hidenseek;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cascadia.hidenseek.Match.Status;
import com.cascadia.hidenseek.network.GetMatchListRequest;

import java.util.ArrayList;
import java.util.List;

public class SelectMatch extends Activity {

	ListView listView;
	
	public static String selectedMatch;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_match);

		listView = (ListView) findViewById(R.id.configPlayerList);
		
		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				selectedMatch = listView.getItemAtPosition(arg2).toString();
				finish();
			}
		});
        new Thread(checkMatches).start();
	}

	/**
	 * initList creates a list of all the matches that are in a pending state
	 */
	private Runnable checkMatches = new Runnable() {

        @Override
        public void run() {
            while (selectedMatch == null) {
                GetMatchListRequest request = new GetMatchListRequest(Status.Pending) {

                    @Override
                    protected void onException(Exception e) { }

                    @Override
                    protected void onComplete(List<Match> matches) {
                        //Gets the list of matches and puts in listview
                        ArrayList<String> gameTitles = new ArrayList<String>();
                        for (Match m : matches) {
                            if (m.getStatus() == Status.Pending) {
                                String title = m.getId() + " - " + m.getName();
                                gameTitles.add(title);
                            }
                        }
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SelectMatch.this,
                                android.R.layout.simple_list_item_single_choice, gameTitles);
                        listView.setAdapter(arrayAdapter);
                    }
                };
                request.doRequest();

                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select_match, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
