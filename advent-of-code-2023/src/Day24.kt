import lib.combinations
import lib.component6

private const val DAY = "Day24"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput(), 7..27L) shouldBe 2
        measureAnswer { part1(input(), 200_000_000_000_000..400_000_000_000_000) }
    }

    //"Part 2" {
    //    part2(testInput()) shouldBe 0
    //    measureAnswer { part2(input()) }
    //}
}

// region Part 1
private fun part1(input: List<Hailstone>, testArea: LongRange): Int {
    return input.combinations().count { (first, second) ->
        val (a1, b1, c1) = first.toLine()
        val (a2, b2, c2) = second.toLine()

        val k = determinant(a1, a2, b1, b2)
        if (k == 0.0) return@count false // Parallel vectors

        val px = determinant(c1, c2, b1, b2) / k
        val py = determinant(a1, a2, c1, c2) / k

        px in testArea && py in testArea &&
            first.isPointInFuture(px, py) && second.isPointInFuture(px, py)
    }
}

private fun Hailstone.toLine() = Line(
    a = v.y,
    b = -v.x,
    c = v.y * p.x - v.x * p.y,
)

private fun Hailstone.isPointInFuture(px: Double, py: Double) = v.x * (px - p.x) >= 0 && v.y * (py - p.y) >= 0

/** Representing the line in common form: ax + by = c */
private data class Line(val a: Long, val b: Long, val c: Long)

/** Calculates determinant of the 2x2 matrix containing [a], [b], [c], [d] values. */
private fun determinant(a: Long, b: Long, c: Long, d: Long): Double {
    return a.toDouble() * d - b.toDouble() * c
}

private operator fun LongRange.contains(value: Double): Boolean = value >= first && value <= last
// endregion

private fun part2(input: List<Hailstone>): Int = TODO()

private fun readInput(name: String) = readLines(name) { line ->
    val (x, y, z, dx, dy, dz) = line.split(" @ ", ", ").map { it.toLong() }
    Hailstone(XYZ(x, y, z), XYZ(dx, dy, dz))
}

private data class XYZ(val x: Long, val y: Long, val z: Long) {
    override fun toString(): String = "($x, $y, $z)"
}

private class Hailstone(val p: XYZ, val v: XYZ) {
    override fun toString(): String = "$p @ $v"
}
