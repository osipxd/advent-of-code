import lib.matrix.Direction
import lib.matrix.Direction.*
import lib.matrix.Direction.Companion.moveInDirection
import lib.matrix.Direction.Companion.nextInDirection
import lib.matrix.Position

private const val DAY = "Day18"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 62
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 952408144115
        measureAnswer { part2(input()) }
    }
}


private fun part1(input: List<Command>): Int {
    var position = Position.Zero
    val seenPositions = mutableSetOf(position)
    for (command in input) {
        repeat(command.distance) {
            position = position.nextInDirection(command.direction)
            seenPositions += position
        }
    }

    val queue = ArrayDeque<Position>()
    queue += Position(1, 1)

    while (queue.isNotEmpty()) {
        val pos = queue.removeFirst()
        for (n in pos.neighbors()) {
            if (n !in seenPositions) queue.addLast(n)
            seenPositions += n
        }
    }

    return seenPositions.size
}

fun Position.neighbors(): List<Position> = Direction.entries.map { nextInDirection(it) }

private fun part2(input: List<Command>): Int = TODO()

private fun readInput(name: String) = readLines(name).map { line ->
    val (dir, dist, color) = line.split(" ")
    val direction = when (dir) {
        "U" -> UP
        "D" -> DOWN
        "L" -> LEFT
        else -> RIGHT
    }
    Command(direction, dist.toInt(), color.trim('(', ')'))
}

private data class Command(val direction: Direction, val distance: Int, val color: String)
