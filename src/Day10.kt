fun main() {
    val testInput = readInput("Day10_test")
    val input = readInput("Day10")

    "Part 1" {
        part1(testInput) shouldBe 13140
        answer(part1(input))
    }

    "Part 2" {
        part2(testInput)
        println()
        part2(input)
    }
}

private fun part1(input: List<String>): Int {
    var cycle = 0
    var register = 1
    var signalStrength = 0

    fun tick(registerChange: Int = 0) {
        cycle++
        if ((cycle + 20) % 40 == 0) signalStrength += cycle * register
        register += registerChange
    }

    for (command in input) {
        tick()
        if (command.startsWith("addx")) tick(command.split(" ").last().toInt())
    }

    return signalStrength
}

private const val ANSI_RESET = "\u001B[0m"
private const val ANSI_WHITE_BG = "\u001B[47m"
private const val ANSI_GREY_BG = "\u001B[40m"

// This part has a bug!
private fun part2(input: List<String>) {
    var cycle = 0
    var register = 1

    fun tick(registerChange: Int = 0) {
        cycle++

        val drawable = register .. register + 2
        print(if ((cycle % 40) in drawable) "$ANSI_WHITE_BG#$ANSI_RESET" else "$ANSI_GREY_BG.$ANSI_RESET")
        if (cycle % 40 == 0) println()

        register += registerChange
    }

    for (command in input) {
        tick()
        if (command.startsWith("addx")) tick(command.split(" ").last().toInt())
    }
}

private fun readInput(name: String) = readLines(name)
