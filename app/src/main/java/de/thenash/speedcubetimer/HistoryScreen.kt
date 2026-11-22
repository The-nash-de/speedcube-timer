package de.thenash.speedcubetimer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .background(Color.Black)
            .padding(
                start = 16.dp,
                top = 24.dp,
                end = 16.dp
            )
    ) {
        Text(
            text = "History",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 18.dp)
        )

        if (solves.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Noch keine Solves",
                    color = Color(0xFFA9A9AD),
                    fontSize = 15.sp
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(solves) { solve ->
                    SolveCard(solve)
                }
            }
        }
    }
}

@Composable
private fun SolveCard(
    solve: Solve
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1B1B1D)
    ) {
        Column(
            modifier = Modifier
                .padding(
                    horizontal = 10.dp,
                    vertical = 10.dp
                )
        ) {
            Text(
                text = formatSolveDate(solve.timestamp),
                color = Color(0xFFA9A9AD),
                fontSize = 12.sp
            )

            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = formatSolveTime(solve.timeMillis),
                    color = Color.White,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

private fun formatSolveTime(
    milliseconds: Long
): String {
    return "%.2f".format(
        milliseconds / 1000.0
    )
}

private fun formatSolveDate(
    timestamp: Long
): String {
    val formatter = SimpleDateFormat(
        "d.M.",
        Locale.getDefault()
    )

    return formatter.format(
        Date(timestamp)
    )
}