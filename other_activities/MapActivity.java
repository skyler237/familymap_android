package com.skyler.android.familymap.other_activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.skyler.android.familymap.R;
import com.skyler.android.familymap.main_activity.MainActivity;
import com.skyler.android.familymap.model.Event;
import com.skyler.android.familymap.model.FamilyMapModel;
import com.skyler.android.familymap.model.Person;

import java.util.ArrayList;

/**
 * This class manages the map activity screen to display a particular event
 * Provides much of the same functionality of the map fragment
 */
public class MapActivity extends AppCompatActivity {

    private static final float RELATIONSHIP_LINE_MAX_WIDTH = 12.0f;
    private MapView mMapView;
    private GoogleMap mMap;

    private Event eventBeingDisplayed;
    private TextView mEventPreviewTextView;
    private ImageView mEventPreviewGenderIcon;
    private LinearLayout mEventPreviewLayout;
    private android.widget.Toolbar mMapToolbar;
    private ImageView mToolbarFilterIcon;
    private ImageView mToolbarSearchIcon;
    private ImageView mSettingsGoToTopIcon;

    private Person personInfoDisplaying = null;

    private Drawable SEARCH_ICON;
    private Drawable FILTER_ICON;
    private Drawable GEAR_ICON;
    private Drawable ANDROID_ICON;
    private Drawable MALE_ICON;
    private Drawable FEMALE_ICON;
    private ArrayList<Polyline> mRelationshipLines = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);

        SEARCH_ICON = new IconDrawable(getBaseContext(), Iconify.IconValue.fa_search).colorRes(R.color.white).sizeDp(20);
        FILTER_ICON = new IconDrawable(getBaseContext(), Iconify.IconValue.fa_filter).colorRes(R.color.white).sizeDp(20);
        GEAR_ICON = new IconDrawable(getBaseContext(), Iconify.IconValue.fa_gear).colorRes(R.color.white).sizeDp(20);
        ANDROID_ICON = new IconDrawable(getBaseContext(), Iconify.IconValue.fa_android).colorRes(R.color.androidGreen).sizeDp(50);
        MALE_ICON = new IconDrawable(getBaseContext(), Iconify.IconValue.fa_male).colorRes(R.color.male_icon).sizeDp(50);
        FEMALE_ICON = new IconDrawable(getBaseContext(), Iconify.IconValue.fa_female).colorRes(R.color.female_icon).sizeDp(50);


        // Inflate the layout for this fragment
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getBaseContext().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMap = mMapView.getMap();

        for (Event event :
                FamilyMapModel.SINGLETON.getUserEvents()) {

            // Add a marker for each event
            LatLng eventLocation = new LatLng(event.getLatitude(), event.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(eventLocation)
                    .title(event.toString())
                    .icon(BitmapDescriptorFactory.defaultMarker(event.getColor()))
                    .snippet(event.getEventId()); // Store event ID in snippet
            mMap.addMarker(markerOptions);

        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                displayMarkerEventInfo(marker);

                updateRelationshipLines();
                updateMapType();
                return true;
            }
        });


        mEventPreviewLayout = (LinearLayout) findViewById(R.id.eventPreview);
        mEventPreviewLayout.setClickable(false);
        mEventPreviewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to Person Activity
                if (personInfoDisplaying != null) {
                    Intent intent = new Intent(getBaseContext(), PersonActivity.class);
                    intent.putExtra("PERSON_ID", personInfoDisplaying.getPersonId());
                    startActivity(intent);
                }
            }
        });

        mEventPreviewGenderIcon = (ImageView) findViewById(R.id.eventPreviewGenderIcon);
        mEventPreviewGenderIcon.setImageDrawable(ANDROID_ICON); //Set the android icon before any event is selected

        mEventPreviewTextView = (TextView) findViewById(R.id.eventPreviewText);
        mEventPreviewTextView.setText("Click on a marker\nto see event details.");

        // Zoom to the selected event and display its info
        String selectedEventId = getIntent().getExtras().getString("EVENT_ID");
        displayEventInfo(selectedEventId);
        eventBeingDisplayed = FamilyMapModel.SINGLETON.getEvent(selectedEventId);
        personInfoDisplaying = FamilyMapModel.SINGLETON.getPerson(eventBeingDisplayed.getPersonId());

        // Show the lines and map type
        updateRelationshipLines();
        updateMapType();

        mMapToolbar = (android.widget.Toolbar) findViewById(R.id.mapToolbar);
        setActionBar(mMapToolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mMapToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mMapToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                finish();
            }
        });


        Drawable goToTopIcon = new IconDrawable(this, Iconify.IconValue.fa_angle_double_up).colorRes(R.color.white).sizeDp(35);
        mSettingsGoToTopIcon = (ImageView) findViewById(R.id.toolbarSettingsIcon); // Reuse the spot for the settings icon from map fragment
        mSettingsGoToTopIcon.setImageDrawable(goToTopIcon);
        mSettingsGoToTopIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to top
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    /**
     * Displays the event info of the selected marker in the preview area at the bottom of the screen
     * @param marker - the selected marker for which we will display the info
     */
    private void displayMarkerEventInfo(Marker marker) {
        mEventPreviewLayout.setClickable(true); // Make the preview clickable only if there is a person selected
        Event event = FamilyMapModel.SINGLETON.getEvent(marker.getSnippet());
        eventBeingDisplayed = event;
        mEventPreviewTextView.setText(marker.getTitle());
        personInfoDisplaying = FamilyMapModel.SINGLETON.getUserPersonMap().get(event.getPersonId());
        if (personInfoDisplaying.getGender() == Person.Gender.MALE) {
            mEventPreviewGenderIcon.setImageDrawable(MALE_ICON);
        } else {
            mEventPreviewGenderIcon.setImageDrawable(FEMALE_ICON);
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
    }

    /**
     * Displays the event info of the selected event (which triggered the creation of this activity
     * @param eventId - the selected event's id
     */
    private void displayEventInfo(String eventId) {
        mEventPreviewLayout.setClickable(true); // Make the preview clickable only if there is a person selected
        Event event = FamilyMapModel.SINGLETON.getEvent(eventId);
        mEventPreviewTextView.setText(event.toString());
        personInfoDisplaying = FamilyMapModel.SINGLETON.getUserPersonMap().get(event.getPersonId());
        if (personInfoDisplaying.getGender() == Person.Gender.MALE) {
            mEventPreviewGenderIcon.setImageDrawable(MALE_ICON);
        } else {
            mEventPreviewGenderIcon.setImageDrawable(FEMALE_ICON);
        }

        LatLng position = event.getLatLng();
        Log.i("MapActivity", "LatLng=" + position.toString());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(event.getLatLng()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(event.getLatLng(), 4.5f));
    }

    /**
     * Updates all relationship polylines on the screen by clearing all lines and redrawing the appropriate ones
     * This function is called each time an event is touched or each time the map is resumed from another activity
     */
    private void updateRelationshipLines() {
        // Draw family tree lines if there is an event to display
        if (eventBeingDisplayed != null) {
            clearRelationshipLines();
            if (FamilyMapModel.SINGLETON.mSettings.isFamilyTreeLinesOn()) {
                int generation = 1;
                drawFamilyTreeLines(eventBeingDisplayed, generation);
            }

            // Draw Spouse lines
            if (FamilyMapModel.SINGLETON.mSettings.isSpouseLinesOn()) {
                drawSpouseLines(eventBeingDisplayed);
            }

            // Draw Life Story lines
            if (FamilyMapModel.SINGLETON.mSettings.isLifeStoryLinesOn()) {
                drawLifeStoryLines(personInfoDisplaying);
            }
        }
    }

    /**
     * Recursive function to draw the map polylines for the current person's life story
     * Connects lines between all of the person's events in chronological order
     *
     * @param person - the person whose life story will be displayed
     */
    private void drawLifeStoryLines(Person person) {
        if (person == null) {
            return; // This may occur if no person has been selected yet
        }
        Event currentEvent = person.getEarliestEvent();
        while (currentEvent != null) {
            Event nextEvent = person.getEventFollowing(currentEvent);
            if (nextEvent != null) {
                Polyline line = mMap.addPolyline(new PolylineOptions()
                        .add(currentEvent.getLatLng(), nextEvent.getLatLng())
                        .color(FamilyMapModel.SINGLETON.mSettings.getLifeStoryLinesColor())
                        .geodesic(true));
                mRelationshipLines.add(line);
            }
            currentEvent = nextEvent;
        }
    }

    /**
     * Draws a map polyline from the current event to the person's spouse's birth (or earliest event) if a spouse exists
     *
     * @param event - the event from which to draw the line
     */
    private void drawSpouseLines(Event event) {
        Person person = FamilyMapModel.SINGLETON.getUserPersonMap().get(event.getPersonId());
        if (person.spouse != null) {
            Event spouseBirth = person.spouse.getEarliestEvent();
            if (spouseBirth != null) {
                Polyline line = mMap.addPolyline(new PolylineOptions()
                        .add(event.getLatLng(), spouseBirth.getLatLng())
                        .color(FamilyMapModel.SINGLETON.mSettings.getSpouseLinesColor())
                        .geodesic(true));
                mRelationshipLines.add(line);
            }
        }
    }

    /**
     * Recursive function to draw family lines. It will recurse up both parent trees until there is no parent found.
     * Draws the lines with the appropriate color and decreasing with for each generation.
     *
     * @param event      - The event from which the line will be drawn
     * @param generation - The current generation from which the lines being drawn (root = 1st gen.)
     */
    private void drawFamilyTreeLines(Event event, int generation) {
        Person person = FamilyMapModel.SINGLETON.getUserPersonMap().get(event.getPersonId());
        if (person.father != null) {
            Event fathersBirth = person.father.getEarliestEvent();
            if (fathersBirth != null) {
                Polyline line = mMap.addPolyline(new PolylineOptions()
                        .add(event.getLatLng(), fathersBirth.getLatLng())
                        .color(FamilyMapModel.SINGLETON.mSettings.getFamilyTreeLinesColor())
                        .width(RELATIONSHIP_LINE_MAX_WIDTH / generation)
                        .geodesic(true));
                mRelationshipLines.add(line);
                drawFamilyTreeLines(fathersBirth, generation + 1); // Recurse through father's tree (if it exists)

            }
        }
        if (person.mother != null) {
            Event mothersBirth = person.mother.getEarliestEvent();
            if (mothersBirth != null) {
                Polyline line = mMap.addPolyline(new PolylineOptions()
                        .add(event.getLatLng(), mothersBirth.getLatLng())
                        .color(FamilyMapModel.SINGLETON.mSettings.getFamilyTreeLinesColor())
                        .width(RELATIONSHIP_LINE_MAX_WIDTH / generation)); // Make the color decrease with generation
                mRelationshipLines.add(line);
                drawFamilyTreeLines(mothersBirth, generation + 1); // Recurse through mother's side
            }
        }
    }

    /**
     * Clears all the relationship polylines on the screen so the appropriate ones can be redrawn
     */
    private void clearRelationshipLines() {
        for (Polyline line : mRelationshipLines) {
            line.remove();
        }
        mRelationshipLines.clear();
    }

    /**
     * Sets the map type to reflect the current setting
     */
    private void updateMapType() {
        switch (FamilyMapModel.SINGLETON.mSettings.getMapType()) {

            case NORMAL:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case HYBRID:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case SATELLITE:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case TERRAIN:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
        }
    }

}
