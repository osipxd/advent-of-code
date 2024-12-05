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

private fun part1(input: Pair<List<Rule>, List<Update>>): Int {
    val (rules, updates) = input
    val followersMap = rules.groupBy { it.first }.mapValues { (_, values) -> values.mapTo(mutableSetOf()) { it.second } }

    return updates.filter { it.isSafe(followersMap) }
        .sumOf { it[it.size / 2] }
}

private fun part2(input: Pair<List<Rule>, List<Update>>): Int {
    val (rules, updates) = input
    val followersMap = rules.groupBy { it.first }.mapValues { (_, values) -> values.mapTo(mutableSetOf()) { it.second } }

    return updates.filterNot { it.isSafe(followersMap) }
        // Don't say anything!
        .map { it.fixedOrder(followersMap).fixedOrder(followersMap).fixedOrder(followersMap).fixedOrder(followersMap).fixedOrder(followersMap).fixedOrder(followersMap).fixedOrder(followersMap).fixedOrder(followersMap).fixedOrder(followersMap).fixedOrder(followersMap).fixedOrder(followersMap) }
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

private fun Update.fixedOrder(followersMap: Map<Int, Set<Int>>): Update {
    val result = ArrayDeque<Int>()

    for (num in this) {
        val followers = followersMap[num].orEmpty()
        val firstFollowerIndex = result.indexOfFirst { it in followers }
        when (firstFollowerIndex) {
            -1 -> result.addLast(num)
            0 -> result.addFirst(num)
            else -> result.add(firstFollowerIndex - 1, num)
        }
    }

    return result
}

private fun readInput(name: String): Pair<List<Rule>, List<Update>> {
    val (rawRules, rawUpdates) = readText(name).split("\n\n")
    val rules = rawRules.lines().map { it.splitInts("|").takePair() }
    val updates = rawUpdates.lines().map { it.splitInts() }

    return rules to updates
}