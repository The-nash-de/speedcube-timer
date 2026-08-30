package de.thenash.speedcubetimer

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query

@Dao
interface SolveDao {

    @Insert
    suspend fun insertSolve(solve: Solve)

    @Query("SELECT * FROM solves ORDER BY timestamp DESC")
    suspend fun getAllSolves(): List<Solve>
}