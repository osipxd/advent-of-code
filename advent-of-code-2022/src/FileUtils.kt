import java.io.File

fun readText(name: String) = file(name).readText()
fun readLines(name: String) = file(name).readLines()

private fun file(name: String) = File("src", "$name.txt")
