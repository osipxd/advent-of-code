private const val DAY = "Day05"

private typealias RangesAndIngredients = Pair<List<LongRange>, List<Long>>

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 3
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 14
        measureAnswer { part2(input()) }
    }
}

private fun part1(input: RangesAndIngredients): Int {
    val (freshRanges, ingredients) = input
    return ingredients.count { freshRanges.any { range -> it in range } }
}

private fun part2(input: RangesAndIngredients): Long {
    val (freshRanges) = input

    var freshCount = 0L
    var lastFresh = 0L
    for (range in freshRanges) {
        if (range.last <= lastFresh) continue
        val start = maxOf(lastFresh + 1, range.first)
        val count = range.last - start + 1
        freshCount += count
        lastFresh = range.last
    }

    return freshCount
}

private fun readInput(name: String): RangesAndIngredients {
    val (rawRanges, rawIngredients) = readText(name).split("\n\n")

    val freshRanges = rawRanges.lineSequence()
        .map { line ->
            val (start, end) = line.splitLongs("-")
            start..end
        }
        .sortedWith(compareBy(LongRange::first, LongRange::last))
        .toList()
    val ingredients = rawIngredients.lineSequence().map(String::toLong).toList()

    return freshRanges to ingredients
}