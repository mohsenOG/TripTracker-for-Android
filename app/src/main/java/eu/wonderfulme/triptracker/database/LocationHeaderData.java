package eu.wonderfulme.triptracker.database;


import org.apache.commons.lang3.StringUtils;

public class LocationHeaderData {
    private final int item_key;
    private final String minTimestamp;
    private final String filename;

    public LocationHeaderData(int item_key, String minTimestamp, String filename) {
        this.item_key = item_key;
        this.minTimestamp = minTimestamp;
        this.filename = filename;
    }

    public int getItem_key() {
        return item_key;
    }

    public String getMinTimestamp() { return minTimestamp; }

    public String getFilename() {
        if (StringUtils.isEmpty(filename)) {
            return minTimestamp;
        } else {
            return filename;
        }
    }
}
