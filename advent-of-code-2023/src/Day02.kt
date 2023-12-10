private const val DAY = "Day02"

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

private fun part1(input: List<Game>): Int {
    val bagColors = Colors(red = 12, green = 13, blue = 14)
    return input
        .filter { it.turns.all { colors -> colors in bagColors } }
        .sumOf { it.id }
}

private fun part2(input: List<Game>): Int {
    return input.sumOf {
        val minColors = Colors(red = 0, green = 0, blue = 0)
        for (colors in it.turns) minColors.extend(colors)
        minColors.power
    }
}

private fun readInput(name: String): List<Game> = readLines(name).map { line ->
    val (rawId, rawTurns) = line.split(": ")
    Game(
        id = rawId.substringAfter(" ").toInt(),
        turns = rawTurns.split("; ").map(::parseColors),
    )
}

private fun parseColors(line: String): Colors {
    val colors = line.split(", ")
        .map { it.split(" ") }
        .associate { (count, color) -> color to count.toInt() }
    return Colors(
        red = colors.getOrDefault("red", 0),
        green = colors.getOrDefault("green", 0),
        blue = colors.getOrDefault("blue", 0),
    )
}

private data class Game(
    val id: Int,
    val turns: List<Colors>
)

@JvmInline
private value class Colors(private val colors: Array<Int>) {

    var red: Int
        get() = colors[0]
        set(value) {
            colors[0] = value
        }
    var green: Int
        get() = colors[1]
        set(value) {
            colors[1] = value
        }
    var blue: Int
        get() = colors[2]
        set(value) {
            colors[2] = value
        }

    val power: Int get() = red * green * blue

    constructor(red: Int, green: Int, blue: Int) : this(arrayOf(red, green, blue))

    operator fun contains(other: Colors): Boolean {
        return other.red <= red && other.green <= green && other.blue <= blue
    }

    fun extend(colorsToFit: Colors) {
        red = maxOf(red, colorsToFit.red)
        green = maxOf(green, colorsToFit.green)
        blue = maxOf(blue, colorsToFit.blue)
    }
}
