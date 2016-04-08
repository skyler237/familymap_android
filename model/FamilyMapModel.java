package com.skyler.android.familymap.model;

import com.skyler.android.familymap.network.HttpClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Skyler on 3/18/2016.
 */
public class FamilyMapModel {
    public static final FamilyMapModel SINGLETON = new FamilyMapModel();
    public User currentUser;
    public HttpClient httpClient;
    private ArrayList<String> personIdList = new ArrayList<>();
    public Settings mSettings = new Settings();

    private FamilyMapModel() {

    }

    public void setCurrentUser(User user){
        currentUser = user;
    }

    public Set<Event> getUserEvents() {
        return currentUser.relatedEvents;
    }

    public HashMap<String,Person> getUserPersonMap() {
        return currentUser.relatedPeople;
    }

    public Event getEvent(String eventId) {
        return currentUser.getEvent(eventId);
    }

    public Person getPerson(String personId) {
        return currentUser.relatedPeople.get(personId);
    }

    public void populateFamilyData() {
        for(int i = 0; i < personIdList.size(); i++) {
            Person person = currentUser.relatedPeople.get(personIdList.get(i));
            if(person.getFatherId() != null) {
                person.setFather(currentUser.relatedPeople.get(person.getFatherId()));
            }
            if(person.getMotherId() != null) {
                person.setMother(currentUser.relatedPeople.get(person.getMotherId()));
            }
            if(person.getSpouseId() != null) {
                person.setSpouse(currentUser.relatedPeople.get(person.getSpouseId()));
            }
        }
    }

    public void addPerson(Person person) {
        currentUser.addRelatedPerson(person);
        personIdList.add(person.getPersonId());
    }
}
