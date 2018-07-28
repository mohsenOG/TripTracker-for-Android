package eu.wonderfulme.triptracker.searcher;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;


import eu.wonderfulme.triptracker.utility.UtilsSharedPref;

import static eu.wonderfulme.triptracker.searcher.LocationService.INTENT_EXTRA_LOCATION_REQUEST_TYPE;

public class SearchLocation {

    public static final int LOCATION_TYPE_SINGLE = 0;
    public static final int LOCATION_TYPE_TRACK = 1;

    private Context mContext;
    private Intent mServiceIntent;

    public SearchLocation(Context context, int requestType) {
        this.mContext = context;
        this.mServiceIntent = new Intent(mContext, LocationService.class);
        mServiceIntent.putExtra(INTENT_EXTRA_LOCATION_REQUEST_TYPE, requestType);
    }

    public boolean isGpsOn() {
        boolean ret = false;
        final LocationManager manager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (manager != null) {
            ret = manager.isProviderEnabled( LocationManager.GPS_PROVIDER);
        }
        return ret;
    }

    public void startService() {
        int lastItemKey = UtilsSharedPref.getLastItemKeyFromSharedPref(mContext);
        UtilsSharedPref.setItemKeyToSharedPref(mContext, lastItemKey + 1);
        mContext.startService(mServiceIntent);
    }

    public void stopService() {
            mContext.stopService(mServiceIntent);
    }
}
