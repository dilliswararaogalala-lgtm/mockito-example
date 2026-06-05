package com.sapido.service;

import com.sapido.model.Location;
import com.sapido.model.Ride;
import com.sapido.model.User;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive examples of Mockito features for ride-sharing service testing.
 * Each test demonstrates a specific Mockito capability.
 * Mocks are created manually within test methods using Mockito.mock().
 */
public class RideServiceTest {

    // Test data
    private final User passenger = new User("user1", "Alice", "alice@example.com", 4.5);
    private final User driver = new User("driver1", "Bob", "bob@example.com", 4.8);
    private final Location pickup = new Location(40.7128, -74.0060, "123 Main St");
    private final Location dropoff = new Location(40.7580, -73.9855, "456 Park Ave");

    /**
     * Example 1: Basic Mockito.mock() and when-then stubbing
     * Demonstrates how to manually create mocks and stub method return values
     */
    @Test
    public void testAcceptRideWithBasicStubbing() {
        // Arrange: create mocks manually using Mockito.mock()
        PaymentService paymentService = mock(PaymentService.class);
        NotificationService notificationService = mock(NotificationService.class);
        RideService rideService = new RideServiceImpl(paymentService, notificationService);

        // Stub payment service to return true
        when(paymentService.processPayment(anyString(), eq(25.0))).thenReturn(true);

        // Act
        Ride ride = rideService.requestRide(passenger, pickup, dropoff);
        rideService.acceptRide(ride.getId(), driver);
        rideService.completeRide(ride.getId());

        // Assert
        assertEquals("COMPLETED", ride.getStatus());
        verify(paymentService).processPayment(ride.getId(), 25.0);
    }

    /**
     * Example 2: Stubbing with anyString() and any() argument matchers
     * Demonstrates flexible argument matching without knowing exact values
     */
    @Test
    public void testProcessPaymentWithArgumentMatchers() {
        // Arrange: create mocks
        PaymentService paymentService = mock(PaymentService.class);
        NotificationService notificationService = mock(NotificationService.class);
        RideService rideService = new RideServiceImpl(paymentService, notificationService);

        // Stub: accept any rideId (anyString) and any double value
        when(paymentService.processPayment(anyString(), anyDouble())).thenReturn(true);

        // Act
        Ride ride = rideService.requestRide(passenger, pickup, dropoff);
        rideService.acceptRide(ride.getId(), driver);
        rideService.completeRide(ride.getId());

        // Assert: verify was called with any String and any double
        verify(paymentService).processPayment(anyString(), anyDouble());
        assertEquals("COMPLETED", ride.getStatus());
    }

    /**
     * Example 3: doNothing() - explicitly stub void methods
     * Demonstrates stubbing void methods that do nothing
     */
    @Test
    public void testNotificationWithDoNothing() {
        // Arrange: create mocks
        PaymentService paymentService = mock(PaymentService.class);
        NotificationService notificationService = mock(NotificationService.class);
        RideService rideService = new RideServiceImpl(paymentService, notificationService);

        // Stub: explicitly define that notification should do nothing
        doNothing().when(notificationService)
                .notifyRideAccepted(any(User.class), any(User.class));
        when(paymentService.processPayment(anyString(), anyDouble())).thenReturn(true);

        // Act
        Ride ride = rideService.requestRide(passenger, pickup, dropoff);
        rideService.acceptRide(ride.getId(), driver);
        rideService.completeRide(ride.getId());

        // Assert: notifications were called
        verify(notificationService).notifyRideAccepted(passenger, driver);
        verify(notificationService).notifyRideCompleted(passenger, 25.0);
    }

    /**
     * Example 4: doThrow() - make mocked methods throw exceptions
     * Demonstrates how to test exception handling
     */
    @Test
    public void testPaymentFailureWithDoThrow() {
        // Arrange: create mocks
        PaymentService paymentService = mock(PaymentService.class);
        NotificationService notificationService = mock(NotificationService.class);
        RideService rideService = new RideServiceImpl(paymentService, notificationService);

        // Configure payment service to throw exception
        RuntimeException paymentException = new RuntimeException("Payment gateway unavailable");
        doThrow(paymentException)
                .when(paymentService).processPayment(anyString(), anyDouble());

        // Act & Assert: expect exception when completing ride
        Ride ride = rideService.requestRide(passenger, pickup, dropoff);
        rideService.acceptRide(ride.getId(), driver);

        RuntimeException actualException = assertThrows(RuntimeException.class, () -> rideService.completeRide(ride.getId()));
        assertEquals(paymentException, actualException);
    }

    /**
     * Example 5: ArgumentCaptor - capture arguments passed to mocked methods
     * Demonstrates how to inspect what arguments were passed to mock methods
     */
    @Test
    public void testCaptureArgumentsWithCaptor() {
        // Arrange: create mocks
        PaymentService paymentService = mock(PaymentService.class);
        NotificationService notificationService = mock(NotificationService.class);
        RideService rideService = new RideServiceImpl(paymentService, notificationService);

        ArgumentCaptor<String> rideIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Double> amountCaptor = ArgumentCaptor.forClass(Double.class);

        when(paymentService.processPayment(anyString(), anyDouble())).thenReturn(true);

        // Act
        Ride ride = rideService.requestRide(passenger, pickup, dropoff);
        rideService.acceptRide(ride.getId(), driver);
        rideService.completeRide(ride.getId());

        // Assert: capture and verify the exact arguments passed
        verify(paymentService).processPayment(rideIdCaptor.capture(), amountCaptor.capture());
        assertEquals(ride.getId(), rideIdCaptor.getValue());
        assertEquals(25.0, amountCaptor.getValue(), 0.0);
    }

    /**
     * Example 6: verify() with times(n) - verify method called exactly n times
     * Demonstrates counting invocations
     */
    @Test
    public void testVerificationWithTimes() {
        // Arrange: create mocks
        PaymentService paymentService = mock(PaymentService.class);
        NotificationService notificationService = mock(NotificationService.class);
        RideService rideService = new RideServiceImpl(paymentService, notificationService);

        when(paymentService.processPayment(anyString(), anyDouble())).thenReturn(true);

        // Act: create and complete multiple rides
        for (int i = 0; i < 3; i++) {
            Ride ride = rideService.requestRide(passenger, pickup, dropoff);
            rideService.acceptRide(ride.getId(), driver);
            rideService.completeRide(ride.getId());
        }

        // Assert: verify payment was processed exactly 3 times
        verify(paymentService, times(3)).processPayment(anyString(), anyDouble());
    }

    /**
     * Example 7: verify() with atLeastOnce() - verify method called at least once
     * Demonstrates flexible verification of minimum call count
     */
    @Test
    public void testVerificationWithAtLeastOnce() {
        // Arrange: create mocks
        PaymentService paymentService = mock(PaymentService.class);
        NotificationService notificationService = mock(NotificationService.class);
        RideService rideService = new RideServiceImpl(paymentService, notificationService);

        when(paymentService.processPayment(anyString(), anyDouble())).thenReturn(true);

        // Act: create rides
        Ride ride1 = rideService.requestRide(passenger, pickup, dropoff);
        rideService.acceptRide(ride1.getId(), driver);
        rideService.completeRide(ride1.getId());

        Ride ride2 = rideService.requestRide(passenger, pickup, dropoff);
        rideService.acceptRide(ride2.getId(), driver);
        rideService.completeRide(ride2.getId());

        // Assert: verify payment was called at least once (actually called twice)
        verify(paymentService, atLeastOnce()).processPayment(anyString(), anyDouble());
    }

    /**
     * Example 8: verify() with atMostOnce() - verify method called at most once
     * Demonstrates upper bound verification
     */
    @Test
    public void testVerificationWithAtMostOnce() {
        // Arrange: create mocks
        PaymentService paymentService = mock(PaymentService.class);
        NotificationService notificationService = mock(NotificationService.class);
        RideService rideService = new RideServiceImpl(paymentService, notificationService);

        when(paymentService.processPayment(anyString(), anyDouble())).thenReturn(true);

        // Act: single ride
        Ride ride = rideService.requestRide(passenger, pickup, dropoff);
        rideService.acceptRide(ride.getId(), driver);
        rideService.completeRide(ride.getId());

        // Assert: verify refund was never called or at most once
        verify(paymentService, atMostOnce()).refund(anyString(), anyDouble());
    }

    /**
     * Example 9: InOrder verification - verify method call sequence
     * Demonstrates verifying the order in which methods were called
     */
    @Test
    public void testInOrderVerification() {
        // Arrange: create mocks
        PaymentService paymentService = mock(PaymentService.class);
        NotificationService notificationService = mock(NotificationService.class);
        RideService rideService = new RideServiceImpl(paymentService, notificationService);

        when(paymentService.processPayment(anyString(), anyDouble())).thenReturn(true);

        // Act
        Ride ride = rideService.requestRide(passenger, pickup, dropoff);
        rideService.acceptRide(ride.getId(), driver);
        rideService.completeRide(ride.getId());

        // Assert: verify methods were called in this specific order
        InOrder inOrder = inOrder(paymentService, notificationService);
        inOrder.verify(notificationService).notifyRideAccepted(passenger, driver);
        inOrder.verify(paymentService).processPayment(ride.getId(), 25.0);
        inOrder.verify(notificationService).notifyRideCompleted(passenger, 25.0);
    }

    /**
     * Example 10: never() - verify a method was never called
     * Demonstrates verifying that a method should not have been invoked
     */
    @Test
    public void testNeverVerification() {
        // Arrange: create mocks
        PaymentService paymentService = mock(PaymentService.class);
        NotificationService notificationService = mock(NotificationService.class);
        RideService rideService = new RideServiceImpl(paymentService, notificationService);

        when(paymentService.processPayment(anyString(), anyDouble())).thenReturn(true);

        // Act: just accept ride, don't complete it
        Ride ride = rideService.requestRide(passenger, pickup, dropoff);
        rideService.acceptRide(ride.getId(), driver);

        // Assert: verify payment was never processed yet
        verify(paymentService, never()).processPayment(anyString(), anyDouble());
    }

    /**
     * Example 11: verifyNoMoreInteractions() - ensure no unexpected calls
     * Demonstrates detecting unexpected method invocations
     */
    @Test
    public void testVerifyNoMoreInteractions() {
        // Arrange: create mocks
        PaymentService paymentService = mock(PaymentService.class);
        NotificationService notificationService = mock(NotificationService.class);
        RideService rideService = new RideServiceImpl(paymentService, notificationService);

        when(paymentService.processPayment(anyString(), anyDouble())).thenReturn(true);

        // Act
        Ride ride = rideService.requestRide(passenger, pickup, dropoff);
        rideService.acceptRide(ride.getId(), driver);
        rideService.completeRide(ride.getId());

        // Assert: verify specific interactions happened as expected
        verify(paymentService).processPayment(ride.getId(), 25.0);
        // Ensure no other methods on paymentService were called beyond what we verified
        verifyNoMoreInteractions(paymentService);
    }

    /**
     * Example 12: verifyZeroInteractions() - verify mock was never used
     * Demonstrates ensuring a mock was completely unused
     */
    @Test
    public void testVerifyZeroInteractions() {
        // Arrange: create mocks
        PaymentService paymentService = mock(PaymentService.class);
        NotificationService notificationService = mock(NotificationService.class);
        RideService rideService = new RideServiceImpl(paymentService, notificationService);

        when(paymentService.processPayment(anyString(), anyDouble())).thenReturn(true);

        // Act: only request a ride, don't accept or complete
        Ride ride = rideService.requestRide(passenger, pickup, dropoff);
        rideService.acceptRide(ride.getId(), driver);

        // Assert: verify payment service was never called
        Mockito.verifyNoInteractions(paymentService);
    }

    /**
     * Example 13: Multiple ArgumentCaptors - capture multiple calls with different values
     * Demonstrates capturing arguments from multiple method invocations using getAllValues()
     */
    @Test
    public void testMultipleArgumentCapturesWithLoop() {
        // Arrange: create mocks
        PaymentService paymentService = mock(PaymentService.class);
        NotificationService notificationService = mock(NotificationService.class);
        RideService rideService = new RideServiceImpl(paymentService, notificationService);

        ArgumentCaptor<String> rideIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Double> amountCaptor = ArgumentCaptor.forClass(Double.class);

        when(paymentService.processPayment(anyString(), anyDouble())).thenReturn(true);

        // Act: complete 2 rides
        Ride ride1 = rideService.requestRide(passenger, pickup, dropoff);
        rideService.acceptRide(ride1.getId(), driver);
        rideService.completeRide(ride1.getId());

        Ride ride2 = rideService.requestRide(passenger, pickup, dropoff);
        rideService.acceptRide(ride2.getId(), driver);
        rideService.completeRide(ride2.getId());

        // Assert: capture all invocations using getAllValues()
        verify(paymentService, times(2)).processPayment(rideIdCaptor.capture(), amountCaptor.capture());
        assertEquals(2, rideIdCaptor.getAllValues().size());
        assertEquals(2, amountCaptor.getAllValues().size());
        assertEquals(25.0, amountCaptor.getAllValues().get(0), 0.0);
        assertEquals(25.0, amountCaptor.getAllValues().get(1), 0.0);
    }

    /**
     * Example 14: Combining multiple matchers - use different matchers for different arguments
     * Demonstrates mixing specific values (eq()) with flexible matchers (anyString())
     */
    @Test
    public void testCombiningMultipleMatchers() {
        // Arrange: create mocks
        PaymentService paymentService = mock(PaymentService.class);
        NotificationService notificationService = mock(NotificationService.class);
        RideService rideService = new RideServiceImpl(paymentService, notificationService);

        // Accept any ride ID but verify exact payment amount
        when(paymentService.processPayment(anyString(), eq(25.0))).thenReturn(true);

        // Act
        Ride ride = rideService.requestRide(passenger, pickup, dropoff);
        rideService.acceptRide(ride.getId(), driver);
        rideService.completeRide(ride.getId());

        // Assert: exact matching for one parameter, flexible for other
        verify(paymentService).processPayment(anyString(), eq(25.0));
    }

    /**
     * Example 15: Test exception handling with captures - verify error case behavior
     * Demonstrates combined use of doThrow(), ArgumentCaptor, and exception verification
     */
    @Test
    public void testExceptionHandlingWithCapture() {
        // Arrange: create mocks
        PaymentService paymentService = mock(PaymentService.class);
        NotificationService notificationService = mock(NotificationService.class);
        RideService rideService = new RideServiceImpl(paymentService, notificationService);

        ArgumentCaptor<String> rideCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Double> amountCaptor = ArgumentCaptor.forClass(Double.class);

        // Stub payment to fail with exception
        RuntimeException paymentError = new RuntimeException("Payment gateway error");
        doThrow(paymentError)
                .when(paymentService).processPayment(anyString(), anyDouble());

        // Act & Assert
        Ride ride = rideService.requestRide(passenger, pickup, dropoff);
        rideService.acceptRide(ride.getId(), driver);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> rideService.completeRide(ride.getId()));
        assertEquals(paymentError, exception);

        // Verify payment was attempted and capture the arguments
        verify(paymentService).processPayment(rideCaptor.capture(), amountCaptor.capture());
        assertEquals(ride.getId(), rideCaptor.getValue());
        assertEquals(25.0, amountCaptor.getValue(), 0.0);
    }
}
