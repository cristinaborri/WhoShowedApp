package uk.ac.bbk.cristinaborri.whoshowedapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import uk.ac.bbk.cristinaborri.whoshowedapp.EventDBHandler;

/**
 * Created by Cristina Borri
 */

public class EventOperations {

    public static final String LOGTAG = "EV_MNGMNT_SYS";

    SQLiteOpenHelper dbhandler;
    SQLiteDatabase database;

    private static final String[] allColumns = {
            EventDBHandler.COLUMN_ID,
            EventDBHandler.COLUMN_LOCATION,
            EventDBHandler.COLUMN_DATE,
            EventDBHandler.COLUMN_NAME,
            EventDBHandler.COLUMN_DETAILS
    };

    public EventOperations(Context context){
        dbhandler = new EventDBHandler(context);
    }

    public void open(){
        Log.i(LOGTAG,"Database Opened");
        database = dbhandler.getWritableDatabase();
    }

    public void close(){
        Log.i(LOGTAG, "Database Closed");
        dbhandler.close();
    }

    public Event addEvent(Event Event){
        ContentValues values  = new ContentValues();
        values.put(EventDBHandler.COLUMN_LOCATION,Event.getLocation());
        values.put(EventDBHandler.COLUMN_DATE,Event.getDate());
        values.put(EventDBHandler.COLUMN_NAME, Event.getName());
        values.put(EventDBHandler.COLUMN_DETAILS, Event.getDetails());
        long insertid = database.insert(EventDBHandler.TABLE_EVENT,null,values);
        Event.setId(insertid);
        return Event;
    }

    // Getting single Event
    public Event getEvent(long id) {

        Cursor cursor = database.query(EventDBHandler.TABLE_EVENT,allColumns,EventDBHandler.COLUMN_ID + "=?",new String[]{String.valueOf(id)},null,null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Event e = new Event(
                Long.parseLong(cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4)
        );
        // return Event
        return e;
    }

    public List<Event> getAllEvents() {

        Cursor cursor = database.query(EventDBHandler.TABLE_EVENT,allColumns,null,null,null, null, null);

        List<Event> events = new ArrayList<>();
        if(cursor.getCount() > 0){
            while(cursor.moveToNext()){
                Event event = new Event();
                event.setId(cursor.getLong(cursor.getColumnIndex(EventDBHandler.COLUMN_ID)));
                event.setLocation(cursor.getString(cursor.getColumnIndex(EventDBHandler.COLUMN_LOCATION)));
                event.setDate(cursor.getString(cursor.getColumnIndex(EventDBHandler.COLUMN_DATE)));
                event.setName(cursor.getString(cursor.getColumnIndex(EventDBHandler.COLUMN_NAME)));
                event.setDetails(cursor.getString(cursor.getColumnIndex(EventDBHandler.COLUMN_DETAILS)));

                events.add(event);
            }
        }
        // return All Events
        return events;
    }

    // Updating Event
    public int updateEvent(Event event) {

        ContentValues values = new ContentValues();
        values.put(EventDBHandler.COLUMN_LOCATION, event.getLocation());
        values.put(EventDBHandler.COLUMN_DATE, event.getDate());
        values.put(EventDBHandler.COLUMN_NAME, event.getName());
        values.put(EventDBHandler.COLUMN_DETAILS, event.getDetails());

        // updating row
        return database.update(EventDBHandler.TABLE_EVENT, values,
                EventDBHandler.COLUMN_ID + "=?",new String[] { String.valueOf(event.getId())});
    }

    // Deleting Event
    public void removeEvent(Event event) {
        database.delete(EventDBHandler.TABLE_EVENT, EventDBHandler.COLUMN_ID + "=" + event.getId(), null);
    }

}
