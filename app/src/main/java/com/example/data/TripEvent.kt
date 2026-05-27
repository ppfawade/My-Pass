package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class EventType {
    FLIGHT,
    RAIL,
    BUS,
    TAXI,
    HOTEL,
    HOSTEL,
    APARTMENT,
    PDF,
    TICKET,
    RESERVATION,
    NOTES,
    LINK,
    PHOTO,
    GALLERY
}

enum class EventStatus {
    IN_PROGRESS,
    UPCOMING,
    PAST
}

@Entity(tableName = "trip_events")
data class TripEvent(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tripId: Int, // Associated trip
    val title: String,
    val subtitle: String? = null, // e.g. "JFK 8:45 AM -> CDG 10:30 AM" or "Hôtel des Arts Montmartre"
    val description: String? = null, // for multi-line notes or addresses
    val type: EventType,
    val status: EventStatus,
    val timeLabel: String, // e.g. "Now - 11:30 AM"
    val url: String? = null, // For Link type
    val pdfContent: String? = null, // e.g. "hotel-reservation.pdf\n212 KB"
    val imageResIdName: String? = null // mock external resource if needed, we'll map to Drawables
)
