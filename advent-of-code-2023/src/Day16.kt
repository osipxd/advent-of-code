import lib.countDistinctBy
import lib.matrix.*
import lib.matrix.Direction.*
import lib.matrix.Direction.Companion.nextInDirection
import lib.matrix.Position

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

private fun part1(field: Matrix<Char>): Int = energizeField(field, startBeam = Beam(RIGHT, field.topLeftPosition))
private fun part2(field: Matrix<Char>): Int = field.startBeams().maxOf { startBeam -> energizeField(field, startBeam) }

private fun Matrix<Char>.startBeams(): Sequence<Beam> = sequence {
    for (row in rowIndices) {
        yield(Beam(RIGHT, Position(row, 0)))
        yield(Beam(LEFT, Position(row, lastColumnIndex)))
    }

    for (col in columnIndices) {
        yield(Beam(DOWN, Position(0, col)))
        yield(Beam(UP, Position(lastRowIndex, col)))
    }
}

private fun energizeField(field: Matrix<Char>, startBeam: Beam): Int {
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

    return seenBeams.countDistinctBy { it.position }
}

private data class Beam(
    val direction: Direction,
    val position: Position,
) {

    /** Returns the beam with updated position and direction, and forked beam if it was split. */
    fun move(currentTile: Char): Pair<Beam, Beam?> {
        val (direction, forkedBeam) = processTile(currentTile)
        val position = position.nextInDirection(direction)
        return Beam(direction, position) to forkedBeam
    }

    private fun processTile(tile: Char): Pair<Direction, Beam?> {
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

private fun readInput(name: String) = readMatrix(name)

private fun Direction.turn(mirror: Char) = turn(clockwise = if (vertical) mirror == '/' else mirror == '\\')
