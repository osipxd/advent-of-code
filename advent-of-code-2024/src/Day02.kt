import kotlin.math.abs
import kotlin.math.sign

private const val DAY = "Day02"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 2
        measureAnswer(expected = 369) { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 4
        measureAnswer(expected = 428) { part2(input()) }
    }
}

private fun part1(input: List<List<Int>>): Int = input.count { it.isSafe() }

private fun part2(input: List<List<Int>>): Int = input.count { line ->
    val unsafeJumpIndex = line.indexOfUnsafeJump()
    var isSafe = unsafeJumpIndex == NOT_FOUND
    var indexToRemove = (unsafeJumpIndex - 1).coerceAtLeast(0)

    while (!isSafe && indexToRemove <= unsafeJumpIndex + 1) {
        isSafe = line.dropAt(indexToRemove).isSafe()
        indexToRemove++
    }

    isSafe
}

private fun List<Int>.isSafe(): Boolean = indexOfUnsafeJump() == NOT_FOUND

private fun List<Int>.indexOfUnsafeJump(): Int {
    var sign = 0

    for (i in 0 until lastIndex) {
        val diff = get(i) - get(i + 1)
        if (diff == 0 || abs(diff) > 3 || sign != 0 && diff.sign != sign) {
            return i
        }
        sign = diff.sign
    }

    return -1
}

private fun readInput(name: String) = readLines(name) { it.splitInts() }

// Utils

private const val NOT_FOUND = -1

private fun List<Int>.dropAt(index: Int): List<Int> = toMutableList().apply { removeAt(index) }
