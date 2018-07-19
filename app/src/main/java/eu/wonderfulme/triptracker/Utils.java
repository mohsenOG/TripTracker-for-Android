package eu.wonderfulme.triptracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

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

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(context.getString(R.string.preference_filename), Context.MODE_PRIVATE);
    }

    static public void setRecordPeriodFromSharedPreferences(Context context, int recordPeriod) {
        SharedPreferences sharedPreferences = Utils.getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.preference_record_period), recordPeriod)
                .apply();
    }

    static public int getRecordPeriodFromSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = Utils.getSharedPreferences(context);
        return sharedPreferences.getInt(context.getString(R.string.preference_record_period), 10);
    }

    static public int getItemKeyFromSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = Utils.getSharedPreferences(context);
        return sharedPreferences.getInt(context.getString(R.string.preference_item_key), -100);
    }


    static public boolean isLocationPermissionGranted(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

}
