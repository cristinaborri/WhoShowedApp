package uk.ac.bbk.cristinaborri.whoshowedapp.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Cristina Borri
 * This class will provide the operations that will allow to save update and load the attendees
 */
public class AttendeeDAO extends WsaDAO {
    private static final String[] allColumns = {
            DatabaseHelper.COLUMN_ID,
            DatabaseHelper.COLUMN_ATTENDEE_NAME,
            DatabaseHelper.COLUMN_ATTENDEE_EMAIL,
            DatabaseHelper.COLUMN_ATTENDEE_EVENT_ID,
            DatabaseHelper.COLUMN_ATTENDEE_UID,
            DatabaseHelper.COLUMN_ATTENDEE_UPDATED_ON,
            DatabaseHelper.COLUMN_ATTENDEE_ATTENDED
    };

    public AttendeeDAO(Context context){
        dbHandler = new DatabaseHelper(context);
    }
    
    public void addAttendee(Attendee attendee){
        ContentValues values = prepareAttendeeContentValues(attendee);
        long insertId = database.insert(DatabaseHelper.TABLE_ATTENDEE,null,values);
        attendee.setId(insertId);
    }

    // Getting single Attendee
    public Attendee getAttendee(long id) {

        Cursor cursor = database.query(
                DatabaseHelper.TABLE_ATTENDEE,
                allColumns,
                DatabaseHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                null
        );
        if (cursor != null) {
            cursor.moveToFirst();

            Attendee e = this.attendeeFromCursor(cursor);
            cursor.close();
            return e;
        }
        return new Attendee();
    }

    public List<Attendee> getEventAttendees(long eventId) {

        Cursor cursor = database.query(
                DatabaseHelper.TABLE_ATTENDEE,
                allColumns,
                DatabaseHelper.COLUMN_ATTENDEE_EVENT_ID + "=?",
                new String[]{String.valueOf(eventId)},
                null,
                null,
                null
        );

        List<Attendee> events = new ArrayList<>();
        if(cursor.getCount() > 0){
            while(cursor.moveToNext()){
                events.add(this.attendeeFromCursor(cursor));
            }
        }
        // return All Attendees
        return events;
    }

    public List<Attendee> getEventConfirmedAttendees(long eventId) {

        Cursor cursor = database.query(
                DatabaseHelper.TABLE_ATTENDEE,
                allColumns,
                DatabaseHelper.COLUMN_ATTENDEE_EVENT_ID + "=? AND " + DatabaseHelper.COLUMN_ATTENDEE_ATTENDED + "=1",
                new String[]{String.valueOf(eventId)},
                null,
                null,
                DatabaseHelper.COLUMN_ATTENDEE_UPDATED_ON
        );

        List<Attendee> events = new ArrayList<>();
        if(cursor.getCount() > 0){
            while(cursor.moveToNext()){
                events.add(this.attendeeFromCursor(cursor));
            }
        }
        // return All Attendees
        return events;
    }

    // Updating Attendee
    public void updateAttendee(Attendee attendee) {
        ContentValues values = prepareAttendeeContentValues(attendee);
        // updating row
        database.update(DatabaseHelper.TABLE_ATTENDEE, values,
                DatabaseHelper.COLUMN_ID + "=?",new String[] { String.valueOf(attendee.getId())});
    }

    // Deleting Attendee
    public void removeAttendee(Attendee attendee) {
        database.delete(DatabaseHelper.TABLE_ATTENDEE, DatabaseHelper.COLUMN_ID + "=" + attendee.getId(), null);
    }

    // Deleting Event Attendees
    public void removeEventAttendees(long eventId) {
        database.delete(DatabaseHelper.TABLE_ATTENDEE, DatabaseHelper.COLUMN_ATTENDEE_EVENT_ID + "=" + eventId, null);
    }

    @NonNull
    private ContentValues prepareAttendeeContentValues(Attendee attendee) {
        ContentValues values  = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ATTENDEE_NAME, attendee.getName());
        values.put(DatabaseHelper.COLUMN_ATTENDEE_EMAIL, attendee.getEmail());
        values.put(DatabaseHelper.COLUMN_ATTENDEE_UID, attendee.getUniqueId());
        Date updatedOn = attendee.getUpdatedOn() != null ? attendee.getUpdatedOn() : new Date();
        values.put(DatabaseHelper.COLUMN_ATTENDEE_UPDATED_ON, updatedOn.getTime());
        values.put(DatabaseHelper.COLUMN_ATTENDEE_ATTENDED, attendee.hasAttended() ? 1 : 0);
        values.put(DatabaseHelper.COLUMN_ATTENDEE_EVENT_ID, attendee.getEventId());
        return values;
    }

    private Attendee attendeeFromCursor(Cursor cursor) {
        Attendee attendee = new Attendee();
        attendee.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
        attendee.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ATTENDEE_NAME)));
        attendee.setEmail(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ATTENDEE_EMAIL)));
        attendee.setUniqueId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ATTENDEE_UID)));
        attendee.setUpdatedOn(new Date(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ATTENDEE_UPDATED_ON))));
        attendee.setAttended(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ATTENDEE_ATTENDED)) == 1);
        attendee.setEventId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ATTENDEE_EVENT_ID)));
        return attendee;
    }
 }
