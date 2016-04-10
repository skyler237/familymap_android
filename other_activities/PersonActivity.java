package com.skyler.android.familymap.other_activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.skyler.android.familymap.R;
import com.skyler.android.familymap.main_activity.MainActivity;
import com.skyler.android.familymap.model.Event;
import com.skyler.android.familymap.model.FamilyMapModel;
import com.skyler.android.familymap.model.Person;

import java.util.ArrayList;
import java.util.List;

public class PersonActivity extends AppCompatActivity {
    final int EVENT_GROUP_INDEX = 0;
    final int FAMILY_GROUP_INDEX = 1;
    private TextView mPersonFirstNameText;
    private Person mCurrentPerson;
    private TextView mPersonLastNameText;
    private TextView mPersonGenderText;
    private ExpandableListView mExpandableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCurrentPerson = FamilyMapModel.SINGLETON.getUserPersonMap().get(getIntent().getExtras().getString("PERSON_ID"));

        mPersonFirstNameText = (TextView) findViewById(R.id.person_info_first_name_text);
        mPersonFirstNameText.setText(mCurrentPerson.getFirstName());

        mPersonLastNameText = (TextView) findViewById(R.id.person_info_last_name_text);
        mPersonLastNameText.setText(mCurrentPerson.getLastName());

        mPersonGenderText = (TextView) findViewById(R.id.person_info_gender_text);
        mPersonGenderText.setText(mCurrentPerson.getGender().toString());

        List<Event> events = new ArrayList<>();
        for (Event event :
                mCurrentPerson.relatedEvents) {
            events.add(event);
        }
        List<Person> family = mCurrentPerson.getFamily();
        ExpandableListAdapter listAdapter = new ExpandableListAdapter(events, family);
        mExpandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        mExpandableListView.setAdapter(listAdapter);


        mExpandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                mExpandableListView.setGroupIndicator(getDrawable(R.drawable.ic_expand_more));
            }
        });


        mExpandableListView.expandGroup(EVENT_GROUP_INDEX);
        mExpandableListView.expandGroup(FAMILY_GROUP_INDEX);


        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                switch (groupPosition) {
                    case EVENT_GROUP_INDEX:
                        Event event = (Event) parent.getExpandableListAdapter().getChild(groupPosition, childPosition);
                        Intent intent = new Intent(getBaseContext(), MapActivity.class);
                        intent.putExtra("EVENT_ID", event.getEventId());
                        startActivity(intent);
                        break;

                    case FAMILY_GROUP_INDEX:
                        intent = new Intent(getBaseContext(), PersonActivity.class);
                        intent.putExtra("PERSON_ID", ((Person) parent.getExpandableListAdapter().getChild(groupPosition, childPosition)).getPersonId());
                        startActivity(intent);

                }

                return true;
            }
        });

//        mLifeEventsRecyclerView = (RecyclerView) findViewById(R.id.list_event_info);
//        mLifeEventsRecyclerView.setLayoutManager(newLinearLayoutManager(this));
//        List<Event> events = new ArrayList<>();
//        for (Event event :
//                FamilyMapModel.SINGLETON.getUserEvents()) {
//            events.add(event);
//        }
//        mEventItemAdapter = new EventItemAdapter(events);
//        mLifeEventsRecyclerView.setAdapter(mEventItemAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_person, menu);
        ActionBar toolbar = getSupportActionBar();
        if (toolbar != null) {
            toolbar.setTitle("Family Map: Person Info");
        }
        Drawable goToTopIcon = new IconDrawable(this, Iconify.IconValue.fa_angle_double_up).colorRes(R.color.white).sizeDp(30);
        menu.getItem(0).setIcon(goToTopIcon);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;

            case R.id.action_go_to_top:
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;


        }

        return super.onOptionsItemSelected(item);
    }


    private class ExpandableListAdapter extends BaseExpandableListAdapter {
        // TODO fill in data fields and implement functions here.


        List<Event> mEventList;
        List<Person> mPersonList;

        public ExpandableListAdapter(List<Event> events, List<Person> personList) {
            mEventList = events;
            mPersonList = personList;
        }


        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case EVENT_GROUP_INDEX:
                    return mEventList.size();
                case FAMILY_GROUP_INDEX:
                    return mPersonList.size();
                default:
                    return 0;
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            switch (groupPosition) {
                case EVENT_GROUP_INDEX:
                    return mEventList;
                case FAMILY_GROUP_INDEX:
                    return mPersonList;
                default:
                    return 0;
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch (groupPosition) {
                case EVENT_GROUP_INDEX:
                    return mEventList.get(childPosition);
                case FAMILY_GROUP_INDEX:
                    return mPersonList.get(childPosition);
                default:
                    return null;
            }
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            String groupTitle;
            switch (groupPosition) {
                case EVENT_GROUP_INDEX:
                    groupTitle = "Life Events";
                    break;

                case FAMILY_GROUP_INDEX:
                    groupTitle = "Family";
                    break;

                default:
                    groupTitle = "Invalid group position: " + groupPosition;
            }
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater.from(PersonActivity.this));
                convertView = layoutInflater.inflate(R.layout.group_header, null);
            }

            TextView labelListHeader = (TextView) convertView.findViewById(R.id.bigBoldLine);
            labelListHeader.setText(groupTitle);

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            LayoutInflater layoutInflater;
            switch (groupPosition) {
                case EVENT_GROUP_INDEX:
                    Event event = (Event) getChild(groupPosition, childPosition);
                    layoutInflater = (LayoutInflater.from(PersonActivity.this));
                    convertView = layoutInflater.inflate(R.layout.list_item_event, null);

                    Drawable markerIcon = new IconDrawable(PersonActivity.this, Iconify.IconValue.fa_map_marker).colorRes(R.color.marker_grey).sizeDp(40);
                    ImageView markerIconImage = (ImageView) convertView.findViewById(R.id.event_item_marker_icon);
                    markerIconImage.setImageDrawable(markerIcon);

                    TextView eventInfoText = (TextView) convertView.findViewById(R.id.eventItemInfoText);
                    eventInfoText.setText(event.getInfoText());

                    TextView eventNameText = (TextView) convertView.findViewById(R.id.eventItemNameText);
                    eventNameText.setText(event.getPersonName());
                    break;

                case FAMILY_GROUP_INDEX:
                    Person person = (Person) getChild(groupPosition, childPosition);

                    layoutInflater = (LayoutInflater.from(PersonActivity.this));
                    convertView = layoutInflater.inflate(R.layout.list_item_person, null);


                    Drawable genderIcon = person.getGender().getDrawable(PersonActivity.this);
                    ImageView genderIconImage = (ImageView) convertView.findViewById(R.id.person_item_gender_icon);
                    genderIconImage.setImageDrawable(genderIcon);

                    TextView personNameText = (TextView) convertView.findViewById(R.id.person_item_name_text);
                    personNameText.setText(person.getFirstName() + " " + person.getLastName());

                    TextView personRelationshipText = (TextView) convertView.findViewById(R.id.person_item_relationship_text);
                    Person.Relationship relationship = person.getRelationshipTo(mCurrentPerson);
                    if (relationship != null) {
                        personRelationshipText.setText(relationship.toString());
                    } else {
                        personRelationshipText.setText("Relationship was null...");
                    }
                    break;

                default:
            }
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
