package lib.matrix

import printValue

data class Bounds(
    val rowBounds: IntRange,
    val columnBounds: IntRange,
) {
    constructor(width: Int, height: Int): this(0..<width, 0..<height)
    constructor(size: Int): this(width = size, height = size)
}

val Bounds.topLeftPosition: Position get() = Position(rowBounds.first, columnBounds.first)
val Bounds.bottomRightPosition: Position get() = Position(rowBounds.last, columnBounds.last)

val Matrix<*>.bounds: Bounds get() = Bounds(rowIndices, columnIndices)

operator fun Bounds.contains(position: Position): Boolean =
    position.row in rowBounds && position.column in columnBounds

/** Prints bounds row by row representing each element as a char. */
fun Bounds.debugPrint(transform: (position: Position) -> Char) {
    for (row in rowBounds) {
        columnBounds.joinToString(separator = "") { col -> transform(Position(row, col)).toString() }
            .printValue()
    }
}
