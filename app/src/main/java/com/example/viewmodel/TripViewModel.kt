package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.EventStatus
import com.example.data.TripEvent
import com.example.data.TripRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TripViewModel(private val repository: TripRepository) : ViewModel() {

    val allTrips = repository.allTrips.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val currentTripId = MutableStateFlow<Int?>(null)
    val selectedFilter = MutableStateFlow<EventStatus?>(null) // null for "All"

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    val events: StateFlow<List<TripEvent>> = kotlinx.coroutines.flow.combine(currentTripId, selectedFilter) { tripId, filter ->
        Pair(tripId, filter)
    }.flatMapLatest { (tripId, filter) ->
        if (tripId == null) {
            kotlinx.coroutines.flow.flowOf(emptyList())
        } else {
            repository.getEventsForTrip(tripId, filter)
        }
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            repository.preloadDataIfEmpty()
            allTrips.collect { trips ->
                if (trips.isNotEmpty() && currentTripId.value == null) {
                    currentTripId.value = trips.first().id
                }
            }
        }
    }

    fun setFilter(status: EventStatus?) {
        selectedFilter.value = status
    }

    fun selectTrip(tripId: Int) {
        currentTripId.value = tripId
    }

    fun createTrip(name: String) {
        viewModelScope.launch {
            val newId = repository.createTrip(name)
            currentTripId.value = newId
        }
    }

    fun saveEvent(event: TripEvent) {
        viewModelScope.launch {
            repository.saveEvent(event)
        }
    }

    fun deleteEvent(event: TripEvent) {
        viewModelScope.launch {
            repository.deleteEvent(event)
        }
    }
}

class TripViewModelFactory(private val repository: TripRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TripViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TripViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
