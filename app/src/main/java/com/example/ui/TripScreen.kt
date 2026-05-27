package com.example.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.EventStatus
import com.example.data.EventType
import com.example.data.TripEvent
import com.example.viewmodel.TripViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripScreen(viewModel: TripViewModel, onNavigateToSettings: () -> Unit, onNavigateToProfile: () -> Unit) {
    val events by viewModel.events.collectAsState()
    val filter by viewModel.selectedFilter.collectAsState()
    val allTrips by viewModel.allTrips.collectAsState()
    val currentTripId by viewModel.currentTripId.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

    var showAboutDialog by remember { mutableStateOf(false) }
    var selectedEvent by remember { mutableStateOf<TripEvent?>(null) }
    var editingEvent by remember { mutableStateOf<TripEvent?>(null) }
    var createEventDialog by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    var activeDateTarget by remember { mutableStateOf("") }
    var activeTimeTarget by remember { mutableStateOf("") }
    var onDateConfirmed by remember { mutableStateOf<(String) -> Unit>({}) }
    var onTimeConfirmed by remember { mutableStateOf<(String) -> Unit>({}) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        onDateConfirmed(sdf.format(Date(millis)))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Select Time") },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(onClick = {
                    onTimeConfirmed(String.format(Locale.getDefault(), "%02d:%02d", timePickerState.hour, timePickerState.minute))
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            }
        )
    }

    var showCreateTripDialog by remember { mutableStateOf(false) }

    if (showCreateTripDialog) {
        var newTripName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCreateTripDialog = false },
            title = { Text("Create Trip") },
            text = {
                OutlinedTextField(
                    value = newTripName,
                    onValueChange = { newTripName = it },
                    label = { Text("Trip Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (newTripName.isNotBlank()) {
                        viewModel.createTrip(newTripName)
                    }
                    showCreateTripDialog = false
                }) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateTripDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showAboutDialog) {
        AboutDialog(onDismiss = { showAboutDialog = false })
    }

    selectedEvent?.let { event ->
        EventDetailsDialog(
            event = event,
            onDismiss = { selectedEvent = null },
            onEdit = {
                editingEvent = event
                selectedEvent = null
            }
        )
    }

    editingEvent?.let { event ->
        var editTitle by remember { mutableStateOf(event.title) }
        var editSubtitle by remember { mutableStateOf(event.subtitle ?: "") }
        var editDescription by remember { mutableStateOf(event.description ?: "") }
        var editDate by remember { mutableStateOf(event.timeLabel.substringBefore(" • ").trim()) }
        var editTime by remember { mutableStateOf(event.timeLabel.substringAfter(" • ", "").trim()) }
        
        AlertDialog(
            onDismissRequest = { editingEvent = null },
            title = { Text("Edit Event") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = editTitle, onValueChange = { editTitle = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editSubtitle, onValueChange = { editSubtitle = it }, label = { Text("Subtitle") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editDescription, onValueChange = { editDescription = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editDate,
                            onValueChange = {},
                            label = { Text("Date") },
                            readOnly = true,
                            modifier = Modifier.weight(1f),
                            trailingIcon = {
                                IconButton(onClick = { 
                                    onDateConfirmed = { editDate = it }
                                    showDatePicker = true 
                                }) {
                                    Icon(Icons.Default.DateRange, "Select Date")
                                }
                            }
                        )
                        OutlinedTextField(
                            value = editTime,
                            onValueChange = {},
                            label = { Text("Time") },
                            readOnly = true,
                            modifier = Modifier.weight(1f),
                            trailingIcon = {
                                IconButton(onClick = { 
                                    onTimeConfirmed = { editTime = it }
                                    showTimePicker = true 
                                }) {
                                    Icon(Icons.Default.AccessTime, "Select Time")
                                }
                            }
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val formattedTimeLabel = if (editDate.isNotBlank() && editTime.isNotBlank()) {
                        "$editDate • $editTime"
                    } else if (editDate.isNotBlank()) {
                        editDate
                    } else if (editTime.isNotBlank()) {
                        editTime
                    } else {
                        "No time set"
                    }
                    viewModel.saveEvent(event.copy(
                        title = editTitle, 
                        subtitle = editSubtitle.ifBlank { null }, 
                        description = editDescription.ifBlank { null },
                        timeLabel = formattedTimeLabel
                    ))
                    editingEvent = null
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingEvent = null }) { Text("Cancel") }
            }
        )
    }

    if (createEventDialog && currentTripId != null) {
        var createTitle by remember { mutableStateOf("") }
        var createSubtitle by remember { mutableStateOf("") }
        var createType by remember { mutableStateOf(EventType.NOTES) }
        var createDescription by remember { mutableStateOf("") }
        var createStatus by remember { mutableStateOf(EventStatus.UPCOMING) }
        var createDate by remember { mutableStateOf("") }
        var createTime by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { createEventDialog = false },
            title = { Text("Add Card") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    var expandedType by remember { mutableStateOf(false) }
                    Box {
                        OutlinedTextField(
                            value = createType.name,
                            onValueChange = {},
                            label = { Text("Type") },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = { expandedType = true }) {
                                    Icon(Icons.Default.KeyboardArrowDown, "Select Type")
                                }
                            }
                        )
                        DropdownMenu(expanded = expandedType, onDismissRequest = { expandedType = false }) {
                            EventType.values().forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type.name) },
                                    onClick = { 
                                        createType = type 
                                        expandedType = false 
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = createTitle, onValueChange = { createTitle = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = createSubtitle, onValueChange = { createSubtitle = it }, label = { Text("Subtitle") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = createDescription, onValueChange = { createDescription = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = createDate,
                            onValueChange = {},
                            label = { Text("Date") },
                            readOnly = true,
                            modifier = Modifier.weight(1f),
                            trailingIcon = {
                                IconButton(onClick = { 
                                    onDateConfirmed = { createDate = it }
                                    showDatePicker = true 
                                }) {
                                    Icon(Icons.Default.DateRange, "Select Date")
                                }
                            }
                        )
                        OutlinedTextField(
                            value = createTime,
                            onValueChange = {},
                            label = { Text("Time") },
                            readOnly = true,
                            modifier = Modifier.weight(1f),
                            trailingIcon = {
                                IconButton(onClick = { 
                                    onTimeConfirmed = { createTime = it }
                                    showTimePicker = true 
                                }) {
                                    Icon(Icons.Default.AccessTime, "Select Time")
                                }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    var expandedStatus by remember { mutableStateOf(false) }
                    Box {
                        OutlinedTextField(
                            value = createStatus.name,
                            onValueChange = {},
                            label = { Text("Status") },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = { expandedStatus = true }) {
                                    Icon(Icons.Default.KeyboardArrowDown, "Select Status")
                                }
                            }
                        )
                        DropdownMenu(expanded = expandedStatus, onDismissRequest = { expandedStatus = false }) {
                            EventStatus.values().forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(status.name) },
                                    onClick = { 
                                        createStatus = status 
                                        expandedStatus = false 
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (createTitle.isNotBlank()) {
                        val formattedTimeLabel = if (createDate.isNotBlank() && createTime.isNotBlank()) {
                            "$createDate • $createTime"
                        } else if (createDate.isNotBlank()) {
                            createDate
                        } else if (createTime.isNotBlank()) {
                            createTime
                        } else {
                            "No time set"
                        }
                        viewModel.saveEvent(
                            TripEvent(
                                tripId = currentTripId!!,
                                title = createTitle,
                                subtitle = createSubtitle.ifBlank { null },
                                description = createDescription.ifBlank { null },
                                type = createType,
                                status = createStatus,
                                timeLabel = formattedTimeLabel
                            )
                        )
                        createEventDialog = false
                    }
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { createEventDialog = false }) { Text("Cancel") }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                // User requested: "It should just say 'My Pass' with stylized text."
                Text("My Pass", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = MaterialTheme.colorScheme.primary)
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Profile") },
                    selected = false,
                    onClick = { 
                        coroutineScope.launch { drawerState.close() }
                        onNavigateToProfile()
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = false,
                    onClick = { 
                        coroutineScope.launch { drawerState.close() }
                        onNavigateToSettings() 
                    }
                )
                NavigationDrawerItem(
                    label = { Text("About") },
                    selected = false,
                    onClick = { 
                        coroutineScope.launch { drawerState.close() }
                        showAboutDialog = true 
                    }
                )
            }
        }
    ) {
        val snackbarHostState = androidx.compose.runtime.remember { SnackbarHostState() }
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
            var isSearching by remember { mutableStateOf(false) }
            val searchQuery by viewModel.searchQuery.collectAsState()

            TopAppBar(
                title = {
                    if (isSearching) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.updateSearchQuery(it) },
                            placeholder = { Text("Search...") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(end = 8.dp),
                            shape = CircleShape,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                            ),
                            trailingIcon = {
                                IconButton(onClick = { 
                                    isSearching = false 
                                    viewModel.updateSearchQuery("")
                                }) {
                                    Icon(Icons.Default.Close, contentDescription = "Close search")
                                }
                            }
                        )
                    } else {
                        var expanded by remember { mutableStateOf(false) }
                        Box {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { expanded = true }
                            ) {
                                val currentTripName = allTrips.find { it.id == currentTripId }?.name ?: ""
                                Text(currentTripName, fontWeight = FontWeight.Bold)
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Dropdown")
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                allTrips.forEach { trip ->
                                    DropdownMenuItem(
                                        text = { Text(trip.name) },
                                        onClick = { 
                                            viewModel.selectTrip(trip.id)
                                            expanded = false 
                                        }
                                    )
                                }
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text("Create Trip", color = MaterialTheme.colorScheme.primary) },
                                    onClick = {
                                        expanded = false
                                        showCreateTripDialog = true
                                    }
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    if (!isSearching) {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                },
                actions = {
                    if (!isSearching) {
                        IconButton(onClick = { isSearching = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    if (currentTripId != null) {
                        createEventDialog = true
                    } else {
                        coroutineScope.launch { snackbarHostState.showSnackbar("Create a trip first") }
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Card", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            FilterChipsRow(
                selectedFilter = filter,
                onFilterSelected = { viewModel.setFilter(it) }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp), // padding for FAB
            ) {
                val inProgressEvents = events.filter { it.status == EventStatus.IN_PROGRESS }
                val upcomingEvents = events.filter { it.status == EventStatus.UPCOMING }

                if (inProgressEvents.isNotEmpty()) {
                    item {
                        StatusHeader("IN PROGRESS", Color(0xFF10B981))
                    }
                    itemsIndexed(inProgressEvents, key = { _, e -> e.id }) { index, event ->
                        val isLast = index == inProgressEvents.size - 1 && upcomingEvents.isEmpty()
                        TimelineItem(
                            event = event,
                            isLast = isLast,
                            isFirst = index == 0,
                            onDelete = { viewModel.deleteEvent(event) },
                            onClick = { selectedEvent = event }
                        )
                    }
                }

                if (upcomingEvents.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        StatusHeader("UPCOMING", Color(0xFF3B82F6))
                    }
                    itemsIndexed(upcomingEvents, key = { _, e -> e.id }) { index, event ->
                        val isLast = index == upcomingEvents.size - 1
                        TimelineItem(
                            event = event,
                            isLast = isLast,
                            isFirst = inProgressEvents.isEmpty() && index == 0,
                            onDelete = { viewModel.deleteEvent(event) },
                            onClick = { selectedEvent = event }
                        )
                    }
                }
            }
        }
    }
    }
}

@Composable
fun FilterChipsRow(selectedFilter: EventStatus?, onFilterSelected: (EventStatus?) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        FilterTab(
            text = "All",
            isSelected = selectedFilter == null,
            onClick = { onFilterSelected(null) },
            modifier = Modifier.weight(1f)
        )
        FilterTab(
            text = "In Progress",
            isSelected = selectedFilter == EventStatus.IN_PROGRESS,
            onClick = { onFilterSelected(EventStatus.IN_PROGRESS) },
            modifier = Modifier.weight(1f)
        )
        FilterTab(
            text = "Upcoming",
            isSelected = selectedFilter == EventStatus.UPCOMING,
            onClick = { onFilterSelected(EventStatus.UPCOMING) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun FilterTab(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.background else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
