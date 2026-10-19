package com.inikonoff.clicker

/**
 * Состояние счётчика. [history] — стек изменений текущей сессии
 * (фактически применённых, с учётом ограничения снизу нулём).
 */
data class CounterState(
    val count: Int = 0,
    val step: Int = 1,
    val history: List<Int> = emptyList(),
) {
    fun plus(): CounterState = change(step)

    fun minus(): CounterState = change(-step)

    fun plusOne(): CounterState = change(1)

    fun minusOne(): CounterState = change(-1)

    fun undo(): CounterState {
        val last = history.lastOrNull() ?: return this
        return copy(count = count - last, history = history.dropLast(1))
    }

    fun withStep(newStep: Int): CounterState = copy(step = newStep.coerceAtLeast(1))

    fun reset(): CounterState = copy(count = 0, history = emptyList())

    private fun change(delta: Int): CounterState {
        val applied = (count + delta).coerceAtLeast(0) - count
        if (applied == 0) return this
        return copy(count = count + applied, history = history + applied)
    }
}
