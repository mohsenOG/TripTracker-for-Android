package eu.wonderfulme.triptracker.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.wonderfulme.triptracker.R;

public class UtilsSharedPref {

    private UtilsSharedPref() {}

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
        SharedPreferences sharedPreferences = UtilsSharedPref.getSharedPref(context);
        return sharedPreferences.getInt(context.getString(R.string.preference_record_period), 10);
    }

    static public int getItemKeyFromSharedPref(Context context) {
        SharedPreferences sharedPreferences = UtilsSharedPref.getSharedPref(context);
        return sharedPreferences.getInt(context.getString(R.string.preference_item_key), -100);
    }

    static public void setParkingLocationToSharedPref(Context context, Location location) {
        SharedPreferences sharedPreferences = UtilsSharedPref.getSharedPref(context);
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
        SharedPreferences sharedPref = UtilsSharedPref.getSharedPref(context);
        Set<String> location = sharedPref.getStringSet(context.getString(R.string.preference_parking_location), null);
        if (location == null) return null;
        return new ArrayList<>(location);
    }
}
