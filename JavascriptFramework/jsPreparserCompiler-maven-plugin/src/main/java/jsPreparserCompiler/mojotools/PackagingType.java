package jsPreparserCompiler.mojotools;

import java.util.HashMap;
import java.util.Map;

public enum PackagingType {
	WAR("war"),
	JSJAR("jsjar");
	
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
