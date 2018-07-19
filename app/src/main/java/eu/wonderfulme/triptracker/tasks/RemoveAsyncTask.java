package eu.wonderfulme.triptracker.tasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;

import eu.wonderfulme.triptracker.R;
import eu.wonderfulme.triptracker.database.LocationDbSingleton;

public class RemoveAsyncTask extends AsyncTask<Void, Void, Void> {

    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private int mItemKey;
    private boolean isSuccessful = false;
    private Snackbar mSnackbar;

    public RemoveAsyncTask(Context context, Snackbar snackbar, int itemKey) {
        this.mContext = context;
        this.mSnackbar = snackbar;
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
        mSnackbar.setText(mContext.getResources().getString(R.string.snackbar_remove_succeed))
                .setDuration(Snackbar.LENGTH_LONG)
                .show();
    }
}
