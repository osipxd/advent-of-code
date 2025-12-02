private const val DAY = "Day02"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 1227775554
        measureAnswer { part1(input()) }
    }

    //"Part 2" {
    //    part2(testInput()) shouldBe 0
    //    measureAnswer { part2(input()) }
    //}
}

private fun part1(input: List<LongRange>): Long = input.sumOf { range ->
    val startLength = range.first.length
    val endLength = range.last.length

    val firstHalfStart = range.first.take(startLength / 2)
    val firstHalfEnd = range.last.take((endLength + 1) / 2)

    (firstHalfStart..firstHalfEnd).asSequence()
        .map { "$it$it".toLong() }
        .filter { it in range }
        .sum()
}

private fun part2(input: List<IntRange>): Int = TODO()

private val Long.length get() = toString().length

// Example: 1234.take(2) -> 12
private fun Long.take(count: Int) = toString().take(count).ifEmpty { "0" }.toLong()


private fun readInput(name: String) = readText(name).split(",").map {
    val (start, end) = it.splitLongs("-")
    start..end
}
