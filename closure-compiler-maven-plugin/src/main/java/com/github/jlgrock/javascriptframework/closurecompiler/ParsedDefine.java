package com.github.jlgrock.javascriptframework.closurecompiler;

import java.util.HashMap;

/**
 * Strictly typed version of {@link com.github.jlgrock.javascriptframework.closurecompiler.Define Define}.
 */
public class ParsedDefine {

	public enum Type {
		/**
		 * Supported types.
		 */
		BOOLEAN("boolean"), DOUBLE("double"), STRING("string"), INTEGER("integer");

		/**
		 * The type string.
		 */
		private String type;

		/**
		 * The hashmap used for lookups.
		 */
		public static final HashMap<String, Type> TYPES;

		static {
			TYPES = new HashMap<String, Type>();
			for (Type p : values()) {
				TYPES.put(p.type, p);
			}
		}

		/**
		 * The private constructor.
		 * @param typeIn the type used for lookups
		 */
		Type (final String typeIn) {
			this.type = typeIn;
		}

		/**
		 * Lookup the inclusion strategy by the type.
		 * @param key the type to look for
		 * @return the appropriate inclusion strategy
		 */
		public static Type getByType(final String key) {
			return TYPES.get(key.toLowerCase());
		}
	}

	private String defineName;
	private Object value;
	private Class valueType;

	public void setDefineName(String defineName) {
		this.defineName = defineName;
	}
	public String getDefineName() {
		return this.defineName;
	}

	public void setValue(Object object) {
		this.value = object;
	}
	public Object getValue() {
		return this.value;
	}

	public void setValueType(Class valueType) {
		this.valueType = valueType;
	}
	public Class getValueType() {
		return this.valueType;
	}

	/**
	 * @return
	 */
	public String toString() {
		return this.getDefineName() + "=" + this.getValue().toString() +
				" (type: " + this.getValueType().getCanonicalName() + ")";
	}
}
