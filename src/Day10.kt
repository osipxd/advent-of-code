fun main() {
    val testInput = readInput("Day10_test")
    val input = readInput("Day10")

    "Part 1 + 2" {
        println()
        solve(testInput) shouldBe 13140
        println()
        answer(solve(input))
    }
}

private const val START_OFFSET = 20
private const val WIDTH = 40

private fun solve(commands: List<List<String>>): Int {
    var cycle = 0
    var register = 1
    var signalStrength = 0

    fun tick(argument: Int = 0) {
        // Start new cycle
        cycle++

        // Part 1
        if ((cycle + START_OFFSET) % WIDTH == 0) signalStrength += cycle * register

        // Part 2
        val isFilled = (cycle - 1) % WIDTH in register - 1..register + 1
        print(if (isFilled) "#" else ".")
        if (cycle % WIDTH == 0) println()

        // Finish cycle
        register += argument
    }

    for (parts in commands) {
        tick()
        if (parts[0] == "addx") tick(parts[1].toInt())
    }

    return signalStrength
}

private fun readInput(name: String) = readLines(name).map { it.split(" ") }
