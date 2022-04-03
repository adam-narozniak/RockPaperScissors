package ch.epfl.sweng.rps

import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.times
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginPageTest {
    @get:Rule
    val testRule = ActivityScenarioRule(LoginActivity::class.java)
    @Test
    fun clickOnSignInShowsMainActivity(){
        Intents.init()
        Espresso.onView(ViewMatchers.withId(R.id.login_view)).check(ViewAssertions.matches(
            ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.signIn)).perform(ViewActions.click())
        Intents.intended(hasAction("com.google.android.gms.auth.GOOGLE_SIGN_IN"), times(2))
        Intents.release()
    }
}