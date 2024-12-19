import lib.PairOf

private const val DAY = "Day19"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 6
        measureAnswer { part1(input()) }
    }

    //"Part 2" {
    //    part2(testInput()) shouldBe 0
    //    measureAnswer { part2(input()) }
    //}
}

private fun part1(input: PairOf<List<String>>): Int {
    val (towels, patterns) = input

    fun isPossibleDesign(pattern: String): Boolean {
        if (pattern.isEmpty()) return true
        return towels.any { towel -> pattern.startsWith(towel) && isPossibleDesign(pattern.removePrefix(towel)) }
    }

    return patterns.count(::isPossibleDesign)
}

private fun part2(input: PairOf<List<String>>): Int = TODO()

private fun readInput(name: String): PairOf<List<String>> {
    val (rawTowels, rawPatterns) = readText(name).split("\n\n")
    val towels = rawTowels.split(", ")
    val patterns = rawPatterns.lines()
    return towels to patterns
}