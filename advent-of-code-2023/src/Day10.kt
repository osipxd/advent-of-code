private typealias Maze = List<MutableList<Pipe>>

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

/*
║═╔╗╚

║ - opposite
╔ - if ╔╝ - opposite
  - if ╗ - nothing
╚ - if ╚╗ - opposite
  - if ╝ - nothing
 */

private fun part2(maze: Maze): Int {
    val loop = findLoop(maze)
    var tilesInsideLoop = 0

    // Ignore first and last row as it cannot contain tiles inside loop
    for (row in 1..<maze.lastIndex) {
        var insideLoop = false
        var savedTile: Pipe? = null
        for (col in maze[row].indices) {
            if (row to col in loop) {
                val tile = maze[row][col]
                when {
                    tile == Pipe.TOP_BOTTOM -> insideLoop = !insideLoop
                    tile == Pipe.LEFT_RIGHT -> Unit // skip
                    savedTile == null -> savedTile = tile
                    else -> {
                        if (tile == savedTile.opposite) insideLoop = !insideLoop
                        savedTile = null
                    }
                }
            } else if (insideLoop) {
                tilesInsideLoop++
            }
        }
    }

    return tilesInsideLoop
}

private fun readInput(name: String) = readLines(name).map { it.map(Pipe::bySymbol).toMutableList() }

/** Returns set of positions making loop. Replaces start tile with pipe. */
private fun findLoop(maze: Maze): Set<Position> {
    val startPosition = findStartPosition(maze)
    var (comeFromSide, position) = sequenceOf(TOP, BOTTOM, LEFT, RIGHT)
        .map { side -> side to startPosition.nextBySide(side) }
        .filter { (_, position) -> position in maze }
        .first { (comeFromSide, position) -> maze[position].canBeReachedFrom(comeFromSide) }
    val startSide = comeFromSide

    return buildSet {
        add(position)
        while (position != startPosition) {
            val pipe = maze[position]
            val sideToGo = pipe.nextSide(comeFromSide)
            position = position.nextBySide(sideToGo)
            comeFromSide = sideToGo
            add(position)
        }
    }.also {
        val pipe = Pipe.bySides(startSide or sideOppositeTo(comeFromSide))
        val (row, col) = startPosition
        maze[row][col] = pipe
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
    TOP_LEFT(symbol = 'J', sides = TOP or LEFT),
    TOP_BOTTOM(symbol = '|', sides = TOP or BOTTOM),
    TOP_RIGHT(symbol = 'L', sides = TOP or RIGHT),
    LEFT_RIGHT(symbol = '-', sides = LEFT or RIGHT),
    LEFT_BOTTOM(symbol = '7', sides = LEFT or BOTTOM),
    RIGHT_BOTTOM(symbol = 'F', sides = RIGHT or BOTTOM),
    START(symbol = 'S', sides = 0),
    EMPTY(symbol = '.', sides = 0);

    val opposite: Pipe
        get() = Pipe.bySides(sides xor 0b1111)

    fun nextSide(comeFromSide: Int): Int = sides xor sideOppositeTo(comeFromSide)
    fun canBeReachedFrom(side: Int): Boolean = sideOppositeTo(side) or sides == sides

    companion object {
        fun bySymbol(symbol: Char): Pipe = entries.first { it.symbol == symbol }
        fun bySides(sides: Int): Pipe = entries.first { it.sides == sides }
    }
}

private fun Position.nextBySide(side: Int): Position {
    val (row, col) = this
    return when (side) {
        TOP -> row - 1 to col
        BOTTOM -> row + 1 to col
        LEFT -> row to col - 1
        RIGHT -> row to col + 1
        else -> error("Unexpected side: $side")
    }
}

private fun sideOppositeTo(side: Int): Int = when (side) {
    TOP, BOTTOM -> HORIZONTAL xor side
    LEFT, RIGHT -> VERTICAL xor side
    NONE -> NONE
    else -> error("Unexpected side: $side")
}


private operator fun Maze.contains(position: Position): Boolean {
    val (row, col) = position
    return row in indices && col in get(row).indices
}

private operator fun Maze.get(position: Position): Pipe {
    val (row, col) = position
    return this[row][col]
}

private const val TOP = 0b1000
private const val BOTTOM = 0b0100
private const val HORIZONTAL = 0b1100
private const val LEFT = 0b0010
private const val RIGHT = 0b0001
private const val VERTICAL = 0b0011
private const val NONE = 0b0000
