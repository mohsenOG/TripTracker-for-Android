package eu.wonderfulme.triptracker.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface LocationDao {

    @Insert
    void insertSingleRecord(LocationData data);

    @Query("SELECT * FROM LocationData WHERE item_key = :itemKey")
    List<LocationData> getDbData(int itemKey);

    @Query("DELETE FROM LocationData WHERE item_key = :itemKey")
    void deleteSingleItem(int itemKey);
}
