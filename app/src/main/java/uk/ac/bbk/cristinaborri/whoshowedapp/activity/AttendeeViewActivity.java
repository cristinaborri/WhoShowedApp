package uk.ac.bbk.cristinaborri.whoshowedapp.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import uk.ac.bbk.cristinaborri.whoshowedapp.MainActivity;
import uk.ac.bbk.cristinaborri.whoshowedapp.R;
import uk.ac.bbk.cristinaborri.whoshowedapp.model.Attendee;
import uk.ac.bbk.cristinaborri.whoshowedapp.model.AttendeeDAO;
import uk.ac.bbk.cristinaborri.whoshowedapp.model.Event;
import uk.ac.bbk.cristinaborri.whoshowedapp.model.EventDAO;

/**
 * Created by cristinaborri.
 *
 */

public class AttendeeViewActivity extends AppCompatActivity {

    private long attendeeId;
    private Attendee attendee;
    private AttendeeDAO attendeeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendee_view);
        ActionBar toolbar = getSupportActionBar();
        if (toolbar != null) {
            toolbar.setTitle("Attendee");
            toolbar.setDisplayHomeAsUpEnabled(true);
            toolbar.setDisplayShowHomeEnabled(true);
        }

        attendeeId = getIntent().getLongExtra(MainActivity.EXTRA_ATTENDEE_ID,0);
        EventDAO eventData = new EventDAO(this);
        attendeeData = new AttendeeDAO(this);
        attendeeData.open();
        eventData.open();

        attendee = attendeeData.getAttendee(attendeeId);
        Event event = eventData.getEvent(attendee.getEventId());
        TextView attendeeName = findViewById(R.id.view_attendee_name);
        attendeeName.setText(attendee != null ? attendee.getName() : null);
        TextView attendeeEmail = findViewById(R.id.view_attendee_email);
        attendeeEmail.setText(attendee != null ? attendee.getEmail() : null);
        TextView attendeeCode = findViewById(R.id.view_attendee_code);
        attendeeCode.setText(attendee != null ? attendee.generateUniqueCode(event) : null);
        attendeeData.close();

        Button clipboardButton = findViewById(R.id.copy_to_clipboard);
        clipboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView attendeeCode = findViewById(R.id.view_attendee_code);
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("ISH_CODE", attendeeCode.getText());
                assert clipboard != null;
                clipboard.setPrimaryClip(clip);
                Toast t = Toast.makeText(
                        AttendeeViewActivity.this, "Code copied to clipboard",
                        Toast.LENGTH_SHORT
                );
                t.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_attendee_show, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case android.R.id.home:
                i = new Intent(AttendeeViewActivity.this, AttendeesListActivity.class);
                i.putExtra(MainActivity.EXTRA_EVENT_ID, attendee.getEventId());
                startActivity(i);
                return true;
            case R.id.delete_attendee_menu_item:
                showDeleteDialog();
                return true;
            case R.id.edit_attendee_menu_item:
                i = new Intent(AttendeeViewActivity.this, AttendeeAddUpdateActivity.class);
                i.putExtra(MainActivity.EXTRA_ATTENDEE_ADD_UPDATE, "Update");
                i.putExtra(MainActivity.EXTRA_ATTENDEE_ID, attendeeId);
                startActivity(i);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDeleteDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AttendeeViewActivity.this);
        alertBuilder.setMessage("Do you want to delete the attendee?");
        alertBuilder.setCancelable(true);

        alertBuilder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        attendeeData.open();
                        attendeeData.removeAttendee(attendee);
                        attendeeData.close();
                        addDeleteToast();
                        Intent i = new Intent(AttendeeViewActivity.this, AttendeesListActivity.class);
                        i.putExtra(MainActivity.EXTRA_EVENT_ID, attendee.getEventId());
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
            AttendeeViewActivity.this, "Attendee "+ attendee.getName() + " has been removed successfully!",
            Toast.LENGTH_SHORT
        );
        t.show();
    }
}