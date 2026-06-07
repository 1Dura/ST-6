package com.mycompany.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Test;

public class UtilityTest {
    @Test
    public void formatsCharIntAndMoveBoards() {
        assertEquals("X-O-", Utility.boardLine(new char[] {'X', 'O'}));
        assertEquals("1-2-3-", Utility.boardLine(new int[] {1, 2, 3}));
        assertEquals("0-4-", Utility.moveLine(Arrays.asList(
                Integer.valueOf(0), Integer.valueOf(4))));
    }

    @Test
    public void printWritesLineToStdout() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream oldOut = System.out;
        try {
            System.setOut(new PrintStream(output));
            ArrayList<Integer> moves = new ArrayList<Integer>();
            moves.add(Integer.valueOf(1));
            moves.add(Integer.valueOf(8));
            Utility.print(moves);
        } finally {
            System.setOut(oldOut);
        }
        assertTrue(output.toString().contains("1-8-"));
    }
}
