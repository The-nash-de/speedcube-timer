package de.thenash.speedcubetimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.room3.Room
import de.thenash.speedcubetimer.ui.theme.SpeedcubeTimerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "speedcube_timer.db"
        ).build()

        val solveDao = database.solveDao()

        setContent {
            SpeedcubeTimerTheme {
                TimerScreen(solveDao)
            }
        }
    }
}