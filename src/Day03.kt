fun main() {

    fun readInput(name: String) = readLines(name).asSequence()

    // Common function to solve both part one and two.
    // Time - O(N), Space - O(1)
    fun Sequence<List<String>>.sumOfPriorities(): Int {
        // 1. Convert strings in group to Sets of chars and find intersection of these sets
        // 2. Calculate priorities and it's sum
        return map { it.map(String::toSet).reduce(Set<Char>::intersect).single() }
            .sumOf { if (it in 'a'..'z') it - 'a' + 1 else it - 'A' + 27 }
    }

    fun part1(input: Sequence<String>): Int = input.map { it.chunked(it.length / 2) }.sumOfPriorities()
    fun part2(input: Sequence<String>): Int = input.chunked(3).sumOfPriorities()

    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157)

    val input = readInput("Day03")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
