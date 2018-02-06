package uk.ac.bbk.cristinaborri.whoshowedapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class EventOperationsTest {
    private static final String DATE_EVENT_1 = "10/12/2018";
    private static final String LOCATION_EVENT_1 = "bbk University";
    private static final String NAME_EVENT_1 = "My event 1";
    private static final String DETAILS_EVENT_1 = "details 1";
    private static final String DATE_EVENT_2 = "19/10/2018";
    private static final String LOCATION_EVENT_2 = "Lab";
    private static final String NAME_EVENT_2 = "My event 2";
    private static final String DETAILS_EVENT_2 = "details 2";
    private Context appContext;
    private Event event1;
    private Event event2;
    private EventOperations eventData;

    @Before
    public void setUp() throws Exception {
        // Context of the app under test.
        appContext = InstrumentationRegistry.getTargetContext();
        appContext.deleteDatabase("event.db");
        event1 = new Event();
        event1.setDate(DATE_EVENT_1);
        event1.setLocation(LOCATION_EVENT_1);
        event1.setName(NAME_EVENT_1);
        event1.setDetails(DETAILS_EVENT_1);
        event2 = new Event();
        event2.setDate(DATE_EVENT_2);
        event2.setLocation(LOCATION_EVENT_2);
        event2.setName(NAME_EVENT_2);
        event2.setDetails(DETAILS_EVENT_2);
        eventData = new EventOperations(appContext);
        eventData.open();
    }

    @After
    public void tearDown() throws Exception {
        eventData.close();
    }

    @Test
    public void useAppContext() throws Exception {
        assertEquals("uk.ac.bbk.cristinaborri.whoshowedapp", appContext.getPackageName());
    }

    @Test
    public void addAndGetEvent() throws Exception {
        eventData.addEvent(event1);
        Event foundEvent = eventData.getEvent(event1.getId());
        validateEvent1(foundEvent);
    }

    @Test
    public void getEventsList() throws Exception {
        eventData.addEvent(event1);
        eventData.addEvent(event2);
        List<Event> events = eventData.getAllEvents();
        assertEquals(2, events.size());
        validateEvent1(events.get(0));
        validateEvent2(events.get(1));
    }

    @Test
    public void deleteEvent() throws Exception {
        eventData.addEvent(event1);
        eventData.addEvent(event2);
        List<Event> startingEvents = eventData.getAllEvents();
        assertEquals(2, startingEvents.size());
        eventData.removeEvent(startingEvents.get(0));
        List<Event> events = eventData.getAllEvents();
        assertEquals(1, events.size());
        validateEvent2(events.get(0));
    }

    @Test
    public void updateEvent() throws Exception {
        eventData.addEvent(event1);
        Event event = eventData.getEvent(event1.getId());
        validateEvent1(event);
        event.setDate(DATE_EVENT_2);
        event.setLocation(LOCATION_EVENT_2);
        event.setName(NAME_EVENT_2);
        event.setDetails(DETAILS_EVENT_2);
        eventData.updateEvent(event);
        validateEvent2(event);
    }

    private void validateEvent1(Event event) {
        assertEquals(NAME_EVENT_1, event.getName());
        assertEquals(DATE_EVENT_1, event.getDate());
        assertEquals(LOCATION_EVENT_1, event.getLocation());
        assertEquals(DETAILS_EVENT_1, event.getDetails());
    }

    private void validateEvent2(Event event) {
        assertEquals(NAME_EVENT_2, event.getName());
        assertEquals(DATE_EVENT_2, event.getDate());
        assertEquals(LOCATION_EVENT_2, event.getLocation());
        assertEquals(DETAILS_EVENT_2, event.getDetails());
    }
}