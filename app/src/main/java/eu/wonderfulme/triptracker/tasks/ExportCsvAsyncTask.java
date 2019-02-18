package eu.wonderfulme.triptracker.tasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.opencsv.CSVWriter;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import eu.wonderfulme.triptracker.R;
import eu.wonderfulme.triptracker.database.LocationData;
import eu.wonderfulme.triptracker.utility.Utils;

public class ExportCsvAsyncTask extends AsyncTask<Void, Void, Void> {

    public static final int CSV_CHARACTERS = 4;

    @SuppressLint("StaticFieldLeak")
    private final Context mContext;
    private final List<LocationData> mLocationList;
    private boolean isSuccessful = false;
    private final Snackbar mSnackBar;
    private String mFilename;

    public ExportCsvAsyncTask(Context context, Snackbar snackbar, List<LocationData> locationDataList) {
        this.mContext = context;
        this.mSnackBar = snackbar;
        this.mLocationList = locationDataList;
        checkFilenameExtension();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!exportDir.exists()) {
            exportDir.mkdir();
        }

        try {
            mFilename = exportDir.getAbsolutePath() + "/" + mFilename;
            //Check if the file exist
            File file = new File(mFilename);
            int cnt = 1;
            while (file.exists())
            {
                // Remove extension from filename
                mFilename = mFilename.substring(0, mFilename.length() - CSV_CHARACTERS);
                // Remove extra (x) from the end of name.
                if (cnt > 1 && cnt <= 9)
                    mFilename = mFilename.substring(0, mFilename.length() - 3);
                else if (cnt >= 10) //Hopefully duplicate files are not more than 99 files!
                    mFilename = mFilename.substring(0, mFilename.length() - 4);

                String extra = "(" + String.valueOf(cnt) + ")";
                mFilename += extra + ".csv";
                file = new File(mFilename);
                ++cnt;
            }
            //
            CSVWriter writer = new CSVWriter(new FileWriter(mFilename));
            // Write header to the csv file.
            writer.writeNext(LocationData.getDbHeaders(mContext));
            for (LocationData data: mLocationList) {
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
    protected void onPostExecute(Void v) {
        super.onPostExecute(v);
        if (isSuccessful) {
            int lastSlash = mFilename.lastIndexOf("/") + 1;
            mFilename = mFilename.substring(lastSlash);
            mSnackBar.setText(mContext.getResources().getString(R.string.snackBar_export_csv_successful) + ": " + mFilename)
                    .setDuration(BaseTransientBottomBar.LENGTH_LONG)
                    .setAction(R.string.snackBar_goto_downloads, new SnackBarOnClickListener(mContext, mSnackBar))
                    .show();
        } else {
            mSnackBar.setText(R.string.snackBar_export_csv_failed)
                    .setDuration(BaseTransientBottomBar.LENGTH_LONG)
                    .show();
        }
    }

    private void checkFilenameExtension() {
        // get the route name
        this.mFilename = mLocationList.get(0).getRouteName();
        if (StringUtils.isEmpty(mFilename))
            mFilename = Utils.getFormattedFileName();
        else {
            //Check if user already added .csv to the route name.
            if (mFilename.length() > CSV_CHARACTERS) {
                String extension = mFilename.substring(mFilename.length() - CSV_CHARACTERS - 1);
                if (StringUtils.compareIgnoreCase(extension, ".csv") != 0)
                    mFilename += ".csv";
            } else {
                mFilename += ".csv";
            }
        }
    }

}
