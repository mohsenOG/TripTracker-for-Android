package eu.wonderfulme.triptracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Utils {

    private Utils() {}

    static public String getFormattedFileName() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd_HHmmss");
        Date resultDate = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(resultDate) + "_triptracker.csv";
    }

    static public String getFormattedTime (long timeInMilliseconds) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        Date resultDate = new Date(timeInMilliseconds);
        return simpleDateFormat.format(resultDate);
    }

    private static SharedPreferences getSharedPref(Context context) {
        return context.getSharedPreferences(context.getString(R.string.preference_filename), Context.MODE_PRIVATE);
    }

//    static public void setRecordPeriodToSharedPref(Context context, int recordPeriod) {
//        SharedPreferences sharedPreferences = Utils.getSharedPref(context);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putInt(context.getString(R.string.preference_record_period), recordPeriod)
//                .apply();
//    }

    static public int getRecordPeriodFromSharedPref(Context context) {
        SharedPreferences sharedPreferences = Utils.getSharedPref(context);
        return sharedPreferences.getInt(context.getString(R.string.preference_record_period), 10);
    }

    static public int getItemKeyFromSharedPref(Context context) {
        SharedPreferences sharedPreferences = Utils.getSharedPref(context);
        return sharedPreferences.getInt(context.getString(R.string.preference_item_key), -100);
    }

    static public void setParkingLocationToSharedPref(Context context, Location location) {
        SharedPreferences sharedPreferences = Utils.getSharedPref(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (location == null) {
            editor.putStringSet(context.getString(R.string.preference_parking_location), null);
        }
        else {
            String latitude = String.valueOf(location.getLatitude());
            String longitude = String.valueOf(location.getLongitude());
            Set<String> locationSet = new HashSet<>();
            locationSet.add(latitude);
            locationSet.add(longitude);
            editor.putStringSet(context.getString(R.string.preference_parking_location), locationSet);
        }
        editor.apply();
    }

    /**
     * Function to get the parking location from shared pref.
     * @return null if parking is not set otherwise a list of string. index 0 = latitude, index 1 = longitude
     */
    static public List<String> getParkingLocationFromSharedPref(Context context) {
        SharedPreferences sharedPref = Utils.getSharedPref(context);
        Set<String> location = sharedPref.getStringSet(context.getString(R.string.preference_parking_location), null);
        if (location == null) return null;
        return new ArrayList<>(location);
    }


    static public boolean isLocationPermissionGranted(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

}
