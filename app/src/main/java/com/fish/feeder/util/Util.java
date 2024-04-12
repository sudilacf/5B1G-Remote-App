package com.fish.feeder.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;

public class Util {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getLastFedTime(long epochTime) {

        if(epochTime == 0) {
            return "";
        }

        long currentEpochTime = Instant.now().getEpochSecond() + (8 * 3600);
        long timeDifference = (currentEpochTime - epochTime);

        if(timeDifference < 60 && timeDifference >= 0) {

            return timeDifference == 1 ? "1 second ago" : timeDifference + " seconds ago";

        } else if(timeDifference < 60 * 60 && timeDifference >= 60) {

            int minutes = (int) (timeDifference / 60);
            int remainder = (int) (timeDifference % 60);

            return minutes == 1 && remainder == 0 ? "1 min & 0 sec ago" : minutes + " min & " + remainder + " sec ago";

        } else if (timeDifference < 60 * 60 * 24 && timeDifference >= (60 * 60)) {

            int hours = (int) (timeDifference / 3600);
            int remainder = (int) ((timeDifference % 3600) / 60);

            return hours == 1 && remainder == 0 ? "1 hr & 0 min ago" : hours + " hr & " + remainder + " min ago";

        } else {

            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy hh:mm a", Locale.ENGLISH);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC+8"));

            return sdf.format(new Date(epochTime * 1000));

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getFeedTime(String format) throws ParseException {

        if(format == null || format.isEmpty()) {
            return "";
        }

        long epochTime = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.ENGLISH).parse(format).getTime() / 1000;
        long currentEpochTime = Instant.now().getEpochSecond();
        long timeDifference = (epochTime - currentEpochTime);

        if(timeDifference < 0)
            return "No Scheduled time";

        if(timeDifference < 60 && timeDifference >= 0) {

            return timeDifference == 1 ? "1 second to go" : timeDifference + " seconds to go";

        } else if(timeDifference < 60 * 60 && timeDifference >= 60) {

            int minutes = (int) (timeDifference / 60);
            int remainder = (int) (timeDifference % 60);

            return minutes == 1 && remainder == 0 ? "1 min & 0 sec to go" : minutes + " min & " + remainder + " sec to go";

        } else if (timeDifference < 60 * 60 * 24 && timeDifference >= (60 * 60)) {

            int hours = (int) (timeDifference / 3600);
            int remainder = (int) ((timeDifference % 3600) / 60);

            return hours == 1 && remainder == 0 ? "1 hr & 0 min to go" : hours + " hr & " + remainder + " min to go";

        } else {

            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd hh:mm a");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC+8"));

            return sdf.format(new Date(epochTime * 1000));

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getFormattedTime(String format, int offset) throws ParseException {

        long epochTime = Instant.now().getEpochSecond() + (offset * 3600L);
        Date date = new Date(epochTime);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.ENGLISH);
        String formattedDate = sdf.format(date);

        return formattedDate;

    }

    public static void setGradientBackground(View view, String startColor, String endColor, float radius, boolean ripple) {

        GradientDrawable gradientDrawable = new GradientDrawable();

        gradientDrawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientDrawable.setColors(new int[] {Color.parseColor(startColor), Color.parseColor(endColor)});
        gradientDrawable.setCornerRadius(radius);

        if(ripple) {

            view.setClickable(true);
            view.setBackground(new RippleDrawable(ColorStateList.valueOf(Color.parseColor(startColor)), gradientDrawable, null));

        } else {

            view.setBackground(gradientDrawable);

        }

    }

    public static boolean isConnectedToMachine(Context context) {

        final String machineBSSID = "24:dc:c3:a6:ec:15";

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnectedOrConnecting() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {

            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            Log.i("MAC", wifiInfo.getBSSID());

            return wifiInfo.getBSSID().equals(machineBSSID);

        }

        return false;

    }

    public static  boolean isLocationEnabled(Context context) {

        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        return gps_enabled && network_enabled;

    }

}
