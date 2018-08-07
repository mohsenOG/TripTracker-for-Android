package eu.wonderfulme.triptracker.database;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import eu.wonderfulme.triptracker.R;
import eu.wonderfulme.triptracker.utility.Utils;
import eu.wonderfulme.triptracker.utility.UtilsSharedPref;

public class LocationRepository {

    private LocationDao mLocationDao;
    private LiveData<List<LocationHeaderData>> mAllLocationHeaderData;

    public LocationRepository(Application application) {
        mLocationDao = LocationDbSingleton.getInstance(application).locationDao();
        mAllLocationHeaderData = mLocationDao.getAllLocationHeaderData();
    }

    public LiveData<List<LocationHeaderData>> getAllLocationHeaderData() {
        return mAllLocationHeaderData;
    }

    public void insert(LocationData locationData) {
        new InsertLocationAsyncTask(mLocationDao).execute(locationData);
    }

    public void deleteSingleItemKey(Context context, Snackbar snackbar, int itemKey) {
        new RemoveAsyncTask(context, snackbar, itemKey).execute();
    }

    public void nukeDatabaseIfNeeded(Context context) {
        boolean initWorker = UtilsSharedPref.getNukeDbChecker(context);
        if (!initWorker) {
            PeriodicWorkRequest.Builder nukeDbBuilder = new PeriodicWorkRequest.Builder(NukeDatabaseWorker.class, 1, TimeUnit.DAYS);
            PeriodicWorkRequest worker = nukeDbBuilder.build();
            WorkManager.getInstance().enqueue(worker);
            //Set the shared pref to true.
            UtilsSharedPref.setNukeDbChecker(context, true);
        }
    }

    /**
     * Must not be called from UI thread.
     */
    public int getLastItemKey() {
        return mLocationDao.getLastItemKey();
    }

    /**
     * Must not be called via UI thread.
     */
    public List<LocationData> getLocationDataPerItemKey(int itemKey) {
        return mLocationDao.getLocationDataPerItemKey(itemKey);
    }

    private static class InsertLocationAsyncTask extends AsyncTask<LocationData, Void, Void> {
        private LocationDao mLocationDao;

        InsertLocationAsyncTask(LocationDao locationDao) {
            mLocationDao = locationDao;
        }

        @Override
        protected Void doInBackground(LocationData... locationData) {
            mLocationDao.insertSingleRecord(locationData[0]);
            return null;
        }
    }

    private static class RemoveAsyncTask extends AsyncTask<Void, Void, Void> {
        @SuppressLint("StaticFieldLeak")
        private final Context mContext;
        private final int mItemKey;
        private Snackbar mSnackBar;

        RemoveAsyncTask(Context context, Snackbar snackbar, int itemKey) {
            this.mContext = context;
            this.mSnackBar = snackbar;
            this.mItemKey = itemKey;
        }


        @Override
        protected Void doInBackground(Void... voids) {
            LocationDbSingleton.getInstance(mContext).locationDao().deleteSingleItemKey(mItemKey);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mSnackBar.setText(mContext.getResources().getString(R.string.snackBar_remove_succeed))
                    .setDuration(Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    private class NukeDatabaseWorker extends Worker {
        @NonNull
        @Override
        public Worker.Result doWork() {
            // Get time of the day
            String timeYesterdayFormatted = Utils.getYesterdayFormattedTime();
            // nuke rows more than 30 days.
            mLocationDao.nukeRowsMoreThan30Days(timeYesterdayFormatted);
            return Result.SUCCESS;
        }
    }
}
