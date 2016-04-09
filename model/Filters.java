package com.skyler.android.familymap.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class holds information about which filters are on for each event type
 * Created by Skyler on 4/8/2016.
 */
public class Filters {
    Map<String, Boolean> eventFiltersOn = new HashMap<>();

    public Filters(Set<String> eventTypes) {
        // These filters will always exist
        eventFiltersOn.put("male", true);
        eventFiltersOn.put("female", true);
        eventFiltersOn.put("Father's Side".toLowerCase(), true);
        eventFiltersOn.put("Mother's Side".toLowerCase(), true);

        // Place the rest of the event types in the map, according to which events exist
        for(String eventType : eventTypes) {
            eventFiltersOn.put(eventType.toLowerCase(), true);
        }
    }

    /**
     * Tells us if a certain filter is on or off.
     * @param eventType - the event type to check
     * @return - true if the filter is on (event should be shown); false if off (event should be invisible)
     *      **Note: returns true by default - if the event is not found, keep the marker on by default
     */
    public boolean isEventFilterOn(String eventType) {
        Boolean result = eventFiltersOn.get(eventType.toLowerCase());
        if(result != null) {
            return result;
        }
        else {
            return true;
        }
    }

    public void setEventFilter(String eventType, boolean on_off) {
        if(eventFiltersOn.containsKey(eventType.toLowerCase())) {
            eventFiltersOn.put(eventType.toLowerCase(),on_off); // Overwrite the event filter boolean if it exists
        }
    }
}
