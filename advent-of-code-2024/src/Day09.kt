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

    // 1. Build initial arrangement: calculate starting positions for each file
    // and collect ranges representing available free spaces between files
    val filesIndexes = IntArray(files.size)
    val freeSpaceRanges = ArrayList<IntRange>(freeSpaces.size)

    var currentIndex = 0
    for (cursor in files.indices) {
        filesIndexes[cursor] = currentIndex
        currentIndex += files[cursor]
        if (cursor <= freeSpaces.lastIndex) {
            freeSpaceRanges.add(currentIndex rangeOfSize freeSpaces[cursor])
            currentIndex += freeSpaces[cursor]
        }
    }

    // 2. Define optimized file rearrangement logic using caching.
    // Cache stores the leftmost viable position for each possible file size (0..9).
    // This lets us skip searching positions that we already know are too small.

    // Maps file size (0..9) to the leftmost index where a suitable free space was found
    val sizeToStartIndex = IntArray(MAX_FILE_SIZE + 1) { 0 }

    fun updateCache(fileSize: Int, freeSpaceIndex: Int) {
        for (size in fileSize..MAX_FILE_SIZE) {
            if (sizeToStartIndex[size] < freeSpaceIndex) sizeToStartIndex[size] = freeSpaceIndex else break
        }
    }

    fun findPlaceForFile(fileId: Int): Int {
        val fileSize = files[fileId]

        val startIndex = sizeToStartIndex[fileSize]
        val freeSpaceIndex = (startIndex..fileId).find { index -> freeSpaceRanges[index].size >= fileSize }

        return if (freeSpaceIndex != null) {
            updateCache(fileSize, freeSpaceIndex)
            val range = freeSpaceRanges[freeSpaceIndex]
            freeSpaceRanges[freeSpaceIndex] = range moveStartBy fileSize
            range.start
        } else {
            updateCache(fileSize, Int.MAX_VALUE) // Don't search free space anymore for this size
            filesIndexes[fileId]
        }
    }

    // 3. Process files from right to left, finding optimal positions and calculating checksum
    return files.indices.reversed()
        .sumOf { fileId -> checksumOf(findPlaceForFile(fileId), fileId = fileId, fileSize = files[fileId]) }
}

private const val MAX_FILE_SIZE = 9

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
