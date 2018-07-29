package eu.wonderfulme.triptracker.utility;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.wonderfulme.triptracker.R;

public class Utils {

    private Utils() {}

    static public String getFormattedFileName() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd_HHmmss");
        Date resultDate = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(resultDate) + "_tripTracker.csv";
    }

    static public String getFormattedTime (long timeInMilliseconds) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date resultDate = new Date(timeInMilliseconds);
        return simpleDateFormat.format(resultDate);
    }

    @SuppressLint("SimpleDateFormat")
    static public String getYesterdayFormattedTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return dateFormat.format(cal.getTime());
    }

    static public boolean isLocationPermissionGranted(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

}
