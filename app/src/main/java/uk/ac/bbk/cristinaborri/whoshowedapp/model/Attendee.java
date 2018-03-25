package uk.ac.bbk.cristinaborri.whoshowedapp.model;

/**
 * Created by Cristina Borri
 * This class represents the event and it's persisted using EventDAO
 */

public class Attendee {
    /**
     * This is the (database) identifier of the attendee
     */
    private long id;
    /**
     * This is the name of the attendee
     */
    private String name;
    /**
     * These is the email of the attendee
     */
    private String email;
    /**
     * This is the (database) identifier of the event to attend
     */
    private long eventId;

    /**
     * This constructor will create en empty attendee
     */
    public Attendee() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    @Override
    public String toString() {
        return "Attendee{" +
                "id=" + id +
                ", eventId='" + eventId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
