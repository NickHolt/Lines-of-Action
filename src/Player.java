package loa;


/** Represents a player.  Extensions of this class do the actual playing.
 *  @author Nick Holt
 */
public abstract class Player {

    /** A player that plays the SIDE pieces in GAME. */
    Player(Side side, Game game) {
        _side = side;
        _game = game;
    }

    /** Return my next move from the current position in getBoard(), assuming
     *  that side() == getBoard.turn(). */
    abstract Move makeMove();

    /** Return which side I'm playing. */
    Side side() {
        return _side;
    }

    /** Return the board I am using. */
    Board getBoard() {
        return _game.getBoard();
    }

    /** Return the game I am playing. */
    Game getGame() {
        return _game;
    }

    /** This player's side. */
    private final Side _side;
    /** The game this player is part of. */
    private Game _game;
}
