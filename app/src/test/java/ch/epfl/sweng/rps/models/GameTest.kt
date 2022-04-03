package ch.epfl.sweng.rps.models

import ch.epfl.sweng.rps.models.Game.Mode
import ch.epfl.sweng.rps.models.Game.Uid
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class GameTest {
    @Test
    fun test() {
        assertThrows(AssertionError::class.java) {
            Game("1", "0", listOf(Uid("1"), Uid("2")), Mode(0, Mode.Type.PVP, null, 5), mutableListOf<Round>())
        }
        val game = Game("1", "0", listOf(Uid("1"), Uid("2")), Mode(2, Mode.Type.PVP, null, 5), mutableListOf<Round>())
        assertEquals(game.gameId, "1")
        assertEquals(game.players.size, 2)
        assertEquals(game.mode.playerCount, 2)
        assertEquals(game.mode.type, Mode.Type.PVP)
        assertEquals(game.mode.time, null)
        assertEquals(game.mode.rounds, 5)
    }
}