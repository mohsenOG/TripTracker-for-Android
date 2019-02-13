package eu.wonderfulme.triptracker.location;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;

import eu.wonderfulme.triptracker.utility.UtilsSharedPref;

import static eu.wonderfulme.triptracker.location.LocationService.INTENT_EXTRA_LOCATION_REQUEST_TYPE;

public class SearchLocation implements Parcelable {

    public static final int LOCATION_TYPE_SINGLE = 0;
    public static final int LOCATION_TYPE_TRACK = 1;

    private Context mContext;
    private final Intent mServiceIntent;
    private final int mRequestType;

    public SearchLocation(Context context, int requestType) {
        this.mContext = context;
        mRequestType = requestType;
        this.mServiceIntent = new Intent(mContext, LocationService.class);
        mServiceIntent.putExtra(INTENT_EXTRA_LOCATION_REQUEST_TYPE, mRequestType);
    }

    /**
     * https://stackoverflow.com/a/54648795/6072457
     */
    public boolean isLocationEnabled() {
        // API 28 and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
          final LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
          return lm.isLocationEnabled();
        } else { // API less than 28
            int mode = Settings.Secure.getInt(mContext.getContentResolver(), Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
            return (mode != Settings.Secure.LOCATION_MODE_OFF);
        }
    }

    public void startService() {
        if (mRequestType == LOCATION_TYPE_TRACK) {
            int lastItemKey = UtilsSharedPref.getLastItemKeyFromSharedPref(mContext);
            UtilsSharedPref.setItemKeyToSharedPref(mContext, lastItemKey + 1);
            UtilsSharedPref.setWidgetServiceChecker(mContext, true);
        } else {
            UtilsSharedPref.setWidgetServiceChecker(mContext, false);
        }
        mContext.startService(mServiceIntent);
    }

    public void stopService() {
        UtilsSharedPref.setWidgetServiceChecker(mContext, false);
        mContext.stopService(mServiceIntent);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mServiceIntent, flags);
        dest.writeInt(this.mRequestType);
    }

    protected SearchLocation(Parcel in) {
        this.mServiceIntent = in.readParcelable(Intent.class.getClassLoader());
        this.mRequestType = in.readInt();
    }

    public static final Parcelable.Creator<SearchLocation> CREATOR = new Parcelable.Creator<SearchLocation>() {
        @Override
        public SearchLocation createFromParcel(Parcel source) {
            return new SearchLocation(source);
        }

        @Override
        public SearchLocation[] newArray(int size) {
            return new SearchLocation[size];
        }
    };
}
