package de.thenash.speedcubetimer

import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room3.Room
import de.thenash.speedcubetimer.ui.theme.SpeedcubeTimerTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

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

private enum class TimerState {
    IDLE,
    HOLDING,
    READY,
    RUNNING,
    STOPPED
}

@Composable
fun TimerScreen(solveDao: SolveDao) {
    var timerState by remember { mutableStateOf(TimerState.IDLE) }

    var startTime by remember { mutableLongStateOf(0L) }
    var elapsedTime by remember { mutableLongStateOf(0L) }

    LaunchedEffect(timerState) {
        if (timerState == TimerState.RUNNING) {
            while (true) {
                elapsedTime = SystemClock.elapsedRealtime() - startTime
                delay(10)
            }
        }
    }

    val displayedTime = formatTime(elapsedTime)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown()

                    if (timerState == TimerState.RUNNING) {
                        elapsedTime =
                            SystemClock.elapsedRealtime() - startTime

                        timerState = TimerState.STOPPED

                        val solve = Solve(
                            timeMillis = elapsedTime,
                            timestamp = System.currentTimeMillis()
                        )

                        CoroutineScope(Dispatchers.IO).launch {
                            solveDao.insertSolve(solve)
                        }

                        do {
                            val event = awaitPointerEvent()
                        } while (event.changes.any { it.pressed })

                        return@awaitEachGesture
                    }

                    if (timerState == TimerState.STOPPED) {
                        elapsedTime = 0L
                    }

                    timerState = TimerState.HOLDING

                    val releasedEarly = withTimeoutOrNull(500L) {
                        var released = false

                        while (!released) {
                            val event = awaitPointerEvent()
                            released = event.changes.none { it.pressed }
                        }

                        true
                    } ?: false

                    if (releasedEarly) {
                        timerState = TimerState.IDLE
                        return@awaitEachGesture
                    }

                    timerState = TimerState.READY

                    var stillPressed = true

                    while (stillPressed) {
                        val event = awaitPointerEvent()
                        stillPressed = event.changes.any { it.pressed }
                    }

                    startTime = SystemClock.elapsedRealtime()
                    timerState = TimerState.RUNNING
                }
            }
    ) {
        if (timerState != TimerState.RUNNING) {
            Text(
                text = "Speedcube Timer",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 48.dp)
            )
        }

        Text(
            text = displayedTime,
            color = when (timerState) {
                TimerState.HOLDING -> Color.Red
                TimerState.READY -> Color.Green
                else -> Color.White
            },
            fontSize = 96.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

private fun formatTime(milliseconds: Long): String {
    val seconds = milliseconds / 1000.0
    return "%.2f".format(seconds)
}