import lib.matrix.MatrixVector
import lib.matrix.Position
import lib.matrix.plus

private const val DAY = "Day14"

private typealias RobotSpec = Pair<Position, MatrixVector>
private typealias BathroomSize = Pair<Int, Int>

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput(), size = 7 x 11) shouldBe 12
        measureAnswer { part1(input(), size = 103 x 101) }
    }

    //"Part 2" {
    //    part2(testInput()) shouldBe 0
    //    measureAnswer { part2(input()) }
    //}
}

private const val SIMULATION_TIME = 100

private fun part1(robots: List<RobotSpec>, size: BathroomSize): Long {
    val quadrants = LongArray(4) { 0 }
    for ((position, vector) in robots) {
        val newPosition = position + vector * SIMULATION_TIME
        val quadrant = resolveQuadrant(newPosition, size)
        if (quadrant >= 0) quadrants[quadrant]++
    }

    return quadrants.reduce(Long::times)
}

private fun resolveQuadrant(position: Position, size: BathroomSize): Int {
    val (rows, cols) = size
    val (middleRow, middleCol) = size.map { it / 2 }
    val row = position.row.mod(rows)
    val col = position.column.mod(cols)

    if (row == middleRow || col == middleCol) return -1
    return (if (row > middleRow) 2 else 0) + (if (col > middleCol) 1 else 0)
}

private fun part2(input: List<RobotSpec>): Int = TODO()

private fun readInput(name: String) = readLines(name) { line ->
    val (p, v) = line.split(" ")
    val (col, row) = p.extractNumbers()
    val (dc, dr) = v.extractNumbers()
    RobotSpec(Position(row, col), MatrixVector(dr, dc))
}

// Utils

private fun String.extractNumbers() = substringAfter("=").splitInts()

private infix fun Int.x(other: Int): BathroomSize = this to other

private operator fun MatrixVector.times(times: Int) = MatrixVector(row * times, col * times)
