package eu.wonderfulme.triptracker.tasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;

import eu.wonderfulme.triptracker.R;
import eu.wonderfulme.triptracker.database.LocationDbSingleton;

public class RemoveAsyncTask extends AsyncTask<Void, Void, Void> {

    @SuppressLint("StaticFieldLeak")
    private final Context mContext;
    private final int mItemKey;
    private Snackbar mSnackBar;

    public RemoveAsyncTask(Context context, Snackbar snackbar, int itemKey) {
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
