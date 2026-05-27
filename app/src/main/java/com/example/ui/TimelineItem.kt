package com.example.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.EventStatus
import com.example.data.EventType
import com.example.data.TripEvent

@Composable
fun StatusHeader(statusText: String, dotColor: Color) {
    Row(
        modifier = Modifier.padding(start = 24.dp, bottom = 8.dp, top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = statusText,
            color = dotColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineItem(event: TripEvent, isLast: Boolean, isFirst: Boolean = false, onDelete: () -> Unit = {}, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(horizontal = 16.dp)
    ) {
        // Timeline indicators
        Box(
            modifier = Modifier
                .width(48.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.TopCenter
        ) {
            // Vertical Line
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .padding(top = 28.dp) // Start line below the circle
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                )
            }
            if (!isFirst) {
               Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(28.dp) // End line above the circle
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                ) 
            }

            // Icon Circle
            val icon = getIconForEventType(event.type)
            val iconColor = MaterialTheme.colorScheme.primary
            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Content Card
        val dismissState = rememberSwipeToDismissBoxState(
            confirmValueChange = { dismissValue ->
                if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                    onDelete()
                    true
                } else {
                    false
                }
            }
        )

        val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
        val context = androidx.compose.ui.platform.LocalContext.current

        SwipeToDismissBox(
            state = dismissState,
            enableDismissFromStartToEnd = false,
            enableDismissFromEndToStart = true,
            modifier = Modifier.weight(1f),
            backgroundContent = {
                val color = MaterialTheme.colorScheme.error
                Box(
                    Modifier.fillMaxSize()
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(color)
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onError)
                }
            }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 8.dp)
                    .clickable { onClick() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Main content Row
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = event.timeLabel,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = event.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (!event.subtitle.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = event.subtitle,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (!event.description.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = event.description,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 20.sp
                            )
                        }

                        // Event-specific content (text/chips)
                        EventSpecificTextContent(event)
                    }

                    // Side Image if applicable
                    val isSideImage = setOf(EventType.FLIGHT, EventType.RAIL, EventType.BUS, EventType.TAXI, EventType.HOTEL, EventType.HOSTEL, EventType.APARTMENT, EventType.LINK).contains(event.type)
                    if (isSideImage && !event.imageResIdName.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.width(16.dp))
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(60.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Image, contentDescription = "Image Placeholder", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                // Below Image if applicable
                if (event.type == EventType.PHOTO && !event.imageResIdName.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Image, contentDescription = "Image Placeholder", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        }
    }
}

@Composable
fun EventSpecificTextContent(event: TripEvent) {
    if ((event.type == EventType.FLIGHT || event.type == EventType.RAIL || event.type == EventType.BUS || event.type == EventType.TAXI) && event.status == EventStatus.IN_PROGRESS) {
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF10B981).copy(alpha = 0.1f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
             Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DirectionsTransit, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF10B981))
                Spacer(modifier = Modifier.width(4.dp))
                Text("In Progress", color = Color(0xFF10B981), fontSize = 12.sp, fontWeight = FontWeight.Bold)
             }
        }
    } else if (event.type == EventType.PDF && !event.pdfContent.isNullOrEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color.Red)
            Spacer(modifier = Modifier.width(12.dp))
            val parts = event.pdfContent.split("\n")
            Column {
                Text(parts.getOrNull(0) ?: "Document", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Text(parts.getOrNull(1) ?: "", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
        }
    } else if (event.type == EventType.LINK && !event.url.isNullOrEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = event.url,
            color = MaterialTheme.colorScheme.primary, // typical link color
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

fun getIconForEventType(type: EventType): ImageVector {
    return when (type) {
        EventType.FLIGHT -> Icons.Default.Flight
        EventType.RAIL -> Icons.Default.Train
        EventType.BUS -> Icons.Default.DirectionsBus
        EventType.TAXI -> Icons.Default.LocalTaxi
        EventType.HOTEL -> Icons.Default.Bed
        EventType.HOSTEL -> Icons.Default.LocalHotel
        EventType.APARTMENT -> Icons.Default.Home
        EventType.PDF -> Icons.AutoMirrored.Filled.Article
        EventType.TICKET -> Icons.Default.ConfirmationNumber
        EventType.RESERVATION -> Icons.Default.EventSeat
        EventType.NOTES -> Icons.AutoMirrored.Filled.Notes
        EventType.LINK -> Icons.Default.Link
        EventType.PHOTO -> Icons.Default.Image
        EventType.GALLERY -> Icons.Default.PhotoLibrary
    }
}
