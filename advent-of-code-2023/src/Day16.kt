private const val DAY = "Day16"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 46
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 51
        measureAnswer { part2(input()) }
    }
}

private fun part1(field: List<String>): Int = energizeField(field, startBeam = Beam(DIRECTION_RIGHT, Position(0, 0)))
private fun part2(field: List<String>): Int = field.startBeams().maxOf { startBeam -> energizeField(field, startBeam) }

private fun List<String>.startBeams(): Sequence<Beam> = sequence {
    for (row in indices) {
        yield(Beam(DIRECTION_RIGHT, Position(row, 0)))
        yield(Beam(DIRECTION_LEFT, Position(row, first().lastIndex)))
    }

    for (col in first().indices) {
        yield(Beam(DIRECTION_DOWN, Position(0, col)))
        yield(Beam(DIRECTION_DOWN, Position(lastIndex, col)))
    }
}

private fun energizeField(field: List<String>, startBeam: Beam): Int {
    val beams = ArrayDeque<Beam>()
    val seenBeams = mutableSetOf<Beam>()

    fun addBeam(beam: Beam) {
        if (beam.position in field && beam !in seenBeams) {
            seenBeams += beam
            beams.addLast(beam)
        }
    }

    addBeam(startBeam)
    while (beams.isNotEmpty()) {
        val beam = beams.removeFirst()
        val (nextBeam, forkedBeam) = beam.move(field[beam.position])
        addBeam(nextBeam)
        forkedBeam?.let(::addBeam)
    }

    return seenBeams.distinctBy { it.position }.size
}

private data class Beam(
    val direction: Int,
    val position: Position,
) {

    fun move(currentTile: Char): Pair<Beam, Beam?> {
        val (direction, forkedBeam) = processTile(currentTile)
        var (row, col) = position
        when (direction) {
            DIRECTION_UP -> row--
            DIRECTION_RIGHT -> col++
            DIRECTION_DOWN -> row++
            DIRECTION_LEFT -> col--
        }

        return Beam(direction, row to col) to forkedBeam
    }

    private fun processTile(tile: Char): Pair<Int, Beam?> {
        var forkedBeam: Beam? = null
        var direction = this.direction
        when (tile) {
            in "\\/" -> direction = turn(direction, tile)

            '|' -> if (direction.horizontal) {
                direction = DIRECTION_UP
                forkedBeam = copy(direction = DIRECTION_DOWN)
            }

            '-' -> if (direction.vertical) {
                direction = DIRECTION_LEFT
                forkedBeam = copy(direction = DIRECTION_RIGHT)
            }
        }

        return direction to forkedBeam
    }
}

private fun turn(direction: Int, mirror: Char): Int {
    val turnClockwise = when (direction) {
        DIRECTION_UP, DIRECTION_DOWN -> mirror == '/'
        DIRECTION_RIGHT, DIRECTION_LEFT -> mirror == '\\'
        else -> error("Unexpected direction: $direction")
    }

    return (if (turnClockwise) direction + 1 else direction - 1).mod(4)
}

private fun readInput(name: String) = readLines(name)

private const val DIRECTION_UP = 0
private const val DIRECTION_RIGHT = 1
private const val DIRECTION_DOWN = 2
private const val DIRECTION_LEFT = 3

private val Int.vertical get() = this % 2 == 0
private val Int.horizontal get() = this % 2 == 1

private operator fun List<String>.get(position: Position): Char {
    val (row, col) = position
    return this[row][col]
}

private operator fun List<String>.contains(position: Position): Boolean {
    val (row, col) = position
    return row in indices && col in first().indices
}
