package loa;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** A Player that prompts for moves and reads them from its Game.
 *  @author Nick Holt*/
class HumanPlayer extends Player {

    /** A HumanPlayer that plays the SIDE pieces in GAME.  It uses
     *  GAME.getMove() as a source of moves.  */
    HumanPlayer(Side side, Game game) {
        super(side, game);
    }

    @Override
    Move makeMove() {
        Game game = getGame();
        String move = game.getMove();
        ArrayList<Move> legalMoves = game.getBoard().legalMoves();
        if (legalMoves.size() == 0) {
            System.out.printf("Player %s has no legal moves. %s wins.\n"
                    , this.side(), this.side().opponent());
            System.out.println(game.getBoard());
            System.exit(1);
        }
        Reporter.debug(3, "HumanPlayer recieved move %s", move);
        Pattern pat = Pattern.compile("([a-z][\\d])-([a-z][\\d])");
        Matcher mat = pat.matcher(move);
        mat.matches();
        Reporter.debug(5, "makeMove() move groups: 1: %s 2: %s"
                , mat.group(1), mat.group(2));
        Move newMove = Move.create(Board.col(mat.group(1))
                , Board.row(mat.group(1))
                , Board.col(mat.group(2))
                , Board.row(mat.group(2)));
        if (game.getBoard().isLegal(newMove)) {
            return newMove;
        } else {
            System.out.println("Illegal move. Try again.");
            return makeMove();
        }
    }
}
