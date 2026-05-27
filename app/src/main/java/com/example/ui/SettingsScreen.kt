package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.content.Context

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE) }

    var region by remember { mutableStateOf(sharedPrefs.getString("region", "United States") ?: "United States") }
    var use24Hour by remember { mutableStateOf(sharedPrefs.getBoolean("use24Hour", true)) }
    var pushNotifications by remember { mutableStateOf(sharedPrefs.getBoolean("pushNotifications", true)) }
    var preTripReminders by remember { mutableStateOf(sharedPrefs.getBoolean("preTripReminders", true)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { pad ->
        Column(modifier = Modifier.padding(pad).fillMaxSize().padding(16.dp)) {
            Text("Common Region", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = region, 
                onValueChange = { 
                    region = it 
                    sharedPrefs.edit().putString("region", it).apply()
                }, 
                label = { Text("Region") }, 
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text("Time Zone Settings", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            ListItem(
                headlineContent = { Text("Time Format") }, 
                supportingContent = { Text("24-hour clock (e.g. 15:00)") }, 
                trailingContent = { 
                    Switch(
                        checked = use24Hour, 
                        onCheckedChange = { 
                            use24Hour = it 
                            sharedPrefs.edit().putBoolean("use24Hour", it).apply()
                        }
                    ) 
                }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("Notifications Settings", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            ListItem(
                headlineContent = { Text("Push Notifications") }, 
                trailingContent = { 
                    Switch(
                        checked = pushNotifications, 
                        onCheckedChange = { 
                            pushNotifications = it 
                            sharedPrefs.edit().putBoolean("pushNotifications", it).apply()
                        }
                    ) 
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text("Reminder Settings", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            ListItem(
                headlineContent = { Text("Pre-trip Reminders") }, 
                supportingContent = { Text("Get reminded 24h before travel") }, 
                trailingContent = { 
                    Switch(
                        checked = preTripReminders, 
                        onCheckedChange = { 
                            preTripReminders = it 
                            sharedPrefs.edit().putBoolean("preTripReminders", it).apply()
                        }
                    ) 
                }
            )
        }
    }
}
