package com.jetbrains.kmpapp.board

enum class ResourceKind {
    MegaCredits,
    Steel,
    Titanium,
    Plants,
    Energy,
    Heat,
    ;

    fun label(): String = when (this) {
        MegaCredits -> "M€"
        Steel -> "Steel"
        Titanium -> "Titanium"
        Plants -> "Plants"
        Energy -> "Energy"
        Heat -> "Heat"
    }

    fun valueNote(): String? = when (this) {
        Steel -> "= 2"
        Titanium -> "= 3"
        else -> null
    }

    fun minProduction(): Int = when (this) {
        MegaCredits -> MIN_MEGACREDIT_PRODUCTION
        else -> 0
    }
}

enum class GameSetup {
    Standard,
    CorporateEra,
    ;

    fun label(): String = when (this) {
        Standard -> "Standard"
        CorporateEra -> "Corporate Era"
    }
}

enum class PlayerColor {
    Red,
    Blue,
    Green,
    Yellow,
    Black,
    ;

    fun label(): String = when (this) {
        Red -> "Red"
        Blue -> "Blue"
        Green -> "Green"
        Yellow -> "Yellow"
        Black -> "Black"
    }
}

data class ResourcePile(
    val stock: Int,
    val production: Int,
) {
    fun cubes(): CubeStack = cubesFor(stock)
}

data class CubeStack(
    val gold: Int,
    val silver: Int,
    val bronze: Int,
)

data class PlayerBoard(
    val megaCredits: ResourcePile,
    val steel: ResourcePile,
    val titanium: ResourcePile,
    val plants: ResourcePile,
    val energy: ResourcePile,
    val heat: ResourcePile,
    val terraformRating: Int,
    val playerColor: PlayerColor,
) {
    val megaCreditIncome: Int
        get() = (terraformRating + megaCredits.production).coerceAtLeast(0)

    val canConvertPlants: Boolean
        get() = plants.stock >= PLANT_CONVERSION_COST

    val canConvertHeat: Boolean
        get() = heat.stock >= HEAT_CONVERSION_COST

    fun pile(resource: ResourceKind): ResourcePile = when (resource) {
        ResourceKind.MegaCredits -> megaCredits
        ResourceKind.Steel -> steel
        ResourceKind.Titanium -> titanium
        ResourceKind.Plants -> plants
        ResourceKind.Energy -> energy
        ResourceKind.Heat -> heat
    }

    fun incomeLabel(): String {
        val production = megaCredits.production
        val productionText = if (production < 0) "− ${-production}" else "+ $production"
        return "$terraformRating $productionText = $megaCreditIncome"
    }
}

const val PLANT_CONVERSION_COST = 8
const val HEAT_CONVERSION_COST = 8
const val MIN_MEGACREDIT_PRODUCTION = -5
const val STANDARD_TERRAFORM_RATING = 20

fun cubesFor(amount: Int): CubeStack {
    val safe = amount.coerceAtLeast(0)
    val gold = safe / 10
    val rest = safe % 10
    return CubeStack(gold = gold, silver = rest / 5, bronze = rest % 5)
}

fun standardBoard(playerColor: PlayerColor = PlayerColor.Red): PlayerBoard {
    val produced = ResourcePile(stock = 0, production = 1)
    return PlayerBoard(
        megaCredits = produced,
        steel = produced,
        titanium = produced,
        plants = produced,
        energy = produced,
        heat = produced,
        terraformRating = STANDARD_TERRAFORM_RATING,
        playerColor = playerColor,
    )
}

fun corporateBoard(playerColor: PlayerColor = PlayerColor.Red): PlayerBoard {
    val empty = ResourcePile(stock = 0, production = 0)
    return PlayerBoard(
        megaCredits = empty,
        steel = empty,
        titanium = empty,
        plants = empty,
        energy = empty,
        heat = empty,
        terraformRating = STANDARD_TERRAFORM_RATING,
        playerColor = playerColor,
    )
}

fun PlayerBoard.withPile(resource: ResourceKind, pile: ResourcePile): PlayerBoard = when (resource) {
    ResourceKind.MegaCredits -> copy(megaCredits = pile)
    ResourceKind.Steel -> copy(steel = pile)
    ResourceKind.Titanium -> copy(titanium = pile)
    ResourceKind.Plants -> copy(plants = pile)
    ResourceKind.Energy -> copy(energy = pile)
    ResourceKind.Heat -> copy(heat = pile)
}

fun PlayerBoard.adjustStock(resource: ResourceKind, delta: Int): PlayerBoard {
    val pile = pile(resource)
    return withPile(resource, pile.copy(stock = (pile.stock + delta).coerceAtLeast(0)))
}

fun PlayerBoard.setProduction(resource: ResourceKind, value: Int): PlayerBoard {
    val pile = pile(resource)
    return withPile(resource, pile.copy(production = value.coerceAtLeast(resource.minProduction())))
}

fun PlayerBoard.adjustProduction(resource: ResourceKind, delta: Int): PlayerBoard =
    setProduction(resource, pile(resource).production + delta)

fun PlayerBoard.adjustTerraformRating(delta: Int): PlayerBoard =
    copy(terraformRating = (terraformRating + delta).coerceAtLeast(0))

fun PlayerBoard.produce(): PlayerBoard {
    val heatAfterEnergy = heat.stock + energy.stock
    return copy(
        megaCredits = megaCredits.copy(stock = megaCredits.stock + megaCreditIncome),
        steel = steel.copy(stock = steel.stock + steel.production.coerceAtLeast(0)),
        titanium = titanium.copy(stock = titanium.stock + titanium.production.coerceAtLeast(0)),
        plants = plants.copy(stock = plants.stock + plants.production.coerceAtLeast(0)),
        energy = energy.copy(stock = energy.production.coerceAtLeast(0)),
        heat = heat.copy(stock = heatAfterEnergy + heat.production.coerceAtLeast(0)),
    )
}

fun PlayerBoard.convertPlants(): PlayerBoard {
    if (!canConvertPlants) return this
    return copy(
        plants = plants.copy(stock = plants.stock - PLANT_CONVERSION_COST),
        terraformRating = terraformRating + 1,
    )
}

fun PlayerBoard.convertHeat(): PlayerBoard {
    if (!canConvertHeat) return this
    return copy(
        heat = heat.copy(stock = heat.stock - HEAT_CONVERSION_COST),
        terraformRating = terraformRating + 1,
    )
}

fun PlayerBoard.reset(setup: GameSetup): PlayerBoard = when (setup) {
    GameSetup.Standard -> standardBoard(playerColor)
    GameSetup.CorporateEra -> corporateBoard(playerColor)
}
