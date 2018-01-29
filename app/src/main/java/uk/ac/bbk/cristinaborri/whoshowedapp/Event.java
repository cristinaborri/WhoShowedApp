package uk.ac.bbk.cristinaborri.whoshowedapp;

/**
 * Created by Cristina Borri
 */

public class Event {
    private long id;
    private String location;
    private String date;
    private String name;
    private String details;

    public Event(long id, String location, String date, String name, String details) {
        this.id = id;
        this.location = location;
        this.date = date;
        this.name = name;
        this.details = details;
    }

    public Event() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", location='" + location + '\'' +
                ", date='" + date + '\'' +
                ", name='" + name + '\'' +
                ", details='" + details + '\'' +
                '}';
    }
}
