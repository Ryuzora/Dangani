package com.ryuzora.dangani.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ryuzora.dangani.presentation.auth.AuthViewModel
import com.ryuzora.dangani.presentation.auth.LoginScreen
import com.ryuzora.dangani.presentation.auth.RegisterScreen
import com.ryuzora.dangani.presentation.home.HomeScreen
import com.ryuzora.dangani.presentation.task.TaskDetailScreen
import com.ryuzora.dangani.presentation.task.CreateTaskScreen
import com.ryuzora.dangani.presentation.task.EditTaskScreen
import com.ryuzora.dangani.presentation.mytasks.MyTasksScreen
import com.ryuzora.dangani.presentation.selecthelper.SelectHelperScreen
import com.ryuzora.dangani.presentation.submission.WorkSubmissionScreen
import com.ryuzora.dangani.presentation.notification.NotificationScreen
import com.ryuzora.dangani.presentation.profile.ProfileScreen
import com.ryuzora.dangani.presentation.profile.OtherProfileScreen
import com.ryuzora.dangani.DanganiApplication
import com.ryuzora.dangani.data.remote.FirebaseAuthService
import com.ryuzora.dangani.data.remote.FirestoreService
import com.ryuzora.dangani.data.remote.FirebaseStorageService
import com.ryuzora.dangani.data.repository.UserRepositoryImpl
import com.ryuzora.dangani.domain.usecase.auth.LoginUseCase
import com.ryuzora.dangani.domain.usecase.auth.RegisterUseCase

@Composable
fun DanganiNavGraph(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    // Shared AuthViewModel for Login/Register
    val authViewModel = remember {
        val db = DanganiApplication.instance.database
        val userRepo = UserRepositoryImpl(db.userDao(), FirebaseAuthService(), FirestoreService(), FirebaseStorageService())
        AuthViewModel(LoginUseCase(userRepo), RegisterUseCase(userRepo))
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Auth
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Home
        composable(Screen.Home.route) {
            HomeScreen(
                onTaskClick = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                },
                onCreateTask = {
                    navController.navigate(Screen.CreateTask.route)
                }
            )
        }

        // Task Detail
        composable(
            route = Screen.TaskDetail.route,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: return@composable
            TaskDetailScreen(
                taskId = taskId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProfile = { userId ->
                    navController.navigate(Screen.OtherProfile.createRoute(userId))
                },
                onNavigateToEditTask = {
                    navController.navigate(Screen.EditTask.createRoute(taskId))
                }
                    ,
                    onNavigateToWorkSubmission = { id -> navController.navigate(Screen.WorkSubmission.createRoute(id)) }
            )
        }

        // Create Task
        composable(Screen.CreateTask.route) {
            CreateTaskScreen(
                onNavigateBack = { navController.popBackStack() },
                onTaskCreated = {
                    navController.popBackStack()
                }
            )
        }

        // Edit Task
        composable(
            route = Screen.EditTask.route,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: return@composable
            EditTaskScreen(
                taskId = taskId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSelectHelper = {
                    navController.navigate(Screen.SelectHelper.createRoute(taskId))
                },
                onNavigateToProfile = { userId ->
                    navController.navigate(Screen.OtherProfile.createRoute(userId))
                }
            )
        }

        // Select Helper
        composable(
            route = Screen.SelectHelper.route,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: return@composable
            SelectHelperScreen(
                taskId = taskId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProfile = { userId ->
                    navController.navigate(Screen.OtherProfile.createRoute(userId))
                }
            )
        }

        // Work Submission
        composable(
            route = Screen.WorkSubmission.route,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: return@composable
            WorkSubmissionScreen(
                taskId = taskId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // My Tasks
        composable(Screen.MyTasks.route) {
            MyTasksScreen(
                onTaskClick = { taskId ->
                    navController.navigate(Screen.EditTask.createRoute(taskId))
                },
                onHelperTaskClick = { taskId ->
                    navController.navigate(Screen.WorkSubmission.createRoute(taskId))
                }
            )
        }

        // Notifications
        composable(Screen.Notifications.route) {
            NotificationScreen(
                onNotificationClick = { notification ->
                    navController.navigate(Screen.TaskDetail.createRoute(notification.relatedTaskId))
                }
            )
        }

        // Profile (self)
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToLogin = {
                    authViewModel.resetState()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Other User Profile
        composable(
            route = Screen.OtherProfile.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            OtherProfileScreen(
                userId = userId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
