import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.io.path.readText

fun readText(name: String) = path(name).readText()
fun readLines(name: String) = path(name).readLines()
fun <T> readLines(name: String, transform: (String) -> T) = path(name).readLines().map(transform)

private fun path(name: String) = Path("src", "$name.txt")

/** Splits [this] string and converts each part to [Int]. */
fun String.splitInts(vararg delimiters: String = arrayOf(" ", ",", ", ")): List<Int> =
    split(*delimiters).map(String::toInt)

/** Splits [this] string and converts each part to [Long]. */
fun String.splitLongs(vararg delimiters: String = arrayOf(" ", ",", ", ")): List<Long> =
    split(*delimiters).map(String::toLong)

/** Takes two elements from [this] list and creates [Pair] of it. */
fun <T> List<T>.takePair(): Pair<T, T> = get(0) to get(1)

fun <T> Pair<T, T>.map(transform: (T) -> T): Pair<T, T> = transform(first) to transform(second)

/** More convenient way to print test values. */
fun <T> T.printValue(): T = also(::println)
