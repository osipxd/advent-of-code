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
        solve(testInput(), distanceMultiplier = 100) shouldBe 8410
        measureAnswer { part2(input()) }
    }
}

private fun part1(space: SpaceImage) = solve(space, distanceMultiplier = 2)
private fun part2(space: SpaceImage) = solve(space, distanceMultiplier = 1_000_000)

private fun solve(space: SpaceImage, distanceMultiplier: Int): Long {
    val emptyRows = space.indices.filter { row -> space[row].all { it == '.' } }.toSet()
    val emptyColumns = space.first().indices.filter { col -> space.column(col).all { it == '.' } }.toSet()

    val galaxies = buildList {
        var rowShift = 0
        for (row in space.indices) {
            if (row in emptyRows) {
                rowShift++
            } else {
                var colShift = 0
                for (col in space[row].indices) {
                    if (col in emptyColumns) {
                        colShift++
                    } else if (space[row][col] == '#') {
                        add(row + rowShift * (distanceMultiplier - 1) to col + colShift * (distanceMultiplier - 1))
                    }
                }
            }
        }
    }

    var sum = 0L
    for (i in 0..<galaxies.lastIndex) {
        val (rowI, colI) = galaxies[i]
        for (j in i..galaxies.lastIndex) {
            val (rowJ, colJ) = galaxies[j]
            sum += abs(rowI - rowJ) + abs(colI - colJ)
        }
    }

    return sum
}

private fun readInput(name: String) = readLines(name)

private fun SpaceImage.column(col: Int): Sequence<Char> = sequence { 
    for (row in indices) yield(this@column[row][col])
}
