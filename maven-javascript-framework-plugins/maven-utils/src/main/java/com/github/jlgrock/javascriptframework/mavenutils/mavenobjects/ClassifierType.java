package org.mojo.javascriptframework.mavenutils.mavenobjects;

import java.util.HashMap;
import java.util.Map;

public enum ClassifierType {
	INTERNAL("internal"),
	EXTERNAL("external");
	
	public final String name;
	private static final Map<String, ClassifierType> MAP_BY_NAME;
	static {
		MAP_BY_NAME = new HashMap<String, ClassifierType>();
		for (ClassifierType type : values()) {
			MAP_BY_NAME.put(type.name, type);
		}
	}
	
	private ClassifierType(final String name) {
		this.name = name;
	}
	
	public static ClassifierType getByName(final String name) {
		return MAP_BY_NAME.get(name);
	}
}
