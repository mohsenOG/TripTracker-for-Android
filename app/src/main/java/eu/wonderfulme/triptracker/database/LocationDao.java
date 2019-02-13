package eu.wonderfulme.triptracker.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LocationDao {

    @Insert
    void insertSingleRecord(LocationData data);

    @Query("SELECT item_key, MIN(timestamp) as minTimestamp FROM LocationData GROUP BY item_key")
    LiveData<List<LocationHeaderData>> getAllLocationHeaderData();

    @Query("SELECT * FROM LocationData WHERE item_key = :itemKey")
    LiveData<List<LocationData>> getLocationDataPerItemKey(int itemKey);

    @Query("SELECT item_key FROM LocationData ORDER BY item_key DESC LIMIT 1")
    int getLastItemKey();

    @Query("DELETE FROM LocationData WHERE timestamp <= :checkTime")
    void nukeRowsMoreThan30Days(String checkTime);

    @Query("DELETE FROM LocationData WHERE item_key = :itemKey")
    void deleteSingleItemKey(int itemKey);








}
