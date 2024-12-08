package lib.matrix

data class Bounds(
    val rowBounds: IntRange,
    val columnBounds: IntRange,
)

val Matrix<*>.bounds: Bounds get() = Bounds(rowIndices, columnIndices)

operator fun Bounds.contains(position: Position): Boolean =
    position.row in rowBounds && position.column in columnBounds