import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.io.path.readText

fun readText(name: String) = path(name).readText()
fun readLines(name: String) = path(name).readLines()

private fun path(name: String) = Path("src", "$name.txt")

/** Splits [this] string and converts each part to [Int]. */
fun String.splitInts(delimiter: String = ","): List<Int> = split(delimiter).map(String::toInt)

/** Takes two elements from [this] list and creates [Pair] of it. */
fun <T> List<T>.takePair(): Pair<T, T> = get(0) to get(1)
