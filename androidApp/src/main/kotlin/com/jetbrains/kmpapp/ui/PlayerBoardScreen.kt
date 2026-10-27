package com.jetbrains.kmpapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jetbrains.kmpapp.R
import com.jetbrains.kmpapp.board.BoardViewModel
import com.jetbrains.kmpapp.board.GameSetup
import com.jetbrains.kmpapp.board.PlayerBoard
import com.jetbrains.kmpapp.board.PlayerColor
import com.jetbrains.kmpapp.board.ResourceKind
import com.jetbrains.kmpapp.board.cubesFor
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlayerBoardScreen(viewModel: BoardViewModel = koinViewModel()) {
    val board by viewModel.board.collectAsStateWithLifecycle()
    PlayerBoardContent(
        board = board,
        onAdjustStock = viewModel::adjustStock,
        onSetProduction = viewModel::setProduction,
        onAdjustProduction = viewModel::adjustProduction,
        onAdjustRating = viewModel::adjustTerraformRating,
        onProduce = viewModel::produce,
        onConvertPlants = viewModel::convertPlants,
        onConvertHeat = viewModel::convertHeat,
        onReset = viewModel::reset,
        onColor = viewModel::setPlayerColor,
    )
}

@Composable
fun PlayerBoardContent(
    board: PlayerBoard,
    onAdjustStock: (ResourceKind, Int) -> Unit,
    onSetProduction: (ResourceKind, Int) -> Unit,
    onAdjustProduction: (ResourceKind, Int) -> Unit,
    onAdjustRating: (Int) -> Unit,
    onProduce: () -> Unit,
    onConvertPlants: () -> Unit,
    onConvertHeat: () -> Unit,
    onReset: (GameSetup) -> Unit,
    onColor: (PlayerColor) -> Unit,
) {
    var pendingSetup by remember { mutableStateOf<GameSetup?>(null) }
    val cube = cubeColor(board.playerColor)
    val onCube = onCubeColor(board.playerColor)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BoardPalette.frame)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(6.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(BoardPalette.metal)
                .padding(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                Modifier.weight(1.15f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                StockPlate(
                    resource = ResourceKind.MegaCredits,
                    stock = board.megaCredits.stock,
                    background = BoardPalette.gold,
                    contentColor = BoardPalette.ink,
                    onAdjust = { onAdjustStock(ResourceKind.MegaCredits, it) },
                )
                StockPlate(
                    resource = ResourceKind.Steel,
                    stock = board.steel.stock,
                    background = BoardPalette.steel,
                    contentColor = Color.White,
                    onAdjust = { onAdjustStock(ResourceKind.Steel, it) },
                )
                StockPlate(
                    resource = ResourceKind.Titanium,
                    stock = board.titanium.stock,
                    background = BoardPalette.titanium,
                    contentColor = Color.White,
                    onAdjust = { onAdjustStock(ResourceKind.Titanium, it) },
                )
            }
            Row(
                Modifier.weight(1.05f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                ProductionPlate(
                    resource = ResourceKind.MegaCredits,
                    production = board.megaCredits.production,
                    cube = cube,
                    onCube = onCube,
                    includeNegatives = true,
                    leading = { RatingControl(board, onAdjustRating) },
                    onSet = { onSetProduction(ResourceKind.MegaCredits, it) },
                    onStep = { onAdjustProduction(ResourceKind.MegaCredits, it) },
                )
                ProductionPlate(
                    resource = ResourceKind.Steel,
                    production = board.steel.production,
                    cube = cube,
                    onCube = onCube,
                    includeNegatives = false,
                    onSet = { onSetProduction(ResourceKind.Steel, it) },
                    onStep = { onAdjustProduction(ResourceKind.Steel, it) },
                )
                ProductionPlate(
                    resource = ResourceKind.Titanium,
                    production = board.titanium.production,
                    cube = cube,
                    onCube = onCube,
                    includeNegatives = false,
                    onSet = { onSetProduction(ResourceKind.Titanium, it) },
                    onStep = { onAdjustProduction(ResourceKind.Titanium, it) },
                )
            }
            Row(
                Modifier.weight(0.9f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                listOf(ResourceKind.Plants, ResourceKind.Energy, ResourceKind.Heat).forEach { resource ->
                    ProductionPlate(
                        resource = resource,
                        production = board.pile(resource).production,
                        cube = cube,
                        onCube = onCube,
                        includeNegatives = false,
                        onSet = { onSetProduction(resource, it) },
                        onStep = { onAdjustProduction(resource, it) },
                    )
                }
            }
            Row(
                Modifier.weight(1.15f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                StockPlate(
                    resource = ResourceKind.Plants,
                    stock = board.plants.stock,
                    background = BoardPalette.plant,
                    contentColor = Color.White,
                    actionLabel = stringResource(R.string.convert_plants),
                    actionEnabled = board.canConvertPlants,
                    onAction = onConvertPlants,
                    onAdjust = { onAdjustStock(ResourceKind.Plants, it) },
                )
                StockPlate(
                    resource = ResourceKind.Energy,
                    stock = board.energy.stock,
                    background = BoardPalette.energy,
                    contentColor = Color.White,
                    actionLabel = stringResource(R.string.energy_becomes_heat),
                    actionEnabled = false,
                    onAction = null,
                    onAdjust = { onAdjustStock(ResourceKind.Energy, it) },
                )
                StockPlate(
                    resource = ResourceKind.Heat,
                    stock = board.heat.stock,
                    background = BoardPalette.heat,
                    contentColor = Color.White,
                    actionLabel = stringResource(R.string.convert_heat),
                    actionEnabled = board.canConvertHeat,
                    onAction = onConvertHeat,
                    onAdjust = { onAdjustStock(ResourceKind.Heat, it) },
                )
            }
        }
        BoardToolbar(
            board = board,
            onProduce = onProduce,
            onColor = onColor,
            onRequestReset = { pendingSetup = it },
        )
    }

    pendingSetup?.let { setup ->
        AlertDialog(
            onDismissRequest = { pendingSetup = null },
            title = { Text(stringResource(R.string.reset_title)) },
            text = { Text(stringResource(R.string.reset_body, setup.label())) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onReset(setup)
                        pendingSetup = null
                    },
                ) { Text(stringResource(R.string.reset)) }
            },
            dismissButton = {
                TextButton(onClick = { pendingSetup = null }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun RowScope.StockPlate(
    resource: ResourceKind,
    stock: Int,
    background: Color,
    contentColor: Color,
    onAdjust: (Int) -> Unit,
    actionLabel: String? = null,
    actionEnabled: Boolean = false,
    onAction: (() -> Unit)? = null,
) {
    val cubes = cubesFor(stock)
    val description = stringResource(R.string.stock_description, resource.label(), stock)
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .semantics { contentDescription = description }
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ResourceSymbol(resource, Modifier.size(22.dp), contentColor)
            Spacer(Modifier.width(6.dp))
            Text(
                text = resource.label(),
                color = contentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            resource.valueNote()?.let { note ->
                Text(
                    text = note,
                    color = BoardPalette.ink,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(BoardPalette.gold)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                )
            }
        }
        Box(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stock.toString(),
                color = contentColor,
                fontWeight = FontWeight.Black,
                fontSize = 28.sp,
                maxLines = 1,
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            CubeCount(BoardPalette.gold, cubes.gold, contentColor)
            Spacer(Modifier.width(4.dp))
            CubeCount(BoardPalette.silver, cubes.silver, contentColor)
            Spacer(Modifier.width(4.dp))
            CubeCount(BoardPalette.bronze, cubes.bronze, contentColor)
            Spacer(Modifier.weight(1f))
            StepChip(label = "−", onClick = { onAdjust(-1) })
            Spacer(Modifier.width(4.dp))
            StepChip(label = "+", onClick = { onAdjust(1) })
        }
        if (actionLabel != null) {
            Spacer(Modifier.height(4.dp))
            val clickable = onAction != null && actionEnabled
            Text(
                text = actionLabel,
                color = if (clickable) BoardPalette.ink else BoardPalette.ink.copy(alpha = 0.45f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (clickable) BoardPalette.cell else BoardPalette.cell.copy(alpha = 0.45f))
                    .then(
                        if (clickable) Modifier.clickable(onClick = onAction) else Modifier,
                    )
                    .padding(vertical = 3.dp),
            )
        }
    }
}

@Composable
private fun CubeCount(color: Color, count: Int, contentColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
                .border(1.dp, BoardPalette.ink.copy(alpha = 0.35f), RoundedCornerShape(2.dp)),
        )
        Text(
            text = count.toString(),
            color = contentColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 2.dp),
        )
    }
}

@Composable
private fun RowScope.ProductionPlate(
    resource: ResourceKind,
    production: Int,
    cube: Color,
    onCube: Color,
    includeNegatives: Boolean,
    onSet: (Int) -> Unit,
    onStep: (Int) -> Unit,
    leading: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clip(RoundedCornerShape(10.dp))
            .background(BoardPalette.track)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leading?.invoke()
        Column(Modifier.weight(1f).fillMaxHeight()) {
            if (includeNegatives) {
                NumberRow(
                    values = (-5..-1).toList(),
                    production = production,
                    cube = cube,
                    onCube = onCube,
                    cellBackground = BoardPalette.negativeCell,
                    spacerWeight = 1f,
                    onSet = onSet,
                )
            }
            NumberRow(
                values = (0..5).toList(),
                production = production,
                cube = cube,
                onCube = onCube,
                onSet = onSet,
            )
            NumberRow(
                values = (6..10).toList(),
                production = production,
                cube = cube,
                onCube = onCube,
                onSet = onSet,
                trailing = {
                    StepCell(production = production, cube = cube, onCube = onCube, onStep = onStep)
                },
            )
        }
    }
}

@Composable
private fun ColumnScope.NumberRow(
    values: List<Int>,
    production: Int,
    cube: Color,
    onCube: Color,
    onSet: (Int) -> Unit,
    cellBackground: Color = BoardPalette.cell,
    spacerWeight: Float = 0f,
    trailing: @Composable (RowScope.() -> Unit)? = null,
) {
    Row(Modifier.weight(1f).fillMaxWidth()) {
        values.forEach { value ->
            TrackCell(
                label = value.toString(),
                selected = production == value,
                cube = cube,
                onCube = onCube,
                background = cellBackground,
                onClick = { onSet(value) },
            )
        }
        if (trailing != null) trailing()
        if (spacerWeight > 0f) {
            Spacer(Modifier.weight(spacerWeight))
        }
    }
}

@Composable
private fun RowScope.TrackCell(
    label: String,
    selected: Boolean,
    cube: Color,
    onCube: Color,
    background: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .padding(1.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(if (selected) cube else background)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (selected) onCube else BoardPalette.ink,
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            maxLines = 1,
        )
    }
}

@Composable
private fun RowScope.StepCell(
    production: Int,
    cube: Color,
    onCube: Color,
    onStep: (Int) -> Unit,
) {
    val overflow = production > 10
    Column(
        Modifier
            .weight(1f)
            .fillMaxHeight()
            .padding(1.dp),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(3.dp))
                .background(if (overflow) cube else BoardPalette.cell)
                .clickable { onStep(1) },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (overflow) production.toString() else "›",
                color = if (overflow) onCube else BoardPalette.ink,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                maxLines = 1,
            )
        }
        Spacer(Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(3.dp))
                .background(BoardPalette.cell)
                .clickable { onStep(-1) },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "−",
                color = BoardPalette.ink,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun RatingControl(board: PlayerBoard, onAdjust: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .width(72.dp)
            .fillMaxHeight()
            .padding(end = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        GlobeSymbol(Modifier.size(18.dp))
        Text(
            text = board.terraformRating.toString(),
            color = BoardPalette.cell,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            maxLines = 1,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            StepChip("−") { onAdjust(-1) }
            StepChip("+") { onAdjust(1) }
        }
        Text(
            text = board.incomeLabel(),
            color = BoardPalette.cell,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun StepChip(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 26.dp, height = 22.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(BoardPalette.cell)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = BoardPalette.ink, fontWeight = FontWeight.Black, fontSize = 14.sp)
    }
}

@Composable
private fun BoardToolbar(
    board: PlayerBoard,
    onProduce: () -> Unit,
    onColor: (PlayerColor) -> Unit,
    onRequestReset: (GameSetup) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        ToolbarChip(stringResource(R.string.standard)) { onRequestReset(GameSetup.Standard) }
        ToolbarChip(stringResource(R.string.corporate_era)) { onRequestReset(GameSetup.CorporateEra) }
        Spacer(Modifier.weight(1f))
        PlayerColor.entries.forEach { color ->
            val selected = board.playerColor == color
            Box(
                modifier = Modifier
                    .size(if (selected) 22.dp else 16.dp)
                    .clip(CircleShape)
                    .background(cubeColor(color))
                    .clickable { onColor(color) }
                    .semantics { contentDescription = color.label() },
            )
        }
        Spacer(Modifier.width(4.dp))
        Text(
            text = stringResource(R.string.produce),
            color = BoardPalette.ink,
            fontWeight = FontWeight.Black,
            fontSize = 14.sp,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(BoardPalette.gold)
                .clickable(onClick = onProduce)
                .padding(horizontal = 14.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun ToolbarChip(label: String, onClick: () -> Unit) {
    Text(
        text = label,
        color = BoardPalette.cell,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(BoardPalette.metalDark)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp),
    )
}
