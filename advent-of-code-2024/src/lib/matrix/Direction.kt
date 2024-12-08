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

    fun turn90(clockwise: Boolean = true): Direction {
        val newDirectionOrdinal = (if (clockwise) ordinal + 2 else ordinal - 2).mod(entries.size)
        return entries[newDirectionOrdinal]
    }
}

/** An alias for movement in the specified [direction] as operator 'plus' might not be convenient to use. */
fun Position.nextBy(direction: Direction) = offsetBy(direction.row, direction.col)
