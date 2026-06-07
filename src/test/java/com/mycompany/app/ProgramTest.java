package com.mycompany.app;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.junit.Test;

public class ProgramTest {
    @Test
    public void mainMethodExists() throws Exception {
        Method main = Program.class.getMethod("main", String[].class);
        assertTrue(Modifier.isStatic(main.getModifiers()));
    }
}
