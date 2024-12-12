package lib.matrix

/** Represents directions of movement in Matrix. */
enum class Direction(override val row: Int, override val col: Int) : MatrixVector {
    UP(row = -1, col = 0),
    UP_RIGHT(row = -1, col = +1),
    RIGHT(row = 0, col = +1),
    DOWN_RIGHT(row = +1, col = +1),
    DOWN(row = +1, col = 0),
    DOWN_LEFT(row = +1, col = -1),
    LEFT(row = 0, col = -1),
    UP_LEFT(row = -1, col = -1);

    fun turn45(clockwise: Boolean = true): Direction = turn(steps = 1, clockwise)
    fun turn90(clockwise: Boolean = true): Direction = turn(steps = 2, clockwise)

    private fun turn(steps: Int, clockwise: Boolean): Direction {
        val newDirectionOrdinal = (if (clockwise) ordinal + steps else ordinal - steps).mod(entries.size)
        return entries[newDirectionOrdinal]
    }

    companion object {
        val orthogonal: List<Direction> = listOf(UP, DOWN, LEFT, RIGHT)
        val diagonal: List<Direction> = listOf(UP_RIGHT, DOWN_RIGHT, DOWN_LEFT, UP_LEFT)
    }
}

/** An alias for movement in the specified [direction] as operator 'plus' might not be convenient to use. */
fun Position.nextBy(direction: Direction) = offsetBy(direction.row, direction.col)
