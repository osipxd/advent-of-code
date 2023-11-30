/*

Hints I've used to solve the puzzle:

1. We can map letter to a digit:

   choice = letter - 'A'
   // A -> 0, B -> 1, C -> 2 (the same logic may be applied to X, Y, Z)

   choiceScore = choice + 1

2. Order of symbols (Rock, Paper, Scissors) allows us to quickly understand round outcome.
   Next symbol always beats the current - Paper beats Rock, Scissors beats Paper and Rock beats Scissors.
   So using the previous hint we can use indices to calculate outcome:

   elfChoice = elfLetter - 'A'
   myChoice = myLetter - 'X'

   outcome = when {
       elfChoice == (myChoice + 1) % 3 -> 0 (lose)
       elfChoice == myChoice           -> 1 (draw)
       myChoice == (elfChoice + 1) % 3 -> 2 (win)
   }

   // Or by formula
   outcome = ((myChoice - elfChoice) % 3 + 1) % 3

   outcomeScore = outcome * 3 // loss -> 0, draw -> 3, win -> 6

3. We can use desired outcome and Elf's choice to restore our choice:

   choiceDiff = outcome - 1
   // loss -> -1, draw -> 0, win -> 1

   myChoice = (elfChoice + choiceDiff) % 3
*/
fun main() {

    // Convert lines to pairs of digits in range 0..2
    fun readInput(name: String) = readLines(name).map { it[0] - 'A' to it[2] - 'X' }

    fun part1(input: List<Pair<Int, Int>>): Int {
        return input.sumOf { (elf, me) -> (me + 1) + ((me - elf).mod(3) + 1) % 3 * 3 }

        // Readable version:
        // var score = 0
        // for ((elfChoice, myChoice) in input) {
        //     val choiceScore = myChoice + 1
        //     val outcome = ((myChoice - elfChoice).mod(3) + 1) % 3
        //     score += choiceScore + outcome * 3
        // }
        // return score
    }

    fun part2(input: List<Pair<Int, Int>>): Int {
        return input.sumOf { (elf, outcome) -> outcome * 3 + (elf + outcome).mod(3) + 1 }

        // Readable version:
        // var score = 0
        // for ((elfChoice, outcome) in input) {
        //     val myChoice = (elfChoice + outcome - 1).mod(3)
        //     val choiceScore = myChoice + 1
        //     val outcomeScore = outcome * 3
        //     score += outcomeScore + choiceScore
        // }
        // return score
    }

    val testInput = readInput("Day02_test")
    check(part1(testInput) == 15)

    val input = readInput("Day02")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
