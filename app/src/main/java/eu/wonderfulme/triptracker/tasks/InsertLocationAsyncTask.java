package eu.wonderfulme.triptracker.tasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import eu.wonderfulme.triptracker.database.LocationData;
import eu.wonderfulme.triptracker.database.LocationDbSingleton;

public class InsertLocationAsyncTask extends AsyncTask<LocationData, Void, Void> {

    @SuppressLint("StaticFieldLeak")
    private Context mContext;

    public InsertLocationAsyncTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected Void doInBackground(LocationData... locationData) {
        LocationDbSingleton.getInstance(mContext).locationDao().insertSingleRecord(locationData[0]);
        return null;
    }
}
