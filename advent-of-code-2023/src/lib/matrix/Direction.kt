package lib.matrix

/** Represents direction of movement in Matrix. */
enum class Direction(private val row: Int, private val col: Int) {
    // Do not change order of entries unless you want to break turning logic.
    UP(row = -1, col = 0),
    RIGHT(row = 0, col = +1),
    DOWN(row = +1, col = 0),
    LEFT(row = 0, col = -1);

    val vertical get() = col == 0
    val horizontal get() = row == 0

    fun turn(clockwise: Boolean): Direction {
        val newDirectionOrdinal = (if (clockwise) ordinal + 1 else ordinal - 1).mod(entries.size)
        return entries[newDirectionOrdinal]
    }

    companion object {
        fun Position.nextInDirection(direction: Direction) = offsetBy(direction.row, direction.col)
    }
}
