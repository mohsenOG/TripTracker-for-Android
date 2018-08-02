package eu.wonderfulme.triptracker;

import android.annotation.SuppressLint;
import android.app.Application;
import android.location.Location;
import android.os.AsyncTask;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import eu.wonderfulme.triptracker.database.LocationData;
import eu.wonderfulme.triptracker.database.LocationDbSingleton;
import eu.wonderfulme.triptracker.tasks.NukeDatabaseWorker;
import eu.wonderfulme.triptracker.utility.GoogleApiHelper;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.MobileAds;

import java.util.List;
import java.util.concurrent.TimeUnit;

import eu.wonderfulme.triptracker.utility.Utils;
import eu.wonderfulme.triptracker.utility.UtilsSharedPref;
import io.fabric.sdk.android.Fabric;

/**
 * https://stackoverflow.com/a/33400346/6072457
 */
public class App extends Application {

    private GoogleApiHelper mGoogleApiHelper;
    private static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mInstance = this;
        mGoogleApiHelper = new GoogleApiHelper(mInstance);
        //Init the last DB itemKey to shared Prefs.
        new ItemKeyInitializerAsyncTask().execute();
        // Check if db should be nuked
        nukeDbChecker();
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
            int lastItemKey = LocationDbSingleton.getInstance(mInstance).locationDao().getLastItemKey();
            UtilsSharedPref.setItemKeyToSharedPref(mInstance, lastItemKey);
            return null;
        }
    }

    private void nukeDbChecker() {
        boolean initWorker = UtilsSharedPref.getNukeDbChecker(mInstance);
        if (!initWorker) {
            PeriodicWorkRequest.Builder nukeDbBuilder =  new PeriodicWorkRequest.Builder(NukeDatabaseWorker.class, 1, TimeUnit.DAYS);
            PeriodicWorkRequest worker = nukeDbBuilder.build();
            WorkManager.getInstance().enqueue(worker);
            //Set the shared pref to true.
            UtilsSharedPref.setNukeDbChecker(mInstance, true);
        }
    }
}
