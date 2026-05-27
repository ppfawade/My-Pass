package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TripRepository(private val eventDao: TripEventDao, private val tripDao: TripDao) {
    val allTrips: Flow<List<Trip>> = tripDao.getAllTrips()

    fun getEventsForTrip(tripId: Int, status: EventStatus?): Flow<List<TripEvent>> {
        return if (status == null) {
            eventDao.getEventsForTrip(tripId)
        } else {
            eventDao.getEventsForTripByStatus(tripId, status)
        }
    }

    suspend fun createTrip(name: String): Int {
        return withContext(Dispatchers.IO) {
            tripDao.insert(Trip(name = name)).toInt()
        }
    }

    suspend fun saveEvent(event: TripEvent) {
        withContext(Dispatchers.IO) {
            eventDao.insertEvent(event)
        }
    }

    suspend fun deleteEvent(event: TripEvent) {
        withContext(Dispatchers.IO) {
            eventDao.deleteEvent(event)
        }
    }

    suspend fun preloadDataIfEmpty() {
        withContext(Dispatchers.IO) {
            if (tripDao.getCount() == 0) {
                val demoTripId = tripDao.insert(Trip(name = "My Trip to Paris")).toInt()
                val mockData = listOf(
                    TripEvent(
                        tripId = demoTripId,
                        title = "Flight to Paris (AF 123)",
                        subtitle = "JFK 8:45 AM -> CDG 10:30 AM",
                        type = EventType.FLIGHT,
                        status = EventStatus.IN_PROGRESS,
                        timeLabel = "Now - 11:30 AM",
                        imageResIdName = "flight_image"
                    ),
                    TripEvent(
                        tripId = demoTripId,
                        title = "Check-in at Hotel",
                        subtitle = "Hôtel des Arts Montmartre",
                        description = "5 Rue Tholozé, 75018 Paris, France",
                        type = EventType.HOTEL,
                        status = EventStatus.UPCOMING,
                        timeLabel = "Today • 3:00 PM",
                        imageResIdName = "hotel_image"
                    ),
                    TripEvent(
                        tripId = demoTripId,
                        title = "Hotel Reservation",
                        subtitle = "Confirmation #HDAM78593",
                        type = EventType.PDF,
                        status = EventStatus.UPCOMING,
                        timeLabel = "Today • 4:00 PM",
                        pdfContent = "hotel-reservation.pdf\n212 KB"
                    ),
                    TripEvent(
                        tripId = demoTripId,
                        title = "Notes",
                        description = "Check-in is from 3 PM.\nAsk for a room on a higher floor if possible.",
                        type = EventType.NOTES,
                        status = EventStatus.UPCOMING,
                        timeLabel = "Today • 5:00 PM",
                    ),
                    TripEvent(
                        tripId = demoTripId,
                        title = "Louvre Museum",
                        subtitle = "Official website",
                        type = EventType.LINK,
                        status = EventStatus.UPCOMING,
                        timeLabel = "Tomorrow • 9:00 AM",
                        url = "https://www.louvre.fr/en",
                        imageResIdName = "louvre_image"
                    ),
                    TripEvent(
                        tripId = demoTripId,
                        title = "Trouver un café sympa",
                        type = EventType.PHOTO,
                        status = EventStatus.UPCOMING,
                        timeLabel = "Tomorrow • 1:00 PM",
                        imageResIdName = "cafe_image"
                    )
                )
                eventDao.insertAll(mockData)
            }
        }
    }
}
