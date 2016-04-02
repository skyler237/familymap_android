package com.skyler.android.familymap.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Skyler on 3/18/2016.
 */
public class FamilyMapModel {
    public static final FamilyMapModel SINGLETON = new FamilyMapModel();
    public User currentUser;
    private ArrayList<String> personIdList = new ArrayList<>();

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
                person.setMother(currentUser.relatedPeople.get(person.getMotherId()));
                person.setSpouse(currentUser.relatedPeople.get(person.getSpouseId()));
            }
        }
    }

    public void addPerson(Person person) {
        currentUser.addRelatedPerson(person);
        personIdList.add(person.getPersonId());
    }
}
