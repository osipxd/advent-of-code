import lib.matrix.*

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

private fun part1(input: GuardSituationMap): Int = simulateGuardPath(input)

private fun part2(input: GuardSituationMap): Int {
    var count = 0

    fun GuardPathSimulationContext.willFormCycle(addObstacleAt: Position): Boolean =
        simulateGuardPath(input, position, nextDirection.turn90(), input.obstacles + addObstacleAt) == CYCLE_PATH

    simulateGuardPath(input) {
        if (nextPosition !in visited && willFormCycle(addObstacleAt = nextPosition)) count++
    }

    return count
}

/**
 * Simulates guard's path considering given input circumstances and returns number of visited positions
 * at the moment the guard leaves the map. Or [CYCLE_PATH] if the guard can't escape.
 */
private inline fun simulateGuardPath(
    input: GuardSituationMap,
    start: Position = input.start,
    initialDirection: Direction = Direction.UP,
    obstacles: Set<Position> = input.obstacles,
    onEachStep: GuardPathSimulationContext.() -> Unit = {},
): Int {
    val visitedPositions = mutableSetOf<Position>()
    val visitedStates = mutableSetOf<Pair<Position, Direction>>()
    var position = start
    var direction = initialDirection

    while (position in input.bounds) {
        if (!visitedStates.add(position to direction)) return CYCLE_PATH
        visitedPositions.add(position)

        var nextPosition = position.nextBy(direction)
        while (nextPosition in obstacles) {
            direction = direction.turn90()
            nextPosition = position.nextBy(direction)
        }

        onEachStep(GuardPathSimulationContext(position, nextPosition, direction, visitedPositions))
        position = nextPosition
    }

    return visitedPositions.size
}

private const val CYCLE_PATH = -1

private data class GuardPathSimulationContext(
    val position: Position,
    val nextPosition: Position,
    val nextDirection: Direction,
    val visited: Set<Position>,
)

private fun readInput(name: String): GuardSituationMap {
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

    return GuardSituationMap(
        start = start!!,
        bounds = Bounds(lines.indices, lines.first().indices),
        obstacles = obstacles
    )
}

private data class GuardSituationMap(
    val start: Position,
    val bounds: Bounds,
    val obstacles: Set<Position>,
)
