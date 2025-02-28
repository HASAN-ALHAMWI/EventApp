package com.hasan.eventapp.data.remote

import com.hasan.eventapp.data.managers.MockDataManager
import com.hasan.eventapp.data.models.Booking
import com.hasan.eventapp.data.models.Event
import com.hasan.eventapp.data.models.Payment
import com.hasan.eventapp.data.models.User
import kotlinx.coroutines.delay
import java.util.UUID

interface MockApiService {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(user: User): Result<User>
    suspend fun getEvents(): Result<List<Event>>
    suspend fun getEventDetails(eventId: String): Result<Event>
    suspend fun processPayment(
        eventId: String,
        amount: Float,
        cardNumber: String,
        cardHolderName: String
    ): Result<Payment>
    suspend fun createBooking(
        eventId: String,
        userId: String,
        paymentId: String
    ): Result<Booking>
}

class MockApiServiceImpl(private val mockDataManager: MockDataManager) : MockApiService {

    override suspend fun login(email: String, password: String): Result<User> {
        delay(1000) // Simulate network delay

        val users = mockDataManager.getUsers()
        val user = users.find { it.email == email && it.password == password }

        return if (user != null) {
            Result.success(user)
        } else {
            Result.failure(Exception("Invalid credentials"))
        }
    }

    override suspend fun register(user: User): Result<User> {
        delay(1000) // Simulate network delay

        val users = mockDataManager.getUsers()

        // Check if user with this email already exists
        if (users.any { it.email == user.email }) {
            return Result.failure(Exception("User with this email already exists"))
        }

        // Create and save a new user (JsonDataManager handles ID generation)
        val success = mockDataManager.saveUser(user)

        return if (success) {
            // Fetch the saved user with the assigned ID
            val savedUsers = mockDataManager.getUsers()
            val savedUser = savedUsers.find { it.email == user.email }
                ?: return Result.failure(Exception("Failed to retrieve saved user"))

            Result.success(savedUser)
        } else {
            Result.failure(Exception("Failed to register user"))
        }

    }

    override suspend fun getEvents(): Result<List<Event>> {
        delay(1000) // Simulate network delay

        val events = mockDataManager.getEvents()
        return Result.success(events)
    }

    override suspend fun getEventDetails(eventId: String): Result<Event> {
        delay(300) // Simulate network delay

        val events = mockDataManager.getEvents()
        val event = events.find { it.id == eventId }

        return if (event != null) {
            Result.success(event)
        } else {
            Result.failure(Exception("Event not found"))
        }
    }

    override suspend fun processPayment(
        eventId: String,
        amount: Float,
        cardNumber: String,
        cardHolderName: String
    ): Result<Payment> {
        delay(1500) // Simulate payment processing delay

        // Check if event exists
        val events = mockDataManager.getEvents()
        events.find { it.id == eventId }
            ?: return Result.failure(Exception("Event not found"))

        // For the purpose of this mock, we'll simulate a successful payment
        // In a real implementation, this would involve a payment gateway

        // Validate basic card info (simple validation for demonstration)
        val cardNumberCleaned = cardNumber.replace(" ", "")
        if (cardNumberCleaned.length != 16 || !cardNumberCleaned.all { it.isDigit() }) {
            return Result.failure(Exception("Invalid card number"))
        }

        if (cardHolderName.isBlank()) {
            return Result.failure(Exception("Card holder name is required"))
        }

        // Create a payment record
        val payment = Payment(
            id = UUID.randomUUID().toString(),
            eventId = eventId,
            amount = amount,
            transactionId = "TX-${UUID.randomUUID().toString().substring(0, 8).uppercase()}",
            status = "COMPLETED",
            timestamp = System.currentTimeMillis()
        )

        return Result.success(payment)
    }

    override suspend fun createBooking(
        eventId: String,
        userId: String,
        paymentId: String
    ): Result<Booking> {
        delay(500) // Simulate network delay

        // Validate that event exists
        val events = mockDataManager.getEvents()
        events.find { it.id == eventId }
            ?: return Result.failure(Exception("Event not found"))

        // In a real application, we would validate the payment and user too

        // Create booking record
        val booking = Booking(
            id = UUID.randomUUID().toString(),
            eventId = eventId,
            userId = userId,
            paymentId = paymentId,
            bookingReference = "BK-${UUID.randomUUID().toString().substring(0, 8).uppercase()}",
            status = "CONFIRMED",
            bookingDate = System.currentTimeMillis()
        )

        // In a real app, we'd store this booking in persistent storage

        return Result.success(booking)
    }

}