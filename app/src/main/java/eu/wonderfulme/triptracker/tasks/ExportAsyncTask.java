package eu.wonderfulme.triptracker.tasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import com.google.android.material.snackbar.Snackbar;
import android.os.AsyncTask;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import eu.wonderfulme.triptracker.App;
import eu.wonderfulme.triptracker.R;
import eu.wonderfulme.triptracker.database.LocationRepository;
import eu.wonderfulme.triptracker.utility.Utils;
import eu.wonderfulme.triptracker.database.LocationData;

public class ExportAsyncTask extends AsyncTask<Void, Void, Void> {

    @SuppressLint("StaticFieldLeak")
    private final Context mContext;
    private final int mItemKey;
    private boolean isSuccessful = false;
    private final Snackbar mSnackBar;
    private String mFilename;
    private final LocationRepository mLocationRepos;

    public ExportAsyncTask(Context context, Snackbar snackbar, int itemKey) {
        this.mContext = context;
        this.mSnackBar = snackbar;
        this.mItemKey = itemKey;
        this.mLocationRepos = new LocationRepository(App.getInstance());
    }

    @Override
    protected Void doInBackground(Void... voids) {
        List<LocationData> dataList = mLocationRepos.getLocationDataPerItemKey(mItemKey);

        File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!exportDir.exists()) {
            exportDir.mkdir();
        }

        //TODO Feature request: get the saving name from user!
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
            mSnackBar.setText(mContext.getResources().getString(R.string.snackBar_export_csv_successful) + " " + mFilename)
                    .setDuration(Snackbar.LENGTH_LONG)
                    .setAction(R.string.snackBar_goto_downloads, new SnackBarOnClickListener(mContext, mSnackBar))
                    .show();
        } else {
            mSnackBar.setText(R.string.snackBar_export_csv_failed)
                    .setDuration(Snackbar.LENGTH_LONG)
                    .show();
        }
    }

}
