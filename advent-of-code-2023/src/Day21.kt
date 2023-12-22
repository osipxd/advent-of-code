import lib.matrix.Matrix
import lib.matrix.Position
import lib.matrix.contains
import lib.matrix.get

private const val DAY = "Day21"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput(), steps = 6) shouldBe 16
        measureAnswer { part1(input(), steps = 64) }
    }

    "Part 2" {
        measureAnswer { part2(input()) }
    }
}

private fun part1(input: Matrix<Char>, steps: Int): Int {
    val start = Position(input.rowCount / 2, input.columnCount / 2)

    var pots = setOf(start)
    repeat(steps) {
        pots = pots.flatMap { it.neighbors() }.filter { it in input && input[it] != '#' }.toSet()
    }

    return pots.size
}

private fun part2(input: Matrix<Char>): Long {
    val start = Position(input.rowCount / 2, input.columnCount / 2)
    val stepsInCycle = input.columnCount

    var evenPlots = 0
    var oddPlots = 0
    val seen = mutableSetOf<Position>()
    val queue = ArrayDeque<Pair<Int, Position>>()

    val steps = stepsInCycle + start.column
    fun addNextStep(step: Int, position: Position) {
        if (position !in seen && input.getInfinite(position) != '#') {
            seen += position
            if (step < steps) queue.addLast(step to position)
            if (step % 2 == 0) evenPlots++ else oddPlots++
        }
    }

    var a1 = 0
    var a2 = 0
    var lastStep = 0

    addNextStep(step = 0, start)
    while (queue.isNotEmpty()) {
        val (step, position) = queue.removeFirst()
        if (lastStep != step) {
            lastStep = step
            if (step == 65) {
                a1 = oddPlots
                a2 = evenPlots
            }
        }
        for (neighbor in position.neighbors()) addNextStep(step + 1, neighbor)
    }

    val b = oddPlots - a1 - a2 * 4 + 1 // Why +1? I don't know, but it works
    val patternsCount = (26501365L - start.column) / input.columnCount
    var count = a1.toLong()
    for (i in 1L..patternsCount) {
        val a = if (i % 2 == 0L) a1 else a2
        count += a * i * 4 + b * i
    }

    return count
}

private fun Matrix<Char>.getInfinite(position: Position): Char {
    val (row, col) = position
    return this[row.mod(rowCount), col.mod(columnCount)]
}

private fun readInput(name: String) = readMatrix(name)
