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
    simulateGuard(input) { (position, _), _ -> seen.add(position) }

    return seen.size
}

private fun part2(input: Input): Int {
    val visited = mutableSetOf<Position>()
    var possibleObstaclesCount = 0

    fun willFormCycle(start: Position, initialDirection: Direction, obstacles: Set<Position>): Boolean {
        val seen = mutableSetOf<Pair<Position, Direction>>()
        simulateGuard(input, start, initialDirection, obstacles) { state, _ -> if (!seen.add(state)) return true }
        return false
    }

    simulateGuard(input) { (position, _), (nextPosition, nextDirection) ->
        visited.add(position)

        if (nextPosition !in visited &&
            willFormCycle(
                start = position,
                initialDirection = nextDirection.turn90(),
                obstacles = input.obstacles + nextPosition,
            )
        ) {
            possibleObstaclesCount++
        }
    }

    return possibleObstaclesCount
}

private inline fun simulateGuard(
    input: Input,
    start: Position = input.start,
    initialDirection: Direction = Direction.UP,
    obstacles: Set<Position> = input.obstacles,
    onEachStep: (State, State) -> Unit = { _, _ -> },
) {
    var position = start
    var direction = initialDirection

    while (position in input.bounds) {
        val previousDirection = direction
        var nextPosition = position.nextInDirection(direction)
        while (nextPosition in obstacles) {
            direction = direction.turn90()
            nextPosition = position.nextInDirection(direction)
        }

        onEachStep(position to previousDirection, nextPosition to direction)
        position = nextPosition
    }
}

private fun readInput(name: String): Input {
    val lines = readLines(name)
    val obstacles = mutableSetOf<Position>()
    var start: Position? = null

    for ((row, line) in lines.withIndex()) {
        for ((column, char) in line.withIndex()) {
            when (char) {
                '#' -> obstacles += Position(row, column)
                '^' -> start = Position(row, column)
            }
        }
    }

    return Input(
        start = start!!,
        bounds = Bounds(lines.indices, lines.first().indices),
        obstacles = obstacles
    )
}

private typealias State = Pair<Position, Direction>

private data class Input(
    val start: Position,
    val bounds: Bounds,
    val obstacles: Set<Position>,
)

private data class Bounds(val rowBounds: IntRange, val columnBounds: IntRange) {
    operator fun contains(position: Position): Boolean = position.row in rowBounds && position.column in columnBounds
}
