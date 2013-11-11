package loa;

import ucb.util.CommandArgs;

/** Main class of the Lines of Action program.
 * @author Nick Holt
 */
public class Main {

    /** The main Lines of Action.  ARGS are as described in the
     *  project 3 handout:
     *      [ --white ] [ --ai=N ] [ --seed=S ] [ --time=LIM ] \
     *      [ --debug=D ] [ --display ]
     */
    public static void main(String... args) {
        String options = "--white --ai= --seed= --time="
                + " --debug= --display";
        CommandArgs cArgs =
            new CommandArgs(options, args);

        if (!cArgs.ok()) {
            usage();
        }

        int ai = 1;
        long seed = -1;
        int time = -1;
        int debug = 0;
        Side side;

        if (cArgs.containsKey("--ai")) {
            ai = cArgs.getInt("--ai");
            if (!(ai >= 0 && ai <= 2)) {
                usage();
            }
        }
        if (cArgs.containsKey("--seed")) {
            seed = cArgs.getLong("--seed");
            if (seed < 0) {
                usage();
            }
        }
        if (cArgs.containsKey("--time")) {
            time = cArgs.getInt("--time");
            if (time < 0) {
                usage();
            }
        }
        if (cArgs.containsKey("--debug")) {
            debug = cArgs.getInt("--debug");
            if (debug < 0) {
                usage();
            }
        }
        if (cArgs.containsKey("--white")) {
            side = Side.WHITE;
        } else {
            side = Side.BLACK;
        }
        if (cArgs.containsKey("--display")) {
            System.err.println("Error: Display option not yet implemented. "
                    + "Please send a cash sum of $100 and a jar of cookies "
                    + "to the author of this program to receive this feature.");
            System.exit(0);
        }

        Reporter.setMessageLevel(debug);
        Game game = new Game(2 - ai, side, seed, time);
        game.play();
    }


    /** Print brief description of the command-line format. */
    static void usage() {
        System.out.println("INPUT ERROR");
        System.out.println("__Game usage instructions__");
        System.out.println("-Command line initialization-");
        System.out.println("Initialize with the following format:");
        System.out.println("java loa.Main [ --white ] [ --ai=N ] [ --seed=N ] "
                           + "[ --time=LIM ] [ --debug=N ] [ --display ]");
        System.out.println("Bracketed parameters are optional:");
        System.out.println("[--white] indicates the player plays as white.");
        System.out.println("[ --ai=N ] provides the number N of AI players. "
                + "N = 0 or 1.");
        System.out.println("[ -- seed=N ] provides the seed N for random"
                + " number generation. N is an integer >= 0.");
        System.out.println("[ --time=LIM ] provides the time limit LIM allowed"
                + " for each side's total moves. LIM is a number >= 0.");
        System.out.println("[ --debug=N] provides the degree N of debug"
                + " statements to be printed. N is an integer > 0.");
        System.out.println("[ --display ] creates a GUI interface to play"
                + " the game. NOT CURRENTLY IMPLEMENTED.");
        System.out.println("\n-In game instructions-");
        System.out.println("All input should follow the following format:");
        System.out.println("[ S ] cAnA-cBnB [ C ]");
        System.out.println("Bracketed parameters are optional:");
        System.out.println("[ S ] indicates in game options (if S is a word,"
                + " only the first character is considered):");
        System.out.println("    's' displays the game board.");
        System.out.println("    'p' starts AIs specified in initializtion. "
                + "Has no effect if used previously or no AI specified.");
        System.out.println("    'q' quits the current game.");
        System.out.println("    't' displays the current"
                           + "players remaining time.");
        System.out.println("[ C ] is optional commentary and is ignored by"
                + " the program.");
        System.exit(1);
    }
}
