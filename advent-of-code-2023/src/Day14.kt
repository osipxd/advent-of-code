private typealias Platform = List<CharArray>
private typealias PlatformSnapshot = List<String>

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
    val rotationsCount = 1_000_000_000

    repeat(rotationsCount) { i ->
        input.rotate()

        val snapshot = input.snapshot()
        val cycleStart = memory.indexOf(snapshot)
        if (cycleStart != -1) {
            val cycleSize = i - cycleStart
            val positionInCycle = (rotationsCount - cycleStart - 1) % cycleSize
            return memory[cycleStart + positionInCycle]
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

// Think about more lightweight snapshot here.
private fun Platform.snapshot(): PlatformSnapshot = map(::String)

private fun Platform.calculateNorthWeight(): Int = snapshot().calculateNorthWeight()

@JvmName("calculateNorthWeightSnapshot")
private fun PlatformSnapshot.calculateNorthWeight(): Int {
    var weight = 0
    for (col in indices) {
        for (row in indices) {
            if (this[row][col] == 'O') weight += size - row
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

    val vertical: Boolean get() = !horizontal
    val backward: Boolean get() = !forward

    fun derivePosition(mainAxis: Int, movingAxis: Int, maximum: Int): Pair<Int, Int> {
        fun deriveAxis(orientation: Boolean) = when {
            orientation && forward -> movingAxis
            orientation && backward -> maximum - movingAxis
            else -> mainAxis
        }

        return deriveAxis(vertical) to deriveAxis(horizontal)
    }
}

// region Utils
private operator fun Platform.get(position: Pair<Int, Int>): Char {
    val (row, col) = position
    return this[row][col]
}

private operator fun Platform.set(position: Pair<Int, Int>, value: Char) {
    val (row, col) = position
    this[row][col] = value
}
// endregion
