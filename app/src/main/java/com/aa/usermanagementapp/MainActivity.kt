package com.aa.usermanagementapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.aa.usermanagementapp.presentation.navigation.AppNavHost
import com.aa.usermanagementapp.ui.theme.UserManagementAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UserManagementAppTheme {
                val navController = rememberNavController()
                AppNavHost(navController = navController)
            }
        }
    }
}