package eu.wonderfulme.triptracker;

import android.app.Application;

import eu.wonderfulme.triptracker.utility.GoogleApiHelper;

/**
 * https://stackoverflow.com/a/33400346/6072457
 */
public class App extends Application {

    private GoogleApiHelper mGoogleApiHelper;
    private static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mGoogleApiHelper = new GoogleApiHelper(mInstance);
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
}
