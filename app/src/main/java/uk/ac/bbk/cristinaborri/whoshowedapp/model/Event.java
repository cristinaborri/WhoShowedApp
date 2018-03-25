package uk.ac.bbk.cristinaborri.whoshowedapp.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.Date;

/**
 * Created by Cristina Borri
 * This class represents the event and it's persisted using EventDAO
 */

public class Event {
    /**
     * This is the (database) identifier of the event
     */
    private long id;
    /**
     * These are the coordinates of the event locationCoordinates
     */
    private LatLng locationCoordinates;
    /**
     * This is the name of the event locationCoordinates
     */
    private String locationName;
    /**
     * This is the address of the event locationCoordinates
     */
    private String locationAddress;
    /**
     * These are the bounds for the location viewport (Map)
     */
    private LatLngBounds locationViewPort;
    /**
     * This is the date of the event
     */
    private Date date;
    /**
     * This is the name of the event
     */
    private String name;
    /**
     * These are the details of the event
     */
    private String details;

    /**
     * This constructor will create en empty event
     */
    public Event() {
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LatLng getLocationCoordinates() {
        return locationCoordinates;
    }

    public void setLocationCoordinates(LatLng locationCoordinates) {
        this.locationCoordinates = locationCoordinates;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public LatLngBounds getLocationViewPort() {
        return locationViewPort;
    }

    public void setLocationViewPort(LatLngBounds locationViewPort) {
        this.locationViewPort = locationViewPort;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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
                ", locationName='" + locationName + '\'' +
                ", date='" + date + '\'' +
                ", name='" + name + '\'' +
                ", details='" + details + '\'' +
                '}';
    }
}
