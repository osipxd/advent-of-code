import BeamDirection.*

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

private fun part1(field: List<String>): Int = energizeField(field, startBeam = Beam(RIGHT, Position(0, 0)))
private fun part2(field: List<String>): Int = field.startBeams().maxOf { startBeam -> energizeField(field, startBeam) }

private fun List<String>.startBeams(): Sequence<Beam> = sequence {
    for (row in indices) {
        yield(Beam(RIGHT, Position(row, 0)))
        yield(Beam(LEFT, Position(row, first().lastIndex)))
    }

    for (col in first().indices) {
        yield(Beam(DOWN, Position(0, col)))
        yield(Beam(UP, Position(lastIndex, col)))
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
    val direction: BeamDirection,
    val position: Position,
) {

    /** Returns the beam with updated position and direction, and forked beam if it was split. */
    fun move(currentTile: Char): Pair<Beam, Beam?> {
        val (direction, forkedBeam) = processTile(currentTile)
        val position = direction.nextPosition(position)
        return Beam(direction, position) to forkedBeam
    }

    private fun processTile(tile: Char): Pair<BeamDirection, Beam?> {
        var forkedBeam: Beam? = null
        var direction = this.direction
        when (tile) {
            in "\\/" -> direction = direction.turn(tile)

            '|' -> if (direction.horizontal) {
                direction = UP
                forkedBeam = copy(direction = DOWN)
            }

            '-' -> if (direction.vertical) {
                direction = LEFT
                forkedBeam = copy(direction = RIGHT)
            }
        }

        return direction to forkedBeam
    }
}

private fun readInput(name: String) = readLines(name)

private enum class BeamDirection(private val row: Int, private val col: Int) {
    // Do not change order of entries unless you want to break turning logic.
    UP(row = -1, col = 0),
    RIGHT(row = 0, col = +1),
    DOWN(row = +1, col = 0),
    LEFT(row = 0, col = -1);

    val vertical get() = col == 0
    val horizontal get() = row == 0

    fun nextPosition(position: Position): Position = position.first + row to position.second + col

    fun turn(mirror: Char): BeamDirection {
        return turn(clockwise = if (vertical) mirror == '/' else mirror == '\\')
    }

    fun turn(clockwise: Boolean): BeamDirection {
        val newDirectionOrdinal = (if (clockwise) ordinal + 1 else ordinal - 1).mod(entries.size)
        return entries[newDirectionOrdinal]
    }
}

// region Utils
private operator fun List<String>.get(position: Position): Char {
    val (row, col) = position
    return this[row][col]
}

private operator fun List<String>.contains(position: Position): Boolean {
    val (row, col) = position
    return row in indices && col in first().indices
}
// endregion
