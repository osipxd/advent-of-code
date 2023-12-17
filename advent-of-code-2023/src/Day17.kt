import BeamDirection.DOWN
import BeamDirection.RIGHT
import kotlin.math.min

private const val DAY = "Day17"

fun main() {
    fun testInput(id: Int) = readInput("${DAY}_test$id")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput(1)) shouldBe 102
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput(1)) shouldBe 94
        part2(testInput(2)) shouldBe 71
        measureAnswer { part2(input()) }
    }
}


private fun part1(input: List<List<Int>>): Int = solve(input, steps = 1..3)
private fun part2(input: List<List<Int>>): Int = solve(input, steps = 4..10)

private fun solve(input: List<List<Int>>, steps: IntRange): Int {
    val slots = ArrayDeque<State>()
    val bestSeen = mutableMapOf<Pair<Position, Boolean>, Int>()

    val lookForPosition = input.lastIndex to input.first().lastIndex
    var resultLoss = Int.MAX_VALUE

    fun State.key() = position to lastDirection.horizontal

    fun addState(state: State) {
        val key = state.key()
        if (state.position == lookForPosition) {
            resultLoss = min(resultLoss, state.loss)
        } else if (key !in bestSeen || bestSeen.getValue(key) > state.loss) {
            bestSeen[key] = state.loss
            slots.addLast(state)
        }
    }

    tailrec fun State.goInDirection(newDirection: BeamDirection = lastDirection, steps: IntRange) {
        val newPosition = newDirection.nextPosition(position)
        if (newPosition in input) {
            val newState = copy(
                position = newPosition,
                loss = loss + input[newPosition],
                lastDirection = newDirection,
            )
            val nextSteps = if (steps.first == 1) {
                addState(newState)
                1..<steps.last
            } else {
                (steps.first - 1)..<steps.last
            }
            if (!nextSteps.isEmpty()) newState.goInDirection(steps = nextSteps)
        }
    }

    val initialState = State(position = 0 to 0, loss = 0, lastDirection = RIGHT)
    initialState.goInDirection(RIGHT, steps)
    initialState.goInDirection(DOWN, steps)

    while (slots.isNotEmpty()) {
        val state = slots.removeFirst()
        if (bestSeen.getValue(state.key()) < state.loss) continue
        state.goInDirection(state.lastDirection.turn(clockwise = true), steps)
        state.goInDirection(state.lastDirection.turn(clockwise = false), steps)
    }

    return resultLoss
}

private data class State(
    val position: Position,
    val lastDirection: BeamDirection,
    val loss: Int,
)

private fun readInput(name: String) = readLines(name).map { it.map { it.digitToInt() } }

private operator fun List<List<Int>>.get(position: Position): Int {
    val (row, col) = position
    return this[row][col]
}

private operator fun List<List<Int>>.contains(position: Position): Boolean {
    val (row, col) = position
    return row in indices && col in first().indices
}
