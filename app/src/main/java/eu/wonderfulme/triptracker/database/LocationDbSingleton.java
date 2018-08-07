package eu.wonderfulme.triptracker.database;

import android.arch.persistence.room.Room;
import android.content.Context;

public class LocationDbSingleton {

    private static LocationDatabase instance = null;

    public static LocationDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), LocationDatabase.class, "locationDb")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    private LocationDbSingleton() { }
}
