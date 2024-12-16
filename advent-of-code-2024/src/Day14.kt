import lib.matrix.*

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

    "Part 2" {
        measureAnswer { part2(input()) }
    }
}

private const val SIMULATION_TIME = 100

private fun part1(robots: List<RobotSpec>, size: BathroomSize): Int {
    val quadrants = IntArray(4) { 0 }
    val (middleRow, middleCol) = size.map { it / 2 }

    takeSnapshot(robots, size, SIMULATION_TIME).forEach { (row, col) ->
        if (row != middleRow && col != middleCol) {
            val quadrant = (if (row > middleRow) 2 else 0) + (if (col > middleCol) 1 else 0)
            quadrants[quadrant]++
        }
    }

    return quadrants.reduce(Int::times)
}

private fun part2(robots: List<RobotSpec>): Int {
    val size = 103 x 101
    var simulationTime = 0
    var snapshot: Map<Position, Int> = emptyMap()
    while (!isChristmasTree(snapshot)) {
        snapshot = takeSnapshot(robots, size, ++simulationTime).groupingBy { it }.eachCount()
    }

    Bounds(0..103, 0..101).debugPrint { position ->
        snapshot[position]?.digitToChar() ?: '.'
    }

    return simulationTime
}

// Assume the snapshot contains a Christmas tree if there are no intersecting robots
fun isChristmasTree(snapshot: Map<Position, Int>): Boolean =
    snapshot.isNotEmpty() && snapshot.all { (_, count) -> count == 1 }

private fun takeSnapshot(robots: List<RobotSpec>, size: BathroomSize, simulationTime: Int): List<Position> {
    val (rows, cols) = size

    return robots.map { (position, vector) ->
        val (row, col) = position + vector * simulationTime
        Position(row.mod(rows), col.mod(cols))
    }
}

private fun readInput(name: String) = readLines(name) { line ->
    val (p, v) = line.split(" ")
    val (col, row) = p.extractNumbers()
    val (dc, dr) = v.extractNumbers()
    RobotSpec(Position(row, col), MatrixVector(dr, dc))
}

// Utils

private fun String.extractNumbers() = substringAfter("=").splitInts()

private infix fun Int.x(other: Int): BathroomSize = this to other
