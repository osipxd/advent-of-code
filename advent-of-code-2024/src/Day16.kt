
import lib.matrix.*

private const val DAY = "Day16"

private typealias ReindeerMaze = Matrix<Char>

fun main() {
    fun testInput(n: Int) = readInput("${DAY}_test$n")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput(1)) shouldBe 7036
        part1(testInput(2)) shouldBe 11048
        measureAnswer { part1(input()) }
    }

    //"Part 2" {
    //    part2(testInput()) shouldBe 0
    //    measureAnswer { part2(input()) }
    //}
}

private fun part1(maze: ReindeerMaze): Int {
    val start = Position(row = maze.lastRowIndex - 1, column = 1)
    val end = Position(row = 1, column = maze.lastColumnIndex - 1)
    val finalKey = ReindeerState.Key(end, Direction.RIGHT) // Hardcode direction for the final state

    val bestSeenPoints = mutableMapOf<ReindeerState.Key, Int>()
    fun updateBestSeenPoints(key: ReindeerState.Key, points: Int): Boolean {
        return if (key !in bestSeenPoints || bestSeenPoints.getValue(key) >= points) {
            bestSeenPoints[key] = points
            true
        } else {
            false
        }
    }

    val queue = ArrayDeque<ReindeerState>()

    fun addNext(position: Position, direction: Direction, points: Int) {
        val state = ReindeerState(position, direction, points)
        if (updateBestSeenPoints(state.key(), points)) queue.addLast(state)
    }

    addNext(start, Direction.RIGHT, points = 0)
    addNext(start, Direction.UP, points = TURN_POINTS)
    while (queue.isNotEmpty()) {
        val (position, direction, points) = queue.removeFirst()

        // Walk until a crossroad or a wall
        val stepsForward = position.walk(direction).indexOfFirst {
            maze[it.nextBy(direction)] == '#' || it != position && maze.canTurn(it, direction)
        }
        val nextPosition = position.moveBy(direction, steps = stepsForward)
        if (nextPosition == end) {
            updateBestSeenPoints(finalKey, points + stepsForward)
        } else {
            if (maze[nextPosition.nextBy(direction)] == '.') addNext(nextPosition, direction, points + stepsForward)
            if (maze[nextPosition.nextBy(direction.turn90(clockwise = true))] == '.') addNext(nextPosition, direction.turn90(clockwise = true), points + stepsForward + TURN_POINTS)
            if (maze[nextPosition.nextBy(direction.turn90(clockwise = false))] == '.') addNext(nextPosition, direction.turn90(clockwise = false), points + stepsForward + TURN_POINTS)
        }
    }

    return bestSeenPoints.getValue(finalKey)
}

private fun ReindeerMaze.canTurn(position: Position, direction: Direction): Boolean {
    return this[position.nextBy(direction.turn90(clockwise = true))] != '#' ||
        this[position.nextBy(direction.turn90(clockwise = false))] != '#'
}

private fun part2(maze: ReindeerMaze): Int = TODO()

private const val TURN_POINTS = 1000

private data class ReindeerState(
    val position: Position,
    val direction: Direction,
    val points: Int,
) {

    fun key() = Key(position, direction)

    data class Key(val position: Position, val direction: Direction)
}

private fun readInput(name: String) = readMatrix(name)
