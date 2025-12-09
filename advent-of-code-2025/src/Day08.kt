private const val DAY = "Day08"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput(), maxConnections = 10) shouldBe 40
        measureAnswer { part1(input(), maxConnections = 1000) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 25272
        measureAnswer { part2(input()) }
    }
}

private fun part1(input: List<Point>, maxConnections: Int): Int {
    val closestPairs = input.findClosestPairs()

    val dsu = DisjointSetUnion(input.size)
    for ((from, to) in closestPairs.take(maxConnections)) dsu.union(from, to)

    return dsu.sizes()
        .sortedDescending()
        .take(3)
        .reduce(Int::times)
}

private fun part2(input: List<Point>): Long {
    val closestPairs = input.findClosestPairs()

    val dsu = DisjointSetUnion(input.size)
    for ((from, to) in closestPairs) {
        if (dsu.union(from, to) == input.size) {
            return input[from].x.toLong() * input[to].x.toLong()
        }
    }

    error("No solution found")
}

private fun List<Point>.findClosestPairs(): List<Pair<Int, Int>> {
    val allPairs = mutableListOf<Triple<Int, Int, Long>>()
    for (from in indices) {
        for (to in (from + 1)..lastIndex) {
            val distance = this[from].squaredDistanceTo(this[to])
            allPairs.add(Triple(from, to, distance))
        }
    }

    return allPairs
        .sortedBy { it.third }
        .map { it.first to it.second }
}

private data class Point(val x: Int, val y: Int, val z: Int) {
    override fun toString(): String = "($x, $y, $z)"

    fun squaredDistanceTo(other: Point): Long =
        (other.x - this.x).pow2() + (other.y - this.y).pow2() + (other.z - this.z).pow2()
}

private class DisjointSetUnion(size: Int) {
    private val parents = IntArray(size) { it }
    private val size = IntArray(size) { 1 }

    fun findRoot(element: Int): Int {
        if (element == parents[element]) return element
        return findRoot(parents[element]).also { parents[element] = it }
    }

    fun union(a: Int, b: Int): Int {
        var rootA = findRoot(a)
        var rootB = findRoot(b)
        if (rootA != rootB) {
            if (size[rootA] < size[rootB]) rootA = rootB.also { rootB = rootA }
            parents[rootB] = rootA
            size[rootA] += size[rootB]
        }
        return size[rootA]
    }

    fun sizes() = roots().map { size[it] }

    private fun roots() = parents.filterIndexed { index, value -> value == index }
}

private fun readInput(name: String) = readLines(name) {
    val (x, y, z) = it.splitInts()
    Point(x, y, z)
}

// Utils

private fun Int.pow2(): Long = this.toLong() * this
