package de.thenash.speedcubetimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                var selectedTab by remember {
                    mutableStateOf(0)
                }

                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        CustomBottomBar(
                            selectedTab = selectedTab,
                            onTabSelected = {
                                selectedTab = it
                            }
                        )
                    }
                ) { innerPadding ->

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (selectedTab) {
                            0 -> TimerScreen(solveDao)
                            1 -> HistoryScreen(solveDao)
                            2 -> StatisticsScreen(solveDao)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomBottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .background(Color(0xFF151517))
            .padding(
                top = 8.dp,
                bottom = 6.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomBarItem(
            icon = Icons.Default.Timer,
            label = "Timer",
            selected = selectedTab == 0,
            onClick = {
                onTabSelected(0)
            },
            modifier = Modifier.weight(1f)
        )

        BottomBarItem(
            icon = Icons.Default.History,
            label = "History",
            selected = selectedTab == 1,
            onClick = {
                onTabSelected(1)
            },
            modifier = Modifier.weight(1f)
        )

        BottomBarItem(
            icon = Icons.Default.ShowChart,
            label = "Statistik",
            selected = selectedTab == 2,
            onClick = {
                onTabSelected(2)
            },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun BottomBarItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable {
                onClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(
                    width = 48.dp,
                    height = 30.dp
                )
                .background(
                    color = if (selected) {
                        Color(0xFF30333D)
                    } else {
                        Color.Transparent
                    },
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) {
                    Color(0xFFDCE3FF)
                } else {
                    Color(0xFF8E8E93)
                },
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = label,
            color = if (selected) {
                Color.White
            } else {
                Color(0xFF8E8E93)
            },
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 3.dp)
        )
    }
}