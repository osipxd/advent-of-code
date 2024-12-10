import lib.matrix.*

private const val DAY = "Day10"

private typealias TopologicalMap = Matrix<Int>

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 36
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 81
        measureAnswer { part2(input()) }
    }
}

private fun part1(input: TopologicalMap): Int = calculateTrailheadScores(input, countAllPaths = false)
private fun part2(input: TopologicalMap): Int = calculateTrailheadScores(input, countAllPaths = true)

private fun calculateTrailheadScores(input: TopologicalMap, countAllPaths: Boolean): Int =
    input.valuePositions { it == LOWEST }.sumOf { trailheadScore(input, it, countAllPaths) }

private fun trailheadScore(map: TopologicalMap, trailhead: Position, countAllPaths: Boolean): Int {
    data class State(val position: Position, val height: Int)

    val seen = mutableSetOf<Position>()
    val queue = ArrayDeque<State>()

    fun addNext(position: Position, height: Int) {
        if (countAllPaths || seen.add(position)) queue += State(position, height)
    }

    addNext(trailhead, height = LOWEST)
    while (queue.isNotEmpty() && queue.first().height < HIGHEST) {
        val (position, height) = queue.removeFirst()
        val nextHeight = height + 1
        Direction.orthogonal.forEach { direction ->
            val nextPosition = position.nextBy(direction)
            if (map.getOrNull(nextPosition) == nextHeight) addNext(nextPosition, nextHeight)
        }
    }

    return queue.size
}

private const val LOWEST = 0
private const val HIGHEST = 9

private fun readInput(name: String) = readMatrix(name) { line -> line.map { it.digitToInt() } }
