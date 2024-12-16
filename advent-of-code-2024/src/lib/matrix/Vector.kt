package lib.matrix

interface MatrixVector {
    val row: Int
    val col: Int

    companion object {
        fun between(pos1: Position, pos2: Position): MatrixVector = MatrixVector(
            row = pos2.row - pos1.row,
            col = pos2.column - pos1.column,
        )
    }
}

fun MatrixVector(row: Int, col: Int): MatrixVector = MatrixVectorImpl(row, col)

private data class MatrixVectorImpl(override val row: Int, override val col: Int) : MatrixVector

operator fun MatrixVector.unaryMinus(): MatrixVector = MatrixVector(-row, -col)

operator fun MatrixVector.times(times: Int) = MatrixVector(row * times, col * times)

operator fun Position.plus(vector: MatrixVector): Position = offsetBy(vector.row, vector.col)
operator fun Position.minus(vector: MatrixVector): Position = offsetBy(-vector.row, -vector.col)

fun Position.walk(vector: MatrixVector): Sequence<Position> = sequence {
    var position = this@walk
    while (true) {
        yield(position)
        position += vector
    }
}

fun Sequence<Position>.inBounds(bounds: Bounds) = takeWhile { it in bounds }
