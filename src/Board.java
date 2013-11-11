package loa;

import java.util.ArrayList;
import static loa.Side.*;
import static loa.Piece.*;


/** Represents the state of a game of Lines of Action. A Board is immutable.
 *  Its MutableBoard subclass allows moves to be made.
 *  @author Nick Holt
 */
class Board {

    /** All of the moves made in this game thus far. */
    private static ArrayList<Move> moves = new ArrayList<Move>();

    /** A Board whose initial contents are taken from
     *  INITIALCONTENTS and in which it is PLAYER's move. The resulting
     *  Board has
     *        get(col, row) == INITIALCONTENTS[row][col]
     *  Assumes that PLAYER is not null and INITIALCONTENTS is 9x9
     *  (including buffer layers).
     */
    Board(Piece[][] initialContents, Side player) {
        assert player != null && initialContents.length == 10;
        config = initialContents;
        _player = player;
    }

    /** A new board in the standard initial position. */
    Board() {
        this(INITIAL_PIECES, BLACK);
    }

    /** A Board whose initial contents and state are copied from
     *  BOARD. */
    Board(Board board) {
        config = board.getConfigCopy();
        _player = board.getPlayer();
    }

    /** Return the contents of column C, row R, where 1 <= C,R <= 8,
     *  where column 1 corresponds to column 'a' in the standard
     *  notation. */
    Piece get(int c, int r) {
        return config[r][c];
    }

    /** Return the contents of the square SQ.  SQ must be the
     *  standard printed designation of a square (having the form cr,
     *  where c is a letter from a-h and r is a digit from 1-8). */
    Piece get(String sq) {
        return get(col(sq), row(sq));
    }

    /** Return the column number (a value in the range 1-8) for SQ.
     *  SQ is as for {@link get(String)}. */
    static int col(String sq) {
        char c = sq.charAt(0);
        for (int i = 0; i < LETTERS.length; i++) {
            if (LETTERS[i] == c) {
                return i + 1;
            }
        }
        assert false;
        return 0;
    }

    /** Return the row number (a value in the range 1-8) for SQ.
     *  SQ is as for {@link get(String)}. */
    static int row(String sq) {
        return Integer.parseInt(sq.substring(1));
    }

    /** Return the Side that is currently next to move. */
    Side turn() {
        return _player;
    }

    /** Return true iff MOVE is legal for the player currently on move. */
    boolean isLegal(Move move) {
        if (move.getCol0() > 8 || move.getRow0() > 8
                || move.getCol1() > 8 || move.getRow1() > 8
                || move.getCol0() < 1 || move.getRow0() < 1
                || move.getCol1() < 1 || move.getRow1() < 1) {
            Reporter.debug(2, "Move illegal: out of board.");
            return false;
        }
        if (!legalDirectionCheck(move)) {
            return false;
        }
        Piece origin = get(move.getCol0(), move.getRow0());
        if (origin.side() == _player.opponent()
                || origin.side() == null) {
            Reporter.debug(2, "Move illegal: cannot"
                           + "move an opponent or empty space");
            Reporter.debug(3, "Origin: (%d, %d). Destination (%d, %d)"
                    , move.getCol0(), move.getRow0()
                    , move.getCol1(), move.getRow1());
            return false;
        }
        ArrayList<ArrayList<Piece>> pieces
            = getLinePieces(move.getCol0(), move.getRow0()
                    , move.getCol1(), move.getRow1());
        ArrayList<Piece> to = pieces.get(0),
                fro = pieces.get(1);
        int count = 0, dist = -1;
        boolean passed = false;
        for (Piece piece : to) {
            Reporter.debug(4, "dist: %d, move length: %d", dist, move.length());
            dist++;
            if (dist == move.length()) {
                if (piece.side() == _player.opponent().opponent()) {
                    Reporter.debug(2, "Move illegal: "
                                   + "can't land on a friendly piece");
                    return false;
                }
                passed = true;
            }
            if (!passed) {
                if (piece.side() == _player.opponent()) {
                    Reporter.debug(2, "Move illegal: "
                                   + "can't jump over an enemy (%s)"
                            , _player.opponent());
                    return false;
                }
                if (piece.side() != null) {
                    count++;
                }
            } else {
                if (piece.side() != null) {
                    count++;
                } else {
                    continue;
                }
            }
        }
        return isLegal2(fro, count, move.length());
    }

    /** The second part of isLegal() to pass the stylecheck.
     *  FRO, COUNT, MOVELENGTH. Return the result.
     */
    private boolean isLegal2(ArrayList<Piece> fro
            , int count, int moveLength) {
        for (Piece piece : fro) {
            if (piece.side() != null) {
                count++;
            } else {
                continue;
            }
        }
        if (count != moveLength) {
            Reporter.debug(2, "Move illegal:"
                           + "Move length must equal number of pieces in line");
            Reporter.debug(3, "count: %d, move"
                           + "length: %d", count, moveLength);
            return false;
        } else {
            return true;
        }
    }

    /** Sub method to get past style check. Check's
     *  MOVE's direction and returns true if it is valid. */
    private boolean legalDirectionCheck(Move move) {
        int cd = Math.abs(move.getCol1() - move.getCol0());
        int cr = Math.abs(move.getRow1() - move.getRow0());
        if (cd != 0 && cr != 0 && cd != cr) {
            Reporter.debug(2, "Move illegal: must move in allowed direction.");
            return false;
        }
        return true;
    }

    /** Return an ArrayList of all legal moves for current player. */
    public ArrayList<Move> legalMoves() {
        ArrayList<Move> legalMoves = new ArrayList<Move>();
        int x = 0, y = 0;
        for (Piece[] row : config) {
            for (Piece piece : row) {
                if (piece.side() == _player.opponent().opponent()) {
                    Reporter.debug(5, "AI piece found at (%d, %d)", x, y);
                    int c = x; int r = y;
                    for (int d = 0; d <= SEVEN; d++) {
                        ArrayList<ArrayList<Piece>> line
                            = getLinePieces(c, r, d);
                        int distance = 0;
                        for (Piece neighbor : line.get(0)) {
                            if (neighbor.side() != null) {
                                distance++;
                            }
                        }
                        for (Piece neighbor : line.get(1)) {
                            if (neighbor.side() != null) {
                                distance++;
                            }
                        }
                        Move move = Move.create(c, r, moveC(c, d, distance)
                                                , moveR(r, d, distance));
                        if (isLegal(move)) {
                            Reporter.debug(4, "legalMoves()"
                                           + "generated a legal"
                                           + "move with origin"
                                           + "(%d, %d) and"
                                           + "destination (%d, %d)."
                                           + "Player is %s.", move.getCol0()
                                           , move.getRow0(), move.getCol1()
                                           , move.getRow1(), _player);
                            legalMoves.add(move);
                        }
                    }
                }
                x++;
            }
            x = 0;
            y++;
        }
        Reporter.debug(3, "legalMoves found: %s", legalMoves);
        return legalMoves;
    }

    /** Return true iff the game is currently over.  A game is over if
     *  either player has all his pieces continguous. */
    boolean gameOver() {
        return piecesContiguous(BLACK) || piecesContiguous(WHITE);
    }

    /** Return true iff PLAYER's pieces are continguous. */
    boolean piecesContiguous(Side player) {
        ArrayList<int[]> remaining = getCoordinates(player);
        if (remaining.size() <= 1) {
            return true;
        }
        ArrayList<int[]> grouped = new ArrayList<int[]>();
        grouped.add(remaining.get(0)); remaining.remove(0);
        boolean connected, allAway;
        while (remaining.size() != 0) {
            allAway = true;
            for (int[] piece : remaining) {
                connected = isConnected(grouped, piece);
                if (connected) {
                    grouped.add(piece); remaining.remove(piece);
                    allAway = false;
                    break;
                }
            }
            if (allAway) {
                return false;
            }
        }
        Reporter.debug(4, "Contiguous check found %d unconnected pieces."
                , remaining.size());
        return true;
    }

    /** Return the total number of moves that have been made (and not
     *  retracted).  Each valid call to makeMove with a normal move increases
     *  this number by 1. */
    int movesMade() {
        return moves.size();
    }

    /** Returns move #K used to reach the current position, where
     *  0 <= K < movesMade().  Does not include retracted moves. */
    Move getMove(int k) {
        return moves.get(k);
    }

    @Override
    public String toString() {
        String result = "===";
        for (Piece[] row : config) {
            for (Piece piece : row) {
                if (!piece.textName().equals("*")) {
                    result += piece.textName() + " ";
                }
            }
            result += "\n";
        }
        result = result.substring(0, result.length() - 1);
        result += "Next move: " + _player.opponent().opponent();
        result += "\nMoves: " + movesMade();
        result += "\n===\n";
        return result;
    }

    /** @return config. */
    public Piece[][] getConfig() {
        return config;
    }

    /** @return a copy of my configuration. */
    public Piece[][] getConfigCopy() {
        Piece[][] result = new Piece[config.length][config.length];
        for (int i = 0; i < config.length; i++) {
            for (int j = 0; j < config[i].length; j++) {
                result[i][j] = config[i][j];
            }
        }
        return result;

    }

    /** @return _player. */
    public Side getPlayer() {
        return _player;
    }

    /** Adds a MOVE to MOVES. */
    public void addMove(Move move) {
        moves.add(move);
    }

    /** Removes the last move from MOVES. */
    public void removeMove() {
        moves.remove(moves.size() - 1);
    }

    /** Returns LETTERS. */
    public static char[] getLetters() {
        return LETTERS;
    }

    /** A utility method that takes a starting position p =(C, R) and
     *  a direction D and returns an ArrayList of two ArrayLists.
     *  The first of these ArrayLists contains the pieces on the line
     *  starting from p and pointing in D, while the second ArrayList
     *  contains the pieces starting from p - 1 and pointing in -D.
     *  THESE LISTS INCLUDE EMPTY SPACES AS PIECES.
     *  0 <= D < 8, where the number refers to one of the
     *  8 compass directions, counted clockwise with north = 0.
     */
    ArrayList<ArrayList<Piece>> getLinePieces(int c, int r, int d) {
        Reporter.debug(3, "getLinePieces() called"
                       + "with origin (%d, %d) and direction %d."
                       , c, r, d);
        int colUnit = UNIT_VECTORS[d][0], rowUnit = UNIT_VECTORS[d][1]
                , col = c + colUnit, row = r + rowUnit;
        Reporter.debug(4, "Direction: %d. colUnit: %d."
                       + "rowUnit: %d", d, colUnit, rowUnit);
        ArrayList<Piece> l0 = new ArrayList<Piece>()
                , l1 = new ArrayList<Piece>();
        l0.add(get(c, r));
        for (Piece target = get(col, row);
             target.side() != BUFFER;
             col += colUnit, row += rowUnit, target = get(col, row)) {
            l0.add(target);
        }
        col = c - colUnit; row = r - rowUnit;
        for (Piece target = get(col, row);
             target.side() != BUFFER;
             col -= colUnit, row -= rowUnit, target = get(col, row)) {
            l1.add(target);
        }
        ArrayList<ArrayList<Piece>> result = new ArrayList<ArrayList<Piece>>();
        result.add(l0); result.add(l1);
        Reporter.debug(3, "Pieces in the positive direction: %s\n"
                       + "Pieces in the negative direction: %s", l0, l1);
        return result;
    }

    /** A utility method that takes a starting position (C0, R0) and
     *  end position (C1, R1) and returns an ArrayList of two ArrayLists,
     *  as per {@link getLinePieces(int c, int r, int d)}.
     */
    ArrayList<ArrayList<Piece>> getLinePieces(int c0, int r0
            , int c1, int r1) {
        Move temp =  Move.create(c0, r0, c1, r1);
        int cd = (c1 - c0) / temp.length()
                , rd = (r1 - r0) / temp.length()
                , d = 0;
        Reporter.debug(6, "getLinePieces(int, int, int, int)"
                       + "variables: c0 = %d,"
                       + "r0 = %d, c1 = %d, r1 = %d, temp.length()"
                       + "= %d", c0, r0, c1, r1, temp.length());
        Reporter.debug(5, "Direction generation pieces: cd = %d, rd = %d,"
                , cd, rd);
        assert cd >= 0 && rd >= 0
                && cd <= 1 && rd <= 1;
        if (cd == 0) {
            if (rd == -1) {
                d = FOUR;
            } else if (rd == 1) {
                d = ZERO;
            } else {
                assert false;
            }
        } else if (cd == 1) {
            if (rd == -1) {
                d = THREE;
            } else if (rd == 0) {
                d = TWO;
            } else if (rd == 1) {
                d = ONE;
            } else {
                assert false;
            }
        } else if (cd == -1) {
            if (rd == -1) {
                d = FIVE;
            } else if (rd == 0) {
                d = SIX;
            } else if (rd == 1) {
                d = SEVEN;
            } else {
                assert false;
            }
        } else {
            assert false;
        }
        return getLinePieces(c0, r0, d);
    }

    /** A utility method that returns an ArrayList of coordinates in the
     *  form {x, y} of PLAYER's pieces on the board. x and y are not
     *  indices. I.E. x = 1 refers to the first column.*/
    ArrayList<int[]> getCoordinates(Side player) {
        ArrayList<int[]> result = new ArrayList<int[]>();
        int y = 0;
        for (Piece[] row : config) {
            int x = 0;
            for (Piece piece: row) {
                if (piece.side() != player.opponent()
                        && piece.side() != null
                        && piece.side() != BUFFER) {
                    int[] temp = new int[2];
                    temp[0] = x; temp[1] = y;
                    result.add(temp);
                }
                x++;
            }
            y++;
        }
        return result;
    }

    /** A utility method that returns true if PIECE (represented by
     *  an array of coordinates) is connected to any other piece in
     *  PIECES, and false otherwise. Pieces are considered connected
     *  if pieces are one square away from each other.*/
    private static boolean isConnected(ArrayList<int[]> pieces
            , int[] piece) {
        for (int[] target : pieces) {
            int xd = Math.abs(piece[0] - target[0])
                    , yd = Math.abs(piece[1] - target[1]);
            if (xd <= 1 && yd <= 1) {
                return true;
            }
        }
        return false;
    }

    /** A utility method that returns a new row based on
     *  original row C, direction D, and DISTANCE. */
    private int moveC(int c, int d, int distance) {
        return c + UNIT_VECTORS[d][0] * distance;
    }

    /** A utility method that returns a new row based on
     *  original row R, direction D, and DISTANCE. */
    private int moveR(int r, int d, int distance) {
        return r + UNIT_VECTORS[d][1] * distance;
    }

    /** Returns the (index + 1) of C in LETTERS. Assumes LETTERS
     *  contains C. */
    public static int findChar(char c) {
        for (int i = 0; i < LETTERS.length; i++) {
            if (c == LETTERS[i]) {
                return i + 1;
            }
        }
        assert false;
        return 0;
    }

    /** The current configuration of THIS. */
    private Piece[][] config;

    /** The player currently playing THIS. */
    private Side _player;

    /** The standard initial configuration for Lines of Action. */
    static final Piece[][] INITIAL_PIECES = {
        { BUF, BUF, BUF, BUF, BUF, BUF, BUF, BUF, BUF, BUF  },
        { BUF, EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP, BUF  },
        { BUF, WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP, BUF  },
        { BUF, WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP, BUF  },
        { BUF, WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP, BUF  },
        { BUF, WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP, BUF  },
        { BUF, WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP, BUF  },
        { BUF, WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP, BUF  },
        { BUF, EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP, BUF },
        { BUF, BUF, BUF, BUF, BUF, BUF, BUF, BUF, BUF, BUF  }
    };

    /** The standard initial configuration for Lines of Action. */
    static final Piece[][] INITIAL_PIECES2 = {
        { BUF, BUF, BUF, BUF, BUF, BUF, BUF, BUF, BUF, BUF  },
        { BUF, EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP, BUF  },
        { BUF, WP,  WP, EMP, EMP, EMP, EMP, EMP, EMP, BUF  },
        { BUF, WP,  WP, EMP, EMP, EMP, EMP, EMP, EMP, BUF  },
        { BUF, WP,  WP, EMP, EMP, EMP, EMP, EMP, EMP, BUF  },
        { BUF, WP,  WP, EMP, EMP, EMP, EMP, EMP, EMP, BUF  },
        { BUF, WP,  WP, EMP, EMP, EMP, EMP, EMP, EMP, BUF  },
        { BUF, WP,  EMP, EMP, WP, EMP, EMP, EMP, EMP, BUF  },
        { BUF, EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP, BUF },
        { BUF, BUF, BUF, BUF, BUF, BUF, BUF, BUF, BUF, BUF  }
    };

    /** Partial alphabet. */
    private static final char[] LETTERS = {'a', 'b', 'c', 'd', 'e'
        , 'f', 'g', 'h'};

    /** Integers 0 - 7. */
    private static final int ZERO = 0, ONE = 1, TWO = 2, THREE = 3
            , FOUR = 4, FIVE = 5, SIX = 6, SEVEN = 7;

    /** An array that maps direction to unit vectors.
        The index is the direction d. */
    private static final int[][] UNIT_VECTORS = {{0, 1}, {1, 1}, {1, 0}
        , {1, -1}, {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}};
}
