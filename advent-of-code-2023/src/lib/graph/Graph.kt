package lib.graph

class Graph<T> : Iterable<GraphNode<T>> {
    private val nodes = mutableListOf<GraphNode<T>>()

    fun add(node: GraphNode<T>) {
        nodes += node
    }

    operator fun plusAssign(node: GraphNode<T>) {
        add(node)
    }

    override fun iterator(): Iterator<GraphNode<T>> = nodes.iterator()
}

class GraphNode<T>(
    val value: T,
) {
    private val previous = mutableMapOf<GraphNode<T>, Int>()
    private val next = mutableMapOf<GraphNode<T>, Int>()

    fun previousNodes(): Sequence<GraphNode<T>> = previous.keys.asSequence()
    fun nextNodes(): Sequence<GraphNode<T>> = next.keys.asSequence()

    override fun toString(): String = "GraphNode($value)"

    companion object {
        fun <T> GraphNode<T>.connectToNext(next: GraphNode<T>, weight: Int = 0) {
            this.next[next] = weight
            next.previous[this] = weight
        }
    }
}
