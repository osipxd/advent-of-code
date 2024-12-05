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

    //"Part 2" {
    //    part2(testInput()) shouldBe 0
    //    measureAnswer { part2(input()) }
    //}
}

private fun part1(input: Pair<List<Rule>, List<Update>>): Int {
    val (rules, updates) = input
    val followersMap = rules.groupBy { it.first }.mapValues { (_, values) -> values.mapTo(mutableSetOf()) { it.second } }

    return updates.filter { it.isSafe(followersMap) }
        .sumOf { it[it.size / 2] }
}

private fun Update.isSafe(followersMap: Map<Int, Set<Int>>): Boolean {
    for (i in indices.reversed()) {
        val num = get(i)
        val followers = followersMap[num] ?: continue

        if ((subList(0, i).toSet() intersect followers).isNotEmpty()) return false
    }
    return true
}

private fun part2(input: Pair<List<Rule>, List<Update>>): Int = TODO()

private fun readInput(name: String): Pair<List<Rule>, List<Update>> {
    val (rawRules, rawUpdates) = readText(name).split("\n\n")
    val rules = rawRules.lines().map { it.splitInts("|").takePair() }
    val updates = rawUpdates.lines().map { it.splitInts() }

    return rules to updates
}