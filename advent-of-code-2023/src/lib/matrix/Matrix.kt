package lib.matrix

/**
 * 2-dimensional matrix.
 * Assumes that the given [lines] are all equal by length.
 */
class Matrix<T>(lines: List<List<T>>) {

    val rowCount: Int = lines.size
    val lastRowIndex: Int = rowCount - 1
    val rowIndices: IntRange = 0..lastRowIndex

    val columnCount: Int = lines.first().size
    val lastColumnIndex: Int = columnCount - 1
    val columnIndices: IntRange = 0..lastColumnIndex

    private val values: MutableList<T> = MutableList(rowCount * columnCount) { i ->
        lines[i / columnCount][i % columnCount]
    }
    operator fun get(row: Int, column: Int): T = values[index(row, column)]

    private fun index(row: Int, column: Int): Int = row * columnCount + column
}

val Matrix<*>.topLeftPosition: Position get() = Position.Zero
val Matrix<*>.bottomRightPosition: Position get() = Position(lastRowIndex, lastColumnIndex)

fun <T> List<List<T>>.toMatrix(): Matrix<T> = Matrix(this)
