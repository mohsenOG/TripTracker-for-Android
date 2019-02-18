package eu.wonderfulme.triptracker.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.wonderfulme.triptracker.R;
import eu.wonderfulme.triptracker.database.LocationHeaderData;

public class UtilsSharedPref {

    public static final int DEFAULT_RECORD_PERIOD = 10;
    public static final int DEFAULT_LAST_ITEM_KEY = -100;

    private UtilsSharedPref() {}

    private static SharedPreferences getSharedPref(Context context) {
        return context.getSharedPreferences(context.getString(R.string.preference_filename), Context.MODE_PRIVATE);
    }

    static public void setRecordPeriodToSharedPref(Context context, int recordPeriod) {
        SharedPreferences sharedPreferences = UtilsSharedPref.getSharedPref(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.preference_record_period), recordPeriod)
                .apply();
    }

    static public int getRecordPeriodFromSharedPref(Context context) {
        SharedPreferences sharedPreferences = UtilsSharedPref.getSharedPref(context);
        return sharedPreferences.getInt(context.getString(R.string.preference_record_period), DEFAULT_RECORD_PERIOD);
    }

    static public int getLastItemKeyFromSharedPref(Context context) {
        SharedPreferences sharedPreferences = UtilsSharedPref.getSharedPref(context);
        return sharedPreferences.getInt(context.getString(R.string.preference_item_key), DEFAULT_LAST_ITEM_KEY);
    }

    static public void setItemKeyToSharedPref(Context context, int lastItemKey) {
        SharedPreferences sharedPreferences = UtilsSharedPref.getSharedPref(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.preference_item_key), lastItemKey);
        editor.apply();

    }

    static public void setNukeDbChecker(Context context, boolean value) {
        SharedPreferences sharedPreferences = UtilsSharedPref.getSharedPref(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.preference_nuke_db_checker), value);
        editor.apply();
    }

    static public boolean getNukeDbChecker(Context context) {
        SharedPreferences sharedPreferences = UtilsSharedPref.getSharedPref(context);
        return sharedPreferences.getBoolean(context.getString(R.string.preference_nuke_db_checker), false);
    }

    static public void setWidgetServiceChecker(Context context, boolean isRunning) {
        SharedPreferences sharedPreferences = UtilsSharedPref.getSharedPref(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.preference_widget_service_status), isRunning);
        editor.apply();
    }

    static public boolean getWidgetServiceChecker(Context context) {
        SharedPreferences sharedPreferences = UtilsSharedPref.getSharedPref(context);
        return sharedPreferences.getBoolean(context.getString(R.string.preference_widget_service_status), true);
    }

    static public void setWidgetRouteList(Context context, List<LocationHeaderData> routes) {
        List<String> routesList = new ArrayList<>();
        for (LocationHeaderData headerData : routes) {
            routesList.add(headerData.getRouteName());
        }
        Set<String> targetSet = new HashSet<>(routesList);
        SharedPreferences sharedPreferences = UtilsSharedPref.getSharedPref(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(context.getString(R.string.preference_widget_route_list), targetSet);
        editor.apply();
    }

    static public String getWidgetRoutes(Context context) {
        SharedPreferences sharedPreferences = UtilsSharedPref.getSharedPref(context);
        Set<String> retSet = sharedPreferences.getStringSet(context.getString(R.string.preference_widget_route_list), null);
        if (retSet != null) {
            StringBuilder builder = new StringBuilder();
            for (String eachSet : retSet) {
                builder.append(eachSet).append("\n");
            }
            return builder.toString();
        } else {
            return null;
        }
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

    static public int recordPeriodFinder(Context context, String recordType) {
        if (StringUtils.equals(recordType, context.getString(R.string.pref_settings_record_type_car))) {
            return 10;
        } else if (StringUtils.equals(recordType, context.getString(R.string.pref_settings_record_type_bicycle))) {
            return 25;
        } else if (StringUtils.equals(recordType, context.getString(R.string.pref_settings_record_type_walk))) {
            return 40;
        } else {
            return 20;
        }
    }
}
