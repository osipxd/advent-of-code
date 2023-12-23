import lib.graph.Graph
import lib.graph.GraphNode
import lib.graph.GraphNode.Companion.connectToNext
import lib.graph.walkFromRoots
import lib.matrix.*
import lib.matrix.Direction.*
import lib.matrix.Direction.Companion.nextInDirection
import kotlin.math.max

private const val DAY = "Day23"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 94
        measureAnswer { part1(input()) }
    }

    //"Part 2" {
    //    part2(testInput()) shouldBe 0
    //    measureAnswer { part2(input()) }
    //}
}

private fun part1(input: Matrix<Char>): Int {
    val graph = buildGraph(input)
    val longestPath = mutableMapOf<Position, Int>()

    graph.walkFromRoots { node ->
        val currentPath = longestPath.getOrDefault(node.value, defaultValue = 0)
        for ((nextNode, path) in node.next) {
            val currentPathToNext = longestPath.getOrDefault(nextNode.value, defaultValue = 0)
            longestPath[nextNode.value] = max(currentPathToNext, currentPath + path)
        }
    }

    val endPosition = input.bottomRightPosition.offsetBy(column = -1)
    return longestPath.getValue(endPosition)
}

private fun part2(input: Matrix<Char>): Int = TODO()

private fun buildGraph(input: Matrix<Char>): Graph<Position> {
    val graph = Graph<Position>()
    val nodesToRoute = ArrayDeque<GraphNode<Position>>()

    fun nextNode(position: Position): GraphNode<Position> {
        val newNode = position !in graph
        return graph.getOrCreate(position).also {
            if (newNode) nodesToRoute.addLast(it)
        }
    }

    fun calculateRoutes(node: GraphNode<Position>) {
        val queue = ArrayDeque<Pair<Int, Position>>()
        val seen = mutableSetOf(node.value)

        fun addNextStep(step: Int, position: Position) {
            if (position in seen) return
            if (input.isNodeAt(position)) {
                val nextNode = nextNode(position)
                node.connectToNext(nextNode, step)
            } else {
                seen += position
                queue.addFirst(step to position)
            }
        }
        input.findWays(node.value).forEach { addNextStep(step = 1, it) }

        while (queue.isNotEmpty()) {
            val (step, position) = queue.removeFirst()
            input.findWays(position)
                .forEach { addNextStep(step + 1, it) }
        }
    }

    nextNode(Position(row = 0, column = 1))
    graph.add(input.bottomRightPosition.offsetBy(column = -1))

    while (nodesToRoute.isNotEmpty()) {
        calculateRoutes(nodesToRoute.removeFirst())
    }

    return graph
}

private fun Matrix<Char>.isNodeAt(position: Position): Boolean {
    if (position.row == 0 || position.row == lastRowIndex) return true
    return position.neighbors().count { this[it] == '#' } < 2
}

private fun Matrix<Char>.findWays(position: Position): List<Position> = buildList {
    val matrix = this@findWays
    fun checkAndAdd(position: Position, values: String) {
        if (position in matrix && matrix[position] in values) add(position)
    }

    checkAndAdd(position.nextInDirection(UP), ".^")
    checkAndAdd(position.nextInDirection(LEFT), ".<")
    checkAndAdd(position.nextInDirection(DOWN), ".v")
    checkAndAdd(position.nextInDirection(RIGHT), ".>")
}

private fun readInput(name: String) = readMatrix(name)
