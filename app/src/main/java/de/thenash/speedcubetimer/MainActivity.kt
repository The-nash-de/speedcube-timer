package de.thenash.speedcubetimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
                        NavigationBar(
                            modifier = Modifier.height(72.dp),
                            containerColor = Color(0xFF151517),
                            tonalElevation = 0.dp
                        ) {
                            NavigationBarItem(
                                selected = selectedTab == 0,
                                onClick = {
                                    selectedTab = 0
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = "Timer",
                                        modifier = Modifier.size(27.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = "Timer",
                                        fontSize = 11.sp
                                    )
                                },
                                alwaysShowLabel = true,
                                colors = navigationItemColors()
                            )

                            NavigationBarItem(
                                selected = selectedTab == 1,
                                onClick = {
                                    selectedTab = 1
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = "History",
                                        modifier = Modifier.size(27.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = "History",
                                        fontSize = 11.sp
                                    )
                                },
                                alwaysShowLabel = true,
                                colors = navigationItemColors()
                            )

                            NavigationBarItem(
                                selected = selectedTab == 2,
                                onClick = {
                                    selectedTab = 2
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.ShowChart,
                                        contentDescription = "Statistik",
                                        modifier = Modifier.size(27.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = "Statistik",
                                        fontSize = 11.sp
                                    )
                                },
                                alwaysShowLabel = true,
                                colors = navigationItemColors()
                            )
                        }
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
                            2 -> Text(
                                text = "Statistik folgt",
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun navigationItemColors() =
    NavigationBarItemDefaults.colors(
        selectedIconColor = Color(0xFFDCE3FF),
        selectedTextColor = Color.White,
        indicatorColor = Color(0xFF30333D),
        unselectedIconColor = Color(0xFF8E8E93),
        unselectedTextColor = Color(0xFF8E8E93)
    )