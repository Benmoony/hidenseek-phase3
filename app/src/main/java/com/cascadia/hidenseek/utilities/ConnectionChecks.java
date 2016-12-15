package com.cascadia.hidenseek.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by deb on 12/7/16.
 *
 * A class that handles checking if the device has a network connection or not,
 * and notifies the user if not.
 */

public class ConnectionChecks {

    private ConnectivityManager connectivityManager;
    private Activity context;

    public ConnectionChecks(Activity context) {
        connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.context = context;
    }
    public boolean isConnected() {

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    // Show an alert warning the user that network connection is missing.
    // This must be called within the GUI task
    public void showAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set title
        alertDialogBuilder.setTitle("No Internet Connection");

        alertDialogBuilder
                .setMessage("An internet connection is required.")
                .setCancelable(true);

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

}
