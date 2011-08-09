package namespaceclosure.mojotools;

import java.util.HashMap;
import java.util.Map;

public enum ScopeType {
	COMPILE("compile"),
	TEST("test");
	
	public final String name;
	private static final Map<String, ScopeType> MAP_BY_NAME;
	static {
		MAP_BY_NAME = new HashMap<String, ScopeType>();
		for (ScopeType type : values()) {
			MAP_BY_NAME.put(type.name, type);
		}
	}
	
	private ScopeType(final String typeName) {
		this.name = typeName;
	}
	
	public static ScopeType getByName(final String name) {
		return MAP_BY_NAME.get(name);
	}
}
