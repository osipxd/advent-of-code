fun main() {
    val testInput = readLines("Day07_test")
    val input = readLines("Day07")

    "Part 1" {
        part1(testInput) shouldBe 95437
        measureAnswer { part1(input) }
    }

    "Part 2" {
        part2(testInput) shouldBe 24933642
        measureAnswer { part2(input) }
    }
}

private fun part1(input: List<String>): Int {
    val dirs = inflateDirs(input).values

    return dirs.asSequence()
        .filter { it.size <= 100_000 }
        .sumOf { it.size }
}

private fun part2(input: List<String>): Int {
    val dirs = inflateDirs(input)

    val totalSpace = 70_000_000
    val freeSpace = totalSpace - dirs.getValue("/").size
    val neededSpace = 30_000_000 - freeSpace

    var minimalToDelete = totalSpace
    for (dir in dirs.values) {
        if (dir.size in (neededSpace + 1) until minimalToDelete) minimalToDelete = dir.size
    }

    return minimalToDelete
}

private fun inflateDirs(input: List<String>): Map<String, Dir> {
    val rootDir = Dir("/")
    val dirs = mutableMapOf("/" to rootDir)
    val stack = ArrayDeque<Dir>()

    fun addDir(path: String) {
        dirs[path] = Dir(path)
    }

    fun goBack() {
        val currentDir = stack.removeFirst()
        if (stack.isNotEmpty()) stack.first().size += currentDir.size
    }

    for (line in input) {
        val parts = line.split(" ")
        val cwd = stack.firstOrNull()?.path.orEmpty()

        when (parts[0]) {
            "$" -> when (parts[1]) {
                "cd" -> when (parts[2]) {
                    ".." -> goBack()
                    "/" -> stack.addFirst(rootDir)
                    else -> stack.addFirst(dirs.getValue("$cwd/${parts[2]}"))
                }
            }

            "dir" -> addDir("$cwd/${parts[1]}")
            else -> stack.first().size += parts[0].toInt()
        }
    }

    while (stack.isNotEmpty()) {
        goBack()
    }

    return dirs
}

private class Dir(val path: String, var size: Int = 0)
