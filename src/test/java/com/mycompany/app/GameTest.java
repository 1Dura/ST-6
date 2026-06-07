package com.mycompany.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class GameTest {
    private Game game;

    @Before
    public void setUp() {
        System.setProperty("java.awt.headless", "true");
        game = new Game();
    }

    @Test
    public void newGameStartsWithEmptyBoardAndXTurn() {
        assertEquals('X', game.getCurrentPlayer().getSymbol());
        for (char cell : game.copyBoard()) {
            assertEquals(Game.EMPTY, cell);
        }
    }

    @Test
    public void makeMoveWritesMarkerAndSwitchesPlayer() {
        assertTrue(game.makeMove(4));
        assertEquals('X', game.cellAt(4));
        assertEquals('O', game.getCurrentPlayer().getSymbol());
    }

    @Test
    public void makeMoveRejectsFilledAndOutOfRangeCells() {
        assertTrue(game.makeMove(0));
        assertFalse(game.makeMove(0));
        assertFalse(game.makeMove(-1));
        assertFalse(game.makeMove(9));
    }

    @Test
    public void checkStateFindsRowsColumnsAndDiagonals() {
        assertEquals(State.XWIN, game.checkState(new char[] {
            'X', 'X', 'X',
            ' ', 'O', ' ',
            'O', ' ', ' '
        }));
        assertEquals(State.OWIN, game.checkState(new char[] {
            'X', ' ', 'O',
            'X', ' ', 'O',
            ' ', ' ', 'O'
        }));
        assertEquals(State.XWIN, game.checkState(new char[] {
            'X', 'O', ' ',
            ' ', 'X', 'O',
            ' ', ' ', 'X'
        }));
    }

    @Test
    public void checkStateFindsDrawAndPlayingPositions() {
        assertEquals(State.DRAW, game.checkState(new char[] {
            'X', 'O', 'X',
            'X', 'O', 'O',
            'O', 'X', 'X'
        }));
        assertEquals(State.PLAYING, game.checkState(new char[] {
            'X', 'O', 'X',
            ' ', 'O', ' ',
            ' ', 'X', ' '
        }));
    }

    @Test
    public void generateMovesReturnsOnlyEmptyCells() {
        List<Integer> moves = game.generateMoves(new char[] {
            'X', ' ', 'O',
            ' ', ' ', 'X',
            'O', 'X', ' '
        });
        assertEquals(4, moves.size());
        assertTrue(moves.contains(Integer.valueOf(1)));
        assertFalse(moves.contains(Integer.valueOf(0)));
    }

    @Test
    public void evaluatePositionScoresWinLossDrawAndOpenGame() {
        Player x = new Player('X');
        assertEquals(Game.WIN_SCORE, game.evaluatePosition(new char[] {
            'X', 'X', 'X',
            ' ', 'O', ' ',
            ' ', ' ', 'O'
        }, x));
        assertEquals(-Game.WIN_SCORE, game.evaluatePosition(new char[] {
            'O', 'O', 'O',
            ' ', 'X', ' ',
            ' ', ' ', 'X'
        }, x));
        assertEquals(0, game.evaluatePosition(new char[] {
            'X', 'O', 'X',
            'X', 'O', 'O',
            'O', 'X', 'X'
        }, x));
        assertEquals(-1, game.evaluatePosition(new char[] {
            'X', ' ', ' ',
            ' ', 'O', ' ',
            ' ', ' ', ' '
        }, x));
    }

    @Test
    public void minimaxWinsWhenWinningMoveExists() {
        Player o = new Player('O');
        int move = game.bestMove(new char[] {
            'O', 'O', ' ',
            'X', 'X', ' ',
            ' ', ' ', ' '
        }, o);
        assertEquals(2, move);
    }

    @Test
    public void minimaxBlocksImmediateLoss() {
        Player o = new Player('O');
        int move = game.bestMove(new char[] {
            'X', 'X', ' ',
            ' ', 'O', ' ',
            ' ', ' ', ' '
        }, o);
        assertEquals(2, move);
    }

    @Test
    public void resetClearsBoardAndReturnsToX() {
        game.makeMove(0);
        game.makeMove(1);
        game.reset();
        assertEquals('X', game.getCurrentPlayer().getSymbol());
        for (char cell : game.copyBoard()) {
            assertEquals(Game.EMPTY, cell);
        }
    }
}
