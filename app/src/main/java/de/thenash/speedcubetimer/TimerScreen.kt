package de.thenash.speedcubetimer

import android.os.SystemClock
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

private enum class TimerState {
    IDLE,
    HOLDING,
    READY,
    RUNNING,
    STOPPED
}

@Composable
fun TimerScreen(solveDao: SolveDao) {
    var timerState by remember {
        mutableStateOf(TimerState.IDLE)
    }

    var startTime by remember {
        mutableLongStateOf(0L)
    }

    var elapsedTime by remember {
        mutableLongStateOf(0L)
    }

    var solves by remember {
        mutableStateOf<List<Solve>>(emptyList())
    }

    LaunchedEffect(Unit) {
        solves = solveDao.getAllSolves()
    }

    LaunchedEffect(timerState) {
        if (timerState == TimerState.RUNNING) {
            while (true) {
                elapsedTime = SystemClock.elapsedRealtime() - startTime
                delay(10)
            }
        }
    }

    val displayedTime = formatTime(elapsedTime)

    val bestTime = solves.minOfOrNull {
        it.timeMillis
    }

    val averageTime = if (solves.isNotEmpty()) {
        solves.map {
            it.timeMillis
        }.average()
    } else {
        null
    }

    val recentSolves = solves.take(5)

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

                        solves = listOf(solve) + solves

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
                            released = event.changes.none {
                                it.pressed
                            }
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
                        stillPressed = event.changes.any {
                            it.pressed
                        }
                    }

                    startTime = SystemClock.elapsedRealtime()
                    timerState = TimerState.RUNNING
                }
            }
    ) {
        if (timerState != TimerState.RUNNING) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(
                        start = 24.dp,
                        top = 32.dp,
                        end = 24.dp
                    )
            ) {
                Text(
                    text = "3×3 Würfel",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Scramble folgt",
                    color = Color(0xFFA9A9AD),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 14.dp)
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 20.dp
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    StatLine(
                        label = "Durchschnitt",
                        value = averageTime?.let {
                            formatTime(it.toLong())
                        } ?: "--"
                    )

                    StatLine(
                        label = "Bester",
                        value = bestTime?.let {
                            formatTime(it)
                        } ?: "--"
                    )

                    StatLine(
                        label = "Anzahl",
                        value = solves.size.toString()
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = "Letzte Zeiten",
                        color = Color(0xFFF2F2F2),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )

                    if (recentSolves.isEmpty()) {
                        Text(
                            text = "--",
                            color = Color(0xFFA9A9AD),
                            fontSize = 15.sp
                        )
                    } else {
                        recentSolves.forEach { solve ->
                            Text(
                                text = formatTime(solve.timeMillis),
                                color = Color(0xFFD8D8DA),
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
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

@Composable
private fun StatLine(
    label: String,
    value: String
) {
    Row {
        Text(
            text = "$label: ",
            color = Color(0xFFA9A9AD),
            fontSize = 15.sp
        )

        Text(
            text = value,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatTime(milliseconds: Long): String {
    val seconds = milliseconds / 1000.0
    return "%.2f".format(seconds)
}