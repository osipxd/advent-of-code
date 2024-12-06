import lib.matrix.Direction
import lib.matrix.Direction.Companion.nextInDirection
import lib.matrix.Position

private const val DAY = "Day06"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 41
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 6
        measureAnswer { part2(input()) }
    }
}

private fun part1(input: Input): Int {
    val seen = mutableSetOf<Position>()
    var position = input.start
    var direction = Direction.UP

    while (position.row in 0..<input.height && position.column in 0..<input.width) {
        seen.add(position)

        var nextPosition = position.nextInDirection(direction)
        while (nextPosition in input.obstacles) {
            direction = direction.turn90()
            nextPosition = position.nextInDirection(direction)
        }
        position = nextPosition
    }

    return seen.size
}

private fun part2(input: Input): Int {
    var position = input.start
    var direction = Direction.UP
    val possibleObstacles = mutableSetOf<Position>()

    while (true) {
        var nextPosition = position.nextInDirection(direction)
        while (nextPosition in input.obstacles) {
            direction = direction.turn90()
            nextPosition = position.nextInDirection(direction)
        }

        if (nextPosition.row !in 0..<input.height || nextPosition.column !in 0..<input.width) {
            break
        }

        if (nextPosition !in possibleObstacles &&
            nextPosition != input.start &&
            willFormCycle(
                start = input.start,
                initialDirection = Direction.UP,
                obstacles = input.obstacles + nextPosition,
                size = input.width to input.height
            )
        ) {
            possibleObstacles.add(nextPosition)
        }

        position = nextPosition
    }

    return possibleObstacles.size
}

private fun willFormCycle(
    start: Position,
    initialDirection: Direction,
    obstacles: Set<Position>,
    size: Pair<Int, Int>
): Boolean {
    val seen = mutableSetOf<Pair<Position, Direction>>()
    val (width, height) = size
    var position = start
    var direction = initialDirection

    while (position.row in 0..<height && position.column in 0..<width) {
        if (!seen.add(position to direction)) return true

        var nextPosition = position.nextInDirection(direction)
        while (nextPosition in obstacles) {
            direction = direction.turn90()
            nextPosition = position.nextInDirection(direction)
        }
        position = nextPosition
    }

    return false
}

private fun readInput(name: String): Input {
    var width = 0
    var height = 0
    val obstacles = mutableSetOf<Position>()
    var start: Position? = null

    readLines(name) { line ->
        height++
        if (width == 0) width = line.length

        for ((column, char) in line.withIndex()) {
            if (char == '#') {
                obstacles += Position(height - 1, column)
            } else if (char == '^') {
                start = Position(height - 1, column)
            }
        }
    }

    return Input(start!!, width, height, obstacles)
}

private data class Input(
    val start: Position,
    val width: Int,
    val height: Int,
    val obstacles: Set<Position>,
)