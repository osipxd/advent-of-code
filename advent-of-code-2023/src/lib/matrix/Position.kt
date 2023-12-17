package lib.matrix

/** Data class representing position in Matrix. */
data class Position(val row: Int, val column: Int) {

    fun offsetBy(row: Int, column: Int): Position = Position(this.row + row, this.column + column)

    companion object {
        val Zero: Position = Position(0, 0)
    }
}

operator fun Matrix<*>.contains(position: Position): Boolean {
    return position.row in rowIndices && position.column in columnIndices
}

operator fun <T> Matrix<T>.get(position: Position): T = this[position.row, position.column]