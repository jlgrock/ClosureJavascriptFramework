package com.github.jlgrock.javascriptframework.closurecompiler;

import org.junit.Test;

public class ErrorLevelTest {
	@Test
	public void testEnum() {
		ErrorLevel.SIMPLE.equals(ErrorLevel.getCompileLevelByName("SIMPLE"));
		ErrorLevel.WARNING.equals(ErrorLevel.getCompileLevelByName("WARNING"));
		ErrorLevel.STRICT.equals(ErrorLevel.getCompileLevelByName("STRICT"));

		ErrorLevel.SIMPLE.equals(ErrorLevel.getCompileLevelByName("Simple"));
		ErrorLevel.WARNING.equals(ErrorLevel.getCompileLevelByName("Warning"));
		ErrorLevel.STRICT.equals(ErrorLevel.getCompileLevelByName("Strict"));
	}
}
