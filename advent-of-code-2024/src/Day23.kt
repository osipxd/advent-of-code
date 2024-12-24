import lib.PairOf

private const val DAY = "Day23"

private typealias ConnectionTable = Map<String, Set<String>>

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 7
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe "co,de,ka,ta"
        measureAnswer { part2(input()) }
    }
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

private fun part2(connections: List<PairOf<String>>): String {
    val connectionTable = mutableMapOf<String, MutableSet<String>>()

    for ((firstHost, secondHost) in connections) {
        connectionTable.getOrPut(firstHost) { mutableSetOf() } += secondHost
        connectionTable.getOrPut(secondHost) { mutableSetOf() } += firstHost
    }

    val biggestCluster = connectionTable.keys.fold(emptySet<String>()) { cluster, host ->
        connectionTable.findBiggestCluster(host, cluster)
    }
    return biggestCluster.sorted().joinToString(",")
}

private fun ConnectionTable.findBiggestCluster(
    startHost: String,
    currentBiggestCluster: Set<String>,
): Set<String> {
    if (startHost in currentBiggestCluster) return currentBiggestCluster

    data class State(
        val cluster: Set<String> = emptySet(),
        val checked: Set<String> = emptySet(),
    ) {
        constructor(host: String) : this(
            cluster = getValue(host) + host,
            checked = setOf(host),
        )

        val hostsToCheck: Set<String> = cluster - checked

        fun includeHost(host: String): State {
            val hostConnections = getValue(host)
            return State(cluster intersect (hostConnections + host), checked + host)
        }
    }

    val seen = mutableSetOf<State>()
    val queue = ArrayDeque<State>()
    var biggestCluster = currentBiggestCluster

    fun addNext(state: State) {
        if (state.cluster.size > biggestCluster.size && seen.add(state)) queue.addLast(state)
    }

    addNext(State(startHost))
    while (queue.isNotEmpty()) {
        val state = queue.removeFirst()
        val hostsToCheck = state.hostsToCheck

        if (hostsToCheck.isEmpty()) {
            if (state.cluster.size > biggestCluster.size) biggestCluster = state.cluster
        } else {
            for (host in hostsToCheck) addNext(state.includeHost(host))
        }
    }

    return biggestCluster
}

private fun readInput(name: String) = readLines(name).map { it.split("-").takePair() }