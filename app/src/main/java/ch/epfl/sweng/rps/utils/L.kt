package ch.epfl.sweng.rps.utils

import android.util.Log
import androidx.annotation.VisibleForTesting
import java.util.*

object L {
    private val instances = mutableMapOf<String, LogService>()

    fun of(name: String): LogService = instances.getOrPut(name) { LogService(name) }

    fun dispose(name: String) {
        instances[name]?.dispose()
        instances.remove(name)
    }

    fun unregister(logService: LogService) {
        instances.remove(logService.name)
    }

    fun unregisterAll() {
        instances.values.forEach { it.dispose() }
        instances.clear()
    }

    enum class Level(val priority: Int) {
        VERBOSE(Log.VERBOSE),
        DEBUG(Log.DEBUG),
        INFO(Log.INFO),
        WARN(Log.WARN),
        ERROR(Log.ERROR),
        ASSERT(Log.ASSERT);
    }

    data class LogEntry(
        val tag: String,
        val message: String,
        val time: Date,
        val level: Level
    )

    class LogService(val name: String) : ChangeNotifier<LogService>() {
        @VisibleForTesting
        val logs = LinkedList<LogEntry>()
        private var logsSize = 100

        var size: Int
            get() = logsSize
            set(value) {
                if (value < 0) {
                    throw IllegalArgumentException("Log size must be positive")
                }
                logsSize = value
                while (logs.size > logsSize) {
                    logs.removeFirst()
                }
                notifyListeners()
            }

        fun log(message: String, level: Level = Level.INFO) {
            val e = LogEntry(name, message, Date(), level)
            Log.println(e.level.priority, e.tag, e.message)
            logs.add(e)
            if (logs.size > logsSize) {
                logs.removeFirst()
            }
            notifyListeners()
        }

        fun e(message: String) {
            log(message, Level.ERROR)
        }

        fun w(message: String) {
            log(message, Level.WARN)
        }

        fun i(message: String) {
            log(message, Level.INFO)
        }

        fun d(message: String) {
            log(message, Level.DEBUG)
        }

        fun v(message: String) {
            log(message, Level.VERBOSE)
        }

        fun clear() {
            logs.clear()
            notifyListeners()
        }
    }
}