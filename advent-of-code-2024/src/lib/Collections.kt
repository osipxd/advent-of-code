package lib

/** Returns a sequence of all possible two-elements combinations. */
fun <T> List<T>.pairCombinations(): Sequence<Pair<T, T>> = sequence {
    for (i in indices) for (j in (i + 1)..lastIndex) yield(get(i) to get(j))
}
