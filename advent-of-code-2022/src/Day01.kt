fun main() {

    fun readInput(name: String) = readText(name).splitToSequence("\n\n")
        .map { it.lineSequence().map(String::toInt) }

    // Time - O(N), Space - O(1)
    //   where N is number of Elves
    fun part1(elves: Sequence<Sequence<Int>>): Int = elves.maxOf { it.sum() }

    // Time - O(N*log(N)), Space - O(N)
    fun part2Simple(elves: Sequence<Sequence<Int>>): Int {
        return elves.map { it.sum() }
            .sortedDescending()
            .take(3)
            .sum()
    }

    // Time - O(N), Space - O(1)
    fun part2Fast(elves: Sequence<Sequence<Int>>): Int {
        val topThree = arrayOf(0, 0, 0)

        for (elf in elves) {
            val sum = elf.sum()

            when {
                sum > topThree[0] -> {
                    topThree[2] = topThree[1]
                    topThree[1] = topThree[0]
                    topThree[0] = sum
                }

                sum > topThree[1] -> {
                    topThree[2] = topThree[1]
                    topThree[1] = sum
                }

                sum > topThree[2] -> {
                    topThree[2] = sum
                }
            }
        }

        return topThree.sum()
    }

    val testInput = readInput("Day01_test")
    check(part1(testInput) == 24000)

    val input = readInput("Day01")
    println(part1(input))
    println(part2Simple(input))
    println(part2Fast(input))
}
