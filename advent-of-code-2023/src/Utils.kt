import lib.matrix.Matrix
import lib.matrix.toMatrix
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.io.path.readText

fun readMatrix(fileName: String): Matrix<Char> = readMatrix(fileName) { line -> line.map { it } }
fun <T> readMatrix(fileName: String, lineElements: (String) -> List<T>): Matrix<T> =
    readLines(fileName, lineElements).toMatrix()

fun readText(name: String) = path(name).readText()
fun readLines(name: String) = path(name).readLines()
fun <T> readLines(name: String, transform: (String) -> T) = path(name).readLines().map(transform)

private fun path(name: String) = Path("src", "$name.txt")

/** Splits [this] string and converts each part to [Int]. */
fun String.splitInts(vararg delimiters: String = arrayOf(" ", ",", ", ")): List<Int> =
    split(*delimiters).map(String::toInt)

/** Takes two elements from [this] list and creates [Pair] of it. */
fun <T> List<T>.takePair(): Pair<T, T> = get(0) to get(1)

/** More convenient way to print test values. */
fun <T> T.printValue(): T = also(::println)
