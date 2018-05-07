package uk.ac.bbk.cristinaborri.whoshowedapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import uk.ac.bbk.cristinaborri.whoshowedapp.MainActivity;
import uk.ac.bbk.cristinaborri.whoshowedapp.R;
import uk.ac.bbk.cristinaborri.whoshowedapp.model.Attendee;
import uk.ac.bbk.cristinaborri.whoshowedapp.model.AttendeeDAO;

/**
 * Created by Cristina Borri
 * This class is the activity called to update or create an attendee
 */

public class AttendeeAddUpdateActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText emailEditText;
    private Attendee attendee;
    private AttendeeDAO attendeeData;
    private String mode;
    private long eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendee_add_update);

        attendee = new Attendee();
        nameEditText = findViewById(R.id.edit_attendee_name);
        emailEditText = findViewById(R.id.edit_attendee_email);

        attendeeData = new AttendeeDAO(this);
        attendeeData.open();

        mode = getIntent().getStringExtra(MainActivity.EXTRA_ATTENDEE_ADD_UPDATE);
        String toolbarTitle = "Add Attendee";

        if(mode.equals("Add")){
            eventID = getIntent().getLongExtra(MainActivity.EXTRA_EVENT_ID, 0);
            attendee.init(eventID);
        }

        if(mode.equals("Update")){
            toolbarTitle = "Update Attendee";
            long attendeeID = getIntent().getLongExtra(MainActivity.EXTRA_ATTENDEE_ID, 0);
            initializeAttendee(attendeeID);
            eventID = attendee.getEventId();
        }

        ActionBar toolbar = getSupportActionBar();
        if (toolbar != null) {
            toolbar.setTitle(toolbarTitle);
            toolbar.setDisplayHomeAsUpEnabled(true);
            toolbar.setDisplayShowHomeEnabled(true);
        }
    }

    private void saveAttendee() {
        attendee.setName(nameEditText.getText().toString());
        attendee.setEmail(emailEditText.getText().toString());
        if(mode.equals("Add")) {
            attendeeData.addAttendee(attendee);
            attendeeData.close();
            addSuccessToast();
            Intent i = new Intent(AttendeeAddUpdateActivity.this, AttendeesListActivity.class);
            i.putExtra(MainActivity.EXTRA_EVENT_ID, eventID);
            startActivity(i);
        } else {
            attendeeData.updateAttendee(attendee);
            attendeeData.close();
            addSuccessToast();
            Intent i = new Intent(AttendeeAddUpdateActivity.this, AttendeesListActivity.class);
            i.putExtra(MainActivity.EXTRA_EVENT_ID, eventID);
            startActivity(i);
        }
    }

    private void addSuccessToast() {
        String action = "added";
        if(mode.equals("Update")) {
            action = "updated";
        }
        Toast t = Toast.makeText(
                AttendeeAddUpdateActivity.this, "Attendee "+ attendee.getName() + " has been " + action + " successfully!",
                Toast.LENGTH_SHORT
        );
        t.show();
    }

    private void initializeAttendee(long attendeeId) {
        attendee = attendeeData.getAttendee(attendeeId);
        nameEditText.setText(attendee.getName());
        emailEditText.setText(attendee.getEmail());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_attendee_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                attendeeData.close();
                onBackPressed();
                return true;
            case R.id.save_attendee:
                saveAttendee();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}