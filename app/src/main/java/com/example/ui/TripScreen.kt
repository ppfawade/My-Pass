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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripScreen(viewModel: TripViewModel) {
    val events by viewModel.events.collectAsState()
    val filter by viewModel.selectedFilter.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                Text("My Pass Menu", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = false,
                    onClick = { /*TODO*/ }
                )
                NavigationDrawerItem(
                    label = { Text("About") },
                    selected = false,
                    onClick = { /*TODO*/ }
                )
            }
        }
    ) {
        val snackbarHostState = androidx.compose.runtime.remember { SnackbarHostState() }
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
            TopAppBar(
                title = {
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { expanded = true }
                        ) {
                            Text("My Trip to Paris", fontWeight = FontWeight.Bold)
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Dropdown")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("My Trip to Paris") },
                                onClick = { expanded = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Create Trip") },
                                onClick = {
                                    expanded = false
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Create Trip clicked")
                                    }
                                }
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            drawerState.open()
                        }
                    }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Search functionality coming soon")
                        }
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Options menu clicked")
                        }
                    }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            CustomBottomNavigation(snackbarHostState, coroutineScope)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /*TODO*/ },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
                modifier = Modifier.offset(y = 48.dp) // Offset to overlap bottom bar
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Trip", tint = MaterialTheme.colorScheme.onPrimary)
            }
        },
        floatingActionButtonPosition = FabPosition.Center
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
                            onDelete = { viewModel.deleteEvent(event) }
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
                            onDelete = { viewModel.deleteEvent(event) }
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

@Composable
fun CustomBottomNavigation(snackbarHostState: SnackbarHostState, coroutineScope: kotlinx.coroutines.CoroutineScope) {
    var selectedItem by remember { mutableIntStateOf(1) }
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = selectedItem == 0,
            onClick = { 
                selectedItem = 0
                coroutineScope.launch { snackbarHostState.showSnackbar("Home") }
            }
        )
        // Add empty space for FAB
        NavigationBarItem(
            icon = { Icon(Icons.Default.Luggage, contentDescription = "My Trips", tint = Color.Transparent) },
            label = { Text("My Trips", color = MaterialTheme.colorScheme.primary) },
            selected = selectedItem == 1,
            onClick = { 
                selectedItem = 1 
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = selectedItem == 2,
            onClick = { 
                selectedItem = 2 
                coroutineScope.launch { snackbarHostState.showSnackbar("Profile") }
            }
        )
    }
}
