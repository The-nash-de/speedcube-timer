package de.thenash.speedcubetimer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(solveDao: SolveDao) {
    var solves by remember {
        mutableStateOf<List<Solve>>(emptyList())
    }

    LaunchedEffect(Unit) {
        solves = solveDao.getAllSolves()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "History"
        )

        LazyColumn {
            items(solves) { solve ->
                Text(
                    text = "${formatSolveTime(solve.timeMillis)}  ${formatTimestamp(solve.timestamp)}",
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

private fun formatSolveTime(milliseconds: Long): String {
    return "%.2f".format(milliseconds / 1000.0)
}

private fun formatTimestamp(timestamp: Long): String {
    val formatter = SimpleDateFormat(
        "dd.MM.yyyy HH:mm:ss",
        Locale.getDefault()
    )

    return formatter.format(Date(timestamp))
}