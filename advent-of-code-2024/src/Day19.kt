import lib.PairOf

private const val DAY = "Day19"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 6
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 16
        measureAnswer { part2(input()) }
    }
}

private fun part1(input: PairOf<List<String>>): Int {
    val (towels, patterns) = input

    fun isPossibleDesign(pattern: String): Boolean {
        if (pattern.isEmpty()) return true
        return towels.any { towel -> pattern.startsWith(towel) && isPossibleDesign(pattern.removePrefix(towel)) }
    }

    return patterns.count(::isPossibleDesign)
}

private fun part2(input: PairOf<List<String>>): Long {
    val (towels, patterns) = input

    val memory = mutableMapOf<String, Long>()
    fun countPossibleArrangements(pattern: String): Long = memory.getOrPut(pattern) {
        if (pattern.isEmpty()) return 1L
        towels.sumOf { towel ->
            if (pattern.startsWith(towel)) countPossibleArrangements(pattern.removePrefix(towel))
            else 0L
        }
    }

    return patterns.sumOf(::countPossibleArrangements)
}

private fun readInput(name: String): PairOf<List<String>> {
    val (rawTowels, rawPatterns) = readText(name).split("\n\n")
    val towels = rawTowels.split(", ")
    val patterns = rawPatterns.lines()
    return towels to patterns
}