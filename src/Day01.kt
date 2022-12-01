fun main() {

    fun readInput(name: String) = readText(name).splitToSequence("\n\n")
        .map { it.lineSequence().map(String::toInt) }

    // Time - O(N), Space - O(1)
    //   where N is number of Elves
    fun part1(elves: Sequence<Sequence<Int>>): Int = elves.maxOf { it.sum() }

    // Time - O(N*log(N)), Space - O(N)
    fun part2(elves: Sequence<Sequence<Int>>): Int {
        return elves.map { it.sum() }
            .sortedDescending()
            .take(3)
            .sum()
    }

    val testInput = readInput("Day01_test")
    check(part1(testInput) == 24000)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
