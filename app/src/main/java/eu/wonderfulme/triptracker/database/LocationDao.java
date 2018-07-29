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
    void deleteSingleItemKey(int itemKey);

    @Query("SELECT * FROM LocationData ORDER BY item_key DESC LIMIT 1")
    int getLastItemKey();

    @Query("DELETE FROM LocationData WHERE timestamp <= :checkTime")
    void nukeRowsMoreThan30Days(String checkTime);
}
