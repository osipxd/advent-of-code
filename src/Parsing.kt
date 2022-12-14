
/** Splits [this] string and converts each part to [Int]. */
fun String.splitInts(delimiter: String = ","): List<Int> = split(delimiter).map(String::toInt)

/** Takes two elements from [this] list and creates [Pair] of it. */
fun <T> List<T>.takePair(): Pair<T, T> = get(0) to get(1)
