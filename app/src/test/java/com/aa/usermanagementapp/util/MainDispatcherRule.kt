package com.aa.usermanagementapp.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * A JUnit rule that replaces the Main dispatcher with a [TestDispatcher] for the duration
 * of each test. Required for testing ViewModels that launch coroutines on Dispatchers.Main.
 *
 * Uses [UnconfinedTestDispatcher] by default — coroutines run eagerly, so state changes
 * are immediately observable without needing [kotlinx.coroutines.test.advanceUntilIdle].
 */
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
