package com.sdi.castivate.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Network
{



    /**
     * This method check internet connection. True for connection enabled, false
     * otherwise.
     */
    public static boolean isNetworkConnected(Context con) {
        ConnectivityManager cm = (ConnectivityManager) con
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        // Check for Wifi connection
        NetworkInfo wifiNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {

            if (wifiNetwork.getState() == NetworkInfo.State.CONNECTED)
                return true;
        }

        // Check for mobile 3G and 2G connection.
        NetworkInfo mobileNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            if (mobileNetwork.getState() == NetworkInfo.State.CONNECTED)
                return true;
        }

        // Some android devices fails to returns Wifi and 3G and to 2G
        // information we need to use active network connection.
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {

            if (activeNetwork.getState() == NetworkInfo.State.CONNECTED)
                return true;
        }

        return false;
    }
    
}
