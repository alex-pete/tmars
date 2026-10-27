import SwiftUI
import KMPNativeCoroutinesAsync
import KMPObservableViewModelSwiftUI
import Shared

struct PlayerBoardView: View {
    @StateViewModel var viewModel = BoardViewModel()
    @State private var pendingSetup: GameSetup?

    var body: some View {
        let board = viewModel.board
        let cube = BoardChrome.cube(board.playerColor)
        let onCube = BoardChrome.onCube(board.playerColor)

        VStack(spacing: 6) {
            VStack(spacing: 6) {
                HStack(spacing: 6) {
                    StockPlate(
                        resource: ResourceKind.megaCredits,
                        pile: board.megaCredits,
                        background: BoardChrome.gold,
                        content: BoardChrome.ink,
                        onAdjust: { viewModel.adjustStock(resource: ResourceKind.megaCredits, delta: Int32($0)) }
                    )
                    StockPlate(
                        resource: ResourceKind.steel,
                        pile: board.steel,
                        background: BoardChrome.steel,
                        content: .white,
                        onAdjust: { viewModel.adjustStock(resource: ResourceKind.steel, delta: Int32($0)) }
                    )
                    StockPlate(
                        resource: ResourceKind.titanium,
                        pile: board.titanium,
                        background: BoardChrome.titanium,
                        content: .white,
                        onAdjust: { viewModel.adjustStock(resource: ResourceKind.titanium, delta: Int32($0)) }
                    )
                }
                HStack(spacing: 6) {
                    ProductionPlate(
                        production: amount(board.megaCredits.production),
                        cube: cube,
                        onCube: onCube,
                        includeNegatives: true,
                        onSet: { viewModel.setProduction(resource: ResourceKind.megaCredits, value: Int32($0)) },
                        onStep: { viewModel.adjustProduction(resource: ResourceKind.megaCredits, delta: Int32($0)) }
                    ) {
                        RatingControl(
                            rating: amount(board.terraformRating),
                            income: board.incomeLabel(),
                            onAdjust: { viewModel.adjustTerraformRating(delta: Int32($0)) }
                        )
                    }
                    ProductionPlate(
                        production: amount(board.steel.production),
                        cube: cube,
                        onCube: onCube,
                        includeNegatives: false,
                        onSet: { viewModel.setProduction(resource: ResourceKind.steel, value: Int32($0)) },
                        onStep: { viewModel.adjustProduction(resource: ResourceKind.steel, delta: Int32($0)) }
                    ) { EmptyView() }
                    ProductionPlate(
                        production: amount(board.titanium.production),
                        cube: cube,
                        onCube: onCube,
                        includeNegatives: false,
                        onSet: { viewModel.setProduction(resource: ResourceKind.titanium, value: Int32($0)) },
                        onStep: { viewModel.adjustProduction(resource: ResourceKind.titanium, delta: Int32($0)) }
                    ) { EmptyView() }
                }
                HStack(spacing: 6) {
                    lowerProduction(ResourceKind.plants, board: board, cube: cube, onCube: onCube)
                    lowerProduction(ResourceKind.energy, board: board, cube: cube, onCube: onCube)
                    lowerProduction(ResourceKind.heat, board: board, cube: cube, onCube: onCube)
                }
                HStack(spacing: 6) {
                    StockPlate(
                        resource: ResourceKind.plants,
                        pile: board.plants,
                        background: BoardChrome.plant,
                        content: .white,
                        actionTitle: "8 → greenery",
                        actionEnabled: board.canConvertPlants,
                        onAction: { viewModel.convertPlants() },
                        onAdjust: { viewModel.adjustStock(resource: ResourceKind.plants, delta: Int32($0)) }
                    )
                    StockPlate(
                        resource: ResourceKind.energy,
                        pile: board.energy,
                        background: BoardChrome.energy,
                        content: .white,
                        actionTitle: "becomes heat",
                        actionEnabled: false,
                        onAction: nil,
                        onAdjust: { viewModel.adjustStock(resource: ResourceKind.energy, delta: Int32($0)) }
                    )
                    StockPlate(
                        resource: ResourceKind.heat,
                        pile: board.heat,
                        background: BoardChrome.heat,
                        content: .white,
                        actionTitle: "8 → temperature",
                        actionEnabled: board.canConvertHeat,
                        onAction: { viewModel.convertHeat() },
                        onAdjust: { viewModel.adjustStock(resource: ResourceKind.heat, delta: Int32($0)) }
                    )
                }
            }
            .padding(6)
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(BoardChrome.metal)
            .clipShape(RoundedRectangle(cornerRadius: 12))

            toolbar(board)
        }
        .padding(6)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(BoardChrome.frame)
        .confirmationDialog(
            "Reset the board?",
            isPresented: Binding(
                get: { pendingSetup != nil },
                set: { if !$0 { pendingSetup = nil } }
            ),
            titleVisibility: .visible,
            presenting: pendingSetup
        ) { setup in
            Button("Reset", role: .destructive) {
                viewModel.reset(setup: setup)
                pendingSetup = nil
            }
            Button("Cancel", role: .cancel) {
                pendingSetup = nil
            }
        } message: { setup in
            Text("Stock, production, and terraform rating return to the \(setup.label()) setup.")
        }
    }

    private func lowerProduction(
        _ resource: ResourceKind,
        board: PlayerBoard,
        cube: Color,
        onCube: Color
    ) -> some View {
        ProductionPlate(
            production: amount(board.pile(resource: resource).production),
            cube: cube,
            onCube: onCube,
            includeNegatives: false,
            onSet: { viewModel.setProduction(resource: resource, value: Int32($0)) },
            onStep: { viewModel.adjustProduction(resource: resource, delta: Int32($0)) }
        ) { EmptyView() }
    }

    private func toolbar(_ board: PlayerBoard) -> some View {
        HStack(spacing: 6) {
            toolbarChip("Standard") { pendingSetup = GameSetup.standard }
            toolbarChip("Corporate Era") { pendingSetup = GameSetup.corporateEra }
            Spacer()
            ForEach(Array(BoardChrome.markers.enumerated()), id: \.offset) { _, color in
                Circle()
                    .fill(BoardChrome.cube(color))
                    .frame(
                        width: board.playerColor == color ? 22 : 16,
                        height: board.playerColor == color ? 22 : 16
                    )
                    .onTapGesture { viewModel.setPlayerColor(color: color) }
                    .accessibilityLabel(color.label())
            }
            Button("Produce") { viewModel.produce() }
                .buttonStyle(.borderedProminent)
                .tint(BoardChrome.gold)
                .foregroundStyle(BoardChrome.ink)
        }
    }

    private func toolbarChip(_ title: String, action: @escaping () -> Void) -> some View {
        Button(title, action: action)
            .font(.caption.weight(.bold))
            .buttonStyle(.plain)
            .padding(.horizontal, 10)
            .padding(.vertical, 8)
            .background(BoardChrome.metalDark)
            .foregroundStyle(BoardChrome.cell)
            .clipShape(RoundedRectangle(cornerRadius: 8))
    }
}

private struct StockPlate: View {
    let resource: ResourceKind
    let pile: ResourcePile
    let background: Color
    let content: Color
    var actionTitle: String? = nil
    var actionEnabled: Bool = false
    var onAction: (() -> Void)? = nil
    let onAdjust: (Int) -> Void

    var body: some View {
        let stock = amount(pile.stock)
        let cubes = pile.cubes()
        VStack(spacing: 2) {
            HStack(spacing: 6) {
                ResourceGlyph(resource: resource, tint: content)
                    .frame(width: 22, height: 22)
                Text(resource.label())
                    .font(.caption.weight(.bold))
                    .lineLimit(1)
                Spacer(minLength: 4)
                if let note = resource.valueNote() {
                    Text(note)
                        .font(.caption2.weight(.black))
                        .foregroundStyle(BoardChrome.ink)
                        .padding(.horizontal, 6)
                        .padding(.vertical, 2)
                        .background(BoardChrome.gold)
                        .clipShape(Capsule())
                }
            }
            .foregroundStyle(content)

            Text("\(stock)")
                .font(.title.weight(.black))
                .foregroundStyle(content)
                .frame(maxHeight: .infinity)
                .minimumScaleFactor(0.5)

            HStack(spacing: 4) {
                cubeCount(BoardChrome.gold, amount(cubes.gold), content)
                cubeCount(BoardChrome.silver, amount(cubes.silver), content)
                cubeCount(BoardChrome.bronze, amount(cubes.bronze), content)
                Spacer(minLength: 4)
                stepChip("−") { onAdjust(-1) }
                stepChip("+") { onAdjust(1) }
            }

            if let actionTitle {
                Button(actionTitle) { onAction?() }
                    .font(.caption2.weight(.bold))
                    .buttonStyle(.plain)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 3)
                    .background(actionEnabled ? BoardChrome.cell : BoardChrome.cell.opacity(0.45))
                    .foregroundStyle(BoardChrome.ink.opacity(actionEnabled ? 1 : 0.45))
                    .clipShape(RoundedRectangle(cornerRadius: 6))
                    .disabled(!actionEnabled || onAction == nil)
            }
        }
        .padding(.horizontal, 8)
        .padding(.vertical, 4)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(background)
        .clipShape(RoundedRectangle(cornerRadius: 10))
        .accessibilityElement(children: .combine)
        .accessibilityLabel("\(resource.label()) stock \(stock)")
    }

    private func cubeCount(_ color: Color, _ count: Int, _ content: Color) -> some View {
        HStack(spacing: 2) {
            RoundedRectangle(cornerRadius: 2)
                .fill(color)
                .overlay(RoundedRectangle(cornerRadius: 2).stroke(BoardChrome.ink.opacity(0.35), lineWidth: 1))
                .frame(width: 10, height: 10)
            Text("\(count)")
                .font(.caption2.weight(.bold))
                .foregroundStyle(content)
        }
    }

    private func stepChip(_ title: String, action: @escaping () -> Void) -> some View {
        Button(title, action: action)
            .font(.caption.weight(.black))
            .buttonStyle(.plain)
            .frame(width: 26, height: 22)
            .background(BoardChrome.cell)
            .foregroundStyle(BoardChrome.ink)
            .clipShape(RoundedRectangle(cornerRadius: 4))
    }
}

private struct ProductionPlate<Leading: View>: View {
    let production: Int
    let cube: Color
    let onCube: Color
    let includeNegatives: Bool
    let onSet: (Int) -> Void
    let onStep: (Int) -> Void
    @ViewBuilder var leading: () -> Leading

    var body: some View {
        HStack(spacing: 0) {
            leading()
            VStack(spacing: 0) {
                if includeNegatives {
                    numberRow(Array(-5 ... -1), background: BoardChrome.negativeCell, spacer: true)
                }
                numberRow(Array(0 ... 5), background: BoardChrome.cell, spacer: false)
                HStack(spacing: 2) {
                    ForEach(6 ... 10, id: \.self) { value in
                        trackCell(value, background: BoardChrome.cell)
                    }
                    stepCell
                }
            }
        }
        .padding(4)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(BoardChrome.track)
        .clipShape(RoundedRectangle(cornerRadius: 10))
    }

    private func numberRow(_ values: [Int], background: Color, spacer: Bool) -> some View {
        HStack(spacing: 2) {
            ForEach(values, id: \.self) { value in
                trackCell(value, background: background)
            }
            if spacer {
                Color.clear.frame(maxWidth: .infinity)
            }
        }
        .frame(maxHeight: .infinity)
    }

    private func trackCell(_ value: Int, background: Color) -> some View {
        let selected = production == value
        return Button("\(value)") { onSet(value) }
            .font(.caption.weight(.black))
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(selected ? cube : background)
            .foregroundStyle(selected ? onCube : BoardChrome.ink)
            .clipShape(RoundedRectangle(cornerRadius: 3))
            .buttonStyle(.plain)
    }

    private var stepCell: some View {
        let overflow = production > 10
        return VStack(spacing: 2) {
            Button(overflow ? "\(production)" : "›") { onStep(1) }
                .font(.caption.weight(.black))
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .background(overflow ? cube : BoardChrome.cell)
                .foregroundStyle(overflow ? onCube : BoardChrome.ink)
                .clipShape(RoundedRectangle(cornerRadius: 3))
                .buttonStyle(.plain)
            Button("−") { onStep(-1) }
                .font(.caption.weight(.black))
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .background(BoardChrome.cell)
                .foregroundStyle(BoardChrome.ink)
                .clipShape(RoundedRectangle(cornerRadius: 3))
                .buttonStyle(.plain)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

private struct RatingControl: View {
    let rating: Int
    let income: String
    let onAdjust: (Int) -> Void

    var body: some View {
        VStack(spacing: 2) {
            GlobeGlyph()
                .frame(width: 18, height: 18)
            Text("\(rating)")
                .font(.title3.weight(.black))
                .foregroundStyle(BoardChrome.cell)
                .minimumScaleFactor(0.6)
            HStack(spacing: 4) {
                chip("−") { onAdjust(-1) }
                chip("+") { onAdjust(1) }
            }
            Text(income)
                .font(.system(size: 8, weight: .bold))
                .foregroundStyle(BoardChrome.cell)
                .multilineTextAlignment(.center)
                .lineLimit(2)
                .minimumScaleFactor(0.7)
        }
        .frame(width: 72)
        .padding(.trailing, 4)
    }

    private func chip(_ title: String, action: @escaping () -> Void) -> some View {
        Button(title, action: action)
            .font(.caption.weight(.black))
            .buttonStyle(.plain)
            .frame(width: 26, height: 22)
            .background(BoardChrome.cell)
            .foregroundStyle(BoardChrome.ink)
            .clipShape(RoundedRectangle(cornerRadius: 4))
    }
}

private enum BoardChrome {
    static let frame = Color(red: 0x3E / 255, green: 0x2C / 255, blue: 0x28 / 255)
    static let metal = Color(red: 0xB7 / 255, green: 0xBC / 255, blue: 0xC3 / 255)
    static let metalDark = Color(red: 0x8E / 255, green: 0x94 / 255, blue: 0x9C / 255)
    static let track = Color(red: 0x8A / 255, green: 0x5A / 255, blue: 0x34 / 255)
    static let cell = Color(red: 0xF3 / 255, green: 0xF0 / 255, blue: 0xE8 / 255)
    static let negativeCell = Color(red: 0xE7 / 255, green: 0xD3 / 255, blue: 0xD0 / 255)
    static let ink = Color(red: 0x1E / 255, green: 0x1E / 255, blue: 0x1E / 255)
    static let gold = Color(red: 0xE2 / 255, green: 0xA3 / 255, blue: 0x1A / 255)
    static let steel = Color(red: 0x6E / 255, green: 0x4B / 255, blue: 0x30 / 255)
    static let titanium = Color(red: 0x1A / 255, green: 0x1C / 255, blue: 0x24 / 255)
    static let plant = Color(red: 0x2F / 255, green: 0x8F / 255, blue: 0x45 / 255)
    static let energy = Color(red: 0x69 / 255, green: 0x40 / 255, blue: 0xB5 / 255)
    static let heat = Color(red: 0xD2 / 255, green: 0x4A / 255, blue: 0x32 / 255)
    static let silver = Color(red: 0xD5 / 255, green: 0xDC / 255, blue: 0xE3 / 255)
    static let bronze = Color(red: 0xB8 / 255, green: 0x73 / 255, blue: 0x33 / 255)

    static let markers: [PlayerColor] = [
        PlayerColor.red, PlayerColor.blue, PlayerColor.green, PlayerColor.yellow, PlayerColor.black,
    ]

    static func cube(_ color: PlayerColor) -> Color {
        if color == PlayerColor.red { return Color(red: 0xC6 / 255, green: 0x28 / 255, blue: 0x28 / 255) }
        if color == PlayerColor.blue { return Color(red: 0x15 / 255, green: 0x65 / 255, blue: 0xC0 / 255) }
        if color == PlayerColor.green { return Color(red: 0x2E / 255, green: 0x7D / 255, blue: 0x32 / 255) }
        if color == PlayerColor.yellow { return Color(red: 0xF9 / 255, green: 0xA8 / 255, blue: 0x25 / 255) }
        return Color(red: 0x21 / 255, green: 0x21 / 255, blue: 0x21 / 255)
    }

    static func onCube(_ color: PlayerColor) -> Color {
        color == PlayerColor.yellow ? ink : .white
    }
}

private struct ResourceGlyph: View {
    let resource: ResourceKind
    let tint: Color

    var body: some View {
        if resource == ResourceKind.megaCredits {
            Text("€")
                .font(.caption.weight(.black))
                .foregroundStyle(BoardChrome.ink)
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .background(BoardChrome.gold)
                .clipShape(Circle())
                .overlay(Circle().stroke(Color(red: 0xC4 / 255, green: 0x8A / 255, blue: 0x12 / 255), lineWidth: 2))
        } else if resource == ResourceKind.steel {
            ToolGlyph(tint: tint)
        } else if resource == ResourceKind.titanium {
            StarGlyph(tint: tint)
        } else if resource == ResourceKind.plants {
            LeafGlyph(tint: tint)
        } else if resource == ResourceKind.energy {
            BoltGlyph(tint: tint)
        } else {
            HeatGlyph(tint: tint)
        }
    }
}

private struct ToolGlyph: View {
    let tint: Color
    var body: some View {
        Canvas { context, size in
            var head = Path()
            head.move(to: CGPoint(x: size.width * 0.18, y: size.height * 0.28))
            head.addLine(to: CGPoint(x: size.width * 0.82, y: size.height * 0.28))
            context.stroke(head, with: .color(tint), lineWidth: size.width * 0.12)
            var handle = Path()
            handle.move(to: CGPoint(x: size.width * 0.34, y: size.height * 0.28))
            handle.addLine(to: CGPoint(x: size.width * 0.34, y: size.height * 0.82))
            context.stroke(handle, with: .color(tint), lineWidth: size.width * 0.12)
            let wrench = Path(ellipseIn: CGRect(x: size.width * 0.52, y: size.height * 0.46, width: size.width * 0.32, height: size.width * 0.32))
            context.fill(wrench, with: .color(tint))
        }
    }
}

private struct StarGlyph: View {
    let tint: Color
    var body: some View {
        Canvas { context, size in
            context.fill(starPath(in: size), with: .color(tint))
        }
    }
}

private struct LeafGlyph: View {
    let tint: Color
    var body: some View {
        Canvas { context, size in
            var leaf = Path()
            leaf.move(to: CGPoint(x: size.width * 0.5, y: size.height * 0.92))
            leaf.addQuadCurve(to: CGPoint(x: size.width * 0.5, y: size.height * 0.08), control: CGPoint(x: -size.width * 0.05, y: size.height * 0.45))
            leaf.addQuadCurve(to: CGPoint(x: size.width * 0.5, y: size.height * 0.92), control: CGPoint(x: size.width * 1.05, y: size.height * 0.45))
            context.fill(leaf, with: .color(tint))
        }
    }
}

private struct BoltGlyph: View {
    let tint: Color
    var body: some View {
        Canvas { context, size in
            var bolt = Path()
            bolt.move(to: CGPoint(x: size.width * 0.58, y: size.height * 0.06))
            bolt.addLine(to: CGPoint(x: size.width * 0.22, y: size.height * 0.54))
            bolt.addLine(to: CGPoint(x: size.width * 0.48, y: size.height * 0.54))
            bolt.addLine(to: CGPoint(x: size.width * 0.38, y: size.height * 0.94))
            bolt.addLine(to: CGPoint(x: size.width * 0.80, y: size.height * 0.40))
            bolt.addLine(to: CGPoint(x: size.width * 0.52, y: size.height * 0.40))
            bolt.closeSubpath()
            context.fill(bolt, with: .color(tint))
        }
    }
}

private struct HeatGlyph: View {
    let tint: Color
    var body: some View {
        Canvas { context, size in
            for index in 0 ..< 3 {
                let x = size.width * (0.25 + CGFloat(index) * 0.25)
                var wave = Path()
                wave.move(to: CGPoint(x: x, y: size.height * 0.1))
                wave.addQuadCurve(
                    to: CGPoint(x: x, y: size.height * 0.5),
                    control: CGPoint(x: x - size.width * 0.12, y: size.height * 0.32)
                )
                wave.addQuadCurve(
                    to: CGPoint(x: x, y: size.height * 0.9),
                    control: CGPoint(x: x + size.width * 0.12, y: size.height * 0.68)
                )
                context.stroke(wave, with: .color(tint), lineWidth: size.width * 0.08)
            }
        }
    }
}

private struct GlobeGlyph: View {
    var body: some View {
        Canvas { context, size in
            let rect = CGRect(origin: .zero, size: size).insetBy(dx: size.width * 0.1, dy: size.height * 0.1)
            context.stroke(Path(ellipseIn: rect), with: .color(BoardChrome.cell), lineWidth: 1.5)
            var equator = Path()
            equator.move(to: CGPoint(x: rect.minX, y: rect.midY))
            equator.addLine(to: CGPoint(x: rect.maxX, y: rect.midY))
            context.stroke(equator, with: .color(BoardChrome.cell), lineWidth: 1.2)
        }
    }
}

private func starPath(in size: CGSize) -> Path {
    var path = Path()
    let center = CGPoint(x: size.width / 2, y: size.height / 2)
    let outer = min(size.width, size.height) / 2
    let inner = outer * 0.42
    for point in 0 ..< 10 {
        let radius = point % 2 == 0 ? outer : inner
        let angle = (Double(point) * .pi / 5) - .pi / 2
        let x = center.x + CGFloat(cos(angle) * radius)
        let y = center.y + CGFloat(sin(angle) * radius)
        if point == 0 {
            path.move(to: CGPoint(x: x, y: y))
        } else {
            path.addLine(to: CGPoint(x: x, y: y))
        }
    }
    path.closeSubpath()
    return path
}

private func amount(_ value: Int32) -> Int { Int(value) }
