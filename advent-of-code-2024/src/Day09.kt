import lib.moveStartBy
import lib.rangeOfSize
import lib.size

private const val DAY = "Day09"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 1928
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 2858
        measureAnswer { part2(input()) }
    }
}

private fun part1(input: Pair<List<Int>, List<Int>>): Long {
    val (files, freeSpaces) = input

    var index = 0
    var cursor = 0
    var moveFileCursor = files.lastIndex
    var remainingFile = 0
    var checksum = 0L

    fun placeFile(fileId: Int, fileSize: Int) {
        checksum += checksumOf(index, fileId, fileSize)
        index += fileSize
    }

    while (cursor < moveFileCursor) {
        // 1. Place the current file
        placeFile(fileId = cursor, fileSize = files[cursor])

        // 2. Move files from the end of the list to the free space after the current file
        var freeSpace = freeSpaces[cursor]
        while (freeSpace > 0 && cursor < moveFileCursor) {
            val moveFileSize = if (remainingFile == 0) files[moveFileCursor] else remainingFile
            freeSpace -= moveFileSize
            remainingFile = (-freeSpace).coerceAtLeast(0)
            placeFile(fileId = moveFileCursor, fileSize = moveFileSize - remainingFile)

            if (remainingFile == 0) moveFileCursor--
        }

        // 3. Move to the next file and free space
        cursor++
    }
    if (remainingFile > 0) placeFile(fileId = moveFileCursor, fileSize = remainingFile)

    return checksum
}

private fun part2(input: Pair<List<Int>, List<Int>>): Long {
    val (files, freeSpaces) = input

    // I. Precalculate indexes of files and free spaces
    val filesIndexes = mutableMapOf<Int, Int>()
    val freeSpaceRanges = mutableListOf<IntRange>()

    var index = 0
    for (cursor in files.indices) {
        filesIndexes[cursor] = index
        index += files[cursor]
        if (cursor <= freeSpaces.lastIndex) {
            freeSpaceRanges.add(index rangeOfSize freeSpaces[cursor])
            index += freeSpaces[cursor]
        }
    }

    // II. Rearrange files and calculate checksum
    var checksum = 0L

    for (cursor in files.indices.reversed()) {
        val fileSize = files[cursor]
        val freeSpaceIndex = freeSpaceRanges.asSequence()
            .take(cursor)
            .indexOfFirst { it.size >= fileSize }

        val fileStartIndex = if (freeSpaceIndex == -1) {
            filesIndexes.getValue(cursor)
        } else {
            val range = freeSpaceRanges[freeSpaceIndex]
            freeSpaceRanges[freeSpaceIndex] = range moveStartBy fileSize
            range.start
        }

        checksum += checksumOf(fileStartIndex, fileId = cursor, fileSize = fileSize)
    }

    return checksum
}

private fun checksumOf(startIndex: Int, fileId: Int, fileSize: Int): Long {
    return (startIndex rangeOfSize fileSize).sumOf { index -> (index * fileId).toLong() }
}

private fun readInput(name: String) = readText(name)
    .map(Char::digitToInt)
    .splitByIndexParity()

// Utils

private fun <T> List<T>.splitByIndexParity(): Pair<List<T>, List<T>> {
    val first = ArrayList<T>((size + 1) / 2)
    val second = ArrayList<T>((size + 1) / 2)
    for ((index, element) in this.withIndex()) {
        if (index % 2 == 0) {
            first.add(element)
        } else {
            second.add(element)
        }
    }
    return Pair(first, second)
}
