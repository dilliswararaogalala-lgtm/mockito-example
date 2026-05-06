package com.sapido.service;

import com.sapido.model.Location;
import com.sapido.model.Ride;
import com.sapido.model.User;

public interface RideService {
    Ride requestRide(User passenger, Location pickup, Location dropoff);
    void acceptRide(String rideId, User driver);
    void completeRide(String rideId);
    Ride getRideDetails(String rideId);
}
