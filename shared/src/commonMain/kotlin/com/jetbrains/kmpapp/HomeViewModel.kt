package com.jetbrains.kmpapp

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.rickclephas.kmp.observableviewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel(val greeting: String) : ViewModel() {
    private val _count = MutableStateFlow(0)

    @NativeCoroutinesState
    val count: StateFlow<Int> = _count.asStateFlow()

    fun increment() {
        _count.update { it + 1 }
    }
}
