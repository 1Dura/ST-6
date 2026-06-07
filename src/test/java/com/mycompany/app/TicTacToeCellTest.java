package com.mycompany.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

public class TicTacToeCellTest {
    @Before
    public void setUp() {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    public void cellStoresCoordinatesAndMarker() {
        TicTacToeCell cell = new TicTacToeCell(5, 2, 1);
        assertEquals(5, cell.getNum());
        assertEquals(2, cell.getCol());
        assertEquals(1, cell.getRow());
        assertEquals(Game.EMPTY, cell.getMarker());

        cell.setMarker('X');
        assertEquals('X', cell.getMarker());
        assertEquals("X", cell.getText());
        assertFalse(cell.isEnabled());
    }
}
