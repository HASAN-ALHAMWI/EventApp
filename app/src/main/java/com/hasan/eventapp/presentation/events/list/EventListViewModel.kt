package com.hasan.eventapp.presentation.events.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasan.eventapp.domain.models.EventDomain
import com.hasan.eventapp.domain.usecases.auth.LogoutUseCase
import com.hasan.eventapp.domain.usecases.events.GetEventsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class EventListViewModel @Inject constructor(
    private val getEventsUseCase: GetEventsUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    // ===========================
    // State Management
    // ===========================
    private val _events = MutableStateFlow<EventListState>(EventListState.Loading)
    val events: StateFlow<EventListState> = _events.asStateFlow()

    private val _logoutState = MutableStateFlow<LogoutState>(LogoutState.Initial)
    val logoutState: StateFlow<LogoutState> = _logoutState.asStateFlow()

    // ===========================
    // Initialization
    // ===========================
    init {
        loadEvents()
    }

    // ===========================
    // Public Methods
    // ===========================
    fun refreshEvents() {
        loadEvents()
    }

    fun logout() {
        viewModelScope.launch {
            try {
                _logoutState.value = LogoutState.Loading
                logoutUseCase.logout()
                _logoutState.value = LogoutState.Success
            } catch (e: Exception) {
                _logoutState.value = LogoutState.Error(
                    e.message ?: ERROR_MESSAGE_LOGOUT_FAILED
                )
            }
        }
    }

    // ===========================
    // Private Methods
    // ===========================
    private fun loadEvents() {
        _events.value = EventListState.Loading

        getEventsUseCase()
            .onEach { result ->
                _events.value = result.fold(
                    onSuccess = { EventListState.Success(it) },
                    onFailure = {
                        EventListState.Error(it.message ?: ERROR_MESSAGE_LOAD_EVENTS)
                    }
                )
            }
            .catch { exception ->
                _events.value = EventListState.Error(
                    exception.message ?: ERROR_MESSAGE_UNKNOWN
                )
            }
            .launchIn(viewModelScope)
    }

    // ===========================
    // Constants
    // ===========================
    companion object {
        private const val ERROR_MESSAGE_LOAD_EVENTS = "Failed to load events"
        private const val ERROR_MESSAGE_UNKNOWN = "Unknown error occurred"
        private const val ERROR_MESSAGE_LOGOUT_FAILED = "Logout failed"
    }
}

sealed class LogoutState {
    data object Initial : LogoutState()
    data object Loading : LogoutState()
    data object Success : LogoutState()
    data class Error(val message: String) : LogoutState()
}

sealed class EventListState {
    data object Loading : EventListState()
    data class Success(val events: List<EventDomain>) : EventListState()
    data class Error(val message: String) : EventListState()
}