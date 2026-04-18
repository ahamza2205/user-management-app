package com.aa.usermanagementapp.presentation.navigation

/**
 * Defines all navigation routes in the app as a sealed class.
 * Using a sealed class ensures exhaustive handling and avoids raw string typos.
 */
sealed class Screen(val route: String) {
    data object AddUser : Screen("add_user")
    data object UserList : Screen("user_list")
}
