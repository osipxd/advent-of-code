import lib.binarySearch
import lib.matrix.*

private const val DAY = "Day18"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput().take(12), gridSize = 7) shouldBe 22
        measureAnswer { part1(input().take(1024), gridSize = 71) }
    }

    "Part 2" {
        part2(testInput(), gridSize = 7) shouldBe "6,1"
        measureAnswer { part2(input(), gridSize = 71) }
    }
}

private fun part1(bytes: List<Position>, gridSize: Int): Int = findPath(bytes, Bounds(gridSize))

private fun part2(bytes: List<Position>, gridSize: Int): String {
    val bounds = Bounds(gridSize)
    val badByteIndex = bytes.indices.binarySearch { index ->
        val pathLength = findPath(bytes.take(index + 1), bounds)
        if (pathLength == -1) 1 else -1
    }.insertionIndex

    val badByte = bytes[badByteIndex]
    return "${badByte.column},${badByte.row}"
}

private fun findPath(bytes: List<Position>, bounds: Bounds): Int {
    val byteSet = bytes.toSet()
    val start = bounds.topLeftPosition
    val end = bounds.bottomRightPosition

    val seen = mutableSetOf<Position>()
    val queue = ArrayDeque<Pair<Position, Int>>()
    fun tryAddNext(position: Position, distance: Int) {
        if (position in bounds &&
            position !in byteSet &&
            seen.add(position)
        ) queue.addLast(position to distance)
    }

    tryAddNext(start, distance = 0)
    while (queue.isNotEmpty()) {
        val (position, distance) = queue.removeFirst()
        if (position == end) return distance
        Direction.orthogonal.forEach { tryAddNext(position.nextBy(it), distance + 1) }
    }

    return -1
}

private fun readInput(name: String) = readLines(name) {
    val (x, y) = it.splitInts().takePair()
    Position(row = y, column = x)
}
