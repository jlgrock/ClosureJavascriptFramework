package jsPreparserCompiler.minifier;

import java.util.HashMap;
import java.util.Map;


public enum MinificationType {
	//google minification types
	GOOGLE_WHITESPACE("WhitespaceOnlyGoogleCompiler"),
	GOOGLE_SIMPLE("SimpleGoogleCompiler"),
	GOOGLE_ADVANCED("AdvancedGoogleCompiler"),

	//yui
	YUI("YuiMinifier"),
	
	//No minification
	NONE("none")
	;
	
	public final String name;
	private static final Map<String, MinificationType> MAP_BY_NAME;
	static {
		MAP_BY_NAME = new HashMap<String, MinificationType>();
		for (MinificationType type : values()) {
			MAP_BY_NAME.put(type.name, type);
		}
	}
	
	private MinificationType(final String typeName) {
		this.name = typeName;
	}
	
	public static MinificationType getByName(final String name) {
		return MAP_BY_NAME.get(name);
	}
}