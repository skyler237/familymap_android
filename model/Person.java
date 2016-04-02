package com.skyler.android.familymap.model;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.skyler.android.familymap.R;
import com.skyler.android.familymap.other_activities.PersonActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
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
    public HashMap<String,Person> relatedPeople = new HashMap<>();
    public Person father;
    public Person mother;
    public Person spouse;
    public List<Person> children = new ArrayList<>();

    public Person() {

    }

    public List<Person> getFamily() {
        List<Person> family = new ArrayList<>();
        if(spouse != null) {
            family.add(spouse);
        }
        if(father != null) {
            family.add(father);
        }
        if(mother != null) {
            family.add(mother);
        }
        family.addAll(children);

        return family;
    }


    public enum Relationship {FATHER, MOTHER, SPOUSE, CHILD;

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

    public enum Gender {MALE, FEMALE;

        @Override
        public String toString() {
            if(this.name().equals("MALE")) {
                return "Male";
            }
            else{
                return "Female";
            }
        }

        public Drawable getDrawable(Context context) {
            if(this.name().equals("MALE")) {
                Drawable drawable = new IconDrawable(context, Iconify.IconValue.fa_male).colorRes(R.color.male_icon).sizeDp(40);
                return drawable;
            }
            else{
                Drawable drawable = new IconDrawable(context, Iconify.IconValue.fa_female).colorRes(R.color.female_icon).sizeDp(40);
                return drawable;
            }
        }
    }
    private String personId;
    private String firstName;
    private String lastName;
    private Gender gender;
    private String fatherId;
    private String motherId;
    private String spouseId;



    public Person(JSONObject jsonData) throws JSONException {
        setPersonId(jsonData.getString("personID"));
        setFirstName(jsonData.getString("firstName"));
        setLastName(jsonData.getString("lastName"));
        setGender(jsonData.getString("gender"));
        if(jsonData.has("father")) { setFatherId(jsonData.getString("father"));}
        if(jsonData.has("mother")) { setMotherId(jsonData.getString("mother"));}
        if(jsonData.has("spouse")) { setSpouseId(jsonData.getString("spouse"));}
    }

    public Relationship getRelationshipTo(Person otherPerson) {
        // TODO: Create this relationship function - check the two person IDs
        // Check if the "otherPerson" is related to this person
        if(this.fatherId != null && this.motherId != null ) {
            if ((this.fatherId.equals(otherPerson.fatherId)) ||
                    (this.motherId.equals(otherPerson.personId))) {
                // This person is the child of other person
                return CHILD;
            }
        }
        else if (this.spouseId != null && otherPerson.spouseId != null ) {
            if ((this.spouseId.equals(otherPerson.personId)) ||
                    (this.personId.equals(otherPerson.spouseId))) {
                return SPOUSE;
            }
        }
        else if(otherPerson.fatherId != null ) {
            if (otherPerson.fatherId.equals(this.personId)) {
                return FATHER;
            }
        }
        else if(otherPerson.motherId != null) {
            if (otherPerson.motherId.equals(this.personId)) {
                return MOTHER;
            }
        }
        else {
            return null;
        }
        return null;
    }

    public void addRelatedPerson(Person person) {
        relatedPeople.put(person.personId, person);
        Relationship relationship = getRelationshipTo(person);
        if(relationship == null) {
            return;
        }
        switch (relationship) {
            case FATHER:
                father = person;
                return;
            case MOTHER:
                mother = person;
            case SPOUSE:
                spouse = person;
                break;
            case CHILD:
                children.add(person);
                break;
        }
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
        if(gender.equals("m")) {
            this.gender = Gender.MALE;
        }
        else{
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
}
