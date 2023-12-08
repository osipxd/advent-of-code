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

private fun part1(input: DessertMap): Int = input.countSteps(fromNode = "AAA") { it == "ZZZ" }

private fun part2(input: DessertMap): Long {
    val instructionsCount = input.instructions.length
    // Assumption is that we can reach each end node in a whole number of instructions cycles.
    // And number of cycles for each node is a prime number.
    return input.nodes.keys.filter { it.endsWith("A") }
        .map { startNode ->
            var stepsUntilEnd = input.countSteps(startNode) { it.endsWith("Z") }
            // This IF is needed only for test input, where one of paths takes 1.5 cycles
            if (stepsUntilEnd % instructionsCount != 0) stepsUntilEnd *= 2
            (stepsUntilEnd / instructionsCount).toLong()
        }
        .reduce(Long::times) * instructionsCount
}

private fun readInput(name: String): DessertMap {
    val (instructions, rawNodes) = readText(name).split("\n\n")
    return DessertMap(
        instructions = instructions,
        nodes = rawNodes.lineSequence().associate { line ->
            val (nodeId, directions) = line.split(" = ")
            val (leftNode, rightNode) = directions.trim('(', ')').split(", ")
            nodeId to MapNode(leftNode, rightNode)
        },
    )
}

private data class DessertMap(
    val instructions: String,
    val nodes: Map<String, MapNode>
) {

    fun countSteps(fromNode: String, predicate: (String) -> Boolean): Int {
        var steps = 0
        var currentNode = fromNode
        fun instruction() = instructions[(steps - 1) % instructions.length]

        while (!predicate(currentNode)) {
            steps++
            currentNode = nodes.getValue(currentNode).nextNode(instruction())
        }

        return steps
    }
}

private data class MapNode(val leftNode: String, val rightNode: String) {

    fun nextNode(instruction: Char) = when (instruction) {
        'L' -> leftNode
        'R' -> rightNode
        else -> error("Unexpected instruction: $instruction")
    }
}
