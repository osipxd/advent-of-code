private typealias Maze = List<MazeRow>
private typealias MazeRow = List<Pipe>

private const val DAY = "Day10"

fun main() {
    fun testInput(id: Int) = readInput("${DAY}_test$id")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput(0)) shouldBe 4
        part1(testInput(1)) shouldBe 8
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput(2)) shouldBe 4
        part2(testInput(3)) shouldBe 4
        part2(testInput(4)) shouldBe 8
        part2(testInput(5)) shouldBe 10
        measureAnswer { part2(input()) }
    }
}

private fun part1(maze: Maze): Int = findLoop(maze).size / 2

private fun part2(maze: Maze): Int {
    val loop = findLoop(maze)

    // Ignore first and last row as it cannot contain tiles lying inside loop
    return (1..<maze.lastIndex).sumOf { row ->
        countInnerTilesInRow(
            row = maze[row],
            isPartOfLoop = { col -> row to col in loop }
        )
    }
}

// Idea is to track flag "if we are inside loop". Initially we are outside,
// but if we face ║, ╔╝ or ╚╗ - we change the flag to the opposite.
// Combinations ╔╗ and ╚╝ doesn't affect the flag. Horizontal pipes just ignored.
private fun countInnerTilesInRow(row: MazeRow, isPartOfLoop: (col: Int) -> Boolean): Int {
    var tilesInsideLoop = 0
    var insideLoop = false
    var savedTile: Pipe? = null

    for ((col, tile) in row.withIndex()) {
        if (isPartOfLoop(col)) {
            when (tile) {
                Pipe.LEFT_RIGHT -> Unit // skip
                Pipe.TOP_BOTTOM -> insideLoop = !insideLoop
                else -> {
                    if (tile == savedTile?.opposite) insideLoop = !insideLoop
                    savedTile = if (savedTile == null) tile else null
                }
            }
        } else if (insideLoop) {
            tilesInsideLoop++
        }
    }
    return tilesInsideLoop
}

private fun readInput(name: String) = readLines(name).map { it.map(Pipe::bySymbol).toMutableList() }

/** Returns set of positions making loop. Replaces start tile with pipe. */
private fun findLoop(maze: Maze): Set<Position> {
    val startPosition = findStartPosition(maze)
    var (comeFromSide, position) = sequenceOf(SIDE_TOP, SIDE_BOTTOM, SIDE_LEFT, SIDE_RIGHT)
        .map { side -> side to startPosition.nextBySide(side) }
        .filter { (_, position) -> position in maze }
        .first { (comeFromSide, position) -> maze[position].canBeReachedFrom(comeFromSide) }
    val startSide = comeFromSide

    return buildSet {
        add(position)
        while (position != startPosition) {
            val pipe = maze[position]
            val sideToGo = pipe.nextSideToGo(comeFromSide)
            position = position.nextBySide(sideToGo)
            comeFromSide = sideToGo
            add(position)
        }
    }.also {
        // Replace start tile with derived pipe tile
        maze[startPosition] = Pipe.bySides(startSide or sideOppositeTo(comeFromSide))
    }
}

private fun findStartPosition(maze: Maze): Position {
    for (row in maze.indices) {
        for (col in maze[row].indices) {
            if (maze[row][col] == Pipe.START) {
                return row to col
            }
        }
    }
    error("Start position not found")
}

private enum class Pipe(val symbol: Char, val sides: Int) {
    TOP_LEFT(symbol = 'J', sides = SIDE_TOP or SIDE_LEFT),
    TOP_BOTTOM(symbol = '|', sides = SIDE_TOP or SIDE_BOTTOM),
    TOP_RIGHT(symbol = 'L', sides = SIDE_TOP or SIDE_RIGHT),
    LEFT_RIGHT(symbol = '-', sides = SIDE_LEFT or SIDE_RIGHT),
    LEFT_BOTTOM(symbol = '7', sides = SIDE_LEFT or SIDE_BOTTOM),
    RIGHT_BOTTOM(symbol = 'F', sides = SIDE_RIGHT or SIDE_BOTTOM),
    START(symbol = 'S', sides = MASK_NONE),
    EMPTY(symbol = '.', sides = MASK_NONE);

    val opposite: Pipe
        get() = Pipe.bySides(sides xor MASK_ALL)

    fun nextSideToGo(comeFromSide: Int): Int = sides xor sideOppositeTo(comeFromSide)
    fun canBeReachedFrom(side: Int): Boolean = sideOppositeTo(side) or sides == sides

    companion object {
        fun bySymbol(symbol: Char): Pipe = entries.first { it.symbol == symbol }
        fun bySides(sides: Int): Pipe = entries.first { it.sides == sides }
    }
}

private fun Position.nextBySide(side: Int): Position {
    val (row, col) = this
    return when (side) {
        SIDE_TOP -> row - 1 to col
        SIDE_BOTTOM -> row + 1 to col
        SIDE_LEFT -> row to col - 1
        SIDE_RIGHT -> row to col + 1
        else -> error("Unexpected side: $side")
    }
}

private fun sideOppositeTo(side: Int): Int = when (side) {
    SIDE_TOP, SIDE_BOTTOM -> MASK_HORIZONTAL xor side
    SIDE_LEFT, SIDE_RIGHT -> MASK_VERTICAL xor side
    else -> error("Unexpected side: $side")
}

private const val MASK_ALL = 0b1111
private const val MASK_HORIZONTAL = 0b1100
private const val MASK_VERTICAL = 0b0011
private const val MASK_NONE = 0b0000

private const val SIDE_TOP = 0b1000
private const val SIDE_BOTTOM = 0b0100
private const val SIDE_LEFT = 0b0010
private const val SIDE_RIGHT = 0b0001

// region Maze utils
private operator fun Maze.contains(position: Position): Boolean {
    val (row, col) = position
    return row in indices && col in get(row).indices
}

private operator fun Maze.get(position: Position): Pipe {
    val (row, col) = position
    return this[row][col]
}

private operator fun Maze.set(position: Position, pipe: Pipe) {
    val (row, col) = position
    // I'm sorry for this hack, but I don't want to make the whole Maze mutable.
    (this[row] as MutableList<Pipe>)[col] = pipe
}
// endregion
