package uk.ac.bbk.cristinaborri.whoshowedapp;

import android.support.annotation.NonNull;
import android.util.Log;
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

import java.util.Objects;

import uk.ac.bbk.cristinaborri.whoshowedapp.activity.AttendanceActivity;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AttendanceService {

    private final AttendanceActivity activity;

    private final AdvertisingOptions mAdvertisingOptions;
    private ConnectionsClient mConnectionsClient;

    private static final String TAG = "WhoShowedApp.Attendance";
    private static final Strategy STRATEGY = Strategy.P2P_STAR;

    public AttendanceService(AttendanceActivity targetActivity) {
        activity = targetActivity;
        mConnectionsClient = Nearby.getConnectionsClient(activity);
        mAdvertisingOptions = (new AdvertisingOptions.Builder()).setStrategy(STRATEGY).build();
    }

    // Callbacks for receiving payloads
    private final PayloadCallback mPayloadCallback =
            new PayloadCallback() {
                @Override
                public void onPayloadReceived(@NonNull String endpointId, Payload payload) {
                    Log.i(TAG, "connection: received: " + new String(Objects.requireNonNull(payload.asBytes()), UTF_8));
                    activity.recordAttendance(new String(Objects.requireNonNull(payload.asBytes()), UTF_8));
                    mConnectionsClient.sendPayload(endpointId, payload);
                }

                @Override
                public void onPayloadTransferUpdate(@NonNull String endpointId, @NonNull PayloadTransferUpdate update) {
                }
            };

    private final ConnectionLifecycleCallback mConnectionLifecycleCallback =
            new ConnectionLifecycleCallback() {

                @Override
                public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
                    Log.i(TAG, "connection: accepting connection, id: " + endpointId);
                    // Automatically accept the connection on both sides.
                    mConnectionsClient.acceptConnection(endpointId, mPayloadCallback);
                }

                @Override
                public void onConnectionResult(@NonNull String endpointId, ConnectionResolution result) {
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

    public void startAdvertising(String serviceName) {

        mConnectionsClient.startAdvertising(
                "register",
                serviceName,
                mConnectionLifecycleCallback,
                mAdvertisingOptions)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unusedResult) {
                                // We're advertising!
                                Toast t = Toast.makeText(
                                        activity, "Listening",
                                        Toast.LENGTH_SHORT
                                );
                                t.show();
                                Log.i(TAG, "connection: advertising endpoint");
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "connection: error advertising endpoint: " + e.getMessage());
                                // We were unable to start advertising.
                                Toast t = Toast.makeText(
                                        activity, "Failed to start listening for attendees",
                                        Toast.LENGTH_SHORT
                                );
                                t.show();
                            }
                        });
    }

    public void stopAdvertising() {
        Log.i(TAG, "connection: stop advertising");
        mConnectionsClient.stopAdvertising();
        mConnectionsClient.stopAllEndpoints();
    }
}