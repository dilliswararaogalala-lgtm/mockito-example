package com.sapido.service;

import com.sapido.model.Location;
import com.sapido.model.Ride;
import com.sapido.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RideServiceImpl implements RideService {
    private final PaymentService paymentService;
    private final NotificationService notificationService;
    private final Map<String, Ride> rides = new HashMap<>();

    public RideServiceImpl(PaymentService paymentService, NotificationService notificationService) {
        this.paymentService = paymentService;
        this.notificationService = notificationService;
    }

    @Override
    public Ride requestRide(User passenger, Location pickup, Location dropoff) {
        if (passenger == null || pickup == null || dropoff == null) {
            throw new IllegalArgumentException("Passenger and locations cannot be null");
        }
        String rideId = UUID.randomUUID().toString();
        Ride ride = new Ride(rideId, passenger, pickup, dropoff);
        rides.put(rideId, ride);
        return ride;
    }

    @Override
    public void acceptRide(String rideId, User driver) {
        if (rideId == null || driver == null) {
            throw new IllegalArgumentException("Ride ID and driver cannot be null");
        }
        Ride ride = rides.get(rideId);
        if (ride == null) {
            throw new RuntimeException("Ride not found");
        }
        ride.setDriver(driver);
        ride.setStatus("ACCEPTED");
        ride.setFare(25.0); // Fixed fare for demo
        notificationService.notifyRideAccepted(ride.getPassenger(), driver);
    }

    @Override
    public void completeRide(String rideId) {
        Ride ride = rides.get(rideId);
        if (ride == null) {
            throw new RuntimeException("Ride not found");
        }
        ride.setStatus("COMPLETED");
        boolean paymentSuccess = paymentService.processPayment(rideId, ride.getFare());
        if (!paymentSuccess) {
            throw new RuntimeException("Payment failed");
        }
        notificationService.notifyRideCompleted(ride.getPassenger(), ride.getFare());
    }

    @Override
    public Ride getRideDetails(String rideId) {
        return rides.get(rideId);
    }
}
