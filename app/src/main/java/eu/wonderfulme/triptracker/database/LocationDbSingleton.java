package eu.wonderfulme.triptracker.database;

import android.content.Context;

import androidx.room.Room;

class LocationDbSingleton {

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
