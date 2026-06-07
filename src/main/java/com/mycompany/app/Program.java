package com.mycompany.app;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

enum State {
    PLAYING,
    OWIN,
    XWIN,
    DRAW
}

final class BoardGrid {
    static final char EMPTY = ' ';
    static final int SIZE = 9;

    private static final int[][] WIN_LINES = {
        {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
        {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
        {0, 4, 8}, {2, 4, 6}
    };

    private final char[] cells = new char[SIZE];

    BoardGrid() {
        clear();
    }

    void clear() {
        Arrays.fill(cells, EMPTY);
    }

    char[] snapshot() {
        return cells.clone();
    }

    char cellAt(int index) {
        return cells[index];
    }

    boolean isOpen(int index) {
        return index >= 0 && index < cells.length && cells[index] == EMPTY;
    }

    boolean place(int index, char symbol) {
        if (!isOpen(index)) {
            return false;
        }
        cells[index] = symbol;
        return true;
    }

    List<Integer> openCells(char[] position) {
        List<Integer> cellsToPlay = new ArrayList<Integer>();
        for (int i = 0; i < position.length; i++) {
            if (position[i] == EMPTY) {
                cellsToPlay.add(Integer.valueOf(i));
            }
        }
        return cellsToPlay;
    }

    State classify(char[] position) {
        char winner = winnerOf(position);
        if (winner == 'X') {
            return State.XWIN;
        }
        if (winner == 'O') {
            return State.OWIN;
        }
        return openCells(position).isEmpty() ? State.DRAW : State.PLAYING;
    }

    private char winnerOf(char[] position) {
        for (int[] line : WIN_LINES) {
            char mark = position[line[0]];
            if (mark != EMPTY
                    && mark == position[line[1]]
                    && mark == position[line[2]]) {
                return mark;
            }
        }
        return EMPTY;
    }
}

final class MinimaxPlanner {
    static final int WIN_SCORE = 100;

    private static final int[] MOVE_PRIORITY = {
        3, 1, 3,
        1, 4, 1,
        3, 1, 3
    };

    int chooseMove(char[] position, char symbol) {
        int bestMove = -1;
        int bestScore = Integer.MIN_VALUE;

        for (Integer move : availableMoves(position)) {
            int index = move.intValue();
            position[index] = symbol;
            int score = scoreForOpponent(position, symbol);
            position[index] = BoardGrid.EMPTY;

            if (score > bestScore
                    || (score == bestScore && isBetterMove(index, bestMove))) {
                bestScore = score;
                bestMove = index;
            }
        }

        return bestMove;
    }

    int scoreForPlayer(char[] position, char symbol) {
        int stateScore = terminalScore(position, symbol);
        if (stateScore != Integer.MIN_VALUE) {
            return stateScore;
        }

        int best = Integer.MIN_VALUE;
        for (Integer move : availableMoves(position)) {
            int index = move.intValue();
            position[index] = symbol;
            best = Math.max(best, scoreForOpponent(position, symbol));
            position[index] = BoardGrid.EMPTY;
        }
        return best;
    }

    int scoreForOpponent(char[] position, char symbol) {
        int stateScore = terminalScore(position, symbol);
        if (stateScore != Integer.MIN_VALUE) {
            return stateScore;
        }

        int worst = Integer.MAX_VALUE;
        char rival = rivalOf(symbol);
        for (Integer move : availableMoves(position)) {
            int index = move.intValue();
            position[index] = rival;
            worst = Math.min(worst, scoreForPlayer(position, symbol));
            position[index] = BoardGrid.EMPTY;
        }
        return worst;
    }

    int evaluate(char[] position, char symbol) {
        int terminalScore = terminalScore(position, symbol);
        if (terminalScore != Integer.MIN_VALUE) {
            return terminalScore;
        }
        return -1;
    }

    List<Integer> availableMoves(char[] position) {
        List<Integer> moves = new ArrayList<Integer>();
        for (int i = 0; i < position.length; i++) {
            if (position[i] == BoardGrid.EMPTY) {
                moves.add(Integer.valueOf(i));
            }
        }
        return moves;
    }

    private int terminalScore(char[] position, char symbol) {
        char winner = winnerOf(position);
        if (winner == BoardGrid.EMPTY) {
            return isDraw(position) ? 0 : Integer.MIN_VALUE;
        }
        return winner == symbol ? WIN_SCORE : -WIN_SCORE;
    }

    private boolean isDraw(char[] position) {
        for (char cell : position) {
            if (cell == BoardGrid.EMPTY) {
                return false;
            }
        }
        return true;
    }

    private char winnerOf(char[] position) {
        int[][] lines = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6}
        };

        for (int[] line : lines) {
            char mark = position[line[0]];
            if (mark != BoardGrid.EMPTY
                    && mark == position[line[1]]
                    && mark == position[line[2]]) {
                return mark;
            }
        }
        return BoardGrid.EMPTY;
    }

    private char rivalOf(char symbol) {
        return symbol == 'X' ? 'O' : 'X';
    }

    private boolean isBetterMove(int candidate, int incumbent) {
        if (incumbent < 0) {
            return true;
        }
        return MOVE_PRIORITY[candidate] > MOVE_PRIORITY[incumbent];
    }
}

final class Player {
    private final char symbol;

    Player(char symbol) {
        this.symbol = symbol;
    }

    char getSymbol() {
        return symbol;
    }
}

final class Game {
    static final char EMPTY = BoardGrid.EMPTY;
    static final int WIN_SCORE = MinimaxPlanner.WIN_SCORE;

    private final BoardGrid board = new BoardGrid();
    private final MinimaxPlanner planner = new MinimaxPlanner();
    private final Player xPlayer = new Player('X');
    private final Player oPlayer = new Player('O');
    private Player currentPlayer = xPlayer;

    void reset() {
        board.clear();
        currentPlayer = xPlayer;
    }

    Player getCurrentPlayer() {
        return currentPlayer;
    }

    Player getComputerPlayer() {
        return oPlayer;
    }

    char[] copyBoard() {
        return board.snapshot();
    }

    char cellAt(int index) {
        return board.cellAt(index);
    }

    boolean makeMove(int index) {
        if (checkState(board.snapshot()) != State.PLAYING) {
            return false;
        }
        if (!board.place(index, currentPlayer.getSymbol())) {
            return false;
        }
        currentPlayer = currentPlayer == xPlayer ? oPlayer : xPlayer;
        return true;
    }

    State checkState(char[] position) {
        return board.classify(position);
    }

    List<Integer> generateMoves(char[] position) {
        return board.openCells(position);
    }

    int evaluatePosition(char[] position, Player player) {
        return planner.evaluate(position, player.getSymbol());
    }

    int bestMoveFor(Player player) {
        return planner.chooseMove(board.snapshot(), player.getSymbol());
    }

    int bestMove(char[] position, Player player) {
        return planner.chooseMove(position, player.getSymbol());
    }

    int scoreForOpponentTurn(char[] position, Player player) {
        return planner.scoreForOpponent(position, player.getSymbol());
    }

    int scoreForPlayerTurn(char[] position, Player player) {
        return planner.scoreForPlayer(position, player.getSymbol());
    }
}

public class Program {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Tic Tac Toe");
                frame.add(new TicTacToePanel(new GridLayout(3, 3)));
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setBounds(5, 5, 500, 500);
                frame.setVisible(true);
            }
        });
    }
}

final class TicTacToeCell extends JButton {
    private final int num;
    private final int row;
    private final int col;
    private char marker;

    TicTacToeCell(int num, int x, int y) {
        this.num = num;
        this.row = y;
        this.col = x;
        this.marker = BoardGrid.EMPTY;
        setText(Character.toString(marker));
        setFont(new Font("Arial", Font.PLAIN, 40));
    }

    void setMarker(char marker) {
        this.marker = marker;
        setText(Character.toString(marker));
        setEnabled(false);
    }

    char getMarker() {
        return marker;
    }

    int getRow() {
        return row;
    }

    int getCol() {
        return col;
    }

    int getNum() {
        return num;
    }
}

final class Utility {
    static String boardLine(char[] board) {
        StringBuilder result = new StringBuilder();
        for (char cell : board) {
            result.append(cell).append('-');
        }
        return result.toString();
    }

    static String boardLine(int[] board) {
        StringBuilder result = new StringBuilder();
        for (int cell : board) {
            result.append(cell).append('-');
        }
        return result.toString();
    }

    static String moveLine(List<Integer> moves) {
        StringBuilder result = new StringBuilder();
        for (Integer move : moves) {
            result.append(move).append('-');
        }
        return result.toString();
    }

    public static void print(char[] board) {
        System.out.println(boardLine(board));
    }

    public static void print(int[] board) {
        System.out.println(boardLine(board));
    }

    public static void print(ArrayList<Integer> moves) {
        System.out.println(moveLine(moves));
    }
}

final class TicTacToePanel extends JPanel implements ActionListener {
    private final Game game;
    private final TicTacToeCell[] cells = new TicTacToeCell[BoardGrid.SIZE];

    TicTacToePanel(GridLayout layout) {
        super(layout);
        game = new Game();
        for (int i = 0; i < cells.length; i++) {
            createCell(i, i % 3, i / 3);
        }
    }

    private void createCell(int num, int x, int y) {
        cells[num] = new TicTacToeCell(num, x, y);
        cells[num].addActionListener(this);
        add(cells[num]);
    }

    public void actionPerformed(ActionEvent event) {
        TicTacToeCell selected = (TicTacToeCell) event.getSource();
        if (!game.makeMove(selected.getNum())) {
            return;
        }
        selected.setMarker('X');
        if (finishIfNeeded()) {
            return;
        }

        int computerMove = game.bestMoveFor(game.getComputerPlayer());
        if (computerMove >= 0 && game.makeMove(computerMove)) {
            cells[computerMove].setMarker('O');
        }
        finishIfNeeded();
    }

    private boolean finishIfNeeded() {
        State state = game.checkState(game.copyBoard());
        if (state == State.PLAYING) {
            return false;
        }
        if (!java.awt.GraphicsEnvironment.isHeadless()) {
            JOptionPane.showMessageDialog(this, state.toString(), "Result",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        return true;
    }
}
