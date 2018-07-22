package eu.wonderfulme.triptracker.searcher;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import static eu.wonderfulme.triptracker.searcher.SearchLocation.LocationRequestType.LOCATION_TYPE_SINGLE;


public class SearchLocation implements LocationService.LocationServiceCallback {

    public enum LocationRequestType {
        LOCATION_TYPE_SINGLE,
        LOCATION_TYPE_TRACK;
    }

    public interface SearchLocationCallback {
        void onParkingLocationSaved();
    }

    private LocationService mLocationService;
    private Context mContext;
    private LocationRequestType mRequestType;
    private SearchLocationCallback mSearchLocationCallback;

    public SearchLocation(Context context, LocationRequestType requestType, SearchLocationCallback searchLocationCallback) {
        this.mContext = context;
        this.mRequestType = requestType;
        this.mSearchLocationCallback = searchLocationCallback;
    }

    @Override
    public void onParkingLocationSaved() {
        if (mSearchLocationCallback != null) {
            mSearchLocationCallback.onParkingLocationSaved();
        }
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
        if (mLocationService == null) {
            mLocationService = new LocationService(mRequestType, this);
            Intent intent = new Intent(mContext, LocationService.class);
            mLocationService.startService(intent);
        }
    }

    public void stopLocationService() {
        if (mLocationService != null && mRequestType != LOCATION_TYPE_SINGLE) {
            mLocationService.stopService(new Intent(mContext, LocationService.class));
        }
    }
}
