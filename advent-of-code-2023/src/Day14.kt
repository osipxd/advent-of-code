private typealias Platform = List<CharArray>

private const val DAY = "Day14"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 136
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 64
        measureAnswer { part2(input()) }
    }
}

private fun part1(input: Platform): Int {
    input.tilt(TiltDirection.NORTH)
    return input.calculateNorthWeight()
}

private fun part2(input: Platform): Int {
    val memory = mutableListOf<List<String>>()
    val cyclesCount = 1_000_000_000
    for (cycle in 0..<cyclesCount) {
        input.rotate()

        val snapshot = input.map(::String)
        val seenBeforeAt = memory.indexOf(snapshot)

        if (seenBeforeAt != -1) {
            val cycleSize = cycle - seenBeforeAt
            return memory[seenBeforeAt - 1 + (cyclesCount - seenBeforeAt) % cycleSize].map { it.toCharArray() }
                .calculateNorthWeight()
        }

        memory += snapshot
    }

    error("There is no cycle?")
}

private fun Platform.rotate() {
    TiltDirection.entries.forEach(::tilt)
}

private fun Platform.tilt(direction: TiltDirection) {
    for (mainAxis in indices) {
        var freePosition = 0
        for (movingAxis in indices) {
            val position = direction.derivePosition(mainAxis, movingAxis, lastIndex)
            when (this[position]) {
                'O' -> {
                    val positionToSet = direction.derivePosition(mainAxis, freePosition, lastIndex)
                    this[position] = '.'
                    this[positionToSet] = 'O'
                    freePosition++
                }

                '#' -> freePosition = movingAxis + 1
            }
        }
    }
}

private fun Platform.calculateNorthWeight(): Int {
    val maxWeight = size
    var weight = 0
    for (col in indices) {
        for (row in indices) {
            if (this[row][col] == 'O') weight += maxWeight - row
        }
    }

    return weight
}

private fun readInput(name: String) = readLines(name).map { it.toCharArray() }
    .also { input -> check(input.size == input.first().size) }

private enum class TiltDirection(val horizontal: Boolean, val forward: Boolean) {
    NORTH(horizontal = false, forward = true),
    WEST(horizontal = true, forward = true),
    SOUTH(horizontal = false, forward = false),
    EAST(horizontal = true, forward = false);

    val vertical: Boolean = !horizontal
    val backward: Boolean = !forward

    fun derivePosition(mainAxis: Int, movingAxis: Int, maximum: Int): Pair<Int, Int> {
        val row = when {
            vertical && forward -> movingAxis
            vertical && backward -> maximum - movingAxis
            else -> mainAxis
        }
        val col = when {
            horizontal && forward -> movingAxis
            horizontal && backward -> maximum - movingAxis
            else -> mainAxis
        }

        return row to col
    }
}

private operator fun Platform.get(position: Pair<Int, Int>): Char {
    val (row, col) = position
    return this[row][col]
}

private operator fun Platform.set(position: Pair<Int, Int>, value: Char) {
    val (row, col) = position
    this[row][col] = value
}
