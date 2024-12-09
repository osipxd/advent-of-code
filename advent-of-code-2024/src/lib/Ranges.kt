package lib

val ClosedRange<Int>.size: Int get() = endInclusive - start + 1

infix fun Int.rangeOfSize(size: Int): IntRange {
    check(size >= 0) { "Size must be non-negative" }
    return this..<(this + size)
}

infix fun IntRange.moveStartBy(diff: Int) = (start + diff)..endInclusive
