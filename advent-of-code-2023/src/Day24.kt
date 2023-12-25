import lib.combinations

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

private fun part1(input: List<Hailstone>, testArea: LongRange): Int {
    return input.combinations().count { (first, second) -> first.xyCrossedWith(second, testArea) }
}

private fun part2(input: List<Hailstone>): Int = TODO()

private fun readInput(name: String) = readLines(name) { line ->
    val (xyz, vector) = line.split(" @ ")
    val (x, y, z) = xyz.split(", ").map { it.toLong() }
    val (dx, dy, dz) = vector.splitInts(", ")
    Hailstone(x, y, z, dx, dy, dz)
}

private class Hailstone(
    val x: Long,
    val y: Long,
    val z: Long,
    val dx: Int,
    val dy: Int,
    val dz: Int,
) {

    // Representing the line in common form: ax + by = c
    val a = dy
    val b = -dx
    val c = dy * x - dx * y

    fun xyCrossedWith(other: Hailstone, testRange: LongRange): Boolean {
        val k = (this.a * other.b - other.a * this.b).toDouble()
        if (k == 0.0) return false // Parallel vectors

        val px = (this.c * other.b - other.c * this.b) / k
        val py = (this.a * other.c - other.a * this.c) / k

        return px in testRange && py in testRange &&
            this.isPointInFuture(px, py) && other.isPointInFuture(px, py)
    }

    private fun isPointInFuture(px: Double, py: Double) = dx * (px - x) >= 0 && dy * (py - y) >= 0
}

private operator fun LongRange.contains(value: Double): Boolean = value >= first && value <= last
