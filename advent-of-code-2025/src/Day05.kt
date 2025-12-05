private const val DAY = "Day05"

private typealias RangesAndIngredients = Pair<List<LongRange>, List<Long>>

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 3
        measureAnswer { part1(input()) }
    }

    //"Part 2" {
    //    part2(testInput()) shouldBe 0
    //    measureAnswer { part2(input()) }
    //}
}

private fun part1(input: RangesAndIngredients): Int {
    val (freshRanges, ingredients) = input
    return ingredients.count { freshRanges.any { range -> it in range } }
}

private fun part2(input: RangesAndIngredients): Int = TODO()

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