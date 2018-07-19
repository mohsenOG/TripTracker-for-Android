package eu.wonderfulme.triptracker.tasks;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.View;

public class SnackbarOnClickListener implements View.OnClickListener {

    private SnackbarActionType mActionType;
    private Context mContext;
    private Snackbar mSnackbar;

    public enum SnackbarActionType {
        GOTO_DOWNLOADS,
        INVALID
    }

    SnackbarOnClickListener(Context context, Snackbar snackbar, SnackbarActionType actionType) {
        this.mActionType = actionType;
        this.mContext = context;
        this.mSnackbar = snackbar;
    }

    @Override
    public void onClick(View v) {
        switch (mActionType) {
            case GOTO_DOWNLOADS:
                Intent dm = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
                dm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(dm);
                mSnackbar.dismiss();
                break;
            case INVALID:
            default:
                mSnackbar.dismiss();
                break;
        }


    }
}
