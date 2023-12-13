import kotlin.math.min

private const val DAY = "Day13"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 405
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 400
        measureAnswer { part2(input()) }
    }
}

private fun part1(input: List<List<String>>): Int = input.sumOf { calculateMirrorPosition(it) }
private fun part2(input: List<List<String>>): Int = input.sumOf { calculateMirrorPosition(it, smudges = 1) }

private fun calculateMirrorPosition(pattern: List<String>, smudges: Int = 0): Int {
    val verticalPosition = findMirrorPosition(pattern, smudges)
    val horizontalPosition = if (verticalPosition == 0) findMirrorPosition(pattern.columns(), smudges) else 0

    return horizontalPosition + verticalPosition * 100
}

private fun List<String>.columns(): List<String> = buildList {
    for (i in first().indices) {
        val column = buildString { for (line in this@columns) append(line[i]) }
        add(column)
    }
}

private fun findMirrorPosition(pattern: List<String>, requiredSmudges: Int): Int {
    for (i in 0..<pattern.lastIndex) {
        var foundSmudges = 0
        for (diff in 0..min(i, pattern.size - i - 2)) {
            foundSmudges += pattern[i - diff] countDiffWith pattern[i + 1 + diff]
            if (foundSmudges > requiredSmudges) break
        }
        if (foundSmudges == requiredSmudges) return i + 1
    }
    return 0
}

private infix fun String.countDiffWith(other: String): Int = indices.count { i -> this[i] != other[i] }

private fun readInput(name: String) = readText(name).split("\n\n").map { it.lines() }
