package com.github.jlgrock.javascriptframework.closurecompiler;

import org.junit.Test;

import com.google.javascript.jscomp.CompilationLevel;

public class CompilationLevelTest {
	@Test
	public void testCompilationLevel() {
		CompilationLevel.SIMPLE_OPTIMIZATIONS.equals(CompilationLevel.valueOf("SIMPLE_OPTIMIZATIONS"));
		CompilationLevel.WHITESPACE_ONLY.equals(CompilationLevel.valueOf("WHITESPACE_ONLY"));
		CompilationLevel.ADVANCED_OPTIMIZATIONS.equals(CompilationLevel.valueOf("ADVANCED_OPTIMIZATIONS"));
	}
	
	
}
