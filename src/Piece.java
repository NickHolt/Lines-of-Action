package loa;

import static loa.Side.*;

/** A Piece denotes the contents of a square.
    @author Nick Holt */
enum Piece {
    /** The names of the pieces.  EMP indicates an empty square,
     *  BUF indicates a buffer piece. */
    BP(BLACK, "b"), WP(WHITE, "w"),  EMP(null, "-"), BUF(BUFFER, "*");

    /** The side this piece belongs to. */
    private Side _side;
    /** The textual representation of this piece. */
    private String _textName;

    /** A Piece on the given SIDE that uses TEXTNAME as its printed
     *  contents. */
    Piece(Side side, String textName) {
        _side = side;
        _textName = textName;
    }

    /** Returns which side (BLACK or WHITE) plays this piece, or null
     *  for an empty square. */
    Side side() { return _side; }
    /** Returns the one-character denotation of this piece on the standard
     *  text display of a checkerboard. */
    String textName() { return _textName; }
}
