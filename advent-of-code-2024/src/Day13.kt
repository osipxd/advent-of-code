

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
        if (maxTaps == -1 || a <= maxTaps && b <= maxTaps) tapsCost(a, b) else 0
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
     * 1. Multiply the first equation by `by` and the second one by `bx`
     *    ⎧ a * ax * by + b * bx * by = tx * by
     *    ⎩ a * ay * bx + b * by * bx = ty * bx
     *
     * 2. Subtract the second equation from the first one and find `a`:
     *    (a * ax * by + b * bx * by) - (a * ay * bx + b * by * bx) = tx * by - ty * bx
     *    a * ax * by - a * ay * bx = tx * by - ty * bx
     *    a (ax * by - ay * bx) = tx * by - ty * bx
     *    a = (tx * by - ty * bx) / (ax * by - ay * bx)
     *
     * 3. To find `b`, subtract `a * ay` from the second equation and divide it by `by`
     *    b = (ty - a * ay) / by
     *
     * Considering `a` and `b` are whole numbers, we should check that there is no remainder on division.
     */
    fun solve(targetShift: Long): Pair<Long, Long> {
        val tx = tx + targetShift
        val ty = ty + targetShift

        val a = (tx * by - ty * bx).divideSafely(ax * by - ay * bx) ?: return 0L to 0L
        val b = (ty - a * ay).divideSafely(by) ?: return 0L to 0L
        return a to b
    }
}

// Utils

private fun Long.divideSafely(divisor: Int): Long? =
    if (divisor != 0 && this % divisor == 0L) this / divisor else null
