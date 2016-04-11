package com.skyler.android.familymap.other_activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.skyler.android.familymap.R;
import com.skyler.android.familymap.main_activity.MainActivity;
import com.skyler.android.familymap.model.FamilyMapModel;
import com.skyler.android.familymap.model.User;
import com.skyler.android.familymap.network.HttpClient;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class SettingsActivity extends AppCompatActivity {

    Switch mLifeStoryLinesSwitch;
    Spinner mLifeStoryLinesColorSpinner;

    Switch mFamilyTreeLinesSwitch;
    Spinner mFamilyTreeLinesColorSpinner;

    Switch mSpouseLinesSwitch;
    Spinner mSpouseLinesColorSpinner;

    Spinner mMapTypeSpinner;

    Button mResyncButton;
    Button mLogoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Process life story line settings
        mLifeStoryLinesSwitch = (Switch) findViewById(R.id.life_story_lines_switch);
        mLifeStoryLinesSwitch.setChecked(FamilyMapModel.SINGLETON.mSettings.isLifeStoryLinesOn());
        mLifeStoryLinesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FamilyMapModel.SINGLETON.mSettings.setLifeStoryLinesOn(isChecked);
            }
        });

        final String[] spinnerColors = getResources().getStringArray((R.array.colors));
        mLifeStoryLinesColorSpinner = (Spinner) findViewById(R.id.life_story_lines_color_spinner);
        mLifeStoryLinesColorSpinner.setSelection(FamilyMapModel.SINGLETON.mSettings.getLifeStoryLinesColorIndex());
        mLifeStoryLinesColorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                FamilyMapModel.SINGLETON.mSettings.setLifeStoryLinesColor(spinnerColors[position]);
                FamilyMapModel.SINGLETON.mSettings.setLifeStoryLinesColorIndex(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });


        // Process family tree line settings
        mFamilyTreeLinesSwitch = (Switch) findViewById(R.id.family_tree_lines_switch);
        mFamilyTreeLinesSwitch.setChecked(FamilyMapModel.SINGLETON.mSettings.isFamilyTreeLinesOn());
        mFamilyTreeLinesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FamilyMapModel.SINGLETON.mSettings.setFamilyTreeLinesOn(isChecked);
            }
        });

        mFamilyTreeLinesColorSpinner = (Spinner) findViewById(R.id.family_tree_lines_color_spinner);
        mFamilyTreeLinesColorSpinner.setSelection(FamilyMapModel.SINGLETON.mSettings.getFamilyTreeLinesColorIndex());
        mFamilyTreeLinesColorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                FamilyMapModel.SINGLETON.mSettings.setFamilyTreeLinesColor(spinnerColors[position]);
                FamilyMapModel.SINGLETON.mSettings.setFamilyTreeLinesColorIndex(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });


        // Process spouse line settings
        mSpouseLinesSwitch = (Switch) findViewById(R.id.spouse_lines_switch);
        mSpouseLinesSwitch.setChecked(FamilyMapModel.SINGLETON.mSettings.isSpouseLinesOn());
        mSpouseLinesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FamilyMapModel.SINGLETON.mSettings.setSpouseLinesOn(isChecked);
            }
        });

        mSpouseLinesColorSpinner = (Spinner) findViewById(R.id.spouse_lines_color_spinner);
        mSpouseLinesColorSpinner.setSelection(FamilyMapModel.SINGLETON.mSettings.getSpouseLinesColorIndex());
        mSpouseLinesColorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                FamilyMapModel.SINGLETON.mSettings.setSpouseLinesColor(spinnerColors[position]);
                FamilyMapModel.SINGLETON.mSettings.setSpouseLinesColorIndex(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Process Map type spinner
        final String[] mapTypes = getResources().getStringArray((R.array.map_types));
        mMapTypeSpinner = (Spinner) findViewById(R.id.map_type_spinner);
        mMapTypeSpinner.setSelection(FamilyMapModel.SINGLETON.mSettings.getMapTypeIndex());
        mMapTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                FamilyMapModel.SINGLETON.mSettings.setMapType(mapTypes[position]);
                FamilyMapModel.SINGLETON.mSettings.setMapTypeIndex(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });


        mResyncButton = (Button) findViewById(R.id.resync_button);
        mResyncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean resyncDataSuccess = FamilyMapModel.SINGLETON.httpClient.resyncData();
                if (resyncDataSuccess) {
                    Toast.makeText(getBaseContext(), "Data synchronization successful", Toast.LENGTH_LONG).show();

                    // Go to top
                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Toast.makeText(getBaseContext(), "Data synchronization failed", Toast.LENGTH_LONG).show();
                }
            }
        });

        mLogoutButton = (Button) findViewById(R.id.logout_button);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FamilyMapModel.SINGLETON.mSettings.resetSettings();
                Intent intent = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_person, menu);
//        ActionBar toolbar = getSupportActionBar();
//        if (toolbar != null) {
//            toolbar.setTitle("Family Map: Settings");
//        }
//        Drawable goToTopIcon = new IconDrawable(this, Iconify.IconValue.fa_angle_double_up).colorRes(R.color.white).sizeDp(30);
//        menu.getItem(0).setIcon(goToTopIcon);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;

//            case R.id.action_go_to_top:
//                Intent intent = new Intent(this, MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                break;


        }

        return super.onOptionsItemSelected(item);
    }
}

