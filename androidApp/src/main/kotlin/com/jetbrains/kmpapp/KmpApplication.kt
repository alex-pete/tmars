package com.jetbrains.kmpapp

import android.app.Application
import com.jetbrains.kmpapp.board.BoardViewModel
import com.jetbrains.kmpapp.di.initKoin
import org.koin.dsl.module

class KmpApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(
            listOf(
                module {
                    factory { BoardViewModel() }
                },
            ),
        )
    }
}
