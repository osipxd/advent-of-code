private const val DAY = "Day05"

private typealias Rule = Pair<Int, Int>
private typealias Update = List<Int>

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 143
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 123
        measureAnswer { part2(input()) }
    }
}

private fun part1(input: Pair<Set<Rule>, List<Update>>): Int {
    val (rules, updates) = input
    val comparator = rules.comparator()

    return updates.filter { it.isSorted(comparator) }
        .sumOf { it[it.size / 2] }
}

private fun part2(input: Pair<Set<Rule>, List<Update>>): Int {
    val (rules, updates) = input
    val comparator = rules.comparator()

    return updates.filterNot { it.isSorted(comparator) }
        .map { it.sortedWith(comparator) }
        .sumOf { it[it.size / 2] }
}

private fun <T> List<T>.isSorted(comparator: Comparator<T>): Boolean {
    return asSequence().windowed(2).all { (o1, o2) -> comparator.compare(o1, o2) == -1 }
}

private fun Set<Rule>.comparator(): Comparator<Int> = Comparator { o1, o2 ->
    when {
        (o1 to o2) in this -> -1
        (o2 to o1) in this -> 1
        else -> error("Where did you get this input? It's not from Advent of Code :)")
    }
}

private fun readInput(name: String): Pair<Set<Rule>, List<Update>> {
    val (rawRules, rawUpdates) = readText(name).split("\n\n")
    val rules = rawRules.lines().map { it.splitInts("|").takePair() }.toSet()
    val updates = rawUpdates.lines().map { it.splitInts() }

    return rules to updates
}
