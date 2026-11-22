package de.thenash.speedcubetimer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.worldcubeassociation.tnoodle.puzzle.ThreeByThreeCubePuzzle

object ScrambleGenerator {

    private val puzzle by lazy {
        ThreeByThreeCubePuzzle()
    }

    suspend fun generate(): String {
        return withContext(Dispatchers.Default) {
            puzzle.generateScramble()
        }
    }
}