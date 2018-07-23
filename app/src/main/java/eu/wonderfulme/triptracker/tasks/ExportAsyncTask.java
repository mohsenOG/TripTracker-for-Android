package eu.wonderfulme.triptracker.tasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.os.AsyncTask;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import eu.wonderfulme.triptracker.R;
import eu.wonderfulme.triptracker.utility.Utils;
import eu.wonderfulme.triptracker.database.LocationData;
import eu.wonderfulme.triptracker.database.LocationDbSingleton;

public class ExportAsyncTask extends AsyncTask<Void, Void, Void> {

    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private int mItemKey;
    private boolean isSuccessful = false;
    private Snackbar mSnackbar;
    private String mFilename;

    public ExportAsyncTask(Context context, Snackbar snackbar, int itemKey) {
        this.mContext = context;
        this.mSnackbar = snackbar;
        this.mItemKey = itemKey;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        List<LocationData> dataList = LocationDbSingleton.getInstance(mContext).locationDao().getDbData(mItemKey);

        File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!exportDir.exists()) {
            exportDir.mkdir();
        }

        mFilename = Utils.getFormattedFileName();
        try {
            String filePath = exportDir.getAbsolutePath() + "/" + mFilename;
            CSVWriter writer = new CSVWriter(new FileWriter(filePath));
            // Write header to the csv file.
            writer.writeNext(LocationData.getDbHeaders(mContext));
            for (LocationData data: dataList) {
                String[] dbRow = LocationData.locationCsvRowBuilder(data.getTimestamp(), data.getLatitude(),
                        data.getLongitude(), data.getAltitude(), data.getSpeed());
                writer.writeNext(dbRow);
            }
            writer.close();
            isSuccessful = true;
        } catch (IOException e) {
            isSuccessful = false;
            return null;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (isSuccessful) {
            mSnackbar.setText(mContext.getResources().getString(R.string.snackbar_export_csv_successful) + " " + mFilename)
                    .setDuration(Snackbar.LENGTH_LONG)
                    .setAction(R.string.snackbar_goto_downloads, new SnackbarOnClickListener(mContext, mSnackbar, SnackbarOnClickListener.SnackbarActionType.GOTO_DOWNLOADS))
                    .show();
        } else {
            mSnackbar.setText(R.string.snackbar_export_csv_failed)
                    .setDuration(Snackbar.LENGTH_LONG)
                    .show();
        }
    }

}
