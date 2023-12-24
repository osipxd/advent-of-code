import lib.combinations
import lib.matrix.Matrix
import lib.matrix.Position
import kotlin.math.abs

private typealias SpaceImage = Matrix<Char>

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
    val emptyRows = space.rowIndices.filterAllEmpty(space::row)
    val emptyColumns = space.columnIndices.filterAllEmpty(space::column)

    var emptyRowsCount = 0
    for (row in space.rowIndices) {
        if (row in emptyRows) {
            emptyRowsCount++
        } else {
            var emptyColumnsCount = 0
            for (col in space.columnIndices) {
                if (space[row, col] == '#') {
                    val position = Position(
                        row + calculateShift(emptyRowsCount),
                        col + calculateShift(emptyColumnsCount),
                    )
                    add(position)
                } else if (col in emptyColumns) {
                    emptyColumnsCount++
                }
            }
        }
    }
}

private fun IntRange.filterAllEmpty(chars: (Int) -> List<Char>): Set<Int> {
    return filterTo(mutableSetOf()) { i -> chars(i).all { it == '.' } }
}

private fun readInput(name: String) = readMatrix(name)

// region Utils
private infix fun Position.distanceTo(other: Position): Int {
    return abs(this.row - other.row) + abs(this.column - other.column)
}
// endregion
