package com.github.jlgrock.javascriptframework.closurecompiler;

import java.util.HashMap;

/**
 * The enumerated Google warning levels.
 */
public enum ErrorLevel {
	/**
	 * The possible error levels.
	 */
	NONE("NONE"), SIMPLE("SIMPLE"), WARNING("WARNING"), STRICT("STRICT");
	
	/**
	 * The string that identifies the error level.
	 */
	private final String name;
	
	/**
	 * A hashmap used for lookup.
	 */
	private static final HashMap<String, ErrorLevel> LEVELS;
	
	static {
		LEVELS = new HashMap<String, ErrorLevel>();
		for (ErrorLevel level : values()) {
			LEVELS.put(level.name, level);
		}
	}
	/**
	 * The enum constructor.
	 * @param nameIn the name of the enumeration
	 */
	ErrorLevel(final String nameIn) {
		name = nameIn;
	}
	
	/**
	 * Get the compile level by looking up via the name.
	 * @param nameIn the name
	 * @return the enumeration level
	 */
	public static final ErrorLevel getCompileLevelByName(final String nameIn) {
		return LEVELS.get(nameIn);
	}
}
