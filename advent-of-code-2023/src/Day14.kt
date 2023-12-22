import lib.matrix.*

private typealias Platform = Matrix<Char>
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

    error("Where is the cycle?")
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
private fun Platform.snapshot(): PlatformSnapshot = rows().map { it.joinToString("") }

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

private fun readInput(name: String) = readMatrix(name)
    .also { input -> check(input.rowCount == input.columnCount) }

private enum class TiltDirection(val horizontal: Boolean, val forward: Boolean) {
    NORTH(horizontal = false, forward = true),
    WEST(horizontal = true, forward = true),
    SOUTH(horizontal = false, forward = false),
    EAST(horizontal = true, forward = false);

    val vertical: Boolean get() = !horizontal
    val backward: Boolean get() = !forward

    fun derivePosition(mainAxis: Int, movingAxis: Int, maximum: Int): Position {
        fun deriveAxis(orientation: Boolean) = when {
            orientation && forward -> movingAxis
            orientation && backward -> maximum - movingAxis
            else -> mainAxis
        }

        return Position(deriveAxis(vertical), deriveAxis(horizontal))
    }
}

// Matrix is always square so slightly simplify API
private val Platform.indices get() = rowIndices
private val Platform.lastIndex get() = lastRowIndex
