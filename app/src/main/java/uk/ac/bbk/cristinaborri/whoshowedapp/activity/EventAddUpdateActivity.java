package uk.ac.bbk.cristinaborri.whoshowedapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import uk.ac.bbk.cristinaborri.whoshowedapp.MainActivity;
import uk.ac.bbk.cristinaborri.whoshowedapp.R;
import uk.ac.bbk.cristinaborri.whoshowedapp.model.Event;
import uk.ac.bbk.cristinaborri.whoshowedapp.model.EventDAO;

/**
 * Created by Cristina Borri
 * This class is the activity called to update or create an event
 */

public class EventAddUpdateActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText dateEditText;
    private EditText detailsEditText;
    private PlaceAutocompleteFragment autocompleteFragment;
    private Event event;
    private EventDAO eventData;
    private String mode;
    private long eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(uk.ac.bbk.cristinaborri.whoshowedapp.R.layout.activity_event_add_update);

        event = new Event();
        nameEditText = findViewById(R.id.edit_event_name);
        dateEditText = findViewById(R.id.edit_event_date);
        detailsEditText = findViewById(R.id.edit_event_details);
        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.location_autocomplete);

        eventData = new EventDAO(this);
        eventData.open();

        mode = getIntent().getStringExtra(MainActivity.EXTRA_EVENT_ADD_UPDATE);
        String toolbarTitle = "Add Event";
        dateEditText.setText(new SimpleDateFormat(MainActivity.DATE_FORMAT, Locale.UK).format(new Date()));
        if(mode.equals("Update")){
            toolbarTitle = "Update Event";
            eventID = getIntent().getLongExtra(MainActivity.EXTRA_EVENT_ID, 0);
            initializeEvent(eventID);
        }

        ActionBar toolbar = getSupportActionBar();
        if (toolbar != null) {
            toolbar.setTitle(toolbarTitle);
            toolbar.setDisplayHomeAsUpEnabled(true);
            toolbar.setDisplayShowHomeEnabled(true);
        }

        autocompleteFragment.setHint("Select a place for the Event");
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                event.setLocationName(place.getName().toString());
                event.setLocationAddress(place.getAddress().toString());
                event.setLocationViewPort(place.getViewport());
                event.setLocationCoordinates(place.getLatLng());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("tag", "An error occurred: " + status);
            }
        });
    }

    private void saveEvent() {
        event.setName(nameEditText.getText().toString());
        setEventDateFromEditText(event);
        event.setDetails(detailsEditText.getText().toString());
        if(mode.equals("Add")) {
            eventData.addEvent(event);
            eventData.close();
            addSuccessToast();
            Intent i = new Intent(EventAddUpdateActivity.this, MainActivity.class);
            startActivity(i);
        } else {
            eventData.updateEvent(event);
            eventData.close();
            addSuccessToast();
            Intent i = new Intent(EventAddUpdateActivity.this, EventViewActivity.class);
            i.putExtra(MainActivity.EXTRA_EVENT_ID, eventID);
            startActivity(i);
        }
    }

    private void setEventDateFromEditText(Event event) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(MainActivity.DATE_FORMAT, Locale.UK);
        try {
            event.setDate(dateFormat.parse(dateEditText.getText().toString()));
        } catch (ParseException e) {
            // TODO: Handle the exception.
            Log.i("tag", "An error occurred: " + e.getMessage());
        }
    }

    private void addSuccessToast() {
        String action = "added";
        if(mode.equals("Update")) {
            action = "updated";
        }
        Toast t = Toast.makeText(
                EventAddUpdateActivity.this, "Event "+ event.getName() + " has been " + action + " successfully!",
                Toast.LENGTH_SHORT
        );
        t.show();
    }

    private void initializeEvent(long eventId) {
        event = eventData.getEvent(eventId);
        nameEditText.setText(event.getName());
        dateEditText.setText(new SimpleDateFormat(MainActivity.DATE_FORMAT, Locale.UK).format(event.getDate()));
        detailsEditText.setText(event.getDetails());
        autocompleteFragment.setText(event.getLocationName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                eventData.close();
                onBackPressed();
                return true;
            case R.id.save_event:
                saveEvent();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}