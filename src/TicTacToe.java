import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/** Interface for human and AI move on tictactoe board. Player attributes when defined in class
 *  {@link TicTacToe} use interface object as the move.
 *
 *  Note : This game is hardcoded for 3*3 tic tac toe board.
 *
 *  Time Complexity : O(n^2)
 */
interface MoveMethod {
    int move();
}

/**
 * Generate AI move on tictactoe board.
 */
class SimpleMoveStrategy implements MoveMethod {
    private TicTacToe game;

    public SimpleMoveStrategy(TicTacToe t) {
        game = t;
    }

    // although override is not necessarily required, it is better to be explicit rather than
    // implicit

    /**
     * AI move fills the first block it finds empty in the ticktactoe matrix.
     *
     * Time Complexity : O(n^2)
     *
     * @return coordinate(number when started from top left to bottom right) of the tictactoe
     * matrix to fill. If all moves are filled, return 0.
     */
    public int move() {
        for (int i = 0; i < TicTacToe.N; i++) {
            for (int j = 0; j < TicTacToe.N; j++) {
                if (game.board[i][j] == 0)
                    return (i * 3 + j + 1);
            }
        }
        return 0;
    }
}

/**
 * Takes human move coordinate for the tictactoe matrix, and apply it.
 */
class HumanMove implements MoveMethod {
    private TicTacToe game;

    public HumanMove(TicTacToe t) {
        game = t;
    }

    // although override is not necessarily required, it is better to be explicit rather than
    // implicit

    /**
     * Takes input from human when it is human turn in the game. Check if the move is
     * valid, return the move coordinate(number when started from top left to bottom right), if
     * input is invalid request user to submit valid input.
     *
     * @return coordinate(number when started from top left to bottom right) of the tictactoe
     * matrix to fill.
     */
    public int move() {
        String move_str;

        int move_int = 0;
        boolean valid_input = false;

        while (!valid_input) {
            System.out.print("Where to ? ");
            move_str = TicTacToe.getUserInput();
            if (Character.isDigit(move_str.toCharArray()[0])) {
                move_int = Integer.parseInt(move_str);
                if ((move_int <= (TicTacToe.N) * (TicTacToe.N)) &&
                        move_int >= 1) {
                    valid_input = true;
                    break;
                }
            }

            if (!valid_input) {
                // We should provide more details on what are valid inputs.
                System.out.println("Invalid input");
                continue;
            }
        }
        return move_int;
    }

}

/**
 * Runs the tic tac game between human and AI.
 */
class TicTacToe {
    /** Dimension of the game. We can move the dimension to inside the constructor to define the
     * game size. */
    protected static final int N = 3;

    /** Space between current board showing moves played with the allowed moves number to
     * input when requesting human to submit move. */
    private static final int HSPACE = 20;

    /** TicTacToe board. Moves will be submitted and checked in here. */
    protected int[][] board;

    /** Taking user input. */
    private static BufferedReader reader =
            new BufferedReader(new InputStreamReader(System.in));

    /** Defining two players that are playing game, human and AI. */
    class Player {
        private String name;

        private int player_type;

        private int player_order;

        private MoveMethod move_strategy;

        public Player(String pname, int type,
                int order, MoveMethod move_s) {
            name = pname;
            player_type = type;
            player_order = order;
            move_strategy = move_s;
        }

        /** Get player name. */
        public String getName() {
            return name;
        }

        /** Get player type. Human or AI. */
        public int getPlayerType() {
            return player_type;
        }

        /** Get next move from the player. */
        public int getMove() {
            return move_strategy.move();
        }
    }

    private Player player1, player2;

    /** Get player 1 POJO. */
    public Player getplayer1() {
        return player1;
    }

    /** Get player 2 POJO. */
    public Player getplayer2() {
        return player2;
    }

    /** Provide the position location played for a 3*3 tic tac toe board. */
    public static String getPosDescription(int pos) {
        String str = "";
        if (pos == 5) {
            str = "center";
            return str;
        }

        if ((pos - 1) / 3 == 0) {
            str += "upper ";
        } else if ((pos - 1) / 3 == 1) {
            str += "middle ";
        } else
            str += "lower ";

        if ((pos - 1) % 3 == 0)
            str += "left";
        else if ((pos - 1) % 3 == 1)
            str += "middle";
        else
            str += "right";

        return str;
    }

    // Not sure if protected was required. Could have been package-private.
    /** Get the user input. */
    protected static String getUserInput() {
        String input = "";
        try {
            input = reader.readLine();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return input;
    }

    // Class is not public. Constructor shouldn't be as well.
    /** Initializes board with 0 as values for each coordinate. 0 means no value input in that
     * coordinate. Initializes both human and AI player. */
    public TicTacToe() {
        board = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                board[i][j] = 0;
            }
        }

        System.out.println("Enter player name");
        player1 = new Player(getUserInput(), 2, 0, new HumanMove(this));

        player2 = new Player("", 1, 1, new SimpleMoveStrategy(this));
        System.out.println("\nHuman player " + player1.getName() +
                " vs Computer Player" + player2.getName() + ":");
    }

    /** Set move played. Mark it by the player type. Get the number for the coordiante, and
     * deduce its position in the board and mark that location with player insignia. */
    public boolean setMove(int move, int p_type) {
        int x_coord = (move - 1) / 3;
        int y_coord = (move - 1) % 3;

        if (board[x_coord][y_coord] == 0) {
            board[x_coord][y_coord] = p_type;
            return true;
        } else {
            System.out.println("Invalid move");
            return false;
        }
    }

    /** Enum defining possible outcome for the game. */
    private enum WinConfig {
        DRAW, WIN, NONE
    }

    /** Deduce after each turn what is the status of the game. Check if any row, column, or
     * diagonal is fileld by a single user. IF so, submit the game is won by that user. If not,
     * then check if all boxes are filled. If so, send the draw message, otherwise none, which
     * means game should continue.
     *
     * Time Complexity : O(n)
     * */
    private WinConfig isWinningConfig() {
        WinConfig w = WinConfig.WIN;
        // rows
        for (int i = 0; i < N; i++) {
            if ((board[i][0] != 0) && (board[i][0] == board[i][1]) &&
                    (board[i][0] == board[i][2])) {
                return w;
            }
        }
        // columns
        for (int i = 0; i < N; i++) {
            if ((board[0][i] != 0) && (board[0][i] == board[1][i]) &&
                    (board[0][i] == board[2][i])) {
                return w;
            }
        }
        // diags
        if ((board[0][0] != 0) && (board[0][0] == board[1][1]) &&
                (board[0][0] == board[2][2])) {
            return w;
        }

        if ((board[2][0] != 0) && (board[2][0] == board[1][1]) &&
                (board[2][0] == board[0][2])) {
            return w;
        }

        // draw
        w = WinConfig.DRAW;
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++) {
                if (board[i][j] == 0)
                    w = WinConfig.NONE;
            }
        return w;

    }

    /** Define the mark for the player, when showing the board while input. Human get "X", AI get
     * "O".
     * Actually, the board is filled with player type internally (1 for AI, 2 for Human).
     * To display how the game is commonly played we use "x" and "O". */
    private String getRowString(int row) {
        String s = "";
        for (int i = 0; i < N; i++) {
            switch (board[row][i]) {
                case 0:
                    s += " ";
                    break;
                case 1:
                    s += "O";
                    break;
                case 2:
                    s += "X";
            }

            if (i != N - 1) {
                s += " | ";
            }
        }

        s += String.format("%" + HSPACE + "s", "");

        for (int i = 0; i < N; i++) {
            s += row * 3 + i + 1;

            if (i == N - 1) {
                s += "\n";
            } else {
                s += " | ";
            }
        }
        return s;
    }

    /** Display the board when requesting human to submit their move.
     *
     * @return the board map
     * */
    public String toString() {
        String s = "";
        // iterate through the rows
        for (int i = 0; i < N; i++) {
            s += getRowString(i);
        }
        return s;
    }

    public static void main(String[] args) {
        System.out.println("Welcome to Tic-Tac-Toe.");
        System.out.println("");

        // Initializes the game. Sets up the board.
        TicTacToe game = new TicTacToe();
        String move_str;
        int move1 = 0;
        int move2 = 0;
        int player_type = 0;
        WinConfig w = WinConfig.NONE;

        System.out.println("Please make your move selection by entering "
                + "a number 1-9 corresponding to the movement "
                + "key on the right.\n");
        System.out.println(game.toString());

        while (game.isWinningConfig() == WinConfig.NONE) {
            // first ask the human to play a move.
            // We are already checking for valid move in the human move class, but that doesn't
            // check if the new move made is the one that has already been submitted.
            // We use do-while here, because we set move1 as 0 by default, which is invalid, and we
            // need a valid move
            // made by user to continue the game. WE use while, as we need a valid input from
            // the user, so until the user submit a valid move, we keep on asking for the valid
            // move.
            do {
                move1 = game.getplayer1().getMove();
            } while (!game.setMove(move1, game.getplayer1().getPlayerType()));

            if ((w = game.isWinningConfig()) == WinConfig.WIN) {
                System.out.println("");
                System.out.println(game.toString());
                System.out.println("You have beaten my poor AI!");
                break;
            } else if (w == WinConfig.DRAW) {
                // We could have used new line at end of each print instead of calling these empty
                System.out.println("");
                System.out.println(game.toString());
                System.out.println("Well played. It is a draw!");
                break;
            }

            move2 = game.getplayer2().getMove();
            System.out.println("");
            System.out.println("You have put an X in the " +
                    TicTacToe.getPosDescription(move1) +
                    ". I will put a O in the " +
                    TicTacToe.getPosDescription(move2) + ".");

            game.setMove(move2, game.getplayer2().getPlayerType());

            // we have only checked for win here and not draw game for AI, as the last move
            // would always be played by human, as first is played by human as well. And we
            // cannot confirm as per the logic defined for the game, if game has a result until
            // all moves are played.
            if (game.isWinningConfig() == WinConfig.WIN) {
                System.out.println("");
                System.out.println(game.toString());
                System.out.println("I won. Thanks for playing.");
                break;
            }
            System.out.println(game.toString());
        }

    }
}
