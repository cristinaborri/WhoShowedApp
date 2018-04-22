package uk.ac.bbk.cristinaborri.whoshowedapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import uk.ac.bbk.cristinaborri.whoshowedapp.activity.EventAddUpdateActivity;
import uk.ac.bbk.cristinaborri.whoshowedapp.listAdapter.EventsListItemAdapter;
import uk.ac.bbk.cristinaborri.whoshowedapp.activity.EventViewActivity;
import uk.ac.bbk.cristinaborri.whoshowedapp.model.Event;
import uk.ac.bbk.cristinaborri.whoshowedapp.model.EventDAO;

/**
 * Created by Cristina Borri
 * This is the main activity of the application
 */

public class MainActivity extends AppCompatActivity {

    List<Event> events;
    public static final String EXTRA_EVENT_ID = "CristinaBorri.wsa.Event.Id";
    public static final String EXTRA_EVENT_ADD_UPDATE = "CristinaBorri.wsa.Event.AddEdit";
    public static final String EXTRA_ATTENDEE_ID = "CristinaBorri.wsa.Attendee.Id";
    public static final String EXTRA_ATTENDEE_ADD_UPDATE = "CristinaBorri.wsa.Attendee.AddEdit";
    public static final String DATE_FORMAT = "dd/MM/yyyy HH:mm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Events");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.add_event_fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent i = new Intent(MainActivity.this, EventAddUpdateActivity.class);
            i.putExtra(EXTRA_EVENT_ADD_UPDATE, "Add");
            startActivity(i);
            }
        });

        EventDAO eventOperations = new EventDAO(this);
        eventOperations.open();
        events = eventOperations.getAllEvents();
        eventOperations.close();

        EventsListItemAdapter adapter = new EventsListItemAdapter(this, events);

        final ListView eventList = findViewById(uk.ac.bbk.cristinaborri.whoshowedapp.R.id.eventList);

        eventList.setAdapter(adapter);
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Event listItem = (Event) eventList.getItemAtPosition(position);

            Intent i = new Intent(MainActivity.this, EventViewActivity.class);
            i.putExtra(EXTRA_EVENT_ID, listItem.getId());

            startActivity(i);
            }
        });
    }
}
