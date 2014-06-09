import scala.collection.immutable.{HashMap, Map}

object PegSolver {
  /** A brute force solver of the peg game from Cracker Barrel.
    *
    * Board layout:
    *
    *  ...
    *  ...      (i-2, j-2) (i-2, j-1) (i-2, j) ...
    *  ...           (i-1, j-1) (i-1, j) ...
    *  ... (i, j-2) (i, j-1) (i, j) (i, j+1) (i, j+2) ...
    *  ...             (i+1, j) (i+1, j+1) ...
    *  ...        (i+2, j) (i+2, j+1) (i+2, j+2) ...
    *  ...
    *
    * Rotations:
    *
    *  0                 10               14
    *  1  2              11 6             9  13
    *  3  4  5           12 7  3          5  8  12
    *  6  7  8  9        13 8  4  1       2  4  7  11
    *  10 11 12 13 14    14 9  5  2  0    0  1  3  6  10
    *
    *  0 1 2 3 4 5 6 7 8 9     6 7 3 8 4 1 9 5 2 0
    *
    * Reflections:
    *
    *  0                 0                 10                14
    *  1  2              2  1              6  11             13 9
    *  3  4  5           5  4  3           3  7  12          12 8  5
    *  6  7  8  9        9  8  7  6        1  4  8  13       11 7  4  2
    *  10 11 12 13 14    14 13 12 11 10    0  2  5  9  14    10 6  3  1  0
    *                        bottom            left              right
    *
    */

  type Peg = Int

  class Board (private[Board] val board: Array[Long], size: Int) {
  
    val length = size*(size+1) / 2

    def this (size: Int, empty: Peg) {
      this(Array.tabulate((size*(size+1) / 2 + 63) / 64)(_ => -1L), size)
      board(board.length - 1) = -1L >>> (64 - ((size*(size+1) / 2) % 64))
      board(empty / 64) = board(empty / 64) & (-1L ^ (1L << (empty % 64)))
    }


    def allMoves (): Map[Peg, List[(Peg, Peg)]] = {

      val indBoard = Array.tabulate(size)(i => Array.tabulate(i+1)(j => i*(i+1) / 2 + j))
      var jumps = new HashMap[Peg, List[(Peg, Peg)]]

      for (i <- 0 until size) {
        for (j <- 0 until i+1) {

          val indices = List(((i-1,j-1), (i-2,j-2)),
                             ((i-1,j), (i-2,j)),
                             ((i,j+1), (i,j+2)),
                             ((i+1,j+1), (i+2,j+2)),
                             ((i+1,j), (i+2,j)),
                             ((i,j-1), (i,j-2)))

          val valid = indices.filter {
            case ((i1,j1), (i2,j2)) => {
              i1 >= 0 && i1 < size &&
              i2 >= 0 && i2 < size &&
              j1 >= 0 && j1 < indBoard(i1).length &&
              j2 >= 0 && j2 < indBoard(i2).length
            }
          }

          val jumpList = valid.map {
            case ((i1,j1), (i2,j2)) => (indBoard(i1)(j1), indBoard(i2)(j2))
          }

          jumps += ((i*(i+1) / 2 + j, jumpList))
        }
      }

      jumps
    }


    def apply (peg: Peg) : Boolean =
      (board(peg / 64) & (1L << (peg % 64))) == (1L << (peg % 64))


    def count () : Int =
      board.map(l => java.lang.Long.bitCount(l)).fold(0)(_ + _)


    override def equals (other: Any) : Boolean = other match {
      case board2: Board => board.sameElements(board2.board)
      case _ => false
    }


    // Piggyback off of String hashing
    override def hashCode : Int =
      board.map(_.toHexString).fold("")(_ + _).hashCode


    def makeMove (move: Move) : Board = {    

      val newBoard = new Board(board.clone, size)

      if (newBoard(move.start) && newBoard(move.skip) && !newBoard(move.end)) {
        newBoard(move.start) = false
        newBoard(move.skip) = false
        newBoard(move.end) = true
      }

      newBoard
    }


    def print () : Unit = board.foreach(println)


    // Reflect the board across an axis perpendicular to the bottom edge
    def reflectBottom () : Board = {
      
      val reflected = new Board(Array.tabulate((size*(size+1) / 2 + 63) / 64)(i => 0L), size)
    
      for (i <- 0 until size) {
        for (j <- 0 to i) {
          reflected(i*(i+1) / 2 + j) = this(i*(i+1) / 2 + i-j)
        }
      }

      reflected
    }

  
    // Reflect the board across an axis perpendicular to the left edge
    def reflectLeft () : Board = {
      
      val reflected = new Board(Array.tabulate((size*(size+1) / 2 + 63) / 64)(i => 0L), size)
    
      for (i <- 0 until size) {
        for (j <- 0 to i) {
          reflected(i*(i+1) / 2 + j) = this((size+j-i-1)*(size+j-i) / 2 + j)
        }
      }

      reflected
    }

  
    // Reflect the board across an axis perpendicular to the right edge
    def reflectRight () : Board = {
      
      val reflected = new Board(Array.tabulate((size*(size+1) / 2 + 63) / 64)(i => 0L), size)
    
      for (i <- 0 until size) {
        for (j <- 0 to i) {
          reflected(i*(i+1) / 2 + j) = this((size-j-1)*(size-j) / 2 + size-i-1)
        }
      }

      reflected
    }

  
    // Rotate the board by 120 degrees CCW
    def rotateCCW () : Board = {
      
      val rotated = new Board(Array.tabulate((size*(size+1) / 2 + 63) / 64)(i => 0L), size)
    
      for (i <- 0 until size) {
        for (j <- 0 to i) {
          rotated(i*(i+1) / 2 + j) = this((size+j-i-1)*(size+j-i) / 2 + size-i-1)
        }
      }

      rotated
    }

  
    // Rotate the board by 120 degrees CW
    def rotateCW () : Board = {
      
      val rotated = new Board(Array.tabulate((size*(size+1) / 2 + 63) / 64)(i => 0L), size)
    
      for (i <- 0 until size) {
        for (j <- 0 until i+1) {
          rotated(i*(i+1) / 2 + j) = this((size-1-j)*(size-j) / 2 + i-j)
        }
      }

      rotated
    }

  
    private def update (peg: Peg, exists: Boolean) : Unit = {
      if (peg >= length) {
        throw new ArrayIndexOutOfBoundsException
      }
      else if (exists) {
        board(peg / 64) = board(peg / 64) | (1L << (peg % 64))
      }
      else {
        board(peg / 64) = board(peg / 64) & (-1L ^ (1L << (peg % 64)))
      }
    }

  }

  case class Move (val start: Peg, val skip: Peg, val end: Peg) {

    override def equals (other: Any) : Boolean = other match {
      case move: Move => start == move.start && skip == move.skip && end == move.end
      case _ => false
    }

    // Piggyback off of String hashing
    override def hashCode : Int =
      (start.toString + skip.toString + end.toString).hashCode

  }

  sealed trait Tree
  object Leaf extends Tree {
    val empty = Leaf(None)
    val singleton = Leaf(Some(Nil))
  }
  case class Leaf (val moves: Option[List[Move]]) extends Tree
  object Node extends Tree {
    val empty = Node(None, None)
  }
  case class Node (val left: Option[Tree], val right: Option[Tree]) extends Tree {

    private def insert (board: Board, moves: Option[List[Move]], peg: Peg) : Node = {
      (left, right, board(peg), peg == board.length-1) match {

        case (_, _, false, true) => moves match {
          case None => Node(Some(Leaf.empty), right)
          case Some(Nil) => Node(Some(Leaf.singleton), right)
          case _ => Node(Some(Leaf(moves)), right)
        }

        case (_, _, true, true) => moves match {
          case None => Node(left, Some(Leaf.empty))
          case Some(Nil) => Node(left, Some(Leaf.singleton))
          case _ => Node(left, Some(Leaf(moves)))
        }

        case (Some(l: Node), _, false, false) =>
          Node(Some(l.insert(board, moves, peg+1)), right)

        case (_, Some(r: Node), true, false) =>
          Node(left, Some(r.insert(board, moves, peg+1)))

        case (_, _, false, false) =>
          Node(Some(Node.empty.insert(board, moves, peg+1)), right)

        case (_, _, true, false) =>
          Node(left, Some(Node.empty.insert(board, moves, peg+1)))

      }
    }

    def insert (board: Board, moves: Option[List[Move]]) : Node = insert(board, moves, 0)

    private def get (board: Board, peg: Peg) : Option[Option[List[Move]]] = {
      (left, right, board(peg), peg == board.length-1) match {
        case (Some(l: Node), _, false, false) => l.get(board, peg+1)
        case (_, Some(r: Node), true, false) => r.get(board, peg+1)
        case (Some(Leaf(moves)), _, false, true) => Some(moves)
        case (_, Some(Leaf(moves)), true, true) => Some(moves)
        case _ => None
      }
    }

    def get (board: Board) : Option[Option[List[Move]]] = get(board, 0)

  }

  var seen = Node(None, None)


  def solve (board: Board, jumps: Map[Peg, List[(Peg, Peg)]])
            (peg: Peg)
      : Option[List[Move]] = {

    def solveLoop (pegJumps: List[(Peg, Peg)]) : Option[List[Move]] = {
      pegJumps match {
        case Nil => None

        case (jskip, jend)::js => {
          if (board(jskip) && !board(jend)) {
            val moved = board.makeMove(Move(peg, jskip, jend))

            solve(moved, jumps)(0) match {
              case None => solveLoop(js)
              case Some(moves) => Some(Move(peg, jskip, jend)::moves)
            }
          }

          else {
            solveLoop(js)
          }
        }
      }
    }

    // As ugly as it is, a lookup chain like this is the most efficient
    def lookupAll (board: Board) : Option[Option[List[Move]]] = {
      seen.get(board) match {
        case None => seen.get(board.rotateCW) match {
          case None => seen.get(board.rotateCCW) match {
            case None => seen.get(board.reflectBottom) match {
              case None => seen.get(board.reflectLeft) match {
                case None => seen.get(board.reflectRight) match {
                  case None => None
                  case result => result
                }
                case result => result
              }
              case result => result
            }
            case result => result
          }
          case result => result
        }
        case result => result
      }
    }

    lookupAll(board) match {

      case None => {
        if (peg >= board.length) {
          seen = seen.insert(board, None)
          None
        }
        else if (!board(peg)) {
          solve(board, jumps)(peg+1)
        }
        else if (board.count == 1) {
          seen = seen.insert(board, Some(Nil))
          Some(Nil) // victory!
        }
        else {
          solveLoop(jumps(peg)) match {
            case None => solve(board, jumps)(peg+1)
            case moves => {
              seen = seen.insert(board, moves)
              moves
            }
          }
        }
      }

      case Some(None) => None // Unsolvable
      case Some(moves) => moves

    }
  }


  def main (args: Array[String]) : Unit = {
    val size = args(0).toInt
    val totalPegs = size*(size+1) / 2

    for (i <- 0 until totalPegs) {
      // board is defined by the current Peg positions and the possible Moves
      val board = new Board(size, i)
      val jumps = board.allMoves

      println("Solving with empty position " + i)

      solve(board, jumps)(0) match {
        case None => println("No solution found!")
        case Some(moves) => moves.foreach(println)
      }
    }
  }

}

