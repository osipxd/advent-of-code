import kotlin.math.absoluteValue
import kotlin.math.sign

fun main() {
    val input = readInput("Day09")

    "Part 1" {
        val testInput = readInput("Day09_test1")
        part1(testInput) shouldBe 13
        answer(part1(input))
    }

    "Part 2" {
        val testInput = readInput("Day09_test2")
        part2(testInput) shouldBe 36
        answer(part2(input))
    }
}

private fun part1(input: List<Pair<Command, Int>>): Int = simulateRope(length = 1, input)
private fun part2(input: List<Pair<Command, Int>>): Int = simulateRope(length = 9, input)

// Time - `O(M)`, Space - `O(L + V)`, where
//   M - number of total moves (sum of all command arguments)
//   L - rope length (including head)
//   V - number of points visited by tail
private fun simulateRope(length: Int, commands: List<Pair<Command, Int>>): Int {
    // length + head
    val positions = Array(length + 1) { 0 to 0 }

    // Here we will track coordinates visited by tail
    val visited = mutableSetOf(positions.last())

    for ((command, times) in commands) {
        // I don't know how to simulate all moves once, so let's do it step-by-step
        repeat(times) {
            // Move head
            positions[0] = command.apply(positions[0])

            // Simulate tail moves
            for (i in 1..positions.lastIndex) {
                val (headR, headC) = positions[i - 1]
                var (r, c) = positions[i]

                val diffR = headR - r
                val diffC = headC - c

                // If head moved too far, let's move closer to it
                // According the rules, we always try to make closer both row and column
                if (diffR.absoluteValue > 1 || diffC.absoluteValue > 1) {
                    r += diffR.sign
                    c += diffC.sign
                }

                positions[i] = r to c
            }

            visited += positions.last()
        }
    }

    return visited.size
}

private fun readInput(name: String) = readLines(name).map {
    val (command, number) = it.split(" ")
    Command.valueOf(command) to number.toInt()
}

private enum class Command(val dr: Int = 0, val dc: Int = 0) {
    R(dc = +1), U(dr = +1), L(dc = -1), D(dr = -1);

    /** Returns the next coordinates on this direction. */
    fun apply(coordinates: Pair<Int, Int>): Pair<Int, Int> {
        val (r, c) = coordinates
        return (r + dr) to (c + dc)
    }
}
