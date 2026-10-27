package com.jetbrains.kmpapp.board

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class PlayerBoardTest {
    @Test
    fun standardSetupStartsWithRatingAndProduction() {
        val board = standardBoard()

        assertEquals(20, board.terraformRating)
        assertEquals(1, board.megaCredits.production)
        assertEquals(1, board.steel.production)
        assertEquals(1, board.heat.production)
        assertEquals(0, board.plants.stock)
        assertEquals(21, board.megaCreditIncome)
    }

    @Test
    fun corporateEraStartsWithoutProduction() {
        val board = corporateBoard()

        assertEquals(20, board.terraformRating)
        assertEquals(0, board.megaCredits.production)
        assertEquals(0, board.energy.production)
        assertEquals(20, board.megaCreditIncome)
    }

    @Test
    fun stockCannotDropBelowZero() {
        val board = standardBoard().adjustStock(ResourceKind.Steel, -4)

        assertEquals(0, board.steel.stock)
    }

    @Test
    fun megaCreditProductionFloorsAtMinusFive() {
        val board = standardBoard()
            .setProduction(ResourceKind.MegaCredits, -9)
            .adjustProduction(ResourceKind.MegaCredits, -1)

        assertEquals(MIN_MEGACREDIT_PRODUCTION, board.megaCredits.production)
    }

    @Test
    fun otherProductionCannotBeNegative() {
        val board = standardBoard().setProduction(ResourceKind.Titanium, -3)

        assertEquals(0, board.titanium.production)
    }

    @Test
    fun productionCanPassTen() {
        val board = standardBoard().setProduction(ResourceKind.Plants, 12)

        assertEquals(12, board.plants.production)
    }

    @Test
    fun incomeAddsRatingToMegaCreditProduction() {
        val board = standardBoard()
            .adjustTerraformRating(-6)
            .setProduction(ResourceKind.MegaCredits, -5)

        assertEquals(14, board.terraformRating)
        assertEquals(9, board.megaCreditIncome)
        assertEquals("14 − 5 = 9", board.incomeLabel())
    }

    @Test
    fun incomeNeverGoesBelowZero() {
        val board = standardBoard()
            .adjustTerraformRating(-20)
            .setProduction(ResourceKind.MegaCredits, -5)

        assertEquals(0, board.megaCreditIncome)
    }

    @Test
    fun terraformRatingCannotGoBelowZero() {
        val board = standardBoard().adjustTerraformRating(-100)

        assertEquals(0, board.terraformRating)
    }

    @Test
    fun produceMovesEnergyIntoHeatThenAddsProduction() {
        val board = testBoard(
            megaCreditStock = 4,
            megaCreditProduction = -5,
            terraformRating = 20,
            steelStock = 2,
            steelProduction = 3,
            plantStock = 1,
            plantProduction = 2,
            energyStock = 7,
            energyProduction = 1,
            heatStock = 2,
            heatProduction = 3,
        )

        val produced = board.produce()

        assertEquals(19, produced.megaCredits.stock)
        assertEquals(5, produced.steel.stock)
        assertEquals(3, produced.plants.stock)
        assertEquals(1, produced.energy.stock)
        assertEquals(12, produced.heat.stock)
        assertEquals(-5, produced.megaCredits.production)
        assertEquals(20, produced.terraformRating)
    }

    @Test
    fun convertPlantsSpendsEightAndRaisesRating() {
        val board = testBoard(plantStock = 9, terraformRating = 20).convertPlants()

        assertEquals(1, board.plants.stock)
        assertEquals(21, board.terraformRating)
    }

    @Test
    fun convertPlantsDoesNothingBelowEight() {
        val board = testBoard(plantStock = 7, terraformRating = 20)
        val converted = board.convertPlants()

        assertSame(board, converted)
        assertFalse(board.canConvertPlants)
    }

    @Test
    fun convertHeatSpendsEightAndRaisesRating() {
        val board = testBoard(heatStock = 8, terraformRating = 14).convertHeat()

        assertEquals(0, board.heat.stock)
        assertEquals(15, board.terraformRating)
        assertTrue(testBoard(heatStock = 8).canConvertHeat)
    }

    @Test
    fun cubesSplitIntoGoldSilverAndBronze() {
        assertEquals(CubeStack(0, 0, 0), cubesFor(0))
        assertEquals(CubeStack(1, 1, 2), cubesFor(17))
        assertEquals(CubeStack(2, 0, 3), cubesFor(23))
        assertEquals(CubeStack(0, 0, 0), cubesFor(-4))
    }

    @Test
    fun resetKeepsThePlayerColorAndAppliesTheSetup() {
        val viewModel = BoardViewModel()
        viewModel.setPlayerColor(PlayerColor.Blue)
        viewModel.adjustStock(ResourceKind.Steel, 4)
        viewModel.reset(GameSetup.CorporateEra)

        val board = viewModel.board.value
        assertEquals(PlayerColor.Blue, board.playerColor)
        assertEquals(0, board.steel.stock)
        assertEquals(0, board.steel.production)
        assertEquals(20, board.terraformRating)
    }

    private fun testBoard(
        megaCreditStock: Int = 0,
        megaCreditProduction: Int = 0,
        steelStock: Int = 0,
        steelProduction: Int = 0,
        titaniumStock: Int = 0,
        titaniumProduction: Int = 0,
        plantStock: Int = 0,
        plantProduction: Int = 0,
        energyStock: Int = 0,
        energyProduction: Int = 0,
        heatStock: Int = 0,
        heatProduction: Int = 0,
        terraformRating: Int = 20,
    ) = PlayerBoard(
        megaCredits = ResourcePile(megaCreditStock, megaCreditProduction),
        steel = ResourcePile(steelStock, steelProduction),
        titanium = ResourcePile(titaniumStock, titaniumProduction),
        plants = ResourcePile(plantStock, plantProduction),
        energy = ResourcePile(energyStock, energyProduction),
        heat = ResourcePile(heatStock, heatProduction),
        terraformRating = terraformRating,
        playerColor = PlayerColor.Red,
    )
}
