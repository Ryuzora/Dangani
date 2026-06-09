package com.ryuzora.dangani.presentation.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object CreateTask : Screen("create_task")
    object MyTasks : Screen("my_tasks")
    object Notifications : Screen("notifications")
    object Profile : Screen("profile")

    object TaskDetail : Screen("task_detail/{taskId}") {
        fun createRoute(taskId: String) = "task_detail/$taskId"
    }

    object EditTask : Screen("edit_task/{taskId}") {
        fun createRoute(taskId: String) = "edit_task/$taskId"
    }

    object SelectHelper : Screen("select_helper/{taskId}") {
        fun createRoute(taskId: String) = "select_helper/$taskId"
    }

    object WorkSubmission : Screen("work_submission/{taskId}") {
        fun createRoute(taskId: String) = "work_submission/$taskId"
    }

    object OtherProfile : Screen("other_profile/{userId}") {
        fun createRoute(userId: String) = "other_profile/$userId"
    }
}
