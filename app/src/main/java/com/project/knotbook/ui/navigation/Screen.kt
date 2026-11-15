package com.project.knotbook.ui.navigation

sealed class Screen(val route: String){
    data object Splash: Screen("splash")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object Projects : Screen("project")
    data object AddProject : Screen("addproject")
    data object ProjectDetails : Screen("projectDetails/{projectId}") {
        fun createRoute(projectId: Long): String {
            return "projectDetails/$projectId"
        }
    }
    data object AddTime : Screen("addTime/{projectId}") {
        fun createRoute(projectId: Long) = "addTime/$projectId"
    }
    data object ProjectPhotos : Screen("projectPhotos/{projectId}") {
        fun createRoute(projectId: Long) = "projectPhotos/$projectId"
    }
}