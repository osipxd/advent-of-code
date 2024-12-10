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

    //"Part 2" {
    //    part2(testInput()) shouldBe 0
    //    measureAnswer { part2(input()) }
    //}
}

private fun part1(input: TopologicalMap): Int =
    input.positions()
        .filter { input[it] == 0 }
        .sumOf { countTrailheadScore(input, it) }

private fun countTrailheadScore(input: Matrix<Int>, trailhead: Position): Int {
    val seen = mutableSetOf<Position>()
    val queue = ArrayDeque<Pair<Position, Int>>()

    fun addNext(position: Position, step: Int) {
        if (seen.add(position)) queue += position to step
    }

    addNext(trailhead, step = 0)
    while (seen.isNotEmpty()) {
        val (position, step) = queue.removeFirst()
        if (step == 9) return queue.size + 1
        val nextStep = step + 1

        Direction.orthogonal.forEach { direction ->
            val nextPosition = position.nextBy(direction)
            if (nextPosition in input && input[nextPosition] == nextStep) addNext(nextPosition, nextStep)
        }
    }

    return 0
}

private fun part2(input: TopologicalMap): Int = TODO()

private fun readInput(name: String) = readMatrix(name) { line -> line.map { it.digitToInt() } }
