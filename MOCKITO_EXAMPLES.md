# Minimalistic Ride-Sharing App - Mockito Examples

A demonstration of key Mockito features for unit testing, with a simple ride-sharing application as the domain model.

## Project Structure

**Source Code (7 files):**
- `src/main/java/com/rideshare/model/User.java` - Passenger/Driver entity
- `src/main/java/com/rideshare/model/Location.java` - Geographic location
- `src/main/java/com/rideshare/model/Ride.java` - Ride entity
- `src/main/java/com/rideshare/service/RideService.java` - Core service interface
- `src/main/java/com/rideshare/service/RideServiceImpl.java` - Service implementation with dependencies
- `src/main/java/com/rideshare/service/PaymentService.java` - Payment processing interface
- `src/main/java/com/rideshare/service/NotificationService.java` - Notification interface

**Test Code (1 file):**
- `test/com/rideshare/service/RideServiceTest.java` - 15 focused test methods

**Total: 8 files (under 14 limit)**

---

## Mockito Features Demonstrated

### 1. **mock() - Creating Mocks Manually**
**Test Method:** `testAcceptRideWithBasicStubbing()`

Creates mock objects directly using `Mockito.mock()` instead of annotations. This is the foundational Mockito feature.

```java
PaymentService paymentService = mock(PaymentService.class);
NotificationService notificationService = mock(NotificationService.class);
```

---

### 2. **when().thenReturn() - Stubbing Return Values**
**Test Method:** `testProcessPaymentWithArgumentMatchers()`

Defines what a mock should return when a specific method is called.

```java
when(paymentService.processPayment(anyString(), anyDouble())).thenReturn(true);
```

---

### 3. **anyString() & any() - Flexible Argument Matching**
**Test Methods:** `testProcessPaymentWithArgumentMatchers()`, `testCombiningMultipleMatchers()`

Uses argument matchers to match any value of a specific type instead of exact values.

```java
when(paymentService.processPayment(anyString(), anyDouble())).thenReturn(true);
verify(paymentService).processPayment(anyString(), anyDouble());
```

---

### 4. **doNothing() - Explicit Void Method Stubbing**
**Test Method:** `testNotificationWithDoNothing()`

Explicitly stubs void methods (methods that return nothing). Useful for clarity and readability.

```java
doNothing().when(notificationService)
        .notifyRideAccepted(any(User.class), any(User.class));
```

---

### 5. **doThrow() - Making Mocks Throw Exceptions**
**Test Methods:** `testPaymentFailureWithDoThrow()`, `testExceptionHandlingWithCapture()`

Configures a mock to throw an exception when called, enabling exception handling tests.

```java
doThrow(new RuntimeException("Payment gateway unavailable"))
        .when(paymentService).processPayment(anyString(), anyDouble());
```

---

### 6. **ArgumentCaptor - Capturing Arguments**
**Test Methods:** `testCaptureArgumentsWithCaptor()`, `testMultipleArgumentCapturesWithLoop()`, `testExceptionHandlingWithCapture()`

Captures the actual arguments passed to a mock method for detailed verification.

```java
ArgumentCaptor<String> rideIdCaptor = ArgumentCaptor.forClass(String.class);
verify(paymentService).processPayment(rideIdCaptor.capture(), amountCaptor.capture());
assertEquals(ride.getId(), rideIdCaptor.getValue());
```

**For multiple calls, use `getAllValues()`:**
```java
assertEquals(2, rideIdCaptor.getAllValues().size());
assertEquals(ride1.getId(), rideIdCaptor.getAllValues().get(0));
assertEquals(ride2.getId(), rideIdCaptor.getAllValues().get(1));
```

---

### 7. **verify() with times(n) - Exact Call Count**
**Test Method:** `testVerificationWithTimes()`

Verifies a method was called exactly n times.

```java
verify(paymentService, times(3)).processPayment(anyString(), anyDouble());
```

---

### 8. **verify() with atLeastOnce() - Minimum Call Count**
**Test Method:** `testVerificationWithAtLeastOnce()`

Verifies a method was called at least once (flexible lower bound).

```java
verify(paymentService, atLeastOnce()).processPayment(anyString(), anyDouble());
```

---

### 9. **verify() with atMostOnce() - Maximum Call Count**
**Test Method:** `testVerificationWithAtMostOnce()`

Verifies a method was called at most once (upper bound limit).

```java
verify(paymentService, atMostOnce()).refund(anyString(), anyDouble());
```

---

### 10. **InOrder - Verifying Call Sequence**
**Test Method:** `testInOrderVerification()`

Verifies that methods were called in a specific order.

```java
InOrder inOrder = inOrder(paymentService, notificationService);
inOrder.verify(notificationService).notifyRideAccepted(passenger, driver);
inOrder.verify(paymentService).processPayment(ride.getId(), 25.0);
inOrder.verify(notificationService).notifyRideCompleted(passenger, 25.0);
```

---

### 11. **never() - Verify Method Never Called**
**Test Method:** `testNeverVerification()`

Verifies that a method was NOT called (equivalent to `times(0)`).

```java
verify(paymentService, never()).processPayment(anyString(), anyDouble());
```

---

### 12. **verifyNoMoreInteractions() - Detecting Unexpected Calls**
**Test Method:** `testVerifyNoMoreInteractions()`

Ensures no other methods on the mock were called beyond what was explicitly verified.

```java
verify(paymentService).processPayment(ride.getId(), 25.0);
verifyNoMoreInteractions(paymentService);
```

---

### 13. **verifyZeroInteractions() - Verify Mock Was Never Used**
**Test Method:** `testVerifyZeroInteractions()`

Verifies that a mock was completely unused throughout the test.

```java
verifyZeroInteractions(paymentService);
```

---

### 14. **Combining Matchers - Mixed Flexible and Exact Matching**
**Test Method:** `testCombiningMultipleMatchers()`

Uses different matchers for different arguments (e.g., `anyString()` for one parameter, `eq(25.0)` for another).

```java
when(paymentService.processPayment(anyString(), eq(25.0))).thenReturn(true);
verify(paymentService).processPayment(anyString(), eq(25.0));
```

---

### 15. **Combined Exception Handling with Captures**
**Test Method:** `testExceptionHandlingWithCapture()`

Demonstrates combining multiple Mockito features: `doThrow()`, `ArgumentCaptor`, and exception verification.

```java
doThrow(new RuntimeException("Payment gateway error"))
        .when(paymentService).processPayment(anyString(), anyDouble());

try {
    rideService.completeRide(ride.getId());
    fail("Should have thrown exception");
} catch (RuntimeException e) {
    assertTrue(e.getMessage().contains("Payment failed"));
}

verify(paymentService).processPayment(rideCaptor.capture(), amountCaptor.capture());
```

---

## Key Takeaways

1. **Manual Mock Creation**: Create mocks with `mock(Class.class)` instead of relying on annotations
2. **Stubbing Strategy**: Use `when-then` for return values, `doX-when` for void methods
3. **Argument Matchers**: Use flexible matchers (`anyString()`, `any()`) for generic cases and specific values for precise verification
4. **Verification Modes**: Choose appropriate verification (times, atLeastOnce, atMostOnce, never) based on test requirements
5. **Argument Capture**: Use `ArgumentCaptor` to inspect and assert on actual method arguments
6. **Order Verification**: Use `InOrder` when method call sequence matters
7. **Exception Testing**: Use `doThrow()` to test error handling paths
8. **Clean Verification**: Use `verifyNoMoreInteractions()` and `verifyZeroInteractions()` to detect unintended calls
