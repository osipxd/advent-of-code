import lib.graph.Graph
import lib.graph.GraphNode
import lib.graph.GraphNode.Companion.connectToNext
import lib.matrix.*
import lib.matrix.Direction.*
import lib.matrix.Direction.Companion.nextInDirection

private const val DAY = "Day23"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 94
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 154
        measureAnswer { part2(input()) }
    }
}

private fun part1(input: Matrix<Char>) = findLongestPath(input, twoWay = false)
private fun part2(input: Matrix<Char>) = findLongestPath(input, twoWay = true)

private fun findLongestPath(input: Matrix<Char>, twoWay: Boolean): Int {
    val graph = buildGraph(input, twoWay)
    val endPosition = input.bottomRightPosition.offsetBy(column = -1)

    fun longestPath(node: GraphNode<Position>, seen: Set<Position>): Int {
        if (node.value == endPosition) return 0
        val nextNodes = node.next
            .filter { (node, _) -> node.value !in seen }
            .ifEmpty { return -1 }

        val seenWithThis = seen + node.value
        return nextNodes.maxOf { (nextNode, pathToNode) ->
            val pathFromNode = longestPath(nextNode, seenWithThis)
            if (pathFromNode == -1) -1 else pathToNode + pathFromNode
        }
    }

    return longestPath(graph.roots.single(), emptySet())
}

private fun buildGraph(input: Matrix<Char>, twoWay: Boolean): Graph<Position> {
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
                if (twoWay && !node.isRoot) nextNode.connectToNext(node, step)
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
