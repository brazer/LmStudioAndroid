package com.salanevich.lmstudioandroid

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.salanevich.lmstudioandroid.ui.screen.ChatScreen
import com.salanevich.lmstudioandroid.ui.screen.PreferencesScreen
import kotlinx.serialization.Serializable

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Chat) {
        composable<Chat> {
            ChatScreen {
                navController.navigate(Preferences)
            }
        }
        composable<Preferences> {
            PreferencesScreen {
                navController.popBackStack()
            }
        }
    }
}

@Serializable
data object Chat

@Serializable
data object Preferences