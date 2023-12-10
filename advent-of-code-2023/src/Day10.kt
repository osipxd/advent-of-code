private typealias Maze = List<List<Pipe>>
private typealias Position = Pair<Int, Int>

private const val DAY = "Day10"

fun main() {
    fun testInput(id: Int) = readInput("${DAY}_test$id")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput(0)) shouldBe 4
        part1(testInput(1)) shouldBe 8
        measureAnswer { part1(input()) }
    }

    //"Part 2" {
    //    part2(testInput()) shouldBe 0
    //    measureAnswer { part2(input()) }
    //}
}

private fun part1(maze: Maze): Int {
    val startPosition = findStartPosition(maze)
    var (comeFromSide, position) = sequenceOf(TOP, BOTTOM, LEFT, RIGHT)
        .map { side -> side to startPosition.nextBySide(side) }
        .first { (comeFromSide, position) -> maze[position].canBeReachedFrom(comeFromSide) }

    var steps = 1
    while (position != startPosition) {
        val pipe = maze[position]
        val sideToGo = pipe.nextSide(comeFromSide)
        position = position.nextBySide(sideToGo)
        comeFromSide = sideToGo
        steps++
    }

    return steps / 2
}

private fun part2(input: Maze): Int = TODO()

private fun readInput(name: String) = readLines(name).map { it.map(Pipe::bySymbol) }

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
    TOP_RIGHT(symbol = 'J', sides = TOP or LEFT),
    TOP_BOTTOM(symbol = '|', sides = TOP or BOTTOM),
    TOP_LEFT(symbol = 'L', sides = TOP or RIGHT),
    LEFT_RIGHT(symbol = '-', sides = LEFT or RIGHT),
    LEFT_BOTTOM(symbol = '7', sides = LEFT or BOTTOM),
    RIGHT_BOTTOM(symbol = 'F', sides = RIGHT or BOTTOM),
    START(symbol = 'S', sides = 0),
    NONE(symbol = '.', sides = 0);

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
