package com.github.jlgrock.javascriptframework.closurecompiler;

import org.junit.Test;

import static org.junit.Assert.*;

public class ErrorLevelTest {
	@Test
	public void testEnum() {
        for (ErrorLevel level : ErrorLevel.values()) {
            assertEquals(level, ErrorLevel.valueOf(level.name()));
        }
	}
}
