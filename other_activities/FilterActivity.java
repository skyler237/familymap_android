package com.skyler.android.familymap.other_activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.skyler.android.familymap.R;
import com.skyler.android.familymap.model.FamilyMapModel;

import java.util.List;

/**
 * Handles the filter activity screen for the app
 */
public class FilterActivity extends AppCompatActivity {
    private RecyclerView mFilterRecyclerView;
    private RecyclerView.Adapter mFilterAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mFilterRecyclerView = (RecyclerView) findViewById(R.id.filter_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) mLayoutManager).setOrientation(LinearLayoutManager.VERTICAL);
        mFilterRecyclerView.setLayoutManager(mLayoutManager);

        List<String> eventTypes = FamilyMapModel.SINGLETON.getEventFilterTypes();


        mFilterAdapter = new FilterItemAdapter(eventTypes);
        mFilterRecyclerView.setAdapter(mFilterAdapter);


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

    /**
     * Inner class used to implement the recycler view for filters
     */
    private class FilterItemHolder extends RecyclerView.ViewHolder {
        private String mEventType;

        private TextView mFilterLabel;
        private TextView mFilterDescription;
        private Switch mFilterSwitch;

        public FilterItemHolder(View itemView) {
            super(itemView);

            mFilterLabel = (TextView) itemView.findViewById(R.id.event_filter_label);
            mFilterDescription = (TextView) itemView.findViewById(R.id.event_filter_description);
            mFilterSwitch = (Switch) itemView.findViewById(R.id.event_filter_switch);
            mFilterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    FamilyMapModel.SINGLETON.mFilters.setEventFilter(mEventType, isChecked);
                }
            });

        }

        public void bindFilter(String eventType) {
            mEventType = eventType;
            mFilterLabel.setText(eventType.substring(0, 1).toUpperCase() + eventType.substring(1) + " Event"); // Capitalize the event name
            mFilterDescription.setText("FILTER BY " + eventType.toUpperCase() + " EVENTS");
            mFilterSwitch.setChecked(FamilyMapModel.SINGLETON.mFilters.isEventFilterOn(mEventType));
        }
    }

    /**
     * Inner class used to help implement the filter recycler view
     */
    private class FilterItemAdapter extends RecyclerView.Adapter<FilterItemHolder> {
        private List<String> mEventTypes;

        public FilterItemAdapter(List<String> eventTypes) {
            mEventTypes = eventTypes;
        }

        @Override
        public FilterItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(FilterActivity.this);
            View view = inflater.inflate(R.layout.list_item_filter, parent, false);
            return new FilterItemHolder(view);
        }

        @Override
        public void onBindViewHolder(FilterItemHolder holder, int position) {
            String eventType = mEventTypes.get(position);
            holder.bindFilter(eventType);
        }

        @Override
        public int getItemCount() {
            return mEventTypes.size();
        }
    }

}
