import lib.matrix.*

private const val DAY = "Day16"

private typealias ReindeerMaze = Matrix<Char>

fun main() {
    fun testInput(n: Int) = readInput("${DAY}_test$n")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput(1)) shouldBe 7036
        part1(testInput(2)) shouldBe 11048
        measureAnswer(expected = 95476) { part1(input()) }
    }

    "Part 2" {
        part2(testInput(1)) shouldBe 45
        part2(testInput(2)) shouldBe 64
        measureAnswer { part2(input()) }
    }
}

private fun part1(maze: ReindeerMaze): Int = findBestPath(maze).first
private fun part2(maze: ReindeerMaze): Int = findBestPath(maze).second

private fun findBestPath(maze: ReindeerMaze): Pair<Int, Int> {
    val bestSeenPoints = mutableMapOf<ReindeerState.Key, Int>()
    fun updateBestSeenPoints(key: ReindeerState.Key, points: Int): Boolean {
        return (key !in bestSeenPoints || bestSeenPoints.getValue(key) >= points)
            .also { if (it) bestSeenPoints[key] = points }
    }

    val queue = ArrayDeque<ReindeerState>()
    fun tryAddNext(state: ReindeerState) {
        val isDeadEnd = maze[state.position.nextBy(state.direction)] == '#'
        if (!isDeadEnd && updateBestSeenPoints(state.key(), state.points)) queue.addLast(state)
    }

    val finalKey = maze.finalKey
    val end = finalKey.position

    var visitedTiles = emptySet<Position>()
    fun onFinalState(state: ReindeerState) {
        when {
            finalKey !in bestSeenPoints || bestSeenPoints.getValue(finalKey) > state.points -> {
                bestSeenPoints[finalKey] = state.points
                visitedTiles = state.visited
            }
            bestSeenPoints.getValue(finalKey) == state.points -> {
                visitedTiles = visitedTiles + state.visited
            }
        }
    }

    val startState = ReindeerState(maze.start, Direction.RIGHT)
    tryAddNext(startState)
    tryAddNext(startState.turn(clockwise = false))
    while (queue.isNotEmpty()) {
        val state = queue.removeFirst()
        if (bestSeenPoints.getValue(state.key()) < state.points) continue
        val (position, direction) = state

        // Walk until a crossroad or a wall
        val stepsForward = position.walk(direction).indexOfFirst {
            maze[it.nextBy(direction)] == '#' || it != position && maze.canTurn(it, direction)
        }
        val nextState = state.move(stepsForward)
        if (nextState.position == end) {
            onFinalState(nextState)
        } else {
            tryAddNext(nextState)
            tryAddNext(nextState.turn(clockwise = true))
            tryAddNext(nextState.turn(clockwise = false))
        }
    }

    return bestSeenPoints.getValue(finalKey) to visitedTiles.size
}

private fun ReindeerMaze.canTurn(position: Position, direction: Direction): Boolean {
    return this[position.nextBy(direction.turn90(clockwise = true))] != '#' ||
        this[position.nextBy(direction.turn90(clockwise = false))] != '#'
}

private val ReindeerMaze.start: Position get() = Position(row = lastRowIndex - 1, column = 1)
private val ReindeerMaze.end: Position get() = Position(row = 1, column = lastColumnIndex - 1)

// Hardcode direction for the final state
private val ReindeerMaze.finalKey: ReindeerState.Key get() = ReindeerState.Key(end, Direction.RIGHT)

private const val TURN_POINTS = 1000

private data class ReindeerState(
    val position: Position,
    val direction: Direction,
    val points: Int = 0,
    val visited: Set<Position> = setOf(position),
) {
    fun move(steps: Int) = copy(
        position = position + (direction * steps),
        points = points + steps,
        visited = visited + position.walk(direction).drop(1).take(steps),
    )
    fun turn(clockwise: Boolean) = copy(direction = direction.turn90(clockwise), points = points + TURN_POINTS)

    fun key() = Key(position, direction)

    data class Key(val position: Position, val direction: Direction)
}

private fun readInput(name: String) = readMatrix(name)
