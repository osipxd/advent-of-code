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

    //"Part 2" {
    //    part2(testInput()) shouldBe 0
    //    measureAnswer { part2(input()) }
    //}
}

private fun part1(space: SpaceImage): Int {
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
                        add(row + rowShift to col + colShift)
                    }
                }
            }
        }
    }

    var sum = 0
    for (i in 0..<galaxies.lastIndex) {
        val (rowI, colI) = galaxies[i]
        for (j in i..galaxies.lastIndex) {
            val (rowJ, colJ) = galaxies[j]
            sum += abs(rowI - rowJ) + abs(colI - colJ)
        }
    }

    return sum
}

private fun part2(input: List<String>): Int = TODO()

private fun readInput(name: String) = readLines(name)

private fun SpaceImage.column(col: Int): Sequence<Char> = sequence { 
    for (row in indices) yield(this@column[row][col])
}
