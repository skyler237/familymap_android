package com.skyler.android.familymap.other_activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.skyler.android.familymap.R;
import com.skyler.android.familymap.main_activity.MainActivity;
import com.skyler.android.familymap.model.Event;
import com.skyler.android.familymap.model.FamilyMapModel;
import com.skyler.android.familymap.model.Person;

public class MapActivity extends AppCompatActivity {

    private MapView mMapView;
    private GoogleMap mMap;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // todo: Fix weird bug of crashing on some MapActivity to PersonActivity switches.

        // todo: Add map line functionality
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

//        LatLng selectedEvent = null;

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

//            if (event.getEventId().equals(selectedEventId)) {
//                selectedEvent = eventLocation;
//            }
        }
//        if (selectedEvent != null) {
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(selectedEvent));
//            mMap.animateCamera(CameraUpdateFactory.zoomIn());
//            mMap.animateCamera(CameraUpdateFactory.zoomIn());
//        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                displayMarkerEventInfo(marker);
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
        Log.i("MapActivity", "EventID=" + selectedEventId);
        displayEventInfo(selectedEventId);

        mMapToolbar = (android.widget.Toolbar) findViewById(R.id.mapToolbar);
//        mMapToolbar.setTitleTextColor(0xfff);
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



    private void displayMarkerEventInfo(Marker marker) {
        mEventPreviewLayout.setClickable(true); // Make the preview clickable only if there is a person selected
        Event event = FamilyMapModel.SINGLETON.getEvent(marker.getSnippet());
        mEventPreviewTextView.setText(marker.getTitle());
        personInfoDisplaying = FamilyMapModel.SINGLETON.getUserPersonMap().get(event.getPersonId());
        if(personInfoDisplaying.getGender() == Person.Gender.MALE) {
            mEventPreviewGenderIcon.setImageDrawable(MALE_ICON);
        }
        else {
            mEventPreviewGenderIcon.setImageDrawable(FEMALE_ICON);
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
    }

    private void displayEventInfo(String eventId) {
        mEventPreviewLayout.setClickable(true); // Make the preview clickable only if there is a person selected
        Event event = FamilyMapModel.SINGLETON.getEvent(eventId);
        mEventPreviewTextView.setText(event.toString());
        personInfoDisplaying = FamilyMapModel.SINGLETON.getUserPersonMap().get(event.getPersonId());
        if(personInfoDisplaying.getGender() == Person.Gender.MALE) {
            mEventPreviewGenderIcon.setImageDrawable(MALE_ICON);
        }
        else {
            mEventPreviewGenderIcon.setImageDrawable(FEMALE_ICON);
        }

        LatLng position = event.getLatLng();
        Log.i("MapActivity", "LatLng=" + position.toString());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(event.getLatLng()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(event.getLatLng(), 4.5f));
    }

}
