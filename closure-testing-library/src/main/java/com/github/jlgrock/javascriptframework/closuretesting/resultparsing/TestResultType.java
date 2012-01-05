package com.github.jlgrock.javascriptframework.closuretesting.resultparsing;

import java.util.HashMap;
import java.util.Map;

/**
 * Possible results of tests.
 */
public enum TestResultType {
	/**
	 * Possible results of tests.
	 */
	PASSED("PASSED"), FAILED("FAILED");
	
	/**
	 * The string equivalent.
	 */
	private String name;

	
	/**
	 * A map that allows for searching by name.
	 */
	private static final Map<String, TestResultType> MAP_BY_NAME;

	static {
		MAP_BY_NAME = new HashMap<String, TestResultType>();
		for (TestResultType type : values()) {
			MAP_BY_NAME.put(type.name, type);
		}
	}
	
	/**
	 * Constructor.
	 * 
	 * @param nameIn the string to match the enumeration type
	 */
	TestResultType(final String nameIn) {
		this.name = nameIn;
	}

	/**
	 * Do a lookup of the enumeration by the name string.
	 * 
	 * @param name
	 *            the name string value
	 * @return the enumerated packaging type
	 */
	public static TestResultType getByName(final String name) {
		return MAP_BY_NAME.get(name);
	}

}
