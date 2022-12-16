import kotlin.math.abs

fun main() {
    val testInput = readInput("Day16_test")
    val input = readInput("Day16")

    "Part 1" {
        part1(testInput) shouldBe 1651
        measureAnswer { part1(input) }
    }

    "Part 2" {
        part2(testInput) shouldBe 1707
        measureAnswer { part2(input) }
    }
}

private fun part1(input: Map<String, Valve>): Int {
    val valvesToOpen = input.valuableValves()
    return input.maxRate("AA", timeLeft = 30, valvesToOpen)
}

private fun part2(input: Map<String, Valve>): Int {
    val valvesToOpen = input.valuableValves()

    fun combine(rightCount: Int): MutableSet<Pair<Set<String>, Set<String>>> {
        if (rightCount == 0) return mutableSetOf(valvesToOpen.toSet() to emptySet())

        val combinations = combine(rightCount - 1)
        for ((left, right) in combinations.toList()) {
            for (moveMe in left) combinations += left - moveMe to right + moveMe
        }

        return combinations
    }

    // This can be optimized. Current values:
    // - 20 combinations for test input (answer at 4th combination)
    // - 6435 combinations for my input (answer at 259th combination)
    val combinations = combine(valvesToOpen.size / 2)
        .dropWhile { (left, right) -> abs(left.size - right.size) > 1 }
    println("Total: ${combinations.size}")

    val sharedMem = mutableMapOf<List<Any>, Int>()

    var count = 0
    var currentMax = 0
    return combinations
        .maxOf { (myValves, elephantValves) ->
            val myRate = input.maxRate("AA", timeLeft = 26, myValves, sharedMem)
            val elephantRate = input.maxRate("AA", timeLeft = 26, elephantValves, sharedMem)
            (myRate + elephantRate).also { value ->
                currentMax = maxOf(currentMax, value)
                println("${++count}: $myValves $elephantValves -> $value ($currentMax)")
            }
        }
}

// Returns valves with non-zero rate
private fun Map<String, Valve>.valuableValves(): Set<String> =
    values.asSequence().filter { it.rate != 0 }.map { it.name }.toSet()

private fun Map<String, Valve>.maxRate(
    current: String,
    timeLeft: Int,
    closed: Set<String>,
    mem: MutableMap<List<Any>, Int> = mutableMapOf(),
): Int {
    if (timeLeft <= 2 || closed.isEmpty()) return 0

    return mem.getOrPut(listOf(current, timeLeft, closed)) {
        val valve = getValue(current)
        val ifKeepAsIs = valve.next.maxOf { maxRate(it, timeLeft - 1, closed, mem) }

        val ifOpen = if (valve.rate != 0 && current in closed) {
            val timeAfterOpen = timeLeft - 1
            (valve.rate * timeAfterOpen) +
                valve.next.maxOf { maxRate(it, timeAfterOpen - 1, closed - current, mem) }
        } else {
            0
        }

        maxOf(ifKeepAsIs, ifOpen)
    }
}

// === Input reading ===

private val regex = Regex("^Valve (..) has flow rate=(\\d+); tunnels? leads? to valves? (.+)$")

private fun readInput(name: String) = buildMap {
    for (line in readLines(name)) {
        val (source, rate, destinations) = regex.matchEntire(line)!!.destructured
        put(source, Valve(source, rate.toInt(), destinations.splitToSequence(", ").toSet()))
    }
}

private data class Valve(val name: String, val rate: Int, val next: Set<String>)
