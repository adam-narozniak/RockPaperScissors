package ch.epfl.sweng.rps.ui.settings

import android.content.*
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.db.Env
import ch.epfl.sweng.rps.models.Game
import ch.epfl.sweng.rps.models.Hand
import ch.epfl.sweng.rps.models.Round
import ch.epfl.sweng.rps.services.ProdServiceLocator
import ch.epfl.sweng.rps.services.ServiceLocator
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.firebase.Timestamp


class SettingsActivity : AppCompatActivity(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback,
    SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        private const val TITLE_TAG = "settingsActivityTitle"

        fun applyTheme(
            themeKey: String?,
            sharedPreferences: SharedPreferences?
        ) {
            when (sharedPreferences?.getString(themeKey, "system")) {
                "light" -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                "dark" -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                "system" -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }

        }
    }

    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, HeaderFragment())
                .commit()
        } else {
            title = savedInstanceState.getCharSequence(TITLE_TAG)
        }
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                setTitle(R.string.title_activity_settings)
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save current activity title so we can set it again after a configuration change
        outState.putCharSequence(TITLE_TAG, title)
    }

    override fun onSupportNavigateUp(): Boolean {
        if (supportFragmentManager.popBackStackImmediate()) {
            return true
        }
        finish()
        return true
    }


    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat, pref: Preference
    ): Boolean {
        // Instantiate the new Fragment
        val args = pref.extras
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            classLoader,
            pref.fragment!!
        ).apply {
            arguments = args
            setTargetFragment(caller, 0)
        }
        // Replace the existing Fragment with the new Fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.settings, fragment)
            .addToBackStack(null)
            .commit()
        title = pref.title
        return true
    }

    class HeaderFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.header_preferences, rootKey)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            val button = findPreference<Preference>(getString(R.string.show_license_key))
            button?.setOnPreferenceClickListener {
                startActivity(Intent(view.context, OssLicensesMenuActivity::class.java))
                true
            }
            val uidPreference =
                findPreference<Preference>(getString(R.string.settings_show_uid_text))
            uidPreference?.setSummaryProvider {
                ServiceLocator.getInstance().repository.rawCurrentUid()
            }
            uidPreference?.setOnPreferenceClickListener {
                val clipboard: ClipboardManager? =
                    context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                val clip = ClipData.newPlainText(
                    "Copied uid",
                    ServiceLocator.getInstance().repository.rawCurrentUid()
                )
                clipboard?.setPrimaryClip(clip)
                true
            }
            findPreference<Preference>(getString(R.string.add_artificial_game_settings))?.setOnPreferenceClickListener {
                val id = "artificial_game_1"
                val uid = ServiceLocator.getInstance().repository.rawCurrentUid()!!
                val uid2 = "RquV8FkGInaPnyUnqncOZGJjSKJ3"
                val repo = ServiceLocator.getInstance()
                if (repo !is ProdServiceLocator) return@setOnPreferenceClickListener true

                repo.firebaseReferences.gamesCollection.document(
                    id
                )
                    .set(
                        Game(
                            id = id,
                            players = listOf(
                                uid,
                                uid2
                            ),
                            rounds = mapOf(
                                "0" to Round(
                                    hands = mapOf(
                                        uid to Hand.PAPER,
                                        uid2 to Hand.ROCK
                                    ),
                                    timestamp = Timestamp.now()
                                )
                            ),
                            game_mode = Game.GameMode(
                                playerCount = 2,
                                type = Game.GameMode.Type.PVP,
                                rounds = 1,
                                timeLimit = 0
                            ).toGameModeString(),
                            current_round = 0,
                            done = true,
                            timestamp = Timestamp.now(),
                            player_count = 2
                        )
                    )
                true
            }
        }
    }

//    class AppearanceFragment : PreferenceFragmentCompat() {
//        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
//            setPreferencesFromResource(R.xml.appearance_preferences, rootKey)
//        }
//    }
//
//    class SyncFragment : PreferenceFragmentCompat() {
//        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
//            setPreferencesFromResource(R.xml.sync_preferences, rootKey)
//        }
//    }

    override fun onResume() {
        super.onResume()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val themeKey = getString(R.string.theme_pref_key)
        if (key == themeKey) applyTheme(themeKey, sharedPreferences)
    }

}