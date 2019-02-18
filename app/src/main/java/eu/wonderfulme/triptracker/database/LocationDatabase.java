package eu.wonderfulme.triptracker.database;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {LocationData.class}, version = 9, exportSchema = false)
public abstract class LocationDatabase extends RoomDatabase {
    public abstract LocationDao locationDao();
}
