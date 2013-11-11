package loa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static loa.Side.*;

/** An automated Player.
 *  @author Nick Holt*/
class MachinePlayer extends Player {

    /** A MachinePlayer that plays the SIDE pieces in GAME. */
    MachinePlayer(Side side, Game game) {
        super(side, game);
    }

    @Override
    Move makeMove() {
        Game game = getGame();
        MutableBoard board = game.getBoard();
        assert board.turn() == side();
        double random = game.getRandom();
        ArrayList<Move> legalMoves = board.legalMoves();
        int index = (int) (random * (legalMoves.size() - 1));
        Reporter.debug(5, "legalMoves.size() = %d, index = %d"
                , legalMoves.size(), index);
        if (legalMoves.size() == 0) {
            System.out.printf("Player %s has no legal moves. %s wins.\n"
                    , side(), side().opponent());
            System.exit(1);
        }
        Move result = legalMoves.get(index);
        boolean win = false;
        Collections.shuffle(legalMoves);
        for (Move move : legalMoves) {
            board.makeMove(move);
            win = board.piecesContiguous(side());
            if (win) {
                board.retract();
                result = move;
                win = true;
            } else {
                board.retract();
            }
        }
        if (!win) {
            for (Move move: legalMoves) {
                Reporter.debug(4, "simulation ran with move %s", move);
                int turns;
                if (game.hasTimeLimit()) {
                    int timeLeft = game.timeRemaining(side().opponent());
                    turns = (int) (timeLeft * MOVE_TO_TIME_FACTOR);
                } else {
                    turns = TEN;
                }
                GameSimulation simulation
                    = new GameSimulation(board.getConfigCopy(), turns);
                win = runSimulation(simulation, move);
                Reporter.debug(4, "Simulation win with move %s: %s", move, win);
                if (win) {
                    Reporter.debug(5, "FOUND A WINNING MOVE.");
                    result = move;
                    break;
                }
            }
        }
        if (side() == WHITE) {
            System.out.println("W::" + result);
        } else {
            assert side() == BLACK;
            System.out.println("B::" + result);
        }
        Reporter.debug(3, "MachinePlayer chose move"
                       + "%s from %s.", result, legalMoves);
        return result;
    }

    /** Run a game SIMULATION of N turns and return true iff
     *  the game is winnable by THIS within N turns. The first
     *  move is FIRSTMOVE. Returns false if the game cannot be won
     *  or the opponent wins first.*/
    private boolean runSimulation(GameSimulation simulation, Move firstMove) {
        if (!simulation.hasTurns()) {
            return false;
        }
        Side me = side(), opponent = side().opponent();
        simulation.makeMove(me, firstMove);
        if (simulation.simulationWon(me)) {
            return true;
        }
        simulation.makeRandomMove(opponent);
        if (simulation.simulationWon(opponent)) {
            return false;
        }
        simulation.decreaseTurns();
        ArrayList<Move> legalMoves = simulation.legalMoves(me);
        for (Move move : legalMoves) {
            GameSimulation simCopy = new GameSimulation(
                    simulation.getConfig(), simulation.getTurns());
            boolean win = runSimulation(simCopy, move);
            if (win) {
                return true;
            }
        }
        return false;
    }

    /** A class representing a game simulation. This class is
     *  controlled externally by a MachinePlayer.
     *  @author Nick Holt */
    private class GameSimulation {
        /** Start a new simulation, using
         *  the initial CONFIG, with only
         *  TURNS number of turns allowed by each side. */
        GameSimulation(Piece[][] config, int turns) {
            _turns = turns;
            _config = config;
        }

        /** Perform a MOVE by SIDE. */
        void makeMove(Side side, Move move) {
            MutableBoard simBoard = new MutableBoard(_config, side);
            simBoard.makeMove(move);
            _config = simBoard.getConfigCopy();
        }

        /** Perform a random move by SIDE.
         *  Does nothing if no legal move can be made. */
        void makeRandomMove(Side side) {
            Game game = getGame();
            Random randomsource = game.getRandomSource();
            ArrayList<Move> legalMoves = legalMoves(side);
            if (legalMoves.size() != 0) {
                Move move = legalMoves.get(
                        (int) (randomsource.nextDouble()
                        * legalMoves.size() - 1));
                makeMove(side, move);
            }
        }

        /** Decrease the number of turns allowed by one. */
        void decreaseTurns() {
            _turns -= 1;
        }

        /** Returns true if this simulation has turns left. */
        boolean hasTurns() {
            return _turns > 0;
        }

        /** Returns a list of SIDE's legal moves. */
        ArrayList<Move> legalMoves(Side side) {
            Board simBoard = new Board(_config, side);
            Reporter.debug(4, "simulation's legalMoves found: %s"
                    , simBoard.legalMoves());
            return simBoard.legalMoves();
        }

        /** Returns true iff this game simulation has been won
         *  by SIDE. */
        boolean simulationWon(Side side) {
            Board simboard = new Board(_config, side);
            Reporter.debug(5, "simulationBoard: %s", simboard);
            return simboard.piecesContiguous(side);
        }

        /** Returns this simulation's number of turns. */
        int getTurns() {
            return _turns;
        }

        /** @return this simulations configuration. */
        Piece[][] getConfig() {
            return _config;
        }

        /** The amount of turns left in this game. */
        private int _turns;

        /** The simulation's game board. */
        private Piece[][] _config;
    }

    /** This games move to time factor. */
    private static final double MOVE_TO_TIME_FACTOR = 100;

    /** The number 10. */
    private static final int TEN = 10;
}
