package com.github.jlgrock.javascriptframework.closurecompiler;

/**
 * Represents <code>@define</code> compiler option.
 */
public final class Define {
	/**
	 * The name of the compiler object to set.
	 */
	private String defineName;
	
	/**
	 * The compiler value to set for this name.
	 */
	private String value;
	
	/**
	 * The type (Boolean, String, Integer, Double) to parse to in the compiler.
	 */
	private String valueType;

	/**
	 * @return The name of the object to set.
	 */
	public String getDefineName() {
		return defineName;
	}

	/**
	 * @return The compiler value to set for this name.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @return The type (Boolean, String, Integer, Double) to parse to in the compiler.
	 */
	public String getValueType() {
		return valueType;
	}
}
