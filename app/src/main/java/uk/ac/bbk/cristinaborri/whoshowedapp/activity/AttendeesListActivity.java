package uk.ac.bbk.cristinaborri.whoshowedapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import uk.ac.bbk.cristinaborri.whoshowedapp.R;

public class AttendeesListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendees_list);

//        EventDAO eventOperations = new EventDAO(this);
//        eventOperations.open();
//        //events = eventOperations.getAllEvents();
//        eventOperations.close();

//        EventsListItemAdapter adapter = new EventsListItemAdapter(this, events);
//
//        final ListView eventList = findViewById(uk.ac.bbk.cristinaborri.whoshowedapp.R.id.eventList);
//
//        eventList.setAdapter(adapter);
//        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Event listItem = (Event) eventList.getItemAtPosition(position);
////                Snackbar.make(view, listItem.toString(), Snackbar.LENGTH_LONG)
////                .setAction("Action", null).show();
//
//                Intent i = new Intent(AttendeesListActivity.this, EventViewActivity.class);
//                //i.putExtra(EXTRA_ADD_UPDATE, "Add");
//                startActivity(i);
//            }
//        });

    }
}
