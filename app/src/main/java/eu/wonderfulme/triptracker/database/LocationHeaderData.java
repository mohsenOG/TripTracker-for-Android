package eu.wonderfulme.triptracker.database;

public class LocationHeaderData {
    private int item_key;
    private String timestamp;

    public LocationHeaderData(int item_key, String timestamp) {
        this.item_key = item_key;
        this.timestamp = timestamp;
    }

    public int getItem_key() {
        return item_key;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
