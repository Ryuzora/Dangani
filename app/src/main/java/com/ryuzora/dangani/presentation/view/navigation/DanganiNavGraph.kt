package com.ryuzora.dangani.presentation.view.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ryuzora.dangani.presentation.viewmodel.auth.AuthViewModel
import com.ryuzora.dangani.presentation.view.auth.LoginScreen
import com.ryuzora.dangani.presentation.view.auth.RegisterScreen
import com.ryuzora.dangani.presentation.view.home.HomeScreen
import com.ryuzora.dangani.presentation.view.task.TaskDetailScreen
import com.ryuzora.dangani.presentation.view.task.CreateTaskScreen
import com.ryuzora.dangani.presentation.view.task.EditTaskScreen
import com.ryuzora.dangani.presentation.view.mytasks.MyTasksScreen
import com.ryuzora.dangani.presentation.view.selecthelper.SelectHelperScreen
import com.ryuzora.dangani.presentation.view.submission.WorkSubmissionScreen
import com.ryuzora.dangani.presentation.view.notification.NotificationScreen
import com.ryuzora.dangani.presentation.view.profile.ProfileScreen
import com.ryuzora.dangani.presentation.view.profile.OtherProfileScreen
import com.ryuzora.dangani.presentation.view.customer_service.CustomerServiceScreen
import com.ryuzora.dangani.presentation.viewmodel.customer_service.CustomerServiceViewModel
import com.ryuzora.dangani.data.repository.ChatRepositoryImpl
import com.ryuzora.dangani.domain.usecase.chat.GetChatHistoryUseCase
import com.ryuzora.dangani.domain.usecase.chat.SendMessageUseCase
import com.ryuzora.dangani.DanganiApplication
import com.ryuzora.dangani.data.remote.FirebaseAuthService
import com.ryuzora.dangani.data.remote.FirestoreService
import com.ryuzora.dangani.data.remote.FirebaseStorageService
import com.ryuzora.dangani.data.repository.UserRepositoryImpl
import com.ryuzora.dangani.domain.model.NotificationType
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

    val customerServiceViewModel = remember {
        val chatRepo = ChatRepositoryImpl()
        CustomerServiceViewModel(GetChatHistoryUseCase(chatRepo), SendMessageUseCase(chatRepo))
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
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProfile = { userId ->
                    navController.navigate(Screen.OtherProfile.createRoute(userId))
                }
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
                    val taskId = notification.relatedTaskId
                    if (taskId.isBlank()) return@NotificationScreen

                    val route = when (notification.type) {
                        NotificationType.NEW_APPLICANT -> Screen.SelectHelper.createRoute(taskId)
                        NotificationType.WORK_SUBMITTED -> Screen.EditTask.createRoute(taskId)
                        NotificationType.APPLICATION_ACCEPTED,
                        NotificationType.WORK_ACCEPTED,
                        NotificationType.WORK_REVISION,
                        NotificationType.RATING_RECEIVED -> Screen.WorkSubmission.createRoute(taskId)
                        NotificationType.APPLICATION_NOT_SELECTED -> Screen.TaskDetail.createRoute(taskId)
                    }

                    navController.navigate(route)
                }
            )
        }

        // Profile (self)
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToCustomerService = {
                    navController.navigate(Screen.CustomerService.route)
                }
            )
        }

        composable(Screen.CustomerService.route) {
            CustomerServiceScreen(
                viewModel = customerServiceViewModel,
                onNavigateBack = { navController.popBackStack() }
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




