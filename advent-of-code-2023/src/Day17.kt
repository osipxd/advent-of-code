import BeamDirection.DOWN
import BeamDirection.RIGHT
import kotlin.math.min

private const val DAY = "Day17"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 102
        measureAnswer { part1(input()) }
    }

    //"Part 2" {
    //    part2(testInput()) shouldBe 0
    //    measureAnswer { part2(input()) }
    //}
}

private fun part1(input: List<List<Int>>): Int {
    val slots = ArrayDeque<State>()
    val bestSeen = mutableMapOf<Pair<Position, Boolean>, Int>()

    val lookForPosition = input.lastIndex to input.first().lastIndex
    var resultLoss = Int.MAX_VALUE

    fun State.key() = position to lastDirection.horizontal

    fun addState(state: State) {
        val key = state.key()
        if (state.position == lookForPosition) {
            resultLoss = min(resultLoss, state.loss)
            println(resultLoss)
        } else if (key !in bestSeen || bestSeen.getValue(key) > state.loss) {
            bestSeen[key] = state.loss
            slots.addLast(state)
        }
    }

    tailrec fun State.goInDirection(newDirection: BeamDirection = lastDirection, steps: Int) {
        val newPosition = newDirection.nextPosition(position)
        if (newPosition in input) {
            val newState = copy(
                position = newPosition,
                loss = loss + input[newPosition],
                lastDirection = newDirection,
            )
            addState(newState)
            if (steps > 1) newState.goInDirection(steps = steps - 1)
        }
    }

    val initialState = State(position = 0 to 0, loss = 0, lastDirection = RIGHT)
    initialState.goInDirection(RIGHT, 3)
    initialState.goInDirection(DOWN, 3)

    while (slots.isNotEmpty()) {
        val state = slots.removeFirst()
        if (bestSeen.getValue(state.key()) < state.loss) continue
        state.goInDirection(state.lastDirection.turn(clockwise = true), 3)
        state.goInDirection(state.lastDirection.turn(clockwise = false), 3)
    }

    return resultLoss
}

private data class State(
    val position: Position,
    val lastDirection: BeamDirection,
    val loss: Int,
)

private fun part2(input: List<String>): Int = TODO()

private fun readInput(name: String) = readLines(name).map { it.map { it.digitToInt() } }

private operator fun List<List<Int>>.get(position: Position): Int {
    val (row, col) = position
    return this[row][col]
}

private operator fun List<List<Int>>.contains(position: Position): Boolean {
    val (row, col) = position
    return row in indices && col in first().indices
}
