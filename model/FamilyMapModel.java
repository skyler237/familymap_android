package com.skyler.android.familymap.model;

import com.skyler.android.familymap.network.HttpClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Skyler on 3/18/2016.
 */
public class FamilyMapModel {
    public static final FamilyMapModel SINGLETON = new FamilyMapModel();
    public User currentUser;
    public HttpClient httpClient;
    public Settings mSettings = new Settings();
    public Filters mFilters;
    public boolean resetMapEventPreview = false;
    private ArrayList<String> personIdList = new ArrayList<>();
    public boolean resyncEventMarkers = false;

    private FamilyMapModel() {

    }

    public void setCurrentUser(User user) {
        currentUser = user;
    }

    public Set<Event> getUserEvents() {
        return currentUser.relatedEvents;
    }

    /**
     * This gives the map of PersonIDs and the correlating people that are associated with the current user.
     * @return - map of all people for this session
     */
    public HashMap<String, Person> getUserPersonMap() {
        return currentUser.relatedPeople;
    }

    public Event getEvent(String eventId) {
        return currentUser.getEvent(eventId);
    }

    public Person getPerson(String personId) {
        return currentUser.relatedPeople.get(personId);
    }

    /**
     * Populates all the family data in the model.
     * Explicitly sets spouse, father, and mother based on IDs; implicitly sets children when a father is set
     */
    public void populateFamilyData() {
        for (int i = 0; i < personIdList.size(); i++) {
            Person person = currentUser.relatedPeople.get(personIdList.get(i));
            if (person.getFatherId() != null) {
                person.setFather(currentUser.relatedPeople.get(person.getFatherId()));
            }
            if (person.getMotherId() != null) {
                person.setMother(currentUser.relatedPeople.get(person.getMotherId()));
            }
            if (person.getSpouseId() != null) {
                person.setSpouse(currentUser.relatedPeople.get(person.getSpouseId()));
            }
        }
    }

    /**
     * Adds a person to the model
     * @param person - person to add
     */
    public void addPerson(Person person) {
        currentUser.addRelatedPerson(person);
        personIdList.add(person.getPersonId());
    }

    public void setFilters(Set<String> eventTypes) {
        mFilters = new Filters(eventTypes);
    }

    /**
     * Gives the event types that are found in the data received.
     * Also adds "male," "female," "Father's side," and "Mother's side," which are not explicit event types.
     *
     * @return - A list of strings representing existing event types
     * **Note** converts the set to a string, because the filter recycler view adapter requires a list, not a set
     */
    public List<String> getEventFilterTypes() {
        Set<String> eventTypes = new HashSet<>();

        // Add all the event types to a set, but remain case insensitive
        for(String eventType : Event.eventTypes) {
            eventTypes.add(eventType.toLowerCase());
        }

        List<String> listOfEventTypes = new ArrayList<>();

        for (String eventType : eventTypes) {
            listOfEventTypes.add(eventType);
        }

        // These types are not in the event descriptions, but we want them to appear in the filter list
        listOfEventTypes.add("male");
        listOfEventTypes.add("female");
        listOfEventTypes.add("Father's Side");
        listOfEventTypes.add("Mother's Side");


        return listOfEventTypes;


    }

    /**
     * Searches all people in the model for a particular String
     * @param searchText - text to search for
     * @return - a set of all people who contain <code>searchText</code>
     */
    public Set<Person> searchPeopleFor(String searchText) {
        Set<Person> searchResults = new HashSet<>();

        for (String personId : personIdList) {
            Person person = getUserPersonMap().get(personId);

            if (person.getSearchableText().contains(searchText.toLowerCase())) {
                searchResults.add(person);
            }
        }

        return searchResults;
    }

    /**
     * Searches all events in the model for a particular String
     * @param searchText - text to search for
     * @return - a set of all events who contain <code>searchText</code>
     */
    public Set<Event> searchEventsFor(String searchText) {
        Set<Event> searchResults = new HashSet<>();

        for (Event event : currentUser.relatedEvents) {
            if (event.getSearchableText().contains(searchText.toLowerCase())) {
                searchResults.add(event);
            }
        }
        return searchResults;
    }

    /**
     * Resets all the data in the model.
     *  **Note: this does not clear filters or settings; they are preserved
     */
    public void clearData() {
        currentUser.relatedEvents.clear();
        currentUser.relatedPeople.clear();
        personIdList.clear();
        resetMapEventPreview = true;
        resyncEventMarkers = true;
    }
}
