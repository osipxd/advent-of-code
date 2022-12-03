import scala.io.Source
import util.chaining.scalaUtilChainingOps

@main def Day03(): Unit =
  // Part 1
  input().map(line => line.grouped(line.length / 2).toSeq).pipe(sumOfPriorities).pipe(println)

  // Part 2
  input().grouped(3).pipe(sumOfPriorities).pipe(println)

def input() = Source.fromFile("Day03.txt").getLines

def sumOfPriorities(input: Iterator[Iterable[String]]) = input
    .map(findIntersection)
    .map(getPriority)
    .sum

def findIntersection(words: Iterable[String]) = words
    .map(_.toSet)
    .reduce(_ intersect _)
    .head

def getPriority(c: Char) = c match
  case it if 'a' to 'z' contains it => c - 'a' + 1
  case it if 'A' to 'Z' contains it => c - 'A' + 27
