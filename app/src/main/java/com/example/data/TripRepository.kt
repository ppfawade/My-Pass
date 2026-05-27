package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TripRepository(private val dao: TripEventDao) {
    val allEvents: Flow<List<TripEvent>> = dao.getAllEvents()

    fun getEvents(status: EventStatus?): Flow<List<TripEvent>> {
        return if (status == null) {
            allEvents
        } else {
            dao.getEventsByStatus(status)
        }
    }

    suspend fun preloadDataIfEmpty() {
        withContext(Dispatchers.IO) {
            if (dao.getCount() == 0) {
                val mockData = listOf(
                    TripEvent(
                        title = "Flight to Paris (AF 123)",
                        subtitle = "JFK 8:45 AM -> CDG 10:30 AM",
                        type = EventType.FLIGHT,
                        status = EventStatus.IN_PROGRESS,
                        timeLabel = "Now - 11:30 AM",
                        imageResIdName = "flight_image"
                    ),
                    TripEvent(
                        title = "Check-in at Hotel",
                        subtitle = "Hôtel des Arts Montmartre",
                        description = "5 Rue Tholozé, 75018 Paris, France",
                        type = EventType.ACCOMMODATION,
                        status = EventStatus.UPCOMING,
                        timeLabel = "Today • 3:00 PM",
                        imageResIdName = "hotel_image"
                    ),
                    TripEvent(
                        title = "Hotel Reservation",
                        subtitle = "Confirmation #HDAM78593",
                        type = EventType.PDF,
                        status = EventStatus.UPCOMING,
                        timeLabel = "Today • 4:00 PM",
                        pdfContent = "hotel-reservation.pdf\n212 KB"
                    ),
                    TripEvent(
                        title = "Notes",
                        description = "Check-in is from 3 PM.\nAsk for a room on a higher floor if possible.",
                        type = EventType.NOTES,
                        status = EventStatus.UPCOMING,
                        timeLabel = "Today • 5:00 PM",
                    ),
                    TripEvent(
                        title = "Louvre Museum",
                        subtitle = "Official website",
                        type = EventType.LINK,
                        status = EventStatus.UPCOMING,
                        timeLabel = "Tomorrow • 9:00 AM",
                        url = "https://www.louvre.fr/en",
                        imageResIdName = "louvre_image"
                    ),
                    TripEvent(
                        title = "Trouver un café sympa",
                        type = EventType.PHOTO,
                        status = EventStatus.UPCOMING,
                        timeLabel = "Tomorrow • 1:00 PM",
                        imageResIdName = "cafe_image"
                    )
                )
                dao.insertAll(mockData)
            }
        }
    }
}
