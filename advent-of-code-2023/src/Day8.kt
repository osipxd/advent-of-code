private const val DAY = "Day8"

fun main() {
    fun input() = readInput(DAY)

    "Part 1" {
        fun testInput() = readInput("${DAY}_test")

        part1(testInput()) shouldBe 2
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        fun testInput() = readInput("${DAY}_test2")

        part2(testInput()) shouldBe 6
        measureAnswer { part2(input()) }
    }
}

private fun part1(input: DessertMap): Int {
    var steps = 0
    var currentNode = "AAA"
    while (currentNode != "ZZZ") {
        steps++
        val node = input.nodes.getValue(currentNode)
        currentNode = when (input.getInstruction(steps)) {
            'L' -> node.ifLeft
            'R' -> node.ifRight
            else -> error("Unexpected instruction")
        }
    }

    return steps
}

private fun part2(input: DessertMap): Long {
    val startNodes = input.nodes.keys.filter { it.endsWith("A") }
    val cycles = startNodes.map { startNode ->
        var currentNode = startNode
        var steps = 0L

        while (!currentNode.endsWith("Z")) {
            steps++
            val node = input.nodes.getValue(currentNode)
            currentNode = when (input.getInstruction(steps)) {
                'L' -> node.ifLeft
                'R' -> node.ifRight
                else -> error("Unexpected instruction")
            }
        }
        steps
    }

    val maxCycle = cycles.max()
    var steps = maxCycle
    while (cycles.any { steps % it != 0L }) steps += maxCycle

    return steps
}

private fun readInput(name: String): DessertMap {
    val (instructions, rawNodes) = readText(name).split("\n\n")
    return DessertMap(
        instructions = instructions,
        nodes = rawNodes.lineSequence().associate { line ->
            val (nodeId, directions) = line.split(" = ")
            val (ifLeft, ifRight) = directions.trim('(', ')').split(", ")
            nodeId to MapNode(ifLeft, ifRight)
        },
    )
}

private data class DessertMap(
    val instructions: String,
    val nodes: Map<String, MapNode>
) {
    fun getInstruction(step: Int) = instructions[(step - 1) % instructions.length]
    fun getInstruction(step: Long) = instructions[((step - 1) % instructions.length).toInt()]
}

private data class MapNode(
    val ifLeft: String,
    val ifRight: String,
)
