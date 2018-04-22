package uk.ac.bbk.cristinaborri.whoshowedapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.List;

import uk.ac.bbk.cristinaborri.whoshowedapp.MainActivity;
import uk.ac.bbk.cristinaborri.whoshowedapp.R;
import uk.ac.bbk.cristinaborri.whoshowedapp.listAdapter.AttendanceItemAdapter;
import uk.ac.bbk.cristinaborri.whoshowedapp.model.Attendee;
import uk.ac.bbk.cristinaborri.whoshowedapp.model.AttendeeDAO;

public class AttendanceActivity extends AppCompatActivity {
    List<Attendee> attendees;
    private long eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        eventID = getIntent().getLongExtra(MainActivity.EXTRA_EVENT_ID, 0);

        ActionBar toolbar = getSupportActionBar();
        if (toolbar != null) {
            toolbar.setTitle("Recording attendance");
            toolbar.setDisplayHomeAsUpEnabled(true);
            toolbar.setDisplayShowHomeEnabled(true);
        }

        AttendeeDAO attendeeOperations = new AttendeeDAO(this);
        attendeeOperations.open();
        attendees = attendeeOperations.getEventConfirmedAttendees(eventID);
        attendeeOperations.close();

        AttendanceItemAdapter adapter = new AttendanceItemAdapter(this, attendees);

        final ListView attendeesList = findViewById(R.id.attendance_list);

        attendeesList.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
