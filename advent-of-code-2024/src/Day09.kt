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

    var position = 0
    var forwardCursor = 0
    var backwardCursor = files.lastIndex
    var pendingFileSize = 0
    var checksum = 0L

    fun placeFile(fileId: Int, fileSize: Int) {
        checksum += checksumOf(position, fileId, fileSize)
        position += fileSize
    }

    while (forwardCursor < backwardCursor) {
        // 1. Place the file at forward cursor position
        placeFile(fileId = forwardCursor, fileSize = files[forwardCursor])

        // 2. Fill the following free space with files from the end
        // If a file doesn't fit completely:
        // - Place what fits
        // - Store remaining size in pendingFileSize for next iteration
        // - Keep the same backwardCursor until full file is placed
        var freeSpace = freeSpaces[forwardCursor]
        while (freeSpace > 0 && forwardCursor < backwardCursor) {
            val moveFileSize = if (pendingFileSize != 0) pendingFileSize else files[backwardCursor]
            freeSpace -= moveFileSize
            // Negative freeSpace means the file didn't fit completely
            pendingFileSize = (-freeSpace).coerceAtLeast(0)
            placeFile(fileId = backwardCursor, fileSize = moveFileSize - pendingFileSize)

            if (pendingFileSize == 0) backwardCursor--
        }

        // 3. Advance to next file and its following free space
        forwardCursor++
    }

    // Handle any remaining partial file that didn't fit in the last free space
    if (pendingFileSize > 0) placeFile(fileId = backwardCursor, fileSize = pendingFileSize)

    return checksum
}

private fun part2(input: Pair<List<Int>, List<Int>>): Long {
    val (files, freeSpaces) = input

    // 1. Build initial arrangement:
    // - calculate starting positions for each file
    // - collect ranges representing available free spaces between files
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

    // 2. Define optimized file rearrangement logic.
    // Track the leftmost viable position for each possible file size (0..9).
    // In fact we use 10 forward cursors, one for each file size.
    val sizeToForwardCursor = IntArray(MAX_FILE_SIZE + 1) { 0 }

    fun updateCursors(fileSize: Int, freeSpaceIndex: Int) {
        for (size in fileSize..MAX_FILE_SIZE) {
            if (sizeToForwardCursor[size] < freeSpaceIndex) sizeToForwardCursor[size] = freeSpaceIndex else break
        }
    }

    fun findPlaceForFile(backwardCursor: Int): Int {
        val fileSize = files[backwardCursor]

        val forwardCursor = sizeToForwardCursor[fileSize]
        val freeSpaceIndex = (forwardCursor..backwardCursor)
            .find { index -> freeSpaceRanges[index].size >= fileSize }

        return if (freeSpaceIndex != null) {
            updateCursors(fileSize, freeSpaceIndex)
            val range = freeSpaceRanges[freeSpaceIndex]
            freeSpaceRanges[freeSpaceIndex] = range moveStartBy fileSize
            range.start
        } else {
            updateCursors(fileSize, Int.MAX_VALUE) // Skip future searches for this size
            filesIndexes[backwardCursor]
        }
    }

    // 3. Process files from right to left, finding optimal positions and calculating checksum
    return files.indices.reversed().sumOf { backwardCursor ->
        checksumOf(findPlaceForFile(backwardCursor), fileId = backwardCursor, fileSize = files[backwardCursor])
    }
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
