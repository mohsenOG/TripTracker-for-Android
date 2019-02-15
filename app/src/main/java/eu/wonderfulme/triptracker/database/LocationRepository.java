package eu.wonderfulme.triptracker.database;

import android.annotation.SuppressLint;
import android.app.Application;
import androidx.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.NonNull;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import eu.wonderfulme.triptracker.R;
import eu.wonderfulme.triptracker.utility.Utils;
import eu.wonderfulme.triptracker.utility.UtilsSharedPref;

public class LocationRepository {

    private LocationDao mLocationDao;

    public LocationRepository(Application application) {
        mLocationDao = LocationDbSingleton.getInstance(application).locationDao();
    }

    public LiveData<List<LocationHeaderData>> getAllLocationHeaderData() {
        return mLocationDao.getAllLocationHeaderData();
    }

    public LiveData<List<LocationData>> getLocationDataPerItemKey(int itemKey) {
        return mLocationDao.getLocationDataPerItemKey(itemKey);
    }

    public void insert(LocationData locationData) {
        new InsertLocationAsyncTask(mLocationDao).execute(locationData);
    }

    public void deleteSingleItemKey(Context context, Snackbar snackbar, int itemKey) {
        new RemoveAsyncTask(context, snackbar, itemKey).execute();
    }

    public void updateFilename(int itemKey, String filename)
    {
        new UpdateFilenameAsyncTask(mLocationDao, itemKey, filename).execute();
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

    private static class UpdateFilenameAsyncTask extends AsyncTask<Void, Void, Void> {
        LocationDao locationDao;
        int itemKey;
        String filename;

        public UpdateFilenameAsyncTask(LocationDao locationDao, int itemKey, String filename) {
            this.locationDao = locationDao;
            this.itemKey = itemKey;
            this.filename = filename;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            locationDao.updateFilename(itemKey, filename);
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
                    .setDuration(BaseTransientBottomBar.LENGTH_SHORT)
                    .show();
        }
    }

    private class NukeDatabaseWorker extends Worker {
        public NukeDatabaseWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
        }

        @NonNull
        @Override
        public Worker.Result doWork() {
            // Get time of the day
            String timeYesterdayFormatted = Utils.getYesterdayFormattedTime();
            // nuke rows more than 30 days.
            mLocationDao.nukeRowsMoreThan30Days(timeYesterdayFormatted);
            return Worker.Result.success();
        }
    }
}
