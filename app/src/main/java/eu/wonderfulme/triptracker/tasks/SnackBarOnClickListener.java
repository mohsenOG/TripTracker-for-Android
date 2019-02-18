package eu.wonderfulme.triptracker.tasks;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

class SnackBarOnClickListener implements View.OnClickListener {

    private final Context mContext;
    private final Snackbar mSnackBar;

    SnackBarOnClickListener(Context context, Snackbar snackbar) {
        this.mContext = context;
        this.mSnackBar = snackbar;
    }

    @Override
    public void onClick(View v) {
        Intent dm = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
        dm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(dm);
        mSnackBar.dismiss();
    }

}
