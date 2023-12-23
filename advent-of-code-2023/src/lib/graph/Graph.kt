package lib.graph

class Graph<T> : Iterable<GraphNode<T>> {
    val roots: List<GraphNode<T>>
        get() = nodes.values.filter { it.isRoot }

    private val nodes = mutableMapOf<T, GraphNode<T>>()

    fun add(value: T) {
        add(GraphNode((value)))
    }

    fun add(node: GraphNode<T>) {
        nodes[node.value] = node
    }

    operator fun plusAssign(node: GraphNode<T>) {
        add(node)
    }

    fun getOrCreate(value: T): GraphNode<T> = nodes.getOrPut(value) { GraphNode(value) }

    operator fun contains(value: T): Boolean = value in nodes
    operator fun contains(node: GraphNode<T>): Boolean = node.value in nodes

    override fun iterator(): Iterator<GraphNode<T>> = nodes.values.iterator()
}

class GraphNode<T>(
    val value: T,
) {
    private val _previous = mutableMapOf<GraphNode<T>, Int>()
    val previous: Map<GraphNode<T>, Int>
        get() = _previous

    private val _next = mutableMapOf<GraphNode<T>, Int>()
    val next: Map<GraphNode<T>, Int>
        get() = _next

    val isRoot: Boolean get() = _previous.isEmpty()

    fun previousNodes(): Sequence<GraphNode<T>> = _previous.keys.asSequence()
    fun nextNodes(): Sequence<GraphNode<T>> = next.keys.asSequence()

    override fun toString(): String = "GraphNode($value)"

    companion object {
        fun <T> GraphNode<T>.connectToNext(next: GraphNode<T>, weight: Int = 0) {
            this._next[next] = weight
            next._previous[this] = weight
        }
    }
}
