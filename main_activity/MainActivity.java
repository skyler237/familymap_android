package com.skyler.android.familymap.main_activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.skyler.android.familymap.R;

import com.skyler.android.familymap.model.Event;
import com.skyler.android.familymap.model.FamilyMapModel;

public class MainActivity extends FragmentActivity
        implements LoginFragment.OnLoginButtonPressedListener{

    private LoginFragment loginFragment;
    private MapFragment mapFragment;
    private GoogleMap mMap;
    private TextView mEventPreviewTextView;
    private ImageView mEventPreviewGenderIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = this.getSupportFragmentManager();
        loginFragment = (LoginFragment) fm.findFragmentById(R.id.loginFrameLayout);
        if (loginFragment == null) {
            loginFragment = LoginFragment.newInstance();
            fm.beginTransaction()
                    .add(R.id.loginFrameLayout, loginFragment)
                    .commit();
        }

   }

    @Override
    public void onLoginSuccessful() {
        FragmentManager fm = this.getSupportFragmentManager();
        mapFragment = (MapFragment) fm.findFragmentById(R.id.mapFragmentLayout);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction()
                    .remove(loginFragment)
                    .add(R.id.mapFrameLayout, mapFragment)
                    .commit();
        }
    }



    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
