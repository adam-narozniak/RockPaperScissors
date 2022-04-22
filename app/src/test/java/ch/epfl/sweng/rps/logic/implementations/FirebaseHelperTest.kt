package ch.epfl.sweng.rps.logic.implementations

import ch.epfl.sweng.rps.logic.FirebaseHelper
import ch.epfl.sweng.rps.models.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class FirebaseHelperTest {
    @Test
    fun testProcessUserArgs() {
        val name = "Rei"
        assertEquals(
            hashMapOf(
                User.Field.GAMES_HISTORY_PRIVACY.value to true,
                User.Field.USERNAME.value to name,
            ),
            FirebaseHelper.processUserArguments(
                User.Field.GAMES_HISTORY_PRIVACY to true,
                User.Field.USERNAME to name
            )
        )
    }

    @Test
    fun testProcessUserArgs_empty() {
        assertEquals(
            hashMapOf<String, Any>(),
            FirebaseHelper.processUserArguments()
        )
    }

    @Test
    fun testCreateUser() {
        val name = "Rei"
        val user = FirebaseHelper.userFrom(
            uid = "123",
            name = name,
            email = "example@example.com"
        )
        assertEquals(name, user.username)
        assertEquals(User.Privacy.PUBLIC.name, user.games_history_privacy)
        assertEquals("123", user.uid)
        assertEquals("example@example.com", user.email)
    }
}