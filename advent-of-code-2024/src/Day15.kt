import lib.matrix.*

private const val DAY = "Day15"

private typealias WarehouseMap = Matrix<Char>

fun main() {
    "Part 1" {
        fun testInput(n: Int) = readInput("${DAY}_test$n")
        fun input() = readInput(DAY)

        part1(testInput(1)) shouldBe 2028
        part1(testInput(2)) shouldBe 10092
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        fun testInput(n: Int) = readWideInput("${DAY}_test$n")
        fun input() = readWideInput(DAY)

        part2(testInput(2)) shouldBe 9021
        measureAnswer { part2(input()) }
    }
}

private fun part1(input: Pair<WarehouseMap, List<Direction>>): Int = simulateMovements(input, tileToCount = 'O')
private fun part2(input: Pair<WarehouseMap, List<Direction>>): Int = simulateMovements(input, tileToCount = '[')

private fun simulateMovements(input: Pair<WarehouseMap, List<Direction>>, tileToCount: Char): Int {
    val (map, movements) = input

    fun tryMove(positions: List<Position>, move: Direction): Boolean {
        if (positions.isEmpty()) return true

        val nextPositions = positions.map { it + move }
        if (nextPositions.any { map[it] == '#' }) return false

        val nextToMove = nextPositions.filter { map[it] != '.' }
            .flatMap { position -> getMovingPositions(position, map[position], move) }
            .distinct()

        if (!tryMove(nextToMove, move)) return false
        for (i in positions.indices) {
            map[nextPositions[i]] = map[positions[i]]
            map[positions[i]] = '.'
        }

        return true
    }

    var position = map.valuePositions { it == '@' }.first()
    for (move in movements) {
        if (tryMove(listOf(position), move)) {
            position += move
        }
    }

    return map.valuePositions { it == tileToCount }.sumOf(::gpsCoordinate)
}

private fun getMovingPositions(position: Position, tile: Char, direction: Direction) = sequence {
    yield(position)
    if (direction.isVertical) {
        when (tile) {
            '[' -> yield(position.nextBy(Direction.RIGHT))
            ']' -> yield(position.nextBy(Direction.LEFT))
        }
    }
}

private fun gpsCoordinate(position: Position) = position.row * 100 + position.column

private fun readWideInput(name: String) = readInput(name) { char ->
    when (char) {
        '@' -> "@."
        'O' -> "[]"
        else -> "$char$char"
    }
}

private fun readInput(
    name: String,
    transform: (Char) -> String = { it.toString() },
): Pair<WarehouseMap, List<Direction>> {
    val (rawMap, rawMovements) = readText(name).split("\n\n")
    val map = rawMap.lines().map { mapLine ->
        mapLine.flatMap { transform(it).asIterable() }
    }.toMatrix()
    val movements = rawMovements.mapNotNull(Direction::byCharOrNull)
    return map to movements
}

// Utils

private fun Direction.Companion.byCharOrNull(char: Char): Direction? = when (char) {
    '^' -> Direction.UP
    'v' -> Direction.DOWN
    '<' -> Direction.LEFT
    '>' -> Direction.RIGHT
    else -> null
}

private val Direction.isVertical
    get() = this.row != 0
