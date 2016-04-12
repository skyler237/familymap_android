package com.skyler.android.familymap.model;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;
import java.util.TreeSet;

/**
 * This class holds the model information for an event
 * Created by Skyler on 3/15/2016.
 */
public class Event implements Comparable {
    public static Set<String> eventTypes = new TreeSet<>();

    private String eventId;
    private String personId;
    private double latitude;
    private double longitude;
    private String country;
    private String city;
    private String description;
    private String year;
    private String descendant;
    private float color;
    private String name;

    public Event(JSONObject jsonObject) throws JSONException {
        setEventId(jsonObject.getString("eventID"));
        setPersonId(jsonObject.getString("personID"));
        setLatitude(jsonObject.getDouble("latitude"));
        setLongitude(jsonObject.getDouble("longitude"));
        setCountry(jsonObject.getString("country"));
        setCity(jsonObject.getString("city"));
        setDescription(jsonObject.getString("description"));
        setYear(jsonObject.getString("year"));
        setDescendant(jsonObject.getString("descendant"));
        setColor();

        // Add this event to the appropriate person
        Person person = FamilyMapModel.SINGLETON.getUserPersonMap().get(personId);
        if (person != null) {
            person.addRelatedEvent(this);
            FamilyMapModel.SINGLETON.currentUser.addRelatedEvent(this); // Just in case -- make sure it is in the model
        }
    }

    public Event(String eventID, String eventPersonID, Double latitude, Double longitude,
                 String country, String city, String description, String year) {
        setEventId(eventID);
        this.personId = eventPersonID;
        setLatitude(latitude);
        setLongitude(longitude);
        setCountry(country);
        setCity(city);
        setDescription(description);
        setYear(year);
        setColor();
    }

    @Override
    public String toString() {
        String str = getPersonName() + "\n";
        str += getInfoText();

        return str;
    }

    @Override
    public int hashCode() {
        return eventId.hashCode();
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
        setName(personId);
    }

    public String getPersonName() {
        return name;
    }

    public void setName(String personId) {
        Person person = FamilyMapModel.SINGLETON.getUserPersonMap().get(personId);
        this.name = person.getFirstName() + " " + person.getLastName();
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {
        eventTypes.add(description);
        this.description = description;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }


    public void setDescendant(String descendant) {
        this.descendant = descendant;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (o.getClass() == this.getClass()) {
            Event event = (Event) o;
            boolean result = this.getEventId().equals(event.eventId) &&
                    this.getPersonId().equals(event.getPersonId()) &&
                    this.getLatitude() == event.getLatitude() &&
                    this.getLongitude() == event.getLongitude() &&
                    this.getCountry().equals(event.getCountry()) &&
                    this.getCity().equals(event.getCity()) &&
                    this.getDescription().equals(event.getDescription());
            if(!(event.getYear() == null && this.getYear() == null ||
                    event.getYear().equals(this.getYear()))) {
                return false;
            }
            else {
                return result;
            }
        } else {
            return false;
        }
    }


    @Override
    public int compareTo(Object another) {
        final int THIS_COMES_FIRST = -1;
        final int OTHER_COMES_FIRST = 1;
        if (another.getClass() == this.getClass()) {
            // Births are always first
            if (this.description.toLowerCase().equals("birth") &&
                    !((Event) another).description.toLowerCase().equals("birth")) {
                return THIS_COMES_FIRST;
            } else if (!this.description.toLowerCase().equals("birth") &&
                    ((Event) another).description.toLowerCase().equals("birth")) {
                return OTHER_COMES_FIRST;
            }

            // Deaths are always last
            if (this.description.toLowerCase().equals("death") &&
                    !((Event) another).description.toLowerCase().equals("death")) {
                return 1;
            } else if (!this.description.toLowerCase().equals("death") &&
                    ((Event) another).description.toLowerCase().equals("death")) {
                return THIS_COMES_FIRST;
            }

            // Sort by date, then by descriptions alphabetically
            String year = this.getYear();
            String otherYear = ((Event) another).getYear();
            if (year != null) {
                if (otherYear != null) { // Both have years - return the comparison
                    if (year.compareTo(otherYear) == 0) {  // If the years are equal, check the descriptions
                        int compareDescriptions = this.getDescription().compareTo(((Event) another).getDescription());
                        if (compareDescriptions == 0) {
                            return eventId.compareTo(((Event) another).eventId); // If descriptions are equal, compare IDs
                        } else {
                            return compareDescriptions;
                        }
                    } else {
                        return year.compareTo(otherYear);
                    }
                } else {
                    return THIS_COMES_FIRST;
                }
            } else { // This doesn't have a year...
                if (otherYear != null) {
                    return OTHER_COMES_FIRST;
                } else { // Neither have years, compare their descriptions
                    int compareDescriptions = this.getDescription().compareTo(((Event) another).getDescription());
                    if (compareDescriptions == 0) {
                        return eventId.compareTo(((Event) another).eventId); // If descriptions are equal, compare IDs
                    } else {
                        return compareDescriptions;
                    }
                }
            }
        } else {
            return -1;
        }
    }

    public float getColor() {
        return color;
    }

    /**
     * Assigns a certain color to each type of marker
     */
    public void setColor() {
        switch (description) {
            case "baptism":
                color = BitmapDescriptorFactory.HUE_BLUE;
                break;

            case "birth":
                color = BitmapDescriptorFactory.HUE_YELLOW;
                break;

            case "census":
                color = BitmapDescriptorFactory.HUE_GREEN;
                break;

            case "christening":
                color = BitmapDescriptorFactory.HUE_ORANGE;
                break;

            case "death":
                color = BitmapDescriptorFactory.HUE_VIOLET;
                break;

            case "marriage":
                color = BitmapDescriptorFactory.HUE_ROSE;
                break;

            default:
                color = BitmapDescriptorFactory.HUE_CYAN;
        }
    }

    /**
     * Gets the string version of the event info
     * @return - String of event info
     */
    public String getInfoText() {
        String str = getDescription() + ": " + city + ", " + country + " (" + year + ")";

        return str;

    }

    public LatLng getLatLng() {
        return new LatLng(getLatitude(), getLongitude());
    }

    /**
     * Returns a string of all the information about an event that we want to be searchable
     * **Note: search is case insensitive
     *
     * @return - string of all searchable data
     */
    public String getSearchableText() {
        return (getInfoText() + " " + getPersonName()).toLowerCase();
    }
}
