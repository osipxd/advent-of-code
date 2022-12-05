import java.util.*

// Use run { ... } to reuse the same local variables names :P
fun main() {
    run {
        val (testStacks, testCommands) = readInput("Day05_test")
        check(part1(testStacks.clone(), testCommands) == "CMZ")

        val (inputStacks, inputCommands) = readInput("Day05")
        println("Part 1: " + part1(inputStacks.clone(), inputCommands))
    }

    run {
        val (testStacks, testCommands) = readInput("Day05_test")
        check(part2(testStacks.clone(), testCommands) == "MCD")

        val (inputStacks, inputCommands) = readInput("Day05")
        println("Part 2: " + part2(inputStacks.clone(), inputCommands))
    }
}

private fun part1(crates: Array<LinkedList<Char>>, commands: List<List<Int>>): String {
    for ((count, from, to) in commands) {
        repeat(count) { crates[to].push(crates[from].pop()) }
    }

    return crates.joinToString("") { it.firstOrNull()?.toString().orEmpty() }
}

private fun part2(crates: Array<LinkedList<Char>>, commands: List<List<Int>>): String {
    val buffer = LinkedList<Char>()
    for ((count, from, to) in commands) {
        repeat(count) { buffer.push(crates[from].pop()) }
        while (buffer.isNotEmpty()) crates[to].push(buffer.pop())
    }

    return crates.joinToString("") { it.firstOrNull()?.toString().orEmpty() }
}

// See also Day05_input_reformat.gif if you want to simplify input
private fun readInput(name: String): Pair<Array<LinkedList<Char>>, List<List<Int>>> {
    // Split input to two parts and convert to lines
    val (cratesLines, commandsLines) = readText(name).split("\n\n").map { it.lines() }

    val n = (cratesLines.last().length + 3) / 4
    // size = n + 1  because indices of crates in commands start from 1, not 0
    val crates = Array(n + 1) { LinkedList<Char>() }
    for (lineIndex in cratesLines.size - 2 downTo 0) {
        for (i in 0 until n) {
            val char = cratesLines[lineIndex][i * 4 + 1]
            if (char != ' ') crates[i + 1].push(char)
        }
    }

    // Just take all numbers from commands
    val commands = commandsLines.map { it.split(" ").mapNotNull(String::toIntOrNull) }

    return crates to commands
}
