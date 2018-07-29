package eu.wonderfulme.triptracker.tasks;

import android.support.annotation.NonNull;

import androidx.work.Worker;
import eu.wonderfulme.triptracker.database.LocationDbSingleton;
import eu.wonderfulme.triptracker.utility.Utils;

public class NukeDatabaseWorker extends Worker {
    @NonNull
    @Override
    public Worker.Result doWork() {
        nukeDB();
        return Result.SUCCESS;
    }

    private void nukeDB() {
        // Get time of the day
        String timeYesterdayFormatted = Utils.getYesterdayFormattedTime();
        // nuke rows more than 30 days.
        LocationDbSingleton.getInstance(getApplicationContext()).locationDao().nukeRowsMoreThan30Days(timeYesterdayFormatted);
    }
}
