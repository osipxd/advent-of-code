fun main() {
    val testInput = readInput("Day06_test")
    val input = readInput("Day06")

    check(part1(testInput) == 7)
    println("Part 1: " + part1(input))

    check(part2(testInput) == 19)
    println("Part 2: " + part2(input))
}

private fun part1(input: String): Int = findStartMessageMarker(input, windowSize = 4)
private fun part2(input: String): Int = findStartMessageMarker(input, windowSize = 14)

// Time - O(N), Space - O(M)
//   where N - number of chars in input, M - window size
private fun findStartMessageMarker(input: String, windowSize: Int): Int {
    // Count of each character in the window
    val counts = mutableMapOf<Char, Int>()

    // Init window with first N chars
    for (i in 0 until windowSize) counts.change(input[i], +1)

    // Remove most left character from window, and add the char next after window
    for (prevIndex in 0 until input.length - windowSize) {
        if (counts.size == windowSize) return prevIndex + windowSize
        counts.change(input[prevIndex], -1)
        counts.change(input[prevIndex + windowSize], +1)
    }

    error("Where is the start-of-message marker?")
}

// Changes count for the given [key]. Removes it from the map if result value is 0
private fun MutableMap<Char, Int>.change(key: Char, diff: Int) {
    this[key] = this.getOrDefault(key, 0) + diff
    if (this[key] == 0) this.remove(key)
}

private fun readInput(name: String) = readText(name)
