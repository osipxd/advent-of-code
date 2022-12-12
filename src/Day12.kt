import java.util.*

fun main() {
    "Part 1" {
        val testInput = readInput("Day12_test")
        val input = readInput("Day12")
        part1(testInput) shouldBe 31
        answer(part1(input))
    }

    "Part 2" {
        val testInput = readInput("Day12_test")
        val input = readInput("Day12")
        part2(testInput) shouldBe 29
        answer(part2(input))
    }
}

private fun part1(input: Array<CharArray>): Int {
    val n = input.size
    val m = input[0].size

    val (startR, startC) = input.findChar('S')
    input[startR][startC] = 'a'
    val (endR, endC) = input.findChar('E')
    input[endR][endC] = 'z'

    val distance = mutableMapOf((startR to startC) to 0)

    val stack = LinkedList<Pair<Int, Int>>()
    stack += startR to startC

    fun neighbors(r: Int, c: Int) = sequence {
        if (r > 0) yield(r - 1 to c)
        if (r < n - 1) yield(r + 1 to c)
        if (c > 0) yield(r to c - 1)
        if (c < m - 1) yield(r to c + 1)
    }

    while (stack.isNotEmpty()) {
        val (r, c) = stack.removeFirst()
        val char = input[r][c]

        if (r == endR && c == endC) break

        for ((r2, c2) in neighbors(r, c)) {
            if (r2 to c2 in distance || input[r2][c2] > char + 1) continue
            distance[r2 to c2] = distance.getValue(r to c) + 1
            stack.addLast(r2 to c2)
        }
    }

    return distance.getValue(endR to endC)
}

private fun part2(input: Array<CharArray>): Int {
    val n = input.size
    val m = input[0].size

    val (startR, startC) = input.findChar('S')
    input[startR][startC] = 'a'
    val (endR, endC) = input.findChar('E')
    input[endR][endC] = 'z'

    val distance = mutableMapOf((endR to endC) to 0)

    val stack = LinkedList<Pair<Int, Int>>()
    stack += endR to endC

    fun neighbors(r: Int, c: Int) = sequence {
        if (r > 0) yield(r - 1 to c)
        if (r < n - 1) yield(r + 1 to c)
        if (c > 0) yield(r to c - 1)
        if (c < m - 1) yield(r to c + 1)
    }

    while (stack.isNotEmpty()) {
        val (r, c) = stack.removeFirst()
        val char = input[r][c]

        if (char == 'a') return distance.getValue(r to c)

        for ((r2, c2) in neighbors(r, c)) {
            if (r2 to c2 in distance || char > input[r2][c2] + 1) continue
            distance[r2 to c2] = distance.getValue(r to c) + 1
            stack.addLast(r2 to c2)
        }
    }

    return distance.getValue(endR to endC)
}

private fun Array<CharArray>.findChar(char: Char): Pair<Int, Int> {
    for (r in indices) {
        for (c in this[0].indices) {
            if (this[r][c] == char) return r to c
        }
    }

    error("")
}

private fun readInput(name: String) = readLines(name).map { it.toCharArray() }.toTypedArray()
