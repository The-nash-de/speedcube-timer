package de.thenash.speedcubetimer

import androidx.room3.Database
import androidx.room3.RoomDatabase

@Database(
    entities = [Solve::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun solveDao(): SolveDao
}