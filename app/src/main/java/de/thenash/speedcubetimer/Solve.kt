package de.thenash.speedcubetimer

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "solves")
data class Solve(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val timeMillis: Long,

    val timestamp: Long
)