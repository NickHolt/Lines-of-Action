package loa;

import static loa.Piece.*;
import static loa.Side.*;

/** Represents the state of a game of Lines of Action, and allows making moves.
 *  @author Nick Holt*/
class MutableBoard extends Board {

    /** A MutableBoard whose initial contents are taken from
     *  INITIALCONTENTS and in which it is PLAYER's move. The resulting
     *  Board has
     *        get(col, row) == INITIALCONTENTS[row][col]
     *  Assumes that PLAYER is not null and INITIALCONTENTS is 9x9,
     *  including the buffer zones..
     */
    MutableBoard(Piece[][] initialContents, Side player) {
        super(initialContents, player);
    }

    /** A new board in the standard initial position. */
    MutableBoard() {
        super();
    }

    /** A Board whose initial contents and state are copied from
     *  BOARD. */
    MutableBoard(Board board) {
        super(board);
    }

    /** Assuming isLegal(MOVE), make MOVE. */
    void makeMove(Move move) {
        addMove(move);
        Piece[][] config = getConfig();
        Side player = getPlayer();
        if (config[move.getRow1()][move.getCol1()].side()
            == player.opponent()) {
            move.setCapture(true);
        }
        if (player.opponent() == BLACK) {
            config[move.getRow1()][move.getCol1()] = WP;
        } else if (player.opponent() == WHITE) {
            config[move.getRow1()][move.getCol1()] = BP;
        } else {
            assert false;
        }
        config[move.getRow0()][move.getCol0()] = EMP;
    }

    /** Retract (unmake) one move, returning to the state immediately before
     *  that move.  Requires that movesMade () > 0. */
    void retract() {
        assert movesMade() > 0;
        Piece[][] config = getConfig();
        Move move = getMove(movesMade() - 1);
        Side player = getPlayer();
        Piece me, target;
        removeMove();
        if (player.opponent() == BLACK) {
            me = WP;
            target = BP;
        } else {
            assert player.opponent() == WHITE;
            me = BP;
            target = WP;
        }
        if (!move.getCapture()) {
            target = EMP;
        }
        config[move.getRow1()][move.getCol1()] = target;
        config[move.getRow0()][move.getCol0()] = me;
    }
}
