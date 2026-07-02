package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.data.AppDatabase
import com.example.data.GameRepository
import com.example.ui.GameViewModel
import com.example.ui.GameViewModelFactory
import com.example.ui.screens.MainGameScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Core Database and Repository initialization
        val database = AppDatabase.getDatabase(applicationContext, lifecycleScope)
        val repository = GameRepository(database.appDao())

        // Standard dynamic ViewModel instantiation
        val viewModel: GameViewModel by viewModels {
            GameViewModelFactory(repository)
        }

        setContent {
            MyApplicationTheme {
                MainGameScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
