package com.skyler.android.familymap.other_activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.skyler.android.familymap.R;
import com.skyler.android.familymap.model.Event;
import com.skyler.android.familymap.model.FamilyMapModel;
import com.skyler.android.familymap.model.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView mSearchRecyclerView;
    private RecyclerView.Adapter mSearchAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private EditText mSearchField;
    private Button mClearSearchButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mSearchRecyclerView = (RecyclerView) findViewById(R.id.search_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) mLayoutManager).setOrientation(LinearLayoutManager.VERTICAL);
        mSearchRecyclerView.setLayoutManager(mLayoutManager);

        mClearSearchButton = (Button) findViewById(R.id.clear_search_button);
        mClearSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchField.setText("");
            }
        });

        mSearchField = (EditText) findViewById(R.id.search_field);
        mSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Process search
                String searchText = mSearchField.getText().toString();
                String[] searchTokens = searchText.split("\\s+");


                // Note: using sets here to avoid duplicates
                Set<Person> personSearchResults = FamilyMapModel.SINGLETON.searchPeopleFor(searchTokens[0]);
                Set<Event> eventSearchResults = FamilyMapModel.SINGLETON.searchEventsFor(searchTokens[0]);
                for (int i = 1; i < searchTokens.length; i++) {
                    personSearchResults.retainAll(FamilyMapModel.SINGLETON.searchPeopleFor(searchTokens[i]));
                    eventSearchResults.retainAll(FamilyMapModel.SINGLETON.searchEventsFor(searchTokens[i]));
                }

                // Convert sets to lists to be used by Adapter
                List<Person> personList = new ArrayList<>();
                for (Person person : personSearchResults) {
                    personList.add(person);
                }


                List<Event> eventList = new ArrayList<>();
                for (Event event : eventSearchResults) {
                    eventList.add(event);
                }

                mSearchAdapter = new SearchItemAdapter(personList, eventList);
                mSearchRecyclerView.setAdapter(mSearchAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class SearchItemHolder extends RecyclerView.ViewHolder {
        private Event mEvent;
        private Person mPerson;

        private RelativeLayout mSearchResultLayout;
        private ImageView mIcon;
        private TextView mUpperTextView; // Names are generic to support similar view, but different content
        private TextView mLowerTextView;

        public SearchItemHolder(View itemView) {
            super(itemView);

            mSearchResultLayout = (RelativeLayout) itemView.findViewById(R.id.generic_item_layout);
            mSearchResultLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPerson != null) {
                        // Go to Person Activity
                        Intent intent = new Intent(getBaseContext(), PersonActivity.class);
                        intent.putExtra("PERSON_ID", mPerson.getPersonId());
                        startActivity(intent);
                    } else if (mEvent != null) {
                        // Go to event in Map Activity
                        Intent intent = new Intent(getBaseContext(), MapActivity.class);
                        intent.putExtra("EVENT_ID", mEvent.getEventId());
                        startActivity(intent);
                    }
                }
            });
            mIcon = (ImageView) itemView.findViewById(R.id.item_icon);
            mUpperTextView = (TextView) itemView.findViewById(R.id.item_upper_text_view);
            mLowerTextView = (TextView) itemView.findViewById(R.id.item_lower_text_view);

        }

        public void bindSearchPersonResult(Person person) {
            mPerson = person;
            mEvent = null;

            Drawable genderIcon = person.getGender().getDrawable(SearchActivity.this);
            mIcon.setImageDrawable(genderIcon);
            mUpperTextView.setText(person.getFirstName() + " " + person.getLastName());

            // We can leave the lower Text View blank for the person search results
        }

        public void bindSearchEventResult(Event event) {
            mEvent = event;
            mPerson = null;

            Drawable markerIcon = new IconDrawable(SearchActivity.this, Iconify.IconValue.fa_map_marker).colorRes(R.color.marker_grey).sizeDp(40);
            mIcon.setImageDrawable(markerIcon);
            mUpperTextView.setText(event.getInfoText());
            mLowerTextView.setText(event.getPersonName());
        }
    }

    private class SearchItemAdapter extends RecyclerView.Adapter<SearchItemHolder> {
        private List<Event> mEventList;
        private List<Person> mPersonList;

        public SearchItemAdapter(List<Person> personSearchResults, List<Event> eventSearchResults) {
            mEventList = eventSearchResults;
            mPersonList = personSearchResults;
        }

        @Override
        public SearchItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(SearchActivity.this);
            View view = inflater.inflate(R.layout.list_item_generic, parent, false);
            return new SearchItemHolder(view);
        }

        @Override
        public void onBindViewHolder(SearchItemHolder holder, int position) {
            if (position < mPersonList.size()) { // Place the people first
                Person person = mPersonList.get(position);
                holder.bindSearchPersonResult(person);
            } else { // Place the events after
                Event event = mEventList.get(position - mPersonList.size());
                holder.bindSearchEventResult(event);
            }
        }


        @Override
        public int getItemCount() {
            return mEventList.size() + mPersonList.size();
        }
    }
}
