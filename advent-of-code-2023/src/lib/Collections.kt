package lib

fun <T, K> Iterable<T>.countDistinctBy(selector: (T) -> K): Int {
    val set = HashSet<K>()
    for (value in this) set.add(selector(value))
    return set.size
}
