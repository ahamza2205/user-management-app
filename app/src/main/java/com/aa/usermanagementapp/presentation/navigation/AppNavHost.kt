package com.aa.usermanagementapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aa.usermanagementapp.presentation.adduser.AddUserScreen
import com.aa.usermanagementapp.presentation.users.UsersScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.AddUser.route
    ) {
        composable(route = Screen.AddUser.route) {
            AddUserScreen(
                onNavigateToUsers = {
                    navController.navigate(Screen.UserList.route)
                }
            )
        }

        composable(route = Screen.UserList.route) {
            UsersScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
