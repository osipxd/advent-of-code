package lib.matrix

import printValue
import readLines

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

    fun rows(): List<List<T>> = values.chunked(columnCount)

    fun row(row: Int): List<T> = values.slice(index(row, 0)..index(row, lastColumnIndex))

    fun column(column: Int): List<T> = buildList {
        for (row in rowIndices) add(this@Matrix[row, column])
    }

    operator fun get(row: Int, column: Int): T = values[index(row, column)]

    operator fun set(row: Int, column: Int, value: T) {
        values[index(row, column)] = value
    }

    fun getOrNull(row: Int, column: Int): T? =
        if (row in rowIndices && column in columnIndices) get(row, column) else null

    private fun index(row: Int, column: Int): Int = row * columnCount + column
}

fun Matrix<*>.positions(): Sequence<Position> =
    sequence { for (row in rowIndices) for (column in columnIndices) yield(Position(row, column)) }

fun <T> Matrix<T>.valuePositions(predicate: (T) -> Boolean): Sequence<Position> =
    positions().filter { predicate(this[it]) }

val Matrix<*>.topLeftPosition: Position get() = Position.Zero
val Matrix<*>.bottomRightPosition: Position get() = Position(lastRowIndex, lastColumnIndex)

fun <T> List<List<T>>.toMatrix(): Matrix<T> = Matrix(this)

@JvmName("toMatrixChar")
fun List<String>.toMatrix(): Matrix<Char> = this.map(String::toList).toMatrix()

/** Prints matrix row by row representing each element as a char. */
fun <T> Matrix<T>.debugPrint(transform: (position: Position, value: T) -> Char) {
    for (row in rowIndices) {
        columnIndices.joinToString(separator = "") { col -> transform(Position(row, col), this[row, col]).toString() }
            .printValue()
    }
}

@JvmName("debugPrintChar")
fun Matrix<Char>.debugPrint(transform: (position: Position, value: Char) -> Char = { _, c -> c }) {
    debugPrint<Char>(transform)
}

fun readMatrix(fileName: String): Matrix<Char> = readMatrix(fileName) { line -> line.map { it } }
fun <T> readMatrix(fileName: String, lineElements: (String) -> List<T>): Matrix<T> =
    readLines(fileName, lineElements).toMatrix()
