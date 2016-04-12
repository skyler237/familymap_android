package com.skyler.android.familymap.model;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.skyler.android.familymap.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static com.skyler.android.familymap.model.Person.Relationship.CHILD;
import static com.skyler.android.familymap.model.Person.Relationship.FATHER;
import static com.skyler.android.familymap.model.Person.Relationship.MOTHER;
import static com.skyler.android.familymap.model.Person.Relationship.SPOUSE;

/**
 * Created by Skyler on 3/15/2016.
 */
public class Person {

    public Set<Event> relatedEvents = new TreeSet<>();
    public HashMap<String, Person> relatedPeople = new HashMap<>();
    public Person father;
    public Person mother;
    public Person spouse;
    public List<Person> children = new ArrayList<>();

    private String personId;
    private String firstName;
    private String lastName;
    private Gender gender;
    private String fatherId;
    private String motherId;
    private String spouseId;

    public Person() {

    }

    public Person(JSONObject jsonData) throws JSONException {
        setPersonId(jsonData.getString("personID"));
        setFirstName(jsonData.getString("firstName"));
        setLastName(jsonData.getString("lastName"));
        setGender(jsonData.getString("gender"));
        if (jsonData.has("father")) {
            setFatherId(jsonData.getString("father"));
        }
        if (jsonData.has("mother")) {
            setMotherId(jsonData.getString("mother"));
        }
        if (jsonData.has("spouse")) {
            setSpouseId(jsonData.getString("spouse"));
        }
    }

    public Person(String personID, String firstname, String lastname, String gender, String spouseID, String fatherID, String motherID) {
        setPersonId(personID);
        setFirstName(firstname);
        setLastName(lastname);
        setGender(gender);
        setFatherId(fatherID);
        setMotherId(motherID);
        setSpouseId(spouseID);
    }

    /**
     * Returns a set of all the person IDs for the people in the current users father's side
     *
     * @return - a set of Person IDs (father's side)
     */
    public Set<String> getFathersSideIDs() {
        Set<String> fathersSideIds = new HashSet<>();
        if (father != null) {
            fathersSideIds.add(fatherId);
            fathersSideIds.addAll(father.getAncestorsIDs());
        }
        return fathersSideIds;
    }

    /**
     * Returns a set of all the person IDs for the people in the current users mother's side
     *
     * @return - a set of Person IDs (mother's side)
     */
    public Set<String> getMothersSideIDs() {
        Set<String> mothersSideIds = new HashSet<>();
        if (mother != null) {
            mothersSideIds.add(motherId);
            mothersSideIds.addAll(mother.getAncestorsIDs());
        }
        return mothersSideIds;
    }

    /**
     * Recursive function to gather all the person IDs of a person's ancestors
     *
     * @return - a set of all ancestor Person IDs
     */
    private Set<String> getAncestorsIDs() {
        Set<String> ancestorIds = new HashSet<>();
        if (father != null) {
            ancestorIds.add(fatherId);
            ancestorIds.addAll(father.getAncestorsIDs());
        }
        if (mother != null) {
            ancestorIds.add(motherId);
            ancestorIds.addAll(mother.getAncestorsIDs());
        }
        return ancestorIds;
    }

    @Override
    public int hashCode() {
        return personId.hashCode();
    }

    public Relationship getRelationshipTo(Person otherPerson) {
        // Check if the "otherPerson" is related to this person
        if (this.fatherId != null && this.motherId != null) {
            if ((this.fatherId.equals(otherPerson.personId)) ||
                    (this.motherId.equals(otherPerson.personId))) {
                // This person is the child of other person
                return CHILD;
            }
        }
        if (this.spouseId != null && otherPerson.spouseId != null) {
            if ((this.spouseId.equals(otherPerson.personId)) ||
                    (this.personId.equals(otherPerson.spouseId))) {
                return SPOUSE;
            }
        }
        if (otherPerson.fatherId != null) {
            if (otherPerson.fatherId.equals(this.personId)) {
                return FATHER;
            }
        }
        if (otherPerson.motherId != null) {
            if (otherPerson.motherId.equals(this.personId)) {
                return MOTHER;
            }
        }

        return null;
    }

    public void addRelatedPerson(Person person) {
        relatedPeople.put(person.personId, person);
        Relationship relationship = getRelationshipTo(person);
        if (relationship == null) {
            return;
        }
        switch (relationship) {
            case FATHER: // this person is other person's father -- add that person to the children
                addChild(person);
                return;
            case MOTHER: // this person is other person's mother -- add that person to the children
                addChild(person);
            case SPOUSE:
                spouse = person;
                break;
            case CHILD:
                if (fatherId.equals(person.getPersonId())) {
                    father = person;
                } else if (motherId.equals(person.getPersonId())) {
                    mother = person;
                }
                break;
        }
    }

    public List<Person> getFamily() {
        List<Person> family = new ArrayList<>();
        if (spouseId != null) {
            family.add(FamilyMapModel.SINGLETON.getPerson(spouseId));
        }
        if (fatherId != null) {
            family.add(father);
        }
        if (mother != null) {
            family.add(mother);
        }
        family.addAll(children);

        return family;
    }

    private void addChild(Person person) {
        if (!children.contains(person)) {
            children.add(person);
        }
    }

    public void setMother(Person mother) {
        this.mother = mother;
        mother.addChild(this);
    }

    public void setFather(Person father) {
        this.father = father;
        father.addChild(this);
    }

    public void setSpouse(Person spouse) {
        this.spouse = spouse;
    }

    public void addRelatedEvent(Event event) {
        relatedEvents.add(event);
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(String gender) {
        if (gender.equals("m")) {
            this.gender = Gender.MALE;
        } else {
            this.gender = Gender.FEMALE;
        }
    }

    public String getFatherId() {
        return fatherId;
    }

    public void setFatherId(String fatherId) {
        this.fatherId = fatherId;
    }

    public String getMotherId() {
        return motherId;
    }

    public void setMotherId(String motherId) {
        this.motherId = motherId;
    }

    public String getSpouseId() {
        return spouseId;
    }

    public void setSpouseId(String spouseId) {
        this.spouseId = spouseId;
    }

    public Event getEvent(String eventId) {
        Event desiredEvent = null;
        for (Event event :
                relatedEvents) {
            if (event.getEventId().equals(eventId)) {
                desiredEvent = event;
            }
        }
        return desiredEvent;
    }

    /**
     * Returns the birth event of a Person. If there is no birth event recorded, it will just return the next earliest event recorded
     *
     * @return - the birth event or next earliest event of this Person. returns null if no events are recorded.
     */
    public Event getEarliestEvent() {
        Event earliestEvent = null;
        for (Event event : relatedEvents) {
            // Initially set the first event encountered as the earliest event
            if (earliestEvent == null) {
                earliestEvent = event;
            }
            // replace the earliest event if an earlier one is found
            else if (event.getYear().compareTo(earliestEvent.getYear()) < 0) {
                earliestEvent = event;
            }
        }

        return earliestEvent;
    }

    /**
     * Returns the event that chronologically follows current event
     *
     * @param currentEvent - the current event; we will return the event following this one
     * @return - returns the event following <code>currentEvent</code>
     */
    public Event getEventFollowing(Event currentEvent) {
        Event nextEvent = null;
        Object[] events = relatedEvents.toArray();
        for (int i = 0; i < events.length - 1; i++) { // Go through second to last, to look for current event
            if (events[i] == currentEvent) {
                nextEvent = (Event) events[i + 1]; // If found, return the next event
            }
        }
        return nextEvent;
    }

    @Override
    public boolean equals(Object o) {

        if(this.getClass().equals(o.getClass())) {
            Person person = (Person) o;
            boolean result = person.getPersonId().equals(this.getPersonId()) &&
                    person.getPersonId().equals(this.getPersonId()) &&
                    person.getFirstName().equals(this.getFirstName()) &&
                    person.getLastName().equals(this.getLastName()) &&
                    person.getGender().equals(this.getGender());

            if(person.getSpouseId() == null) {
                if(this.getSpouseId() != null) {
                    return false;
                }
            }
            else {
                if(this.getSpouseId() == null) {
                    return false;
                }
                else if (!person.getSpouseId().equals(this.getSpouseId())) {
                    return false;
                }
            }

            if(person.getFatherId() == null) {
                if(this.getFatherId() != null) {
                    return false;
                }
            }
            else {
                if(this.getFatherId() == null) {
                    return false;
                }
                else if (!person.getFatherId().equals(this.getFatherId())) {
                    return false;
                }
            }


            if(person.getMotherId() == null) {
                if(this.getMotherId() != null) {
                    return false;
                }
            }
            else {
                if(this.getMotherId() == null) {
                    return false;
                }
                else if (!person.getMotherId().equals(this.getMotherId())) {
                    return false;
                }
            }
            return result;
        }
        else {
            return false;
        }
    }

    /**
     * Returns a string of all the information about a person that we want to be searchable
     * **Note: search is case insensitive
     *
     * @return - string of all searchable data
     */
    public String getSearchableText() {
        return (getFirstName() + " " + getLastName()).toLowerCase();
    }

    public enum Relationship {
        FATHER, MOTHER, SPOUSE, CHILD;

        @Override
        public String toString() {
            switch (this) {

                case FATHER:
                    return "Father";

                case MOTHER:
                    return "Mother";

                case SPOUSE:
                    return "Spouse";

                case CHILD:
                    return "Child";

            }
            return null;
        }
    }

    public enum Gender {
        MALE, FEMALE;

        @Override
        public String toString() {
            if (this.name().equals("MALE")) {
                return "Male";
            } else {
                return "Female";
            }
        }


        public Drawable getDrawable(Context context) {
            if (this.name().equals("MALE")) {
                Drawable drawable = new IconDrawable(context, Iconify.IconValue.fa_male).colorRes(R.color.male_icon).sizeDp(40);
                return drawable;
            } else {
                Drawable drawable = new IconDrawable(context, Iconify.IconValue.fa_female).colorRes(R.color.female_icon).sizeDp(40);
                return drawable;
            }
        }

    }

}
