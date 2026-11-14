package com.project.knotbook.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.project.knotbook.ui.screens.AddProjectScreen
import com.project.knotbook.ui.screens.HomeScreen
import com.project.knotbook.ui.screens.LoginScreen
import com.project.knotbook.ui.screens.ProjectsScreen
import com.project.knotbook.ui.screens.RegisterScreen
import com.project.knotbook.ui.screens.SplashScreen
import com.project.knotbook.viewmodel.SessionViewModel

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    val sessionViewModel: SessionViewModel = hiltViewModel()
    val startDestination by
    sessionViewModel.startDestination.collectAsStateWithLifecycle()

    if (startDestination == null) {
        SplashScreen(  onContinue = {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Splash.route) {
                    inclusive = true
                }
            }
        })
    } else {
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route
        )
        {

            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) {
                                inclusive = true
                            }
                        }
                    },
                    onRegisterClick = {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Register.route) {
                                inclusive = true
                            }
                        }
                    },
                    onLoginClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    onProjectsClick = {
                        navController.navigate(Screen.Projects.route)
                    },
                    onAddProjectClick = {
                        navController.navigate(Screen.AddProject.route)
                    },
                    onProjectClick = { projectId ->
                        navController.navigate(
                            Screen.ProjectDetails.createRoute(projectId)
                        )
                    }
                )
            }

            composable(Screen.Projects.route) {
                ProjectsScreen(
                    onHomeClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    },
                    onAddProjectClick = {
                        navController.navigate(Screen.AddProject.route)
                    },
                    onProjectClick = { projectId ->
                        navController.navigate("projectDetails/$projectId")
                    }
                )
            }

            composable(Screen.AddProject.route) {
                AddProjectScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onProjectCreated = { projectId ->
                        navController.navigate("projectDetails/$projectId") {
                            popUpTo(Screen.AddProject.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable(
                route = Screen.ProjectDetails.route,
                arguments = listOf(
                    navArgument("projectId") {
                        type = NavType.LongType
                    }
                )
            ) { backStackEntry ->

            }
        }
    }


}
