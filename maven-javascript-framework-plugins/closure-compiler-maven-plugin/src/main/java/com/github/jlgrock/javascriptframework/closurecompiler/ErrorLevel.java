package com.github.jlgrock.javascriptframework.closurecompiler;

import java.util.HashMap;

public enum ErrorLevel {
	SIMPLE("SIMPLE"), WARNING("WARNING"), STRICT("STRICT");
	
	private final String name;
	
	public static HashMap<String, ErrorLevel> levels;
	
	static {
		levels = new HashMap<String, ErrorLevel>();
		for (ErrorLevel level : values()) {
			levels.put(level.name, level);
		}
	}
	ErrorLevel(final String nameIn) {
		name = nameIn;
	}
	
	public final static ErrorLevel getCompileLevelByName(String nameIn) {
		return levels.get(nameIn);
	}
}
