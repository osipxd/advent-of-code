import lib.PairOf

private const val DAY = "Day23"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 7
        measureAnswer { part1(input()) }
    }

    //"Part 2" {
    //    part2(testInput()) shouldBe 0
    //    measureAnswer { part2(input()) }
    //}
}

private fun part1(connections: List<PairOf<String>>): Int {
    val connectionTable = mutableMapOf<String, MutableSet<String>>()

    var count = 0
    for ((firstHost, secondHost) in connections) {
        connectionTable.getOrPut(firstHost) { mutableSetOf() } += secondHost
        connectionTable.getOrPut(secondHost) { mutableSetOf() } += firstHost

        val thirdHosts = connectionTable.getValue(firstHost) intersect connectionTable.getValue(secondHost)
        count += if (firstHost.isPossibleTarget() || secondHost.isPossibleTarget()) {
            thirdHosts.size
        } else {
            thirdHosts.count { it.isPossibleTarget() }
        }
    }

    return count
}

private fun String.isPossibleTarget() = startsWith("t")

private fun part2(connections: List<PairOf<String>>): Int = TODO()

private fun readInput(name: String) = readLines(name).map { it.split("-").takePair() }