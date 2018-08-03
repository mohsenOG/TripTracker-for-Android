package eu.wonderfulme.triptracker.database;


public class LocationHeaderData {
    private final int item_key;
    private final String minTimestamp;

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
