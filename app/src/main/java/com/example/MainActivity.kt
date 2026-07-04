package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.audio.SoundManager
import com.example.data.AppDatabase
import com.example.data.GameRepository
import com.example.ui.GameViewModel
import com.example.ui.GameViewModelFactory
import com.example.ui.screens.MainGameScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: GameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        SoundManager.init(applicationContext)

        // Core Database and Repository initialization
        val database = AppDatabase.getDatabase(applicationContext, lifecycleScope)
        val repository = GameRepository(database.appDao())

        // Standard dynamic ViewModel instantiation
        viewModel = viewModels<GameViewModel> {
            GameViewModelFactory(repository)
        }.value

        viewModel.restoreSavedAudioPreferences(applicationContext)

        setContent {
            MyApplicationTheme {
                MainGameScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    // Pausing/resuming background music with the app lifecycle avoids it running (and draining
    // battery/using the audio focus other apps might need) while the app is in the background.
    override fun onPause() {
        super.onPause()
        SoundManager.pauseMusic()
    }

    override fun onResume() {
        super.onResume()
        SoundManager.resumeMusic()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            SoundManager.release()
        }
    }
}

