import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class IdealTicTacToe {

    private char playerHuman = 'h';
    private char playerComputer = 'c';
    private char playerChance = playerHuman;
    private Integer[] positionSelected;

    private void playGame(char[][] gameBoard) {
        final Game game = new Game(gameBoard);

        while(game.getAvailableMoves().size() > 0) {
            int value = calculateMinimax(game);
            System.out.println(value);
            game.updatedGameBoard(positionSelected ,playerChance);
            playerChance = (playerChance == playerHuman) ? playerComputer : playerHuman;
        }
        if(game.isGameOver()) {
            if(game.winner == ' ') {
                System.out.println("MAtch drawn");
            } else {
                System.out.println(game.winner);
            }
        }
    }

    private int getScore(final Game game) {
        if(game.winner == playerHuman) {
            return 10;
        } else if(game.winner == playerComputer) {
            return -10;
        } else {
            return 0;
        }
    }

    private int calculateMinimax(final Game game) {
        if(game.isGameOver()) {
            System.out.println("data");
            return getScore(game);
        }

        List<Integer> scores = new ArrayList<>();
        List<Integer[]> moves = new ArrayList<>();

        // move location is the coordinates of the available location. We play and check minimax
        // for each available location.
        for(final Integer[] moveLocation : game.getAvailableMoves()) {
            final Game updatedGame = game.updatedGameBoard(moveLocation, playerChance);
            scores.add(calculateMinimax(updatedGame));
            moves.add(moveLocation);
        }

        if(playerChance == playerHuman) {
            final Integer maxValue = Collections.max(scores);
            positionSelected = moves.get(scores.indexOf(maxValue));
            return maxValue;
        } else {
            final Integer minValue = Collections.min(scores);
            positionSelected = moves.get(scores.indexOf(minValue));
            return minValue;
        }
    }

    public static void main(String[] args) {
        final int gameSize = 3;
        char[][] gameBoard = new char[gameSize][gameSize];

        for(char[] boardRow : gameBoard) {
            Arrays.fill(boardRow, ' ');
        }

        new IdealTicTacToe().playGame(gameBoard);
    }

    class Game {
        char[][] gameBoard;
        final List<Integer[]> availableMoves = new ArrayList<>();
        public char winner = ' ';

        Game(final char[][] gameBoard) {
            this.gameBoard = gameBoard;
        }

        public Game updatedGameBoard(final Integer[] movePosition, final char player) {
            System.out.println(Arrays.toString(movePosition));
            gameBoard[movePosition[0]][movePosition[1]] = player;

            return this;
        }

        public List<Integer[]> getAvailableMoves() {
            for(int i=0; i < gameBoard.length; i++) {
                for (int j=0; j < gameBoard[0].length; j++) {
                    if(gameBoard[i][j] == ' ') {
                        availableMoves.add(new Integer[]{i,j});
                    }
                }
            }

            return availableMoves;
        }

        public boolean isGameOver() {
            for(final char[] row: gameBoard) {
                Predicate<Character> checkRowValues = Predicate.isEqual(row[0]);

                Stream<Character> myStreamOfCharacters = IntStream
                        .range(0, row.length)
                        .mapToObj(i -> row[i]);

                boolean winnerStatus = myStreamOfCharacters.allMatch(checkRowValues);
                if (winnerStatus) {
                    winner = row[0];
                }

                return winnerStatus;
            }

            for(int i = 0; i < gameBoard[0].length; i++) {
                final char columnValueToCheck = gameBoard[0][i];
                boolean check = true;

                for(int j = 0; j < gameBoard.length; j++) {
                    if(gameBoard[i][j] != columnValueToCheck) {
                        check = false;
                        break;
                    }
                }

                if(check) {
                    winner = columnValueToCheck;
                }

                return check;
            }

            int flag = 1;

            for(int i =1; i< gameBoard.length; i++) {
                if (gameBoard[i][i] == gameBoard[0][0]) {
                    continue;
                } else {
                    flag =0;
                    return false;
                }
            }
            if(flag!=0) {
                winner = gameBoard[0][0];
                return true;
            }

            for(int i = gameBoard.length-1; i>= 0; i--) {
                if (gameBoard[i][i] == gameBoard[gameBoard.length-1][gameBoard.length-1]) {
                    continue;
                } else {
                    flag =0;
                    return false;
                }
            }
            if(flag!=0) {
                winner = gameBoard[gameBoard.length-1][gameBoard.length-1];
                return true;
            }

            return false;
        }
    }
}
