package com.inikonoff.clicker

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("counter")

/**
 * Единственный источник правды для счёта: им пользуются и Activity, и (позже) сервис.
 * Каждое изменение сохраняется в DataStore.
 */
object CounterRepository {
    private val KEY_COUNT = intPreferencesKey("count")
    private val KEY_STEP = intPreferencesKey("step")
    private val KEY_HISTORY = stringPreferencesKey("history")

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var store: DataStore<Preferences>

    private val _state = MutableStateFlow(CounterState())
    val state: StateFlow<CounterState> = _state.asStateFlow()

    fun init(context: Context) {
        if (::store.isInitialized) return
        store = context.applicationContext.dataStore
        val prefs = runBlocking { store.data.first() }
        _state.value = CounterState(
            count = prefs[KEY_COUNT] ?: 0,
            step = (prefs[KEY_STEP] ?: 1).coerceAtLeast(1),
            history = prefs[KEY_HISTORY].orEmpty()
                .split(',')
                .mapNotNull { it.toIntOrNull() },
        )
    }

    fun plus() = mutate { it.plus() }

    fun minus() = mutate { it.minus() }

    fun undo() = mutate { it.undo() }

    fun reset() = mutate { it.reset() }

    fun setStep(step: Int) = mutate { it.withStep(step) }

    private fun mutate(transform: (CounterState) -> CounterState) {
        val next = _state.updateAndGet(transform)
        scope.launch {
            store.edit {
                it[KEY_COUNT] = next.count
                it[KEY_STEP] = next.step
                it[KEY_HISTORY] = next.history.joinToString(",")
            }
        }
    }

    private fun <T> MutableStateFlow<T>.updateAndGet(transform: (T) -> T): T {
        update(transform)
        return value
    }
}
