import kotlin.math.abs
import kotlin.math.sign

private const val DAY = "Day02"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 2
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 4
        measureAnswer { part2(input()) }
    }
}

private fun part1(input: List<List<Int>>): Int = input.count { it.isSafe() }

private fun part2(input: List<List<Int>>): Int = input.count { line ->
    var isSafe = line.isSafe()
    var indexToRemove = 0

    while (!isSafe && indexToRemove <= line.lastIndex) {
        val alteredLine = line.toMutableList()
        alteredLine.removeAt(indexToRemove)
        isSafe = alteredLine.isSafe()
        indexToRemove++
    }

    isSafe
}

private fun List<Int>.isSafe(): Boolean {
    var prev = -1
    var sign = 0

    for (curr in this) {
        if (prev == -1) {
            prev = curr
            continue
        }

        val diff = curr - prev
        if (abs(diff) > 3 || diff == 0 || sign != 0 && diff.sign != sign) {
            return false
        }
        prev = curr
        sign = diff.sign
    }

    return true
}

private fun readInput(name: String) = readLines(name) { it.splitInts() }