package com.jetbrains.kmpapp

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HomeViewModelTest {
    @Test
    fun greetingNamesTheCurrentPlatform() {
        val message = Greeting().message
        assertTrue(message.contains(platformName()))
    }

    @Test
    fun incrementRaisesTheSharedCount() {
        val viewModel = HomeViewModel("Hello from test")

        assertEquals(0, viewModel.count.value)
        viewModel.increment()
        assertEquals(1, viewModel.count.value)
    }
}
