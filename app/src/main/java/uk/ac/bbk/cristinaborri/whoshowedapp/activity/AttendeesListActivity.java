package uk.ac.bbk.cristinaborri.whoshowedapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import uk.ac.bbk.cristinaborri.whoshowedapp.MainActivity;
import uk.ac.bbk.cristinaborri.whoshowedapp.R;
import uk.ac.bbk.cristinaborri.whoshowedapp.listAdapter.AttendeesListItemAdapter;
import uk.ac.bbk.cristinaborri.whoshowedapp.model.Attendee;
import uk.ac.bbk.cristinaborri.whoshowedapp.model.AttendeeDAO;

public class AttendeesListActivity extends AppCompatActivity {
    List<Attendee> attendees;
    private long eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendee_list);

        eventID = getIntent().getLongExtra(MainActivity.EXTRA_EVENT_ID, 0);

        ActionBar toolbar = getSupportActionBar();
        if (toolbar != null) {
            toolbar.setTitle("Attendees");
            toolbar.setDisplayHomeAsUpEnabled(true);
            toolbar.setDisplayShowHomeEnabled(true);
        }

        FloatingActionButton fab = findViewById(R.id.add_attendee_fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AttendeesListActivity.this, AttendeeAddUpdateActivity.class);
                i.putExtra(MainActivity.EXTRA_ATTENDEE_ADD_UPDATE, "Add");
                i.putExtra(MainActivity.EXTRA_EVENT_ID, eventID);
                startActivity(i);
            }
        });

        AttendeeDAO attendeeOperations = new AttendeeDAO(this);
        attendeeOperations.open();
        attendees = attendeeOperations.getEventAttendees(eventID);
        attendeeOperations.close();

        AttendeesListItemAdapter adapter = new AttendeesListItemAdapter(this, attendees);

        final ListView attendeesList = findViewById(R.id.attendees_list);

        attendeesList.setAdapter(adapter);
        attendeesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Attendee listItem = (Attendee) attendeesList.getItemAtPosition(position);
            Intent i = new Intent(AttendeesListActivity.this, AttendeeViewActivity.class);
            i.putExtra(MainActivity.EXTRA_ATTENDEE_ID, listItem.getId());
            startActivity(i);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case android.R.id.home:
                i = new Intent(AttendeesListActivity.this, EventViewActivity.class);
                i.putExtra(MainActivity.EXTRA_EVENT_ID, eventID);
                startActivity(i);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
