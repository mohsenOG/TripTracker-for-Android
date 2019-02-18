package eu.wonderfulme.triptracker.database;


import org.apache.commons.lang3.StringUtils;

public class LocationHeaderData {
    private final int item_key;
    private final String minTimestamp;
    private final String routeName;

    public LocationHeaderData(int item_key, String minTimestamp, String routeName) {
        this.item_key = item_key;
        this.minTimestamp = minTimestamp;
        this.routeName = routeName;
    }

    public int getItem_key() {
        return item_key;
    }

    public String getMinTimestamp() { return minTimestamp; }

    public String getRouteName() {
        if (StringUtils.isEmpty(routeName)) {
            return minTimestamp;
        } else {
            return routeName;
        }
    }
}
