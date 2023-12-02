private const val DAY = "Day2"

fun main() {
    val testInput = readInput("${DAY}_test")
    val input = readInput(DAY)

    "Part 1" {
        part1(testInput) shouldBe 8
        measureAnswer { part1(input) }
    }

    "Part 2" {
        part2(testInput) shouldBe 2286
        measureAnswer { part2(input) }
    }
}

private fun part1(input: List<Game>): Int = input.filter(::fitsToBag).sumOf { it.id }

private fun fitsToBag(game: Game): Boolean {
    val bagColors = Colors(red = 12, green = 13, blue = 14)
    return game.turns.all{ colors -> colors in bagColors }
}

private fun part2(input: List<Game>): Int = input.map(::powerOfGame).sum()

private fun powerOfGame(game: Game): Int {
    var maxRed = 0
    var maxGreen = 0
    var maxBlue = 0

    for (turn in game.turns) {
        maxRed = maxOf(turn.red, maxRed)
        maxGreen = maxOf(turn.green, maxGreen)
        maxBlue = maxOf(turn.blue, maxBlue)
    }
    return maxRed * maxGreen * maxBlue
}

private fun readInput(name: String): List<Game> {
    return readLines(name).map { line ->
        val (rawId, rawTurns) = line.split(": ")
        val turns = rawTurns.split("; ")
            .map {
                val colors = it.split(", ")
                    .map { it.split(" ") }
                    .associate { (count, color) -> color to count.toInt() }
                Colors(
                    red = colors.getOrDefault("red", 0),
                    green = colors.getOrDefault("green", 0),
                    blue = colors.getOrDefault("blue", 0)
                )
            }

        Game(
            id = rawId.substringAfter(" ").toInt(),
            turns = turns,
        )
    }
}

private data class Game(
    val id: Int,
    val turns: List<Colors>
)

private data class Colors(val red: Int, val green: Int, val blue: Int) {

    operator fun contains(other: Colors): Boolean {
        return other.red <= red && other.green <= green && other.blue <= blue
    }
}
