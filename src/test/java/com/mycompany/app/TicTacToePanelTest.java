package com.mycompany.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;
import org.junit.Before;
import org.junit.Test;

public class TicTacToePanelTest {
    @Before
    public void setUp() {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    public void panelCreatesNineCells() throws Exception {
        TicTacToePanel panel = new TicTacToePanel(new GridLayout(3, 3));
        TicTacToeCell[] cells = cellsOf(panel);

        assertEquals(9, cells.length);
        assertEquals(9, panel.getComponentCount());
        assertNotNull(cells[0]);
    }

    @Test
    public void actionAddsHumanAndComputerMoves() throws Exception {
        TicTacToePanel panel = new TicTacToePanel(new GridLayout(3, 3));
        TicTacToeCell[] cells = cellsOf(panel);

        panel.actionPerformed(new ActionEvent(cells[0], ActionEvent.ACTION_PERFORMED, ""));

        int filled = 0;
        for (TicTacToeCell cell : cells) {
            if (cell.getMarker() != Game.EMPTY) {
                filled++;
            }
        }
        assertEquals(2, filled);
        assertEquals('X', cells[0].getMarker());
    }

    private TicTacToeCell[] cellsOf(TicTacToePanel panel) throws Exception {
        Field field = TicTacToePanel.class.getDeclaredField("cells");
        field.setAccessible(true);
        return (TicTacToeCell[]) field.get(panel);
    }
}
