package eu.wonderfulme.triptracker.database;

import java.io.Serializable;

public class LocationHeaderData implements Serializable {
    private int item_key;
    private String minTimestamp;

    public LocationHeaderData(int item_key, String minTimestamp) {
        this.item_key = item_key;
        this.minTimestamp = minTimestamp;
    }

    public int getItem_key() {
        return item_key;
    }

    public String getMinTimestamp() {
        return minTimestamp;
    }
}
