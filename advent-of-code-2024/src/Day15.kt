import lib.matrix.*

private const val DAY = "Day15"

private typealias WarehouseMap = Matrix<Char>

fun main() {
    fun testInput(n: Int) = readInput("${DAY}_test$n")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput(1)) shouldBe 2028
        part1(testInput(2)) shouldBe 10092
        measureAnswer { part1(input()) }
    }

    //"Part 2" {
    //    part2(testInput()) shouldBe 0
    //    measureAnswer { part2(input()) }
    //}
}

private fun part1(input: Pair<WarehouseMap, List<Direction>>): Int {
    val (map, movements) = input

    fun tryMove(position: Position, move: Direction): Position {
        val nextPosition = position + move
        val nextTile = map[nextPosition]

        if (nextTile == '#' || nextTile == 'O' && tryMove(nextPosition, move) == nextPosition) return position

        map[nextPosition] = map[position]
        map[position] = '.'
        return nextPosition
    }

    var position = map.valuePositions { it == '@' }.first()
    for (move in movements) {
        position = tryMove(position, move)
    }

    return map.valuePositions { it == 'O' }.sumOf(::gpsCoordinate)
}

private fun gpsCoordinate(position: Position) = position.row * 100 + position.column

private fun part2(input: Pair<WarehouseMap, List<Direction>>): Int = TODO()

private fun readInput(name: String): Pair<WarehouseMap, List<Direction>> {
    val (rawMap, rawMovements) = readText(name).split("\n\n")
    val map = rawMap.lines().toMatrix()
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
