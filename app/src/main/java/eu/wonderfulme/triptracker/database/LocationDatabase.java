package eu.wonderfulme.triptracker.database;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {LocationData.class}, version = 5, exportSchema = false)
public abstract class LocationDatabase extends RoomDatabase {
    public abstract LocationDao locationDao();
}
