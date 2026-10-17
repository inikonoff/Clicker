package com.inikonoff.clicker

import org.junit.Assert.assertEquals
import org.junit.Test

class CounterStateTest {
    @Test
    fun plusUsesStep() {
        val s = CounterState(step = 5).plus().plus()
        assertEquals(10, s.count)
    }

    @Test
    fun minusNeverGoesBelowZero() {
        val s = CounterState(count = 3, step = 5).minus()
        assertEquals(0, s.count)
        assertEquals(listOf(-3), s.history)
        assertEquals(s, s.minus())
    }

    @Test
    fun undoRevertsLastAppliedChange() {
        val s = CounterState(step = 5).plus().plus().plus().undo()
        assertEquals(10, s.count)
    }

    @Test
    fun undoAfterClampedMinusRestoresCount() {
        val s = CounterState(count = 3, step = 5).minus().undo()
        assertEquals(3, s.count)
    }

    @Test
    fun stepChangeAffectsNextClickOnly() {
        val s = CounterState(step = 1).plus().withStep(10).plus()
        assertEquals(11, s.count)
        assertEquals(1, s.withStep(0).step)
    }

    @Test
    fun undoOnEmptyHistoryIsNoop() {
        val s = CounterState(count = 7)
        assertEquals(s, s.undo())
    }
}
