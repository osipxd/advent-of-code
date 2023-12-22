import kotlin.math.abs
import kotlin.math.min

private const val DAY = "Day22"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 5
        measureAnswer { part1(input()) }
    }

    //"Part 2" {
    //    part2(testInput()) shouldBe 0
    //    measureAnswer { part2(input()) }
    //}
}

private fun part1(input: List<SandBrick>): Int {
    val fallenBricks = ArrayDeque<SandBrick>()
    val heights = mutableMapOf<XY, Int>()
    val occupied = mutableMapOf<XYZ, SandBrick>()

    for (brick in input.sortedBy { it.z1 }) {
        val xyPoints = brick.xyPoints()
        brick.z1 = xyPoints.maxOf { heights.getOrDefault(it, 0) } + 1
        for (xy in xyPoints) {
            heights[xy] = brick.z2
            brick.zRange.forEach { z -> occupied[xy.withZ(z)] = brick }
        }
        fallenBricks.addFirst(brick)
    }

    return fallenBricks.count { brick ->
        val upperBricks = brick.xyPoints().withZ(brick.z2 + 1).mapNotNull { occupied[it] }.distinct()
        upperBricks.all { upperBrick ->
            upperBrick.xyPoints().withZ(upperBrick.z1 - 1).any { xyz ->
                occupied[xyz] != null && occupied[xyz] != brick
            }
        }
    }
}

private fun part2(input: List<SandBrick>): Int = TODO()

private fun readInput(name: String) = readLines(name) { line ->
    val (x1, y1, z1, x2, y2, z2) = line.splitInts(",", "~")
    SandBrick(x1, y1, z1, x2, y2, z2)
}

private class SandBrick(
    x1: Int,
    y1: Int,
    z1: Int,
    x2: Int,
    y2: Int,
    z2: Int,
) {
    val xRange = x1..x2
    val yRange = y1..y2

    val height = abs(z2 - z1) + 1
    var z1 = min(z1, z2)
    val z2 get() = z1 + height - 1
    val zRange get() = z1..z2

    fun xyPoints(): List<XY> = xRange.flatMap { x -> yRange.map { y -> XY(x, y) } }
}

private data class XY(val x: Int, val y: Int) {
    fun withZ(z: Int) = XYZ(x, y, z)
}

private fun List<XY>.withZ(z: Int) = map { it.withZ(z) }

private data class XYZ(val x: Int, val y: Int, val z: Int)

private operator fun <E> List<E>.component6() = get(5)
