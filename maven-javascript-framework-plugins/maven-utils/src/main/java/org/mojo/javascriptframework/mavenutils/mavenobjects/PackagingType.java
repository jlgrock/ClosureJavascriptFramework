package org.mojo.javascriptframework.mavenutils.mavenobjects;

import java.util.HashMap;
import java.util.Map;

/**
 * The types of packaging that the current lifecycles can support.
 */
public enum PackagingType {
	/**
	 * The valid packaging types.
	 */
	JAR("jar"), WAR("war"), JSAR("jsar"), FJSAR("fjsar");

	/**
	 * The name of the packaging type.
	 */
	private final String name;

	/**
	 * A map that allows for searching by name.
	 */
	private static final Map<String, PackagingType> MAP_BY_NAME;
	static {
		MAP_BY_NAME = new HashMap<String, PackagingType>();
		for (PackagingType type : values()) {
			MAP_BY_NAME.put(type.name, type);
		}
	}

	/**
	 * Constructor, requires name.
	 * 
	 * @param typeName the name of the enumerated type
	 */
	private PackagingType(final String typeName) {
		this.name = typeName;
	}

	/**
	 * Do a lookup of the enumeration by the name string.
	 * 
	 * @param name
	 *            the name string value
	 * @return the enumerated packaging type
	 */
	public static PackagingType getByName(final String name) {
		return MAP_BY_NAME.get(name);
	}
}
