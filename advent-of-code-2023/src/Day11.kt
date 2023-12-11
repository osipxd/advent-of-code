import kotlin.math.abs

private typealias SpaceImage = List<String>

private const val DAY = "Day11"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 374
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        solve(testInput(), shiftMultiplier = 100) shouldBe 8410
        measureAnswer { part2(input()) }
    }
}

private fun part1(space: SpaceImage) = solve(space, shiftMultiplier = 2)
private fun part2(space: SpaceImage) = solve(space, shiftMultiplier = 1_000_000)

private fun solve(space: SpaceImage, shiftMultiplier: Int): Long {
    return findGalaxies(space, calculateShift = { it * (shiftMultiplier - 1) })
        .combinations()
        .sumOf { (galaxyA, galaxyB) -> (galaxyA distanceTo galaxyB).toLong() }
}

// Can I make this method more clear?
private fun findGalaxies(space: SpaceImage, calculateShift: (Int) -> Int): List<Position> = buildList {
    val emptyRows = space.indices.filterAllEmpty(space::row)
    val emptyColumns = space.first().indices.filterAllEmpty(space::column)

    var emptyRowsCount = 0
    for (row in space.indices) {
        if (row in emptyRows) {
            emptyRowsCount++
        } else {
            var emptyColumnsCount = 0
            for (col in space[row].indices) {
                if (space[row][col] == '#') {
                    add(row + calculateShift(emptyRowsCount) to col + calculateShift(emptyColumnsCount))
                } else if (col in emptyColumns) {
                    emptyColumnsCount++
                }
            }
        }
    }
}

private fun IntRange.filterAllEmpty(chars: (Int) -> Sequence<Char>): Set<Int> {
    return filterTo(mutableSetOf()) { i -> chars(i).all { it == '.' } }
}

private fun readInput(name: String) = readLines(name)

// region Utils
private fun SpaceImage.row(row: Int): Sequence<Char> = this[row].asSequence()

private fun SpaceImage.column(col: Int): Sequence<Char> = sequence {
    for (row in indices) yield(this@column[row][col])
}

private fun <T> List<T>.combinations(): Sequence<Pair<T, T>> = sequence {
    for (i in 0..<lastIndex) {
        for (j in i..lastIndex) {
            yield(get(i) to get(j))
        }
    }
}

private infix fun Position.distanceTo(other: Position): Int {
    return abs(this.first - other.first) + abs(this.second - other.second)
}
// endregion
