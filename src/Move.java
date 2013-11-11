package loa;

/** A move in Lines of Action.
 *  @author Nick Holt*/
class Move {

    /* Implementation note: We create moves by means of static "factory
     * methods" all named create, which in turn use the single (private)
     * constructor.  Factory methods have certain advantages over constructors:
     * they allow you to produce results having an arbitrary subtype of Move,
     * and they don't require that you produce a new object each time.  This
     * second advantage is useful when you are trying to speed up the creation
     * of Moves for use in automated searching for moves.  You can (if you
     * want) create just one instance of the Move representing 1-5, for example
     * and return it whenever that move is requested. */

    /** Return a move of the piece at COLUMN0, ROW0 to COLUMN1, ROW1. */
    static Move create(int column0, int row0, int column1, int row1) {
        return new Move(column0, row0, column1, row1);
    }

    /** A new Move of the piece at COL0, ROW0 to COL1, ROW1. */
    private Move(int col0, int row0, int col1, int row1) {
        _col0 = col0;
        _row0 = row0;
        _col1 = col1;
        _row1 = row1;

    }

    /** Return the column at which this move starts, as an index in 1--8. */
    int getCol0() {
        return _col0;
    }

    /** Return the row at which this move starts, as an index in 1--8. */
    int getRow0() {
        return _row0;
    }

    /** Return the column at which this move ends, as an index in 1--8. */
    int getCol1() {
        return _col1;
    }

    /** Return the row at which this move ends, as an index in 1--8. */
    int getRow1() {
        return _row1;
    }

    /** Return the length of this move (number of squares moved). */
    int length() {
        return Math.max(Math.abs(_row1 - _row0), Math.abs(_col1 - _col0));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Move)) {
            return false;
        }
        Move move2 = (Move) obj;
        return this.hashCode() == move2.hashCode();
    }

    /** Hashcodes are assigned based on the following format:
     *  c0r0c1r1C. Where c0 is the first column, r0 is the
     *  first row (given as an integer), c1 is the second
     *  column, r1 is the second row, and C represents the
     *  boolean _CAPTURE, and is 0 iff _CAPTURE is false,
     *  and 1 otherwise.
     *  @return hashCode.
     */
    public int hashCode() {
        int cap = 0;
        if (_capture) {
            cap = 1;
        }
        return Integer.parseInt(String.valueOf(getCol0())
                + String.valueOf(getRow0()) + String.valueOf(getCol1())
                + String.valueOf(getRow1()) + String.valueOf(cap));
    }

    @Override
    public String toString() {
        return Board.getLetters()[getCol0() - 1]
                + String.valueOf(getRow0()) + "-"
                + Board.getLetters()[getCol1() - 1]
                + String.valueOf(getRow1());
    }

    /** Return CAPTURE. */
    public boolean getCapture() {
        return _capture;
    }

    /** Sets CAPTURE. */
    public void setCapture(boolean capture) {
        _capture = capture;
    }

    /** True iff this move capture(s/d) a piece. Always
     *  false at least until move is played.*/
    private boolean _capture = false;

    /** Column and row numbers of starting and ending points. */
    private int _col0, _row0, _col1, _row1;

}
