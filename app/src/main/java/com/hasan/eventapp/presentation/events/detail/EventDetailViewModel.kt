package com.hasan.eventapp.presentation.events.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasan.eventapp.domain.models.EventDomain
import com.hasan.eventapp.domain.usecases.events.GetEventDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class EventDetailViewModel @Inject constructor(
    private val getEventDetailsUseCase: GetEventDetailsUseCase
) : ViewModel() {

    // ===========================
    // State Management
    // ===========================
    private val _uiState = MutableStateFlow<EventDetailUiState>(EventDetailUiState.Loading)
    val uiState: StateFlow<EventDetailUiState> = _uiState

    // ===========================
    // Public Methods
    // ===========================
    fun loadEventDetails(eventId: String) {
        viewModelScope.launch {
            setLoadingState()
            fetchEventDetails(eventId)
        }
    }

    // ===========================
    // Private Methods
    // ===========================
    private fun setLoadingState() {
        _uiState.value = EventDetailUiState.Loading
    }

    private suspend fun fetchEventDetails(eventId: String) {
        getEventDetailsUseCase(eventId)
            .catch { exception ->
                handleError(exception)
            }
            .collect { result ->
                processResult(result)
            }
    }

    private fun processResult(result: Result<EventDomain>) {
        result.fold(
            onSuccess = { event ->
                _uiState.value = EventDetailUiState.Success(event)
            },
            onFailure = { error ->
                handleError(error)
            }
        )
    }

    private fun handleError(error: Throwable) {
        _uiState.value = EventDetailUiState.Error(
            error.message ?: "An unknown error occurred"
        )
    }
}

sealed class EventDetailUiState {
    data object Loading : EventDetailUiState()
    data class Success(val event: EventDomain) : EventDetailUiState()
    data class Error(val message: String) : EventDetailUiState()
}