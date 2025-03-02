package com.salanevich.lmstudioandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.LifecycleEventObserver
import com.salanevich.lmstudioandroid.ui.theme.LmStudioAndroidTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var listeningRepositoryImpl: LifecycleEventObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(listeningRepositoryImpl)
        enableEdgeToEdge()
        setContent {
            LmStudioAndroidTheme {
                Navigation()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(listeningRepositoryImpl)
    }

}