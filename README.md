# Event App

<p align="center">
  <img src="https://github.com/user-attachments/assets/50574972-68e0-4c07-abe4-6b45c6e7e960" alt="EventBooking App" width="300"/>
  <br>
  <em>A professional event booking application built with modern Android architecture</em>
</p>

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technical Specifications](#technical-specifications)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Setup Guide](#setup-guide)
- [Implementation Details](#implementation-details)
- [Testing Strategy](#testing-strategy)
- [Performance Optimizations](#performance-optimizations)
- [Security Considerations](#security-considerations)
- [Future Roadmap](#future-roadmap)
- [Troubleshooting](#troubleshooting)
- [License](#license)

## Overview

Event App is a sophisticated Android application designed to demonstrate implementation of a robust event booking system following industry best practices. The application allows users to discover events, view detailed information, and simulate the entire booking experience with secure payment processing.

The project showcases a production-ready implementation of Clean Architecture with MVVM design pattern, focusing on code quality, testability, and maintainability. It demonstrates proper separation of concerns, reactive programming principles, dependency injection, and offline-first approach.

## Features

### User Authentication
- Secure login and registration system
- Input validation with detailed error feedback
- Session management using encrypted SharedPreferences
- Persistent login state

### Event List
- Browse events with visually appealing UI
- Smooth scrolling with efficient recycling of views
- Placeholder animations during loading states
- Pull-to-refresh functionality for data updates

### Event Details
- Rich event information display
- Dynamic image loading with proper caching
- Transition animations between screens
- Responsive layout design

### Booking Process
- Multi-step booking flow
- Secure payment simulation
- Input validation with proper error handling
- Booking confirmation with reference details

### Offline Support
- Complete offline functionality
- Local data persistence with Room database
- Synchronization strategy when connection is restored
- Graceful error handling for network issues

## Technical Specifications

### Development Environment
- **IDE**: Android Studio Arctic Fox (2021.3.1) or higher
- **Build System**: Gradle 7.0+
- **Minimum SDK**: API 21 (Android 5.0 Lollipop)
- **Target SDK**: API 33 (Android 13)
- **JDK Version**: 11

### Core Technologies
- **Programming Language**: Kotlin 1.7+
- **UI Construction**: XML-based layouts with Material Design components
- **Concurrency**: Kotlin Coroutines with Flow
- **Dependency Injection**: Hilt
- **Database**: Room Persistence Library
- **Image Loading**: Glide
- **Navigation**: Jetpack Navigation Component
- **Data Serialization**: Kotlinx Serialization
- **Security**: Android Security Crypto

### Third-Party Dependencies
```gradle
// App-level build.gradle
dependencies {
    // UI
    implementation 'androidx.core:core-ktx:1.9.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.8.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    
    // Navigation
    implementation 'androidx.navigation:navigation-fragment-ktx:2.5.3'
    implementation 'androidx.navigation:navigation-ui-ktx:2.5.3'
    
    // Lifecycle
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1'
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.5.1'
    
    // Room Database
    implementation 'androidx.room:room-runtime:2.5.0'
    implementation 'androidx.room:room-ktx:2.5.0'
    kapt 'androidx.room:room-compiler:2.5.0'
    
    // Hilt DI
    implementation 'com.google.dagger:hilt-android:2.45'
    kapt 'com.google.dagger:hilt-compiler:2.45'
    
    // Coroutines
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4'
    
    // Image Loading
    implementation 'com.github.bumptech.glide:glide:4.15.0'
    kapt 'com.github.bumptech.glide:compiler:4.15.0'
    
    // Security
    implementation 'androidx.security:security-crypto:1.1.0-alpha05'
    
    // Testing
    testImplementation 'junit:junit:4.13.2'
    testImplementation 'org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4'
    testImplementation 'io.mockk:mockk:1.13.2'
    testImplementation 'app.cash.turbine:turbine:0.12.1'
}
```

## Architecture

The application is architected following Clean Architecture principles, organized into three distinct layers with clear boundaries and dependencies flowing inward.

### Architectural Diagram

```
┌──────────────────────────────────────────────────────────────────┐
│                      PRESENTATION LAYER                          │
│                                                                  │
│  ┌─────────┐       ┌─────────────┐       ┌──────────────────┐    │
│  │ Fragment│◄──────│  ViewModel  │◄──────│  UI State Class  │    │
│  └─────────┘       └─────────────┘       └──────────────────┘    │
│        ▲                   ▲                                     │
└────────┼───────────────────┼─────────────────────────────────────┘
         │                   │
         │                   │ Depends on
         │                   │
┌────────┼───────────────────┼─────────────────────────────────────┐
│        │                   │       DOMAIN LAYER                  │
│        │                   │                                     │
│        │           ┌───────▼─────┐     ┌─────────────────────┐   │
│        └───────────│   UseCase   │◄────│    Domain Models    │   │
│                    └───────┬─────┘     └─────────────────────┘   │
│                            │                                     │
│                    ┌───────▼─────────┐                           │
│                    │ Repository (I/F)│                           │
│                    └─────────────────┘                           │
│                            ▲                                     │
└────────────────────────────┼─────────────────────────────────────┘
                             │ Implements
                             │
┌────────────────────────────┼─────────────────────────────────────┐
│                    ┌───────▼─────────┐    DATA LAYER             │
│                    │Repository (Impl)│                           │
│                    └───────┬─────────┘                           │
│                            │                                     │
│          ┌─────────────────┴────────────────┐                    │
│          │                                  │                    │
│   ┌──────▼───────┐                  ┌───────▼─────┐              │
│   │  Local Data  │                  │ Remote Data │              │
│   │   Source     │                  │   Source    │              │
│   └──────┬───────┘                  └───────┬─────┘              │
│          │                                  │                    │
│   ┌──────▼───────┐                  ┌───────▼──────┐             │
│   │  Room DB &   │                  │  API Service │             │
│   │    DAOs      │                  │   & Models   │             │
│   └──────────────┘                  └──────────────┘             │
└──────────────────────────────────────────────────────────────────┘
```

### Key Architectural Components

#### 1. Presentation Layer
- **Fragments**: Responsible for UI rendering and user interaction
- **ViewModels**: Manage UI state and communicate with domain layer
- **UI State Classes**: Immutable representations of the UI state
- **Navigation**: Handled through Jetpack Navigation Component

#### 2. Domain Layer
- **Use Cases**: Encapsulate application-specific business rules
- **Domain Models**: Core business entities
- **Repository Interfaces**: Define data operation contracts

#### 3. Data Layer
- **Repository Implementations**: Coordinate data from different sources
- **Local Data Source**: Room database for offline persistence
- **Remote Data Source**: API service interface (simulated in this app)
- **Data Models**: Representations of network and database entities

### Dependency Flow

The architecture strictly enforces a dependency rule where:
- Outer layers depend on inner layers, never the reverse
- Domain layer has no dependencies on framework components
- Data and presentation layers depend on the domain layer
- Data and presentation layers are agnostic of each other

## Project Structure

The project is organized into packages that reflect the architectural layers and functional components:

```
com.hasan.eventapp/
├── data/                     # Data layer
│   ├── local/                # Local database
│   │   ├── EventDatabase.kt  # Room database definition
│   │   └── dao/              # Data Access Objects
│   │       └── EventDao.kt   # Event entity DAO
│   ├── managers/             # Data management utilities
│   │   ├── MockDataManager.kt # Mock data handling
│   │   └── UserManager.kt    # User session management
│   ├── models/               # Data models
│   │   ├── Booking.kt        # Booking data model
│   │   ├── Event.kt          # Event data model
│   │   ├── Payment.kt        # Payment data model
│   │   └── User.kt           # User data model
│   ├── remote/               # Remote data source
│   │   └── MockApiService.kt # Simulated API service
│   └── repositories/         # Repository implementations
│       ├── AuthRepository.kt # Authentication repository
│       ├── BookingRepository.kt # Booking repository
│       ├── EventRepository.kt # Event repository
│       └── PaymentRepository.kt # Payment repository
├── di/                       # Dependency injection
│   └── modules/              # Hilt modules
│       ├── AppModule.kt      # Application-level dependencies
│       ├── DatabaseModule.kt # Database-related dependencies
│       ├── RepositoryModule.kt # Repository dependencies
│       └── UseCaseModule.kt  # Use case dependencies
├── domain/                   # Domain layer
│   └── repositories/         # Repository implementations
│   |    ├── IAuthRepository.kt # Authentication repository interface
│   |    ├── IBookingRepository.kt # Booking repository interface
│   |    ├── IEventRepository.kt # Event repository interface
│   |    └── IPaymentRepository.kt # Payment repository interface
│   ├── models/               # Domain models
│   │   ├── BookingDomain.kt  # Booking domain model
│   │   ├── EventDomain.kt    # Event domain model
│   │   ├── PaymentDomain.kt  # Payment domain model
│   │   └── UserDomain.kt     # User domain model
│   └── usecases/             # Use cases
│       ├── auth/             # Authentication use cases
│       │   ├── LoginUseCase.kt # Login use case
│       │   ├── LogoutUseCase.kt # Logout use case
│       │   └── RegisterUseCase.kt # Registration use case
│       ├── booking/          # Booking use cases
│       │   └── CreateBookingUseCase.kt # Booking creation
│       ├── events/           # Event use cases
│       │   ├── GetEventDetailsUseCase.kt # Event details
│       │   └── GetEventsUseCase.kt # Event listing
│       └── payment/          # Payment use cases
│           └── ProcessPaymentUseCase.kt # Payment processing
├── presentation/             # Presentation layer
│   ├── auth/                 # Authentication UI
│   │   ├── login/            # Login screen
│   │   │   ├── LoginFragment.kt # Login UI
│   │   │   └── LoginViewModel.kt # Login logic
│   │   └── register/         # Registration screen
│   │       ├── RegisterFragment.kt # Registration UI
│   │       └── RegisterViewModel.kt # Registration logic
│   ├── booking/              # Booking UI
│   │   └── BookingConfirmationFragment.kt # Booking confirmation
│   ├── events/               # Event UI
│   │   ├── detail/           # Event detail screen
│   │   │   ├── EventDetailFragment.kt # Event detail UI
│   │   │   └── EventDetailViewModel.kt # Event detail logic
│   │   └── list/             # Event list screen
│   │       ├── EventAdapter.kt # RecyclerView adapter
│   │       ├── EventListFragment.kt # Event list UI
│   │       ├── EventListViewModel.kt # Event list logic
│   │       └── EventSkeletonAdapter.kt # Loading state adapter
│   └── payment/              # Payment UI
│       ├── PaymentBottomSheetFragment.kt # Payment UI
│       └── PaymentViewModel.kt # Payment logic
├── utils/                    # Utilities
│   ├── extensions/           # Kotlin extensions
│   │   ├── DateExtensions.kt # Date formatting utilities
│   │   ├── FloatExtensions.kt # Number formatting utilities
│   │   ├── FragmentExtensions.kt # Fragment helper functions
│   │   ├── StringExtensions.kt # String validation utilities
│   │   ├── TextInputLayoutExtensions.kt # Input field utilities
│   │   └── ViewExtensions.kt # View helper functions
│   ├── Constants.kt          # Application constants
│   ├── InputValidationException.kt # Validation error handling
│   ├── NetworkUtils.kt       # Network connectivity handling
│   ├── SessionManager.kt     # User session management
│   ├── UiAnimationHelper.kt  # Animation utilities
│   └── ValidationUtils.kt    # Input validation utilities
├── EventApplication.kt       # Application class
└── MainActivity.kt           # Main activity
```

## Setup Guide

### Prerequisites

- Android Studio Arctic Fox (2021.3.1) or higher
- JDK 11 or higher
- Android SDK (API level 21+)
- Git

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/HASAN-ALHAMWI/Event-app.git
   cd Event-app
   ```

2. **Open the project in Android Studio**
   - Launch Android Studio
   - Select `File > Open`
   - Navigate to the cloned repository directory
   - Click `OK`

3. **Configure Local Properties (if needed)**
   - Create a `local.properties` file in the project root if not automatically created
   - Ensure it contains the path to your Android SDK:
     ```properties
     sdk.dir=/path/to/your/android/sdk
     ```

4. **Gradle Sync**
   - Wait for the Gradle sync to complete
   - Resolve any dependency issues if prompted

5. **Build the Project**
   - Select `Build > Make Project` (or use shortcut Ctrl+F9 / Cmd+F9)
   - Fix any build errors if they occur

6. **Run the Application**
   - Connect a physical device or set up an emulator
   - Select `Run > Run 'app'` (or use shortcut Shift+F10 / Control+R)

### Test Credentials

For testing the application, use the following credentials:

- **Email**: `user1@gmail.com`
- **Password**: `Test@123`

Or register a new account with validation rules:
- Email must be in a valid format
- Password must be 6-20 characters
- Full name is required

### Common Setup Issues

1. **Gradle Sync Failures**
   - Ensure you have a stable internet connection
   - Check that your SDK path is correctly set
   - Try invalidating caches: `File > Invalidate Caches / Restart`

2. **Minimum SDK Compatibility**
   - The application requires a minimum SDK of 21
   - Ensure your emulator or physical device supports this API level

3. **JDK Version Mismatch**
   - Verify that Android Studio is using JDK 11 or higher
   - Check via `File > Project Structure > SDK Location`

## Implementation Details

### Authentication Flow

The application implements a secure authentication flow:

1. **Login/Registration**:
   - Input validation happens in real-time with proper error feedback
   - Credentials are processed through the authentication use cases
   - Successful authentication establishes a secure session

2. **Session Management**:
   - User credentials are securely stored using `EncryptedSharedPreferences`
   - Session is maintained across app restarts
   - Logout clears all sensitive information

### State Management

Each screen uses a dedicated state system:

```kotlin
// Example of login state management
sealed class LoginState {
    data object Initial : LoginState()
    data object Loading : LoginState()
    data class Success(val user: UserDomain) : LoginState()
    data class ValidationError(
        val emailError: String?,
        val passwordError: String?
    ) : LoginState()
    data class ApiError(val message: String) : LoginState()
}
```

ViewModels expose state through `StateFlow`:

```kotlin
private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
val loginState: StateFlow<LoginState> = _loginState.asStateFlow()
```

UI observes and reacts to state changes:

```kotlin
viewLifecycleOwner.lifecycleScope.launch {
    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.loginState.collectLatest { state ->
            handleLoginState(state)
        }
    }
}
```

### Dependency Injection

Hilt is used to provide dependencies across the application. Key DI modules include:

1. **AppModule**: Provides application-level singleton dependencies
   ```kotlin
   @Module
   @InstallIn(SingletonComponent::class)
   object AppModule {
       @Provides
       @Singleton
       fun provideNetworkUtils(@ApplicationContext context: Context): NetworkUtils {
           return NetworkUtils(context)
       }
       // Other application-level dependencies
   }
   ```

2. **DatabaseModule**: Provides database and DAO instances
   ```kotlin
   @Module
   @InstallIn(SingletonComponent::class)
   object DatabaseModule {
       @Provides
       @Singleton
       fun provideDatabase(@ApplicationContext context: Context): EventDatabase {
           return Room.databaseBuilder(
               context,
               EventDatabase::class.java,
               DATABASE_NAME
           ).build()
       }
       // Other database-related dependencies
   }
   ```

3. **RepositoryModule**: Provides repository implementations
4. **UseCaseModule**: Provides use case instances

### Offline Support Implementation

The application implements a robust offline-first approach:

1. **Repository Layer Strategy**:
   ```kotlin
   fun getEvents(): Flow<Result<List<Event>>> = flow {
       if (networkUtils.isNetworkAvailable()) {
           try {
               val remoteEvents = apiService.getEvents()
               remoteEvents.getOrNull()?.let { events ->
                   // Cache data locally
                   eventDao.insertAllEvents(events)
                   emit(Result.success(events))
               }
           } catch (e: Exception) {
               emit(Result.failure(e))
           }
       } else {
           // Fallback to local data
           val localEvents = eventDao.getAllEvents()
           emit(Result.success(localEvents))
       }
   }
   ```

2. **Data Synchronization**:
   - Local database serves as the single source of truth
   - Remote data is cached locally when available
   - UI works seamlessly with both data sources

3. **Network Status Monitoring**:
   - `NetworkUtils` class monitors connectivity changes
   - Repositories adapt their behavior based on connectivity

### Input Validation System

A comprehensive validation system ensures data integrity:

1. **Validation Rules**: Centralized in `ValidationUtils`
   ```kotlin
   fun validateLoginInput(email: String, password: String): ValidationResult {
       val errors = mutableMapOf<String, String>()
       
       when {
           email.isEmpty() -> errors["email"] = "Email cannot be empty"
           !email.isValidEmail() -> errors["email"] = "Invalid email format"
       }
       
       when {
           password.isEmpty() -> errors["password"] = "Password cannot be empty"
           !password.isValidPassword() -> errors["password"] = 
               "Password must be between 6-20 characters"
       }
       
       return if (errors.isEmpty()) ValidationResult.Success
              else ValidationResult.Error(errors)
   }
   ```

2. **Extension Functions**: Reusable validation logic
   ```kotlin
   fun String.isValidEmail(): Boolean =
       Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+").matcher(this).matches()
   
   fun String.isValidPassword(): Boolean =
       length in Constants.MIN_PASSWORD_LENGTH..Constants.MAX_PASSWORD_LENGTH
   ```

3. **Structured Error Reporting**: Using `InputValidationException`
   ```kotlin
   class InputValidationException(fieldErrors: Map<String, String>) : 
       Exception(fieldErrors.values.firstOrNull() ?: "Validation failed") {
       
       private val fieldErrors: Map<String, String> = fieldErrors
       
       fun getError(field: String): String? = fieldErrors[field]
   }
   ```

### Navigation Implementation

Navigation is managed using the Jetpack Navigation Component:

1. **Navigation Graph**: Defines all destinations and transitions
2. **Type-Safe Navigation**: Using generated `NavDirections` classes
   ```kotlin
   findNavController().navigate(
       LoginFragmentDirections.actionLoginFragmentToEventListFragment()
   )
   ```
3. **Arguments**: Safe passing of data between fragments
   ```kotlin
   findNavController().navigate(
       EventListFragmentDirections.actionEventListFragmentToEventDetailFragment(eventId)
   )
   ```

## Testing Strategy

The application includes a comprehensive testing suite covering all architectural layers.

### Unit Testing

1. **Domain Layer Tests**:
   - Use case functionality
   - Business logic validation
   - Example:
     ```kotlin
     @Test
     fun `validateLoginInput should return Success for valid inputs`() {
         // Act
         val result = ValidationUtils.validateLoginInput(
             email = "test@example.com",
             password = "password123"
         )
     
         // Assert
         assertTrue(result is ValidationUtils.ValidationResult.Success)
     }
     ```

2. **ViewModel Tests**:
   - State transitions
   - Use case interactions
   - Exception handling
   - Example:
     ```kotlin
     @Test
     fun `login with valid credentials should emit Success state`() = runTest {
         // Arrange
         coEvery {
             loginUseCase.invoke("test@example.com", "password")
         } returns Result.success(testUser)
     
         // Act & Assert
         viewModel.loginState.test {
             viewModel.login("test@example.com", "password")
             assertEquals(LoginState.Loading, awaitItem())
             assertEquals(LoginState.Success(testUser), awaitItem())
             cancelAndIgnoreRemainingEvents()
         }
     }
     ```

3. **Repository Tests**:
   - Data source coordination
   - Caching behavior
   - Network handling
   - Example:
     ```kotlin
     @Test
     fun `getEvents should return remote data when network is available`() = runTest {
         // Arrange
         every { networkUtils.isNetworkAvailable() } returns true
         coEvery { apiService.getEvents() } returns Result.success(testEvents)
     
         // Act & Assert
         eventRepository.getEvents().test {
             val result = awaitItem()
             assert(result.isSuccess)
             assertEquals(testEvents, result.getOrNull())
             coVerify { eventDao.insertAllEvents(testEvents) }
             awaitComplete()
         }
     }
     ```

### Test Helpers and Utilities

1. **TestDispatcherRule**: Manages Coroutines main dispatcher in tests
   ```kotlin
   @ExperimentalCoroutinesApi
   class TestDispatcherRule(
       private val testDispatcher: TestDispatcher = StandardTestDispatcher()
   ) : TestWatcher() {
       override fun starting(description: Description) {
           Dispatchers.setMain(testDispatcher)
       }
   
       override fun finished(description: Description) {
           Dispatchers.resetMain()
       }
   }
   ```

2. **Turbine**: Testing Flow emissions
   ```kotlin
   viewModel.events.test {
       assertEquals(EventListState.Loading, awaitItem())
       cancelAndIgnoreRemainingEvents()
   }
   ```

3. **Mockk**: Mocking dependencies
   ```kotlin
   every { networkUtils.isNetworkAvailable() } returns true
   coEvery { apiService.getEvents() } returns Result.success(testEvents)
   ```

### Testing Coverage Targets

- Domain Layer: >90% coverage
- ViewModels: >85% coverage
- Repositories: >80% coverage
- Utility Classes: >75% coverage

## Performance Optimizations

The application implements various optimizations to ensure optimal performance:

### UI Performance

1. **Efficient RecyclerView**:
   - `ListAdapter` with `DiffUtil` for efficient updates
   - View recycling to minimize object creation
   - Proper view holder pattern implementation

2. **Image Loading**:
   - Glide for efficient image loading and caching
   - Proper image sizing and downsampling
   - Memory and disk caching

3. **Lazy Loading**:
   - Content is loaded on-demand as the user navigates
   - Placeholder views during loading states

### Memory Management

1. **Lifecycle Awareness**:
   - Respecting Android lifecycle to prevent memory leaks
   - Proper cancellation of coroutines using lifecycle scope
   - Cleanup of resources in `onDestroyView`

2. **Flow Collection**:
   - Using `repeatOnLifecycle` to automatically handle flow collection based on lifecycle
   ```kotlin
   viewLifecycleOwner.lifecycleScope.launch {
       viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
           viewModel.uiState.collect { state -> renderUiState(state) }
       }
   }
   ```

3. **Bitmap Handling**:
   - Proper scaling and sampling of images
   - Bitmap recycling when appropriate

### Network Optimization

1. **Request Caching**:
   - Data is stored locally to minimize network requests
   - Conditional fetching based on data freshness

2. **Efficient Data Transfer**:
   - Minimizing payload sizes
   - Using proper serialization

## Security Considerations

The application implements security best practices:

### Secure Data Storage

1. **Encrypted Preferences**:
   - User credentials and session data are stored using `EncryptedSharedPreferences`
   ```kotlin
   private val masterKey = MasterKey.Builder(context)
       .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
       .build()
   
   private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
       context,
       "event_app_prefs",
       masterKey,
       EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
       EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
   )
   ```

2. **Secure Database**:
   - Room database with appropriate access control

### Input Sanitization

1. **Validation**:
   - All user inputs are validated before processing
   - Protection against injection attacks

2. **Error Handling**:
   - Proper error handling without exposing internal details

## Future Roadmap

1. **UI/UX Enhancements**
   - Implement dark mode support
   - Add animations and transitions
   - Implement gesture-based navigation

2. **Technical Improvements**
   - Migrate to Jetpack Compose
   - Implement pagination for event lists
   - Add integration tests and UI tests
   - Implement real API integration
   - Add CI/CD pipeline

3. **Feature Additions**
   - Event searching and filtering
   - User profile management
   - Favorites and bookmarks
   - Push notifications for booking updates
   - Social sharing functionality

## Troubleshooting

### Common Issues and Solutions

1. **Login Failures**
   - Check that you're using the correct test credentials
   - Ensure network connectivity
   - Check for validation errors in the input fields

2. **Blank Screens**
   - Verify database initialization
   - Check for exceptions in logcat
   - Ensure ViewModels are properly injected

3. **Build Failures**
   - Check Gradle sync status
   - Verify all dependencies are available
   - Clean and rebuild the project

### Logging and Debugging

The application includes comprehensive logging for troubleshooting:

1. **Log Levels**:
   - DEBUG: Development information
   - INFO: General operational events
   - WARNING: Potential issues that don't affect functionality
   - ERROR: Failures that impact functionality

2. **Debug Tools**:
   - Use Android Studio profiler for performance analysis
   - Examine Room database using Database Inspector
   - View shared preferences in Device File Explorer

## License

```
MIT License

Copyright (c) 2023 Your Name

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
