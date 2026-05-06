package com.sapido.service;

import com.sapido.model.User;

public interface NotificationService {
    void notifyRideAccepted(User passenger, User driver);
    void notifyRideCompleted(User passenger, double fare);
}
