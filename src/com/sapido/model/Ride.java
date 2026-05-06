package com.sapido.model;

public class Ride {
    private String id;
    private User passenger;
    private User driver;
    private Location pickupLocation;
    private Location dropoffLocation;
    private double fare;
    private String status; // REQUESTED, ACCEPTED, COMPLETED, CANCELLED

    public Ride(String id, User passenger, Location pickupLocation, Location dropoffLocation) {
        this.id = id;
        this.passenger = passenger;
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
        this.status = "REQUESTED";
    }

    public String getId() {
        return id;
    }

    public User getPassenger() {
        return passenger;
    }

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }

    public Location getPickupLocation() {
        return pickupLocation;
    }

    public Location getDropoffLocation() {
        return dropoffLocation;
    }

    public double getFare() {
        return fare;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
