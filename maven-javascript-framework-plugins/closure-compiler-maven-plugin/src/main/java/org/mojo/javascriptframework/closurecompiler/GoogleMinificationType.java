package org.mojo.javascriptframework.closurecompiler;

import java.util.HashMap;
import java.util.Map;


public enum GoogleMinificationType {
	//google minification types
	GOOGLE_WHITESPACE("whitespace"),
	GOOGLE_SIMPLE("simple"),
	GOOGLE_ADVANCED("advanced");
	
	public final String name;
	private static final Map<String, GoogleMinificationType> MAP_BY_NAME;
	static {
		MAP_BY_NAME = new HashMap<String, GoogleMinificationType>();
		for (GoogleMinificationType type : values()) {
			MAP_BY_NAME.put(type.name, type);
		}
	}
	
	private GoogleMinificationType(final String typeName) {
		this.name = typeName;
	}
	
	public static GoogleMinificationType getByName(final String name) {
		return MAP_BY_NAME.get(name);
	}

}