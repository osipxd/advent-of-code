private const val DAY = "Day8"

fun main() {
    fun input() = readInput(DAY)

    "Part 1" {
        fun testInput0() = readInput("${DAY}_test0")
        fun testInput1() = readInput("${DAY}_test1")

        part1(testInput0()) shouldBe 2
        part1(testInput1()) shouldBe 6
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        fun testInput() = readInput("${DAY}_test2")

        part2(testInput()) shouldBe 6
        measureAnswer { part2(input()) }
    }
}

private fun part1(input: DessertMap): Int = input.countCycles(fromNode = "AAA") { it == "ZZZ" } * input.stepsInCycle

private fun part2(input: DessertMap): Long {
    // Assumption is that number of cycles for each node is a prime number.
    return input.nodes.keys.filter { it.endsWith("A") }
        .map { startNode -> input.countCycles(startNode) { it.endsWith("Z") }.toLong() }
        .reduce(Long::times) * input.stepsInCycle
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

    val stepsInCycle = instructions.length

    fun countCycles(fromNode: String, predicate: (String) -> Boolean): Int {
        var steps = 0
        var currentNode = fromNode
        fun instruction() = instructions[(steps - 1) % stepsInCycle]

        while (steps % stepsInCycle != 0 || !predicate(currentNode)) {
            steps++
            currentNode = nodes.getValue(currentNode).nextNode(instruction())
        }

        return steps / stepsInCycle
    }
}

private data class MapNode(val leftNode: String, val rightNode: String) {

    fun nextNode(instruction: Char) = when (instruction) {
        'L' -> leftNode
        'R' -> rightNode
        else -> error("Unexpected instruction: $instruction")
    }
}
