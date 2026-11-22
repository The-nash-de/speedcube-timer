package de.thenash.speedcubetimer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.ceil
import kotlin.math.sqrt

@Composable
fun StatisticsScreen(solveDao: SolveDao) {
    var solves by remember {
        mutableStateOf<List<Solve>>(emptyList())
    }

    LaunchedEffect(Unit) {
        solves = solveDao.getAllSolves()
    }

    val times = solves.map {
        it.timeMillis
    }

    val average = if (times.isNotEmpty()) {
        times.average()
    } else {
        null
    }

    val best = times.minOrNull()

    val standardDeviation = calculateStandardDeviation(times)

    val ao5 = calculateAverageOf(times, 5)
    val ao12 = calculateAverageOf(times, 12)
    val ao50 = calculateAverageOf(times, 50)
    val ao100 = calculateAverageOf(times, 100)

    val chronologicalTimes = solves
        .asReversed()
        .map {
            it.timeMillis / 1000f
        }

    val rollingAo5 = calculateRollingAverage(
        solves = solves.asReversed(),
        count = 5
    )

    val rollingAo12 = calculateRollingAverage(
        solves = solves.asReversed(),
        count = 12
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(rememberScrollState())
            .padding(
                start = 18.dp,
                top = 24.dp,
                end = 18.dp,
                bottom = 24.dp
            ),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Statistik",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "Verlauf",
            color = Color(0xFFA9A9AD),
            fontSize = 15.sp,
            modifier = Modifier.padding(top = 6.dp)
        )

        StatisticsChart(
            times = chronologicalTimes,
            ao5 = rollingAo5,
            ao12 = rollingAo12
        )

        ChartLegend()

        Text(
            text = "Übersicht",
            color = Color(0xFFA9A9AD),
            fontSize = 15.sp,
            modifier = Modifier.padding(top = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatisticCard(
                title = "Bestzeit",
                value = best?.let {
                    formatStatisticTime(it.toDouble())
                } ?: "--",
                modifier = Modifier.weight(1f)
            )

            StatisticCard(
                title = "Durchschnitt",
                value = average?.let {
                    formatStatisticTime(it)
                } ?: "--",
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatisticCard(
                title = "Solves",
                value = solves.size.toString(),
                modifier = Modifier.weight(1f)
            )

            StatisticCard(
                title = "Abweichung",
                value = standardDeviation?.let {
                    formatStatisticTime(it)
                } ?: "--",
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = "Averages",
            color = Color(0xFFA9A9AD),
            fontSize = 15.sp,
            modifier = Modifier.padding(top = 10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatisticCard(
                title = "Ao5",
                value = ao5?.let {
                    formatStatisticTime(it)
                } ?: "--",
                modifier = Modifier.weight(1f)
            )

            StatisticCard(
                title = "Ao12",
                value = ao12?.let {
                    formatStatisticTime(it)
                } ?: "--",
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatisticCard(
                title = "Ao50",
                value = ao50?.let {
                    formatStatisticTime(it)
                } ?: "--",
                modifier = Modifier.weight(1f)
            )

            StatisticCard(
                title = "Ao100",
                value = ao100?.let {
                    formatStatisticTime(it)
                } ?: "--",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatisticsChart(
    times: List<Float>,
    ao5: List<Float?>,
    ao12: List<Float?>
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp),
        color = Color(0xFF121214),
        shape = RoundedCornerShape(18.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (times.size < 2) {
                return@Canvas
            }

            val allValues = buildList {
                addAll(times)

                ao5.forEach {
                    if (it != null) {
                        add(it)
                    }
                }

                ao12.forEach {
                    if (it != null) {
                        add(it)
                    }
                }
            }

            if (allValues.isEmpty()) {
                return@Canvas
            }

            val rawMin = allValues.minOrNull() ?: 0f
            val rawMax = allValues.maxOrNull() ?: 1f

            val padding = ((rawMax - rawMin) * 0.12f)
                .coerceAtLeast(0.5f)

            val minValue = rawMin - padding
            val maxValue = rawMax + padding

            val range = (maxValue - minValue)
                .coerceAtLeast(1f)

            repeat(4) { index ->
                val y = size.height * index / 3f

                drawLine(
                    color = Color(0xFF29292D),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f
                )
            }

            fun xPosition(index: Int): Float {
                return size.width *
                        index / (times.size - 1).toFloat()
            }

            fun yPosition(value: Float): Float {
                val normalized =
                    (value - minValue) / range

                return size.height -
                        normalized * size.height
            }

            val solvePath = Path()

            times.forEachIndexed { index, value ->
                val x = xPosition(index)
                val y = yPosition(value)

                if (index == 0) {
                    solvePath.moveTo(x, y)
                } else {
                    solvePath.lineTo(x, y)
                }
            }

            drawPath(
                path = solvePath,
                color = Color(0xFFE8E8EA),
                style = Stroke(
                    width = 2.5f,
                    cap = StrokeCap.Round
                )
            )

            drawAveragePath(
                values = ao5,
                color = Color(0xFFFF4B45),
                xPosition = ::xPosition,
                yPosition = ::yPosition
            )

            drawAveragePath(
                values = ao12,
                color = Color(0xFF8BC34A),
                xPosition = ::xPosition,
                yPosition = ::yPosition
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawAveragePath(
    values: List<Float?>,
    color: Color,
    xPosition: (Int) -> Float,
    yPosition: (Float) -> Float
) {
    var currentPath: Path? = null

    values.forEachIndexed { index, value ->
        if (value == null) {
            currentPath = null
            return@forEachIndexed
        }

        val x = xPosition(index)
        val y = yPosition(value)

        if (currentPath == null) {
            currentPath = Path().apply {
                moveTo(x, y)
            }
        } else {
            currentPath?.lineTo(x, y)
        }

        val nextValue = values.getOrNull(index + 1)

        if (nextValue == null || index == values.lastIndex) {
            currentPath?.let { path ->
                drawPath(
                    path = path,
                    color = color,
                    style = Stroke(
                        width = 4f,
                        cap = StrokeCap.Round
                    )
                )
            }

            currentPath = null
        }
    }
}

@Composable
private fun ChartLegend() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        LegendItem(
            color = Color(0xFFE8E8EA),
            text = "Solves"
        )

        LegendItem(
            color = Color(0xFFFF4B45),
            text = "Ao5"
        )

        LegendItem(
            color = Color(0xFF8BC34A),
            text = "Ao12"
        )
    }
}

@Composable
private fun LegendItem(
    color: Color,
    text: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "●",
            color = color,
            fontSize = 14.sp
        )

        Text(
            text = text,
            color = Color(0xFFD0D0D2),
            fontSize = 13.sp
        )
    }
}

@Composable
private fun StatisticCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color(0xFF1B1B1D),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 18.dp,
                vertical = 18.dp
            )
        ) {
            Text(
                text = title,
                color = Color(0xFFA9A9AD),
                fontSize = 14.sp
            )

            Text(
                text = value,
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

private fun calculateRollingAverage(
    solves: List<Solve>,
    count: Int
): List<Float?> {
    return solves.indices.map { index ->
        if (index + 1 < count) {
            null
        } else {
            val window = solves
                .subList(
                    index - count + 1,
                    index + 1
                )
                .map {
                    it.timeMillis
                }

            calculateAverageOf(
                times = window,
                count = count
            )?.div(1000.0)?.toFloat()
        }
    }
}

private fun calculateAverageOf(
    times: List<Long>,
    count: Int
): Double? {
    if (times.size < count) {
        return null
    }

    val recentTimes = times
        .take(count)
        .sorted()

    val trimCount = ceil(count * 0.05).toInt()

    val trimmedTimes = recentTimes
        .drop(trimCount)
        .dropLast(trimCount)

    return trimmedTimes.average()
}

private fun calculateStandardDeviation(
    times: List<Long>
): Double? {
    if (times.isEmpty()) {
        return null
    }

    val average = times.average()

    val variance = times
        .map { time ->
            val difference = time - average
            difference * difference
        }
        .average()

    return sqrt(variance)
}

private fun formatStatisticTime(
    milliseconds: Double
): String {
    return "%.2f".format(
        milliseconds / 1000.0
    )
}