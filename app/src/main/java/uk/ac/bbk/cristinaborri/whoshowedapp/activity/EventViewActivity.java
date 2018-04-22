package uk.ac.bbk.cristinaborri.whoshowedapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Locale;

import uk.ac.bbk.cristinaborri.whoshowedapp.MainActivity;
import uk.ac.bbk.cristinaborri.whoshowedapp.R;
import uk.ac.bbk.cristinaborri.whoshowedapp.model.AttendeeDAO;
import uk.ac.bbk.cristinaborri.whoshowedapp.model.Event;
import uk.ac.bbk.cristinaborri.whoshowedapp.model.EventDAO;

/**
 * Created by cristinaborri.
 *
 */

public class EventViewActivity extends AppCompatActivity implements OnMapReadyCallback {

    private long eventID;
    private AttendeeDAO attendeeData;
    private EventDAO eventData;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(uk.ac.bbk.cristinaborri.whoshowedapp.R.layout.activity_event_view);
        ActionBar toolbar = getSupportActionBar();
        if (toolbar != null) {
            toolbar.setTitle("Event");
            toolbar.setDisplayHomeAsUpEnabled(true);
            toolbar.setDisplayShowHomeEnabled(true);
        }

        eventID = getIntent().getLongExtra(MainActivity.EXTRA_EVENT_ID,0);
        attendeeData = new AttendeeDAO(this);
        eventData = new EventDAO(this);
        eventData.open();
        event = eventData.getEvent(eventID);
        TextView eventName = findViewById(R.id.view_event_name);
        eventName.setText(event != null ? event.getName() : null);
        TextView eventDate = findViewById(R.id.view_event_date);
        eventDate.setText(event != null ? new SimpleDateFormat(MainActivity.DATE_FORMAT, Locale.UK).format(event.getDate()) : null);
        TextView eventDetails = findViewById(R.id.view_event_details);
        eventDetails.setText(event != null ? event.getDetails() : null);
        TextView eventLocation = findViewById(R.id.view_event_location);
        eventLocation.setText(event != null ? event.getLocationName() : null);
        TextView eventAddress = findViewById(R.id.view_event_address);
        eventAddress.setText(event != null ? event.getLocationAddress() : null);
        eventData.close();

        FloatingActionButton fab = findViewById(R.id.start_event_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EventViewActivity.this, AttendanceActivity.class);
                i.putExtra(MainActivity.EXTRA_EVENT_ID, eventID);
                startActivity(i);
            }
        });

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(uk.ac.bbk.cristinaborri.whoshowedapp.R.menu.menu_event_show, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case android.R.id.home:
                i = new Intent(EventViewActivity.this, MainActivity.class);
                startActivity(i);
                return true;
            case R.id.delete_event_menu_item:
                showDeleteDialog();
                return true;
            case R.id.show_attendees_menu_item:
                i = new Intent(EventViewActivity.this, AttendeesListActivity.class);
                i.putExtra(MainActivity.EXTRA_EVENT_ID, eventID);
                startActivity(i);
                return true;
            case R.id.edit_event_menu_item:
                i = new Intent(EventViewActivity.this, EventAddUpdateActivity.class);
                i.putExtra(MainActivity.EXTRA_EVENT_ID, eventID);
                i.putExtra(MainActivity.EXTRA_EVENT_ADD_UPDATE, "Update");
                startActivity(i);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDeleteDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(EventViewActivity.this);
        alertBuilder.setMessage("Do you want to delete the event?");
        alertBuilder.setCancelable(true);

        alertBuilder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        attendeeData.removeEventAttendees(event.getId());
                        eventData.removeEvent(event);
                        addDeleteToast();
                        Intent i = new Intent(EventViewActivity.this, MainActivity.class);
                        startActivity(i);
                    }
                });

        alertBuilder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog removeAlert = alertBuilder.create();
        removeAlert.show();
    }

    private void addDeleteToast() {
        Toast t = Toast.makeText(
                EventViewActivity.this, "Event "+ event.getName() + " has been removed successfully!",
                Toast.LENGTH_SHORT
        );
        t.show();
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we
     * just add a marker near Africa.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions().position(event.getLocationCoordinates()).title("Marker"));
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(event.getLocationViewPort(), 0));
    }
}