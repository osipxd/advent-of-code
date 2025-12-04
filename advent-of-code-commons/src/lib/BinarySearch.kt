package lib

/**
 * Implementation of `binarySearch` for [IntRange], but returning `SearchResult` instead of `Int`.
 * @see List.binarySearch
 */
fun IntRange.binarySearch(comparison: (Int) -> Int): SearchResult {
    var low = 0
    var high = size
    IntRange.EMPTY

    while (low <= high) {
        val mid = (low + high).ushr(1) // safe from overflows
        val cmp = comparison(mid)

        if (cmp < 0)
            low = mid + 1
        else if (cmp > 0)
            high = mid - 1
        else
            return SearchResult.found(mid)
    }
    return SearchResult.notFound(low)
}

@JvmInline
value class SearchResult(private val result: Int) {

    val insertionIndex: Int
        get() = if (result >= 0) result else -result - 1

    companion object {
        fun found(index: Int) = SearchResult(index)
        fun notFound(index: Int) = SearchResult(-(index + 1))
    }
}
