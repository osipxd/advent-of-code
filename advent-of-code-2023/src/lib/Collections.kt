package lib

fun <T, K> Iterable<T>.countDistinctBy(selector: (T) -> K): Int {
    val set = HashSet<K>()
    for (value in this) set.add(selector(value))
    return set.size
}

fun <T> List<T>.repeat(times: Int): List<T> = List(size * times) { get(it % size) }

/** Returns sequence of all possible two-elements combinations. */
fun <T> List<T>.combinations(): Sequence<Pair<T, T>> = sequence {
    for (i in 0..<lastIndex) {
        for (j in (i + 1)..lastIndex) {
            yield(get(i) to get(j))
        }
    }
}

operator fun <E> List<E>.component6() = get(5)
