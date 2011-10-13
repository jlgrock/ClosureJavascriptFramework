package org.mojo.javascriptframework.mavenutils.mavenobjects;

import java.util.HashMap;
import java.util.Map;

public enum PackagingType {
	JAR("jar"),
	WAR("war"),
	JSAR("jsar");
	
	public final String name;
	private static final Map<String, PackagingType> MAP_BY_NAME;
	static {
		MAP_BY_NAME = new HashMap<String, PackagingType>();
		for (PackagingType type : values()) {
			MAP_BY_NAME.put(type.name, type);
		}
	}
	
	private PackagingType(final String typeName) {
		this.name = typeName;
	}
	
	public static PackagingType getByName(final String name) {
		return MAP_BY_NAME.get(name);
	}
}
