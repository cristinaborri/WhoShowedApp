package uk.ac.bbk.cristinaborri.whoshowedapp.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.nearby.connection.Strategy;

import java.util.List;

import uk.ac.bbk.cristinaborri.whoshowedapp.AttendanceService;
import uk.ac.bbk.cristinaborri.whoshowedapp.MainActivity;
import uk.ac.bbk.cristinaborri.whoshowedapp.R;
import uk.ac.bbk.cristinaborri.whoshowedapp.listAdapter.AttendanceItemAdapter;
import uk.ac.bbk.cristinaborri.whoshowedapp.model.Attendee;
import uk.ac.bbk.cristinaborri.whoshowedapp.model.AttendeeDAO;
import uk.ac.bbk.cristinaborri.whoshowedapp.model.Event;
import uk.ac.bbk.cristinaborri.whoshowedapp.model.EventDAO;

public class AttendanceActivity extends AppCompatActivity {

    private static final Strategy STRATEGY = Strategy.P2P_STAR;

    private static final String[] REQUIRED_PERMISSIONS =
            new String[] {
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };

    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;

    List<Attendee> attendees;

    private long eventID;
    private AttendanceItemAdapter attendanceItemAdapter;
    private AttendanceService attendanceService;

    @Override
    protected void onStart() {
        super.onStart();

        if (!hasPermissions(this, REQUIRED_PERMISSIONS)) {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
        }
    }

    @Override
    protected void onStop() {
        attendanceService.stopAdvertising();
        super.onStop();
    }

    /** Returns true if the app was granted all the permissions. Otherwise, returns false. */
    private static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /** Handles user acceptance (or denial) of our permission request. */
    @CallSuper
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != REQUEST_CODE_REQUIRED_PERMISSIONS) {
            return;
        }

        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Cannot run the application without all the required permissions", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }
        recreate();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        eventID = getIntent().getLongExtra(MainActivity.EXTRA_EVENT_ID, 0);

        EventDAO eventData = new EventDAO(this);
        eventData.open();
        Event event = eventData.getEvent(eventID);
        eventData.close();

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

        attendanceItemAdapter = new AttendanceItemAdapter(this, attendees);

        final ListView attendeesList = findViewById(R.id.attendance_list);

        attendeesList.setAdapter(attendanceItemAdapter);

        attendanceService = new AttendanceService(this);

        attendanceService.startAdvertising(event.getName());
    }

    public void recordAttendance(String uid) {
        AttendeeDAO attendeeOperations = new AttendeeDAO(this);
        attendeeOperations.open();

        Attendee attendee = attendeeOperations.getAttendeeByUId(uid);
        if (attendee != null && !attendee.hasAttended()) {
            attendee.attend();
            attendeeOperations.updateAttendee(attendee);
            attendees = attendeeOperations.getEventConfirmedAttendees(eventID);
            // update data in our adapter
            attendanceItemAdapter.clear();
            attendanceItemAdapter.addAll(attendees);
            // fire the event
            attendanceItemAdapter.notifyDataSetChanged();
        }
        attendeeOperations.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                attendanceService.stopAdvertising();
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
