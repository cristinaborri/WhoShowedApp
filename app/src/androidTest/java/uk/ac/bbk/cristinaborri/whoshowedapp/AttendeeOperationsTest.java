package uk.ac.bbk.cristinaborri.whoshowedapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import uk.ac.bbk.cristinaborri.whoshowedapp.model.Attendee;
import uk.ac.bbk.cristinaborri.whoshowedapp.model.AttendeeDAO;
import uk.ac.bbk.cristinaborri.whoshowedapp.model.DatabaseHelper;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class AttendeeOperationsTest {
    private static final String NAME_ATTENDEE_1 = "My event 1";
    private static final String EMAIL_ATTENDEE_1 = "details 1";

    private static final String NAME_ATTENDEE_2 = "My event 2";
    private static final String EMAIL_ATTENDEE_2 = "details 2";

    private static final long EVENT_ID_1 = 1;
    private static final long EVENT_ID_2 = 2;

    private Context appContext;
    private Attendee attendee1;
    private Attendee attendee2;
    private AttendeeDAO attendeeData;

    @Before
    public void setUp() throws Exception {
        // Context of the app under test.
        appContext = InstrumentationRegistry.getTargetContext();
        appContext.deleteDatabase(DatabaseHelper.DATABASE_NAME);
        attendee1 = new Attendee();
        attendee1.setName(NAME_ATTENDEE_1);
        attendee1.setEmail(EMAIL_ATTENDEE_1);
        attendee1.init(EVENT_ID_1);
        attendee2 = new Attendee();
        attendee2.setName(NAME_ATTENDEE_2);
        attendee2.setEmail(EMAIL_ATTENDEE_2);
        attendee2.init(EVENT_ID_1);
        attendeeData = new AttendeeDAO(appContext);
        attendeeData.open();
    }

    @After
    public void tearDown() throws Exception {
        attendeeData.close();
    }

    @Test
    public void useAppContext() throws Exception {
        assertEquals("uk.ac.bbk.cristinaborri.whoshowedapp", appContext.getPackageName());
    }

    @Test
    public void addAndGetAttendee() throws Exception {
        attendeeData.addAttendee(attendee1);
        Attendee foundAttendee = attendeeData.getAttendee(attendee1.getId());
        validateAttendee1(foundAttendee);
        assertEquals(EVENT_ID_1, foundAttendee.getEventId());
    }

    @Test
    public void getAttendeesList() throws Exception {

        attendeeData.addAttendee(attendee1);
        attendeeData.addAttendee(attendee2);
        List<Attendee> events = attendeeData.getEventAttendees(EVENT_ID_1);
        assertEquals(2, events.size());
        validateAttendee1(events.get(0));
        validateAttendee2(events.get(1));
        assertEquals(EVENT_ID_1, events.get(0).getEventId());
        assertEquals(EVENT_ID_1, events.get(1).getEventId());
    }

    @Test
    public void deleteAttendee() throws Exception {
        attendeeData.addAttendee(attendee1);
        attendeeData.addAttendee(attendee2);
        List<Attendee> startingAttendees = attendeeData.getEventAttendees(EVENT_ID_1);
        assertEquals(2, startingAttendees.size());
        attendeeData.removeAttendee(startingAttendees.get(0));
        List<Attendee> attendees = attendeeData.getEventAttendees(EVENT_ID_1);
        assertEquals(1, attendees.size());
        validateAttendee2(attendees.get(0));
    }

    @Test
    public void deleteEventAttendees() throws Exception {
        attendeeData.addAttendee(attendee1);
        attendeeData.addAttendee(attendee2);
        List<Attendee> startingAttendees = attendeeData.getEventAttendees(EVENT_ID_1);
        assertEquals(2, startingAttendees.size());
        attendeeData.removeEventAttendees(EVENT_ID_1);
        List<Attendee> attendees = attendeeData.getEventAttendees(EVENT_ID_1);
        assertEquals(0, attendees.size());
    }

    @Test
    public void updateAttendee() throws Exception {
        attendeeData.addAttendee(attendee1);
        Attendee attendee = attendeeData.getAttendee(attendee1.getId());
        validateAttendee1(attendee);
        attendee.setName(NAME_ATTENDEE_2);
        attendee.setEmail(EMAIL_ATTENDEE_2);
        attendee.setEventId(EVENT_ID_2);
        attendeeData.updateAttendee(attendee);
        validateAttendee2(attendee);
        assertEquals(EVENT_ID_2, attendee.getEventId());
    }

    private void validateAttendee1(Attendee attendee) {
        assertEquals(NAME_ATTENDEE_1, attendee.getName());
        assertEquals(EMAIL_ATTENDEE_1, attendee.getEmail());
    }

    private void validateAttendee2(Attendee attendee) {
        assertEquals(NAME_ATTENDEE_2, attendee.getName());
        assertEquals(EMAIL_ATTENDEE_2, attendee.getEmail());
    }
}