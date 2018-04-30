package uk.ac.bbk.cristinaborri.whoshowedapp.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import java.util.Objects;

import uk.ac.bbk.cristinaborri.whoshowedapp.MainActivity;
import uk.ac.bbk.cristinaborri.whoshowedapp.R;
import uk.ac.bbk.cristinaborri.whoshowedapp.listAdapter.AttendanceItemAdapter;
import uk.ac.bbk.cristinaborri.whoshowedapp.model.Attendee;
import uk.ac.bbk.cristinaborri.whoshowedapp.model.AttendeeDAO;
import uk.ac.bbk.cristinaborri.whoshowedapp.model.Event;
import uk.ac.bbk.cristinaborri.whoshowedapp.model.EventDAO;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AttendanceActivity extends AppCompatActivity {

    private static final String TAG = "WhoShowedApp.Attendance";
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
    private ConnectionsClient mConnectionsClient;
    private Event event;

    @Override
    protected void onStart() {
        super.onStart();

        if (!hasPermissions(this, REQUIRED_PERMISSIONS)) {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
        }
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
    protected void onStop() {
        mConnectionsClient.stopAllEndpoints();

        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        eventID = getIntent().getLongExtra(MainActivity.EXTRA_EVENT_ID, 0);

        EventDAO eventData = new EventDAO(this);
        eventData.open();
        event = eventData.getEvent(eventID);
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

        AttendanceItemAdapter adapter = new AttendanceItemAdapter(this, attendees);

        final ListView attendeesList = findViewById(R.id.attendance_list);

        attendeesList.setAdapter(adapter);

        mConnectionsClient = Nearby.getConnectionsClient(this);
        startAdvertising();
    }

    private void recordAttendance(String uid) {
        AttendeeDAO attendeeOperations = new AttendeeDAO(this);
        attendeeOperations.open();

        Attendee attendee = attendeeOperations.getAttendeeByUId(uid);
        if (attendee != null && !attendee.hasAttended()) {
            attendee.setAttended(true);
            attendeeOperations.updateAttendee(attendee);
            attendees = attendeeOperations.getEventConfirmedAttendees(eventID);
        }
        attendeeOperations.close();
    }

    // Callbacks for receiving payloads
    private final PayloadCallback mPayloadCallback =
            new PayloadCallback() {
                @Override
                public void onPayloadReceived(@NonNull String endpointId, Payload payload) {
                    //recordAttendance(new String(Objects.requireNonNull(payload.asBytes()), UTF_8));

                    Log.i(TAG, "connection: received: " + new String(Objects.requireNonNull(payload.asBytes()), UTF_8));

                }

                @Override
                public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) { }
            };

    private final ConnectionLifecycleCallback mConnectionLifecycleCallback =
            new ConnectionLifecycleCallback() {

                @Override
                public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo)
                {
                    // Automatically accept the connection on both sides.
                    mConnectionsClient.acceptConnection(endpointId, mPayloadCallback);
                }

                @Override
                public void onConnectionResult(@NonNull String endpointId, ConnectionResolution result)
                {
                    if (result.getStatus().isSuccess()) {
                        Log.i(TAG, "connection: connection successful");
                    } else {
                        Log.i(TAG, "connection: connection failed");
                    }
                }

                @Override
                public void onDisconnected(@NonNull String endpointId) {
                    // We've been disconnected from this endpoint. No more data can be
                    // sent or received.
                    Log.i(TAG, "connection: disconnected");
                }
            };

    private void startAdvertising() {
        mConnectionsClient.startAdvertising(
                "register",
                event.getName(),
                mConnectionLifecycleCallback,
                new AdvertisingOptions(STRATEGY))
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unusedResult) {
                                // We're advertising!
                                Toast t = Toast.makeText(
                                        AttendanceActivity.this, "Listening",
                                        Toast.LENGTH_SHORT
                                );
                                t.show();
                                Log.i(TAG, "connection: advertising endpoint, service_id :"+event.getName());
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "connection: error advertising endpoint: "+ e.getMessage());
                                // We were unable to start advertising.
                                Toast t = Toast.makeText(
                                        AttendanceActivity.this, "Failed to start listening for attendees",
                                        Toast.LENGTH_SHORT
                                );
                                t.show();
                            }
                        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case android.R.id.home:
                mConnectionsClient.stopAdvertising();
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
