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

private fun part1(input: List<String>): Int = input.sumOf { calculateMirrorNumber(it) }
private fun part2(input: List<String>): Int = input.sumOf { calculateMirrorNumber(it, smudges = 1) }

private fun calculateMirrorNumber(pattern: String, smudges: Int = 0): Int {
    val verticalMirror = findMirror(pattern.lines(), smudges)
    val horizontalMirror = if (verticalMirror == 0) findMirror(pattern.columns(), smudges) else 0

    return horizontalMirror + verticalMirror * 100
}

private fun String.columns(): List<String> = buildList {
    val lines = lines()
    for (i in lines.first().indices) {
        val column = buildString { for (line in lines) append(line[i]) }
        add(column)
    }
}

private fun findMirror(lines: List<String>, smudges: Int): Int {
    for (i in 0..<lines.lastIndex) {
        var lineSmudges = 0
        for (diff in 0..min(i, lines.size - i - 2)) {
            lineSmudges += lines[i - diff] countDiff lines[i + 1 + diff]
            if (lineSmudges > smudges) break
        }
        if (lineSmudges == smudges) return i + 1
    }
    return 0
}

private infix fun String.countDiff(other: String): Int = indices.count { i -> this[i] != other[i] }

private fun readInput(name: String) = readText(name).split("\n\n")
