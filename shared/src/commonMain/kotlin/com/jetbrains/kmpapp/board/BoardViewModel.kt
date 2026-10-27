package com.jetbrains.kmpapp.board

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.rickclephas.kmp.observableviewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BoardViewModel : ViewModel() {
    private val _board = MutableStateFlow(standardBoard())

    @NativeCoroutinesState
    val board: StateFlow<PlayerBoard> = _board.asStateFlow()

    fun adjustStock(resource: ResourceKind, delta: Int) = edit { it.adjustStock(resource, delta) }

    fun adjustProduction(resource: ResourceKind, delta: Int) = edit { it.adjustProduction(resource, delta) }

    fun setProduction(resource: ResourceKind, value: Int) = edit { it.setProduction(resource, value) }

    fun adjustTerraformRating(delta: Int) = edit { it.adjustTerraformRating(delta) }

    fun produce() = edit { it.produce() }

    fun convertPlants() = edit { it.convertPlants() }

    fun convertHeat() = edit { it.convertHeat() }

    fun reset(setup: GameSetup) = edit { it.reset(setup) }

    fun setPlayerColor(color: PlayerColor) = edit { it.copy(playerColor = color) }

    private fun edit(transform: (PlayerBoard) -> PlayerBoard) {
        _board.update(transform)
    }
}
