package com.skyler.android.familymap.main_activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.skyler.android.familymap.R;

import com.skyler.android.familymap.model.Event;
import com.skyler.android.familymap.model.FamilyMapModel;
import com.skyler.android.familymap.model.Person;
import com.skyler.android.familymap.other_activities.PersonActivity;
import com.skyler.android.familymap.other_activities.SettingsActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback{
    private static final float RELATIONSHIP_LINE_MAX_WIDTH = 15.0f;
    GoogleMap mMap;
    MapView mMapView;

    private OnFragmentInteractionListener mListener;
    private TextView mEventPreviewTextView;
    private ImageView mEventPreviewGenderIcon;
    private LinearLayout mEventPreviewLayout;
    private ImageView mToolbarFilterIcon;
    private ImageView mToolbarSearchIcon;
    private ImageView mToolbarSettingsIcon;

    private Person personInfoDisplaying = null;
    private Event eventBeingDisplayed = null;

    private Drawable SEARCH_ICON;
    private Drawable FILTER_ICON;
    private Drawable GEAR_ICON;
    private Drawable ANDROID_ICON;
    private Drawable MALE_ICON;
    private Drawable FEMALE_ICON;
    private List<Polyline> mRelationshipLines = new ArrayList<>();

    public MapFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SEARCH_ICON = new IconDrawable(getActivity(), Iconify.IconValue.fa_search).colorRes(R.color.white).sizeDp(30);
        FILTER_ICON = new IconDrawable(getActivity(), Iconify.IconValue.fa_filter).colorRes(R.color.white).sizeDp(30);
        GEAR_ICON = new IconDrawable(getActivity(), Iconify.IconValue.fa_gear).colorRes(R.color.white).sizeDp(30);
        ANDROID_ICON = new IconDrawable(getActivity(), Iconify.IconValue.fa_android).colorRes(R.color.androidGreen).sizeDp(50);
        MALE_ICON = new IconDrawable(getActivity(), Iconify.IconValue.fa_male).colorRes(R.color.male_icon).sizeDp(50);
        FEMALE_ICON = new IconDrawable(getActivity(), Iconify.IconValue.fa_female).colorRes(R.color.female_icon).sizeDp(50);

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = (MapView) v.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMap = mMapView.getMap();

        LatLng userBirth = null;
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

            if (event.getPersonId().equals(FamilyMapModel.SINGLETON.currentUser.getPersonId()) &&
                    event.getDescription().equals("birth")) {
                userBirth = eventLocation;
            }
        }
        if (userBirth != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(userBirth));
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                displayMarkerEventInfo(marker);
                updateRelationshipLines();
                return true;
            }
        });

        mEventPreviewLayout = (LinearLayout) v.findViewById(R.id.eventPreview);
        mEventPreviewLayout.setClickable(false);
        mEventPreviewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to Person Activity
                if(personInfoDisplaying != null) {
                    Intent intent = new Intent(getActivity(), PersonActivity.class);
                    intent.putExtra("PERSON_ID", personInfoDisplaying.getPersonId());
                    startActivity(intent);
                }
            }
        });

        mEventPreviewGenderIcon = (ImageView) v.findViewById(R.id.eventPreviewGenderIcon);
        mEventPreviewGenderIcon.setImageDrawable(ANDROID_ICON); //Set the android icon before any event is selected

        mEventPreviewTextView = (TextView) v.findViewById(R.id.eventPreviewText);
        mEventPreviewTextView.setText("Click on a marker\nto see event details.");


        mToolbarSearchIcon = (ImageView) v.findViewById(R.id.toolbarSearchIcon);
        mToolbarSearchIcon.setImageDrawable(SEARCH_ICON);
        mToolbarSearchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to Search Activity
                // TODO: 3/25/2016 Transfer to search activity
            }
        });

        mToolbarFilterIcon = (ImageView) v.findViewById(R.id.toolbarFilterIcon);
        mToolbarFilterIcon.setImageDrawable(FILTER_ICON);
        mToolbarFilterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to Filter Activity
            }
        });

        mToolbarSettingsIcon = (ImageView) v.findViewById(R.id.toolbarSettingsIcon);
        mToolbarSettingsIcon.setImageDrawable(GEAR_ICON);
        mToolbarSettingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to Settings Activity
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);

            }
        });

        return v;
    }

    /**
     * Updates all relationship polylines on the screen by clearing all lines and redrawing the appropriate ones
     * This function is called each time an event is touched or each time the map is resumed from another activity
     */
    private void updateRelationshipLines() {
        // Draw family tree lines if there is an event to display
        if(eventBeingDisplayed != null) {
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
     * @param person - the person whose life story will be displayed
     */
    private void drawLifeStoryLines(Person person) {
        if(person == null) {
            return; // This may occur if no person has been selected yet
        }
        Event currentEvent = person.getEarliestEvent();
        while(currentEvent != null) {
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
     * @param event - the event from which to draw the line
     */
    private void drawSpouseLines(Event event) {
        Person person = FamilyMapModel.SINGLETON.getUserPersonMap().get(event.getPersonId());
        if(person.spouse != null ) {
            Event spouseBirth = person.spouse.getEarliestEvent();
            if(spouseBirth != null) {
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
     * @param event - The event from which the line will be drawn
     * @param generation - The current generation from which the lines being drawn (root = 1st gen.)
     */
    private void drawFamilyTreeLines(Event event, int generation) {
        Person person = FamilyMapModel.SINGLETON.getUserPersonMap().get(event.getPersonId());
        if(person.father != null) {
            Event fathersBirth = person.father.getEarliestEvent();
            if(fathersBirth != null) {
                Polyline line = mMap.addPolyline(new PolylineOptions()
                        .add(event.getLatLng(), fathersBirth.getLatLng())
                        .color(FamilyMapModel.SINGLETON.mSettings.getFamilyTreeLinesColor())
                        .width(RELATIONSHIP_LINE_MAX_WIDTH / generation)
                        .geodesic(true));
                mRelationshipLines.add(line);
                drawFamilyTreeLines(fathersBirth, generation + 1); // Recurse through father's tree (if it exists)

            }
        }
        if(person.mother != null) {
            Event mothersBirth = person.mother.getEarliestEvent();
            if(mothersBirth != null) {
                Polyline line =  mMap.addPolyline(new PolylineOptions()
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
        for(Polyline line : mRelationshipLines) {
            line.remove();
        }
        mRelationshipLines.clear();
    }

    @Override
    public void onResume() {
        super.onResume();

        updateRelationshipLines();
        updateMapType();
    }

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

    private void displayMarkerEventInfo(Marker marker) {
        mEventPreviewLayout.setClickable(true); // Make the preview clickable only if there is a person selected
        Event event = FamilyMapModel.SINGLETON.getEvent(marker.getSnippet());
        mEventPreviewTextView.setText(marker.getTitle());
        personInfoDisplaying = FamilyMapModel.SINGLETON.getUserPersonMap().get(event.getPersonId());
        eventBeingDisplayed = event;
        if(personInfoDisplaying.getGender() == Person.Gender.MALE) {
            mEventPreviewGenderIcon.setImageDrawable(MALE_ICON);
        }
        else {
            mEventPreviewGenderIcon.setImageDrawable(FEMALE_ICON);
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        String test = "this is a test!";
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
