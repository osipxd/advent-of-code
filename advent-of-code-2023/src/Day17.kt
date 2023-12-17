import lib.matrix.*
import lib.matrix.Direction.Companion.nextInDirection
import lib.matrix.Direction.DOWN
import lib.matrix.Direction.RIGHT
import lib.matrix.Position
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


private fun part1(input: Matrix<Int>): Int = solve(input, steps = 1..3)
private fun part2(input: Matrix<Int>): Int = solve(input, steps = 4..10)

private fun solve(map: Matrix<Int>, steps: IntRange): Int {
    val states = ArrayDeque<State>()
    val bestSeenLoss = mutableMapOf<State.Key, Int>()

    fun addState(state: State) {
        val key = state.key()
        if (key !in bestSeenLoss || bestSeenLoss.getValue(key) > state.loss) {
            bestSeenLoss[key] = state.loss
            states.addLast(state)
        }
    }

    tailrec fun State.goInDirection(newDirection: Direction = lastDirection, steps: IntRange) {
        val newPosition = position.nextInDirection(newDirection)
        if (newPosition !in map) return

        val newState = copy(
            position = newPosition,
            loss = loss + map[newPosition],
            lastDirection = newDirection,
        )

        if (steps.first == 1) addState(newState)
        val nextSteps = (steps.first - 1).coerceAtLeast(1)..<steps.last
        if (!nextSteps.isEmpty()) newState.goInDirection(steps = nextSteps)
    }

    // Prefill queue with initial moves
    val initialState = State(position = map.topLeftPosition, loss = 0, lastDirection = RIGHT)
    initialState.goInDirection(RIGHT, steps)
    initialState.goInDirection(DOWN, steps)

    while (states.isNotEmpty()) {
        val state = states.removeFirst()
        if (bestSeenLoss.getValue(state.key()) < state.loss) continue
        state.goInDirection(state.lastDirection.turn(clockwise = true), steps)
        state.goInDirection(state.lastDirection.turn(clockwise = false), steps)
    }

    fun finalValue(horizontalDirection: Boolean): Int {
        val key = State.Key(map.bottomRightPosition, horizontalDirection)
        return bestSeenLoss[key] ?: Int.MAX_VALUE
    }

    return min(finalValue(horizontalDirection = true), finalValue(horizontalDirection = false))
}

private data class State(
    val position: Position,
    val lastDirection: Direction,
    val loss: Int,
) {

    fun key() = Key(position, lastDirection.horizontal)

    data class Key(val position: Position, val horizontalDirection: Boolean)
}

private fun readInput(name: String) = readMatrix(name) { line -> line.map(Char::digitToInt) }
