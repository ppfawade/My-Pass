package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.TripRepository
import com.example.ui.TripScreen
import com.example.ui.theme.MyPassTheme
import com.example.viewmodel.TripViewModelFactory

import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ui.SettingsScreen
import com.example.ui.ProfileScreen

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val db = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java, "trip_database"
    ).fallbackToDestructiveMigration().build()
    val repository = TripRepository(db.tripEventDao(), db.tripDao())
    val factory = TripViewModelFactory(repository)

    setContent {
      MyPassTheme(darkTheme = true) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val viewModel: com.example.viewmodel.TripViewModel = viewModel(factory = factory)
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "trip") {
                composable("trip") {
                    TripScreen(
                        viewModel = viewModel,
                        onNavigateToSettings = { navController.navigate("settings") },
                        onNavigateToProfile = { navController.navigate("profile") }
                    )
                }
                composable("settings") {
                    SettingsScreen(onBack = { navController.popBackStack() })
                }
                composable("profile") {
                    ProfileScreen(onBack = { navController.popBackStack() })
                }
            }
        }
      }
    }
  }
}
