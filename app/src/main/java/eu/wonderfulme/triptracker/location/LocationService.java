package eu.wonderfulme.triptracker.location;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import eu.wonderfulme.triptracker.R;
import eu.wonderfulme.triptracker.ui.MainActivity;
import eu.wonderfulme.triptracker.utility.Utils;
import eu.wonderfulme.triptracker.database.LocationData;
import eu.wonderfulme.triptracker.tasks.InsertLocationAsyncTask;
import eu.wonderfulme.triptracker.utility.UtilsSharedPref;

import static eu.wonderfulme.triptracker.location.SearchLocation.LOCATION_TYPE_TRACK;

public class LocationService extends Service implements LocationListener {

    public static final String INTENT_EXTRA_LOCATION_REQUEST_TYPE = "INTENT_EXTRA_LOCATION_REQUEST_TYPE";
    private static final String NOTIFICATION_CHANNEL_NAME = "NOTIFICATION_CHANNEL_NAME";
    private static final String NOTIFICATION_CHANNEL_ID = "100";
    private static final int NOTIFICATION_ID = 110;

    public static final String ACTION_PARKING_LOCATION_SAVED = "ACTION_PARKING_LOCATION_SAVED";

    private static final int PARKING_LOCATION_ACCURACY = 15;
    private LocationRequest mLocationRequest;
    private MyLocationCallback mLocationCallback;
    private long mRecordPeriodInSeconds;
    private int mLocationRequestType;

    @Override
    public void onCreate() {
        super.onCreate();
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationCallback = new MyLocationCallback(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            mLocationRequestType = intent.getIntExtra(INTENT_EXTRA_LOCATION_REQUEST_TYPE, -1);
        }
        // Check how the service should implement.
        if (mLocationRequestType == LOCATION_TYPE_TRACK) {
            mRecordPeriodInSeconds = UtilsSharedPref.getRecordPeriodFromSharedPref(this);
            //TODO Correct setIntervals
            //mLocationRequest.setInterval(mRecordPeriodInSeconds * 1000);
            mLocationRequest.setInterval(0);
            StartRequestLocation();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(getString(R.string.notification_locationservice_title))
                    .setContentText(getString(R.string.notification_locationservice_content))
                    .setSmallIcon(R.drawable.ic_notification)
                    .setColor(getResources().getColor(R.color.colorAccent));
            createNotificationChannel();
            startForeground(NOTIFICATION_ID, builder.build());
        } else {
            mLocationRequest.setInterval(0); // No delay.
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            StartRequestLocation();
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(mLocationCallback);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocationCallback.saveLocation(location);
    }


    private class MyLocationCallback extends LocationCallback {
        private Context mContext;

        MyLocationCallback(Context context) {
            this.mContext = context;
        }

        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) return;
            Location location = locationResult.getLastLocation();
            saveLocation(location);
        }

        void saveLocation(Location location) {
            if (mLocationRequestType == LOCATION_TYPE_TRACK) {
                saveLocationOnDatabase(location);
            } else {
                saveParkingLocation(location);
            }
        }

        private void saveParkingLocation(Location location) {
            //TODO Correct the accuracy
            //if (location.hasAccuracy() && location.getAccuracy() <= PARKING_LOCATION_ACCURACY) {
                UtilsSharedPref.setParkingLocationToSharedPref(mContext, location);
                broadcastParkingSaved();
                stopSelf();
            //}
        }

        private void saveLocationOnDatabase(Location location) {
            String timestamp = Utils.getFormattedTime(System.currentTimeMillis());
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            double altitude = -1.;
            if (location.hasAltitude()) {
                location.getAltitude();
            }
            float speed = -1.f;
            if (location.hasSpeed()) {
                speed = location.getSpeed();
            }
            int itemKey = UtilsSharedPref.getLastItemKeyFromSharedPref(mContext);
            if (itemKey == -100)
                return;
            LocationData dbData = new LocationData(timestamp, itemKey, latitude, longitude, altitude, speed);
            new InsertLocationAsyncTask(mContext).execute(dbData);
        }
    }

    private void broadcastParkingSaved() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        Intent intent = new Intent();
        intent.setAction(ACTION_PARKING_LOCATION_SAVED);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void StartRequestLocation() {
        if (Utils.isLocationPermissionGranted(this)) {
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }


}
