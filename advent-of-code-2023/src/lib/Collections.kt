package lib

fun <T, K> Iterable<T>.countDistinctBy(selector: (T) -> K): Int {
    val set = HashSet<K>()
    for (value in this) set.add(selector(value))
    return set.size
}

fun <T> List<T>.repeat(times: Int): List<T> = List(size * times) { get(it % size) }

operator fun <E> List<E>.component6() = get(5)
