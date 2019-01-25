package eu.wonderfulme.triptracker.utility;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import eu.wonderfulme.triptracker.R;
import eu.wonderfulme.triptracker.database.LocationData;

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

    static public PolylineOptions getPolyLine(List<LocationData> data) {
        PolylineOptions options = new PolylineOptions();
        options.clickable(false);
        for (LocationData d : data) {
            options.add(new LatLng(d.getLatitude(), d.getLongitude()));
        }
        return options;
    }

    static public CameraUpdate getMapCameraBounds(List<LocationData> data) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LocationData d : data) {
            builder.include(new LatLng(d.getLatitude(), d.getLongitude()));
        }
        LatLngBounds bounds = builder.build();

        return CameraUpdateFactory.newLatLngBounds(bounds, 50);
    }

    static public String getTimeBaseWelcome(Context context) {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        if(timeOfDay >= 0 && timeOfDay < 12){
           return context.getString(R.string.welcome_morning);
        } else if(timeOfDay >= 12 && timeOfDay < 16){
            return context.getString(R.string.welcome_afternoon);
        } else if(timeOfDay >= 16 && timeOfDay < 21){
            return context.getString(R.string.welcome_evening);
        } else {
            return context.getString(R.string.welcome_night);
        }
    }

}
