
import kotlin.math.abs
import kotlin.math.roundToLong

private const val DAY = "Day13"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 480
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()).printValue()
        measureAnswer { part2(input()) }
    }
}

private fun part1(input: List<ClawMachine>): Long = solve(input, maxTaps = 100)
private fun part2(input: List<ClawMachine>): Long = solve(input, targetShift = 10000000000000)

private fun solve(machines: List<ClawMachine>, targetShift: Long = 0, maxTaps: Int = -1): Long {
    return machines.sumOf { clawMachine ->
        val (a, b) = clawMachine.solve(targetShift)
        val (roundedA, roundedB) = listOf(a, b).map { it.asLongOrNull() }

        if (roundedA == null || roundedB == null || maxTaps != -1 && (roundedA > maxTaps || roundedB > maxTaps)) {
            0
        } else {
            tapsCost(roundedA, roundedB)
        }
    }
}

private fun tapsCost(aTaps: Long, bTaps: Long) = aTaps * 3 + bTaps * 1

private val TWO_NUMBERS_PATTERN = Regex(""".+?(\d+),.+?(\d+)""")

private fun String.extractNumbers() =
    TWO_NUMBERS_PATTERN.matchEntire(this)!!.groupValues.drop(1).map(String::toInt)

private fun readInput(name: String): List<ClawMachine> {
    return readText(name).split("\n\n").map { rawInput ->
        val (a, b, target) = rawInput.lines().map { it.extractNumbers() }
        val (ax, ay) = a
        val (bx, by) = b
        val (tx, ty) = target
        ClawMachine(ax, ay, bx, by, tx, ty)
    }
}

private data class ClawMachine(
    val ax: Int,
    val ay: Int,
    val bx: Int,
    val by: Int,
    val tx: Int,
    val ty: Int,
) {

    /**
     * 0. Initial system
     *    ⎧ a * ax + b * bx = tx
     *    ⎩ a * ay + b * by = ty
     *
     * 1. Subtract `b * bx` from the first equation and divide by `ax`
     *    ⎧ a = tx / ax - b * bx / ax
     *    ⎩ a * ay + b * by = ty
     *
     * 2. Subtract `a * ay` from the second equation and divide by `by`
     *    ⎧ a = tx / ax - b * bx / ax
     *    ⎩ b = ty / by - a * ay / by
     *
     * 3. Use the second equation in the first in place of `b`
     *    a = tx / ax - (ty / by - a * ay / by) * bx / ax              // next: unwrap brackets
     *    a = tx / ax - ty / by * bx / ax + a * ay / by * bx / ax      // next: move all `a`s to the left part
     *    a - a * ay / by * bx / ax = tx / ax - ty / by * bx / ax      // next: move `a` outside of brackets
     *    a * (1 - ay / by * bx / ax) = tx / ax - ty / by * bx / ax    // next: keep only `a` on the left side
     *    a = (tx / ax - ty / by * bx / ax) / (1 - ay / by * bx / ax)
     */
    fun solve(targetShift: Long): Pair<Double, Double> {
        val tx = (tx + targetShift).toDouble()
        val ty = (ty + targetShift).toDouble()

        val a = (tx / ax - ty / by * bx / ax) / (1 - ay.toDouble() / by * bx / ax)
        val b = ty / by - a * ay / by
        return a to b
    }
}

// Utils

private const val EPSILON = 1e-3

private fun Double.asLongOrNull(): Long? {
    val rounded = roundToLong()
    return if (abs(this - rounded) > EPSILON) null else rounded
}
