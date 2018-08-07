package eu.wonderfulme.triptracker;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.AsyncTask;

import eu.wonderfulme.triptracker.database.LocationDbSingleton;
import eu.wonderfulme.triptracker.database.LocationRepository;
import eu.wonderfulme.triptracker.utility.GoogleApiHelper;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.MobileAds;

import eu.wonderfulme.triptracker.utility.UtilsSharedPref;
import io.fabric.sdk.android.Fabric;

/**
 * https://stackoverflow.com/a/33400346/6072457
 */
public class App extends Application {

    private GoogleApiHelper mGoogleApiHelper;
    private static App mInstance;
    private LocationRepository mLocationRepos;

    @Override
    public void onCreate() {
        super.onCreate();
        // Adding fabric crachlytics
        Fabric.with(this, new Crashlytics());
        mInstance = this;
        mGoogleApiHelper = new GoogleApiHelper(mInstance);
        mLocationRepos = new LocationRepository(mInstance);
        //Init the last DB itemKey to shared Prefs.
        new ItemKeyInitializerAsyncTask().execute();
        // Nuke db if needed
        mLocationRepos.nukeDatabaseIfNeeded(mInstance);
        // Init admob
        MobileAds.initialize(mInstance, getString(R.string.admob_app_id));
    }

    public static synchronized App getInstance() {
        return mInstance;
    }

    public GoogleApiHelper getGoogleApiHelperInstance() {
        return this.mGoogleApiHelper;
    }

    public static GoogleApiHelper getGoogleApiHelper() {
        return getInstance().getGoogleApiHelperInstance();
    }

    @SuppressLint("StaticFieldLeak")
    private class ItemKeyInitializerAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            int lastItemKey = mLocationRepos.getLastItemKey();
            UtilsSharedPref.setItemKeyToSharedPref(mInstance, lastItemKey);
            return null;
        }
    }
}
