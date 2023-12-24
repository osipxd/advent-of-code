import lib.component6
import lib.graph.Graph
import lib.graph.GraphNode
import lib.graph.GraphNode.Companion.connectToNext
import kotlin.math.abs
import kotlin.math.min

private const val DAY = "Day22"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 5
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 7
        measureAnswer { part2(input()) }
    }
}

private fun part1(input: List<SandBrick>): Int {
    val graph = buildGraph(input)
    return graph.count { node -> node.nextNodes().none { it.previousNodes().count() == 1 } }
}

private fun part2(input: List<SandBrick>): Int {
    val graph = buildGraph(input)

    return graph.sumOf { startNode ->
        val queue = ArrayDeque<GraphNode<SandBrick>>()
        val visited = mutableSetOf<GraphNode<SandBrick>>()

        fun addNextNode(nextNode: GraphNode<SandBrick>) {
            if (visited.add(nextNode)) queue.addLast(nextNode)
        }

        addNextNode(startNode)
        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            node.nextNodes()
                .filter { nextNode -> nextNode.previousNodes().all { it in visited } }
                .forEach(::addNextNode)
        }
        visited.size - 1
    }
}

private fun buildGraph(bricks: List<SandBrick>): Graph<SandBrick> {
    val graph = Graph<SandBrick>()
    val topBricks = mutableMapOf<XY, GraphNode<SandBrick>>()

    for (brick in bricks.sortedBy { it.z1 }) {
        val node = GraphNode(brick)

        val xyPoints = brick.xyPoints()
        val previousBricks = mutableSetOf<GraphNode<SandBrick>>()
        var topZ = 0
        for (previousBrick in xyPoints.mapNotNull { topBricks[it] }) {
            val currentZ = previousBrick.value.z2
            if (currentZ > topZ) {
                topZ = currentZ
                previousBricks.clear()
            }
            if (currentZ == topZ) previousBricks += previousBrick
        }
        node.value.z1 = topZ + 1
        previousBricks.forEach { it.connectToNext(node) }
        graph += node

        for (xy in xyPoints) topBricks[xy] = node
    }

    return graph
}

private fun readInput(name: String) = readLines(name) { line ->
    val (x1, y1, z1, x2, y2, z2) = line.splitInts(",", "~")
    SandBrick(x1, y1, z1, x2, y2, z2)
}

private class SandBrick(
    x1: Int,
    y1: Int,
    z1: Int,
    x2: Int,
    y2: Int,
    z2: Int,
) {
    val xRange = x1..x2
    val yRange = y1..y2

    val height = abs(z2 - z1) + 1
    var z1 = min(z1, z2)
    val z2 get() = z1 + height - 1

    fun xyPoints(): List<XY> = xRange.flatMap { x -> yRange.map { y -> XY(x, y) } }
}

private data class XY(val x: Int, val y: Int)
