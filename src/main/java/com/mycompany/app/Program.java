package com.mycompany.app;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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

class Player {
    private final char symbol;

    Player(char symbol) {
        this.symbol = symbol;
    }

    char getSymbol() {
        return symbol;
    }
}

class Game {
    static final char EMPTY = ' ';
    static final int BOARD_SIZE = 9;
    static final int WIN_SCORE = 100;

    private final Player xPlayer = new Player('X');
    private final Player oPlayer = new Player('O');
    private final char[] board = new char[BOARD_SIZE];
    private Player currentPlayer = xPlayer;

    Game() {
        reset();
    }

    void reset() {
        for (int i = 0; i < board.length; i++) {
            board[i] = EMPTY;
        }
        currentPlayer = xPlayer;
    }

    Player getCurrentPlayer() {
        return currentPlayer;
    }

    Player getComputerPlayer() {
        return oPlayer;
    }

    char[] copyBoard() {
        return board.clone();
    }

    char cellAt(int index) {
        return board[index];
    }

    boolean makeMove(int index) {
        if (index < 0 || index >= board.length || board[index] != EMPTY
                || checkState(board) != State.PLAYING) {
            return false;
        }
        board[index] = currentPlayer.getSymbol();
        currentPlayer = currentPlayer == xPlayer ? oPlayer : xPlayer;
        return true;
    }

    State checkState(char[] position) {
        char winner = winnerOf(position);
        if (winner == 'X') {
            return State.XWIN;
        }
        if (winner == 'O') {
            return State.OWIN;
        }
        return generateMoves(position).isEmpty() ? State.DRAW : State.PLAYING;
    }

    List<Integer> generateMoves(char[] position) {
        List<Integer> moves = new ArrayList<Integer>();
        for (int i = 0; i < position.length; i++) {
            if (position[i] == EMPTY) {
                moves.add(Integer.valueOf(i));
            }
        }
        return moves;
    }

    int evaluatePosition(char[] position, Player player) {
        State state = checkState(position);
        if (state == State.DRAW) {
            return 0;
        }
        if (state == State.PLAYING) {
            return -1;
        }
        char winner = state == State.XWIN ? 'X' : 'O';
        return winner == player.getSymbol() ? WIN_SCORE : -WIN_SCORE;
    }

    int bestMoveFor(Player player) {
        return bestMove(copyBoard(), player);
    }

    int bestMove(char[] position, Player player) {
        int bestMove = -1;
        int bestScore = -WIN_SCORE - 1;
        List<Integer> moves = generateMoves(position);

        for (Integer move : moves) {
            position[move.intValue()] = player.getSymbol();
            int score = scoreForOpponentTurn(position, player);
            position[move.intValue()] = EMPTY;
            if (score > bestScore) {
                bestScore = score;
                bestMove = move.intValue();
            }
        }
        return bestMove;
    }

    int scoreForOpponentTurn(char[] position, Player player) {
        int score = evaluatePosition(position, player);
        if (score != -1) {
            return score;
        }

        int worstScore = WIN_SCORE + 1;
        char opponent = opponentOf(player.getSymbol());
        for (Integer move : generateMoves(position)) {
            position[move.intValue()] = opponent;
            worstScore = Math.min(worstScore, scoreForPlayerTurn(position, player));
            position[move.intValue()] = EMPTY;
        }
        return worstScore;
    }

    int scoreForPlayerTurn(char[] position, Player player) {
        int score = evaluatePosition(position, player);
        if (score != -1) {
            return score;
        }

        int bestScore = -WIN_SCORE - 1;
        for (Integer move : generateMoves(position)) {
            position[move.intValue()] = player.getSymbol();
            bestScore = Math.max(bestScore, scoreForOpponentTurn(position, player));
            position[move.intValue()] = EMPTY;
        }
        return bestScore;
    }

    private static char opponentOf(char symbol) {
        return symbol == 'X' ? 'O' : 'X';
    }

    private static char winnerOf(char[] position) {
        int[][] lines = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6}
        };

        for (int[] line : lines) {
            char mark = position[line[0]];
            if (mark != EMPTY && mark == position[line[1]]
                    && mark == position[line[2]]) {
                return mark;
            }
        }
        return EMPTY;
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

class TicTacToeCell extends JButton {
    private final int num;
    private final int row;
    private final int col;
    private char marker;

    TicTacToeCell(int num, int x, int y) {
        this.num = num;
        this.row = y;
        this.col = x;
        this.marker = Game.EMPTY;
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

class Utility {
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

class TicTacToePanel extends JPanel implements ActionListener {
    private final Game game;
    private final TicTacToeCell[] cells = new TicTacToeCell[Game.BOARD_SIZE];

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
