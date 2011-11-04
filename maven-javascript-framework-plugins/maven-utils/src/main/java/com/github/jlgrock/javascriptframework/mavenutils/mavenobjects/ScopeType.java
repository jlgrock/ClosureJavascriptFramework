package com.github.jlgrock.javascriptframework.mavenutils.mavenobjects;

import java.util.HashMap;
import java.util.Map;

/**
 * The scope types recognized by the lifecycles.
 */
public enum ScopeType {
	/**
	 * Compile scope, used when compiling the project.
	 */
	COMPILE("compile"), TEST("test"), ANY(null);
	
	/**
	 * The name of the Scope.
	 */
	private final String name;
	
	/**
	 * A hash of all Scope types.
	 */
	private static final Map<String, ScopeType> MAP_BY_NAME;
	static {
		MAP_BY_NAME = new HashMap<String, ScopeType>();
		for (ScopeType type : values()) {
			MAP_BY_NAME.put(type.name, type);
		}
	}
	
	/**
	 * Constructor.
	 * 
	 * @param typeName the name to store in the Scope.
	 */
	private ScopeType(final String typeName) {
		this.name = typeName;
	}
	
	/**
	 * Get the scope static variable by passing in the string.
	 * 
	 * @param name the name to check against
	 * @return the enumerated scope type equivalent (if one exists)
	 */
	public static ScopeType getByName(final String name) {
		return MAP_BY_NAME.get(name);
	}
}
