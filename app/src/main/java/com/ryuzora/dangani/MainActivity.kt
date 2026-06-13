package com.ryuzora.dangani

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.ryuzora.dangani.data.local.DanganiDatabase
import com.ryuzora.dangani.data.remote.FirebaseAuthService
import com.ryuzora.dangani.data.remote.FirestoreService
import com.ryuzora.dangani.data.remote.FirebaseStorageService
import com.ryuzora.dangani.data.repository.NotificationRepositoryImpl
import com.ryuzora.dangani.presentation.view.navigation.BottomNavBar
import com.ryuzora.dangani.presentation.view.navigation.DanganiNavGraph
import com.ryuzora.dangani.presentation.view.navigation.Screen
import com.ryuzora.dangani.ui.theme.DanganiTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

import androidx.compose.runtime.collectAsState
import com.ryuzora.dangani.ui.theme.ThemeManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val themeManager = ThemeManager.getInstance(this)
        
        setContent {
            val isDarkMode by themeManager.isDarkMode.collectAsState()
            
            DanganiTheme(darkTheme = isDarkMode) {
                DanganiApp()
            }
        }
    }
}

@Composable
fun DanganiApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Determine start destination based on Firebase Auth state
    val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
    val startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route

    // Screens where bottom nav should be visible
    val bottomNavScreens = listOf(
        Screen.Home.route,
        Screen.MyTasks.route,
        Screen.Notifications.route,
        Screen.Profile.route
    )
    val showBottomNav = currentRoute in bottomNavScreens

    // Notification unread count
    var notificationCount by remember { mutableIntStateOf(0) }

    // Load unread notification count
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect
            val db = DanganiApplication.instance.database
            val firestoreService = FirestoreService()
            val notifRepo = NotificationRepositoryImpl(db.notificationDao(), firestoreService)

            launch {
                notifRepo.getUnreadCount(userId, "requester").collectLatest { requesterCount ->
                    notifRepo.getUnreadCount(userId, "helper").collectLatest { helperCount ->
                        notificationCount = requesterCount + helperCount
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomNav) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    notificationCount = notificationCount,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            // Pop up to Home to avoid building up a large stack
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        DanganiNavGraph(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}
