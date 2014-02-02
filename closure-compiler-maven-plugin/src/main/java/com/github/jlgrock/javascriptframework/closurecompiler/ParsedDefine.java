package com.github.jlgrock.javascriptframework.closurecompiler;

import java.util.HashMap;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Strictly typed version of
 * {@link com.github.jlgrock.javascriptframework.closurecompiler.Define Define}.
 */
public final class ParsedDefine {

	/**
	 * The types supported when parsing a Define object.
	 */
	public enum Type {
		/**
		 * Supported types.
		 */
		BOOLEAN("boolean"), DOUBLE("double"),  STRING("string"),  INTEGER("integer");

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
		 * 
		 * @param typeIn
		 *            the type used for lookups
		 */
		Type(final String typeIn) {
			this.type = typeIn;
		}

		/**
		 * Lookup the inclusion strategy by the type.
		 * 
		 * @param key
		 *            the type to look for
		 * @return the appropriate inclusion strategy
		 */
		public static Type getByType(final String key) {
			return TYPES.get(key.toLowerCase());
		}
	}

	/**
	 * Indicates the namespace that you would like to set the value of.
	 */
	private String defineName;

	/**
	 * The value that you would like to set the defineName to.
	 */
	private Object value;

	/**
	 * The type that this will have to be coerced to. This can be any of the
	 * types indicated in the {@link ParsedDefine.Type} class.
	 */
	private Class<?> valueType;

	/**
	 * @param defineNameIn
	 *            Indicates the namespace that you would like to set the value
	 *            of.
	 */
	public void setDefineName(final String defineNameIn) {
		defineName = defineNameIn;
	}

	/**
	 * @return The value that you would like to set the defineName to.
	 */
	public String getDefineName() {
		return this.defineName;
	}

	/**
	 * @param object
	 *            The value that you would like to set the defineName to.
	 */
	public void setValue(final Object object) {
		this.value = object;
	}

	/**
	 * @return The value that you would like to set the defineName to.
	 */
	public Object getValue() {
		return this.value;
	}

	/**
	 * @param valueTypeIn
	 *            The type that this will have to be coerced to. This can be any
	 *            of the types indicated in the {@link ParsedDefine.Type} class.
	 */
	public void setValueType(final Class<?> valueTypeIn) {
		valueType = valueTypeIn;
	}

	/**
	 * @return The type that this will have to be coerced to. This can be any of
	 *         the types indicated in the {@link ParsedDefine.Type} class.
	 */
	public Class<?> getValueType() {
		return this.valueType;
	}

	/**
	 * @return the string representation of this object and the properties
	 *         within.
	 */
	public String toString() {
		return this.getDefineName() + "=" + this.getValue().toString()
				+ " (type: " + this.getValueType().getCanonicalName() + ")";
	}

	/**
	 * Sets the values on the ParsedDefine bean from the origin Define object.
	 * 
	 * @param d
	 *            the define object to parse
	 * @param p
	 *            the parsedDefine to populate
	 * @throws MojoExecutionException
	 *             if there is any problem with unsupported types or bad format
	 */
	public static void parseDefine(final Define d, final ParsedDefine p)
			throws MojoExecutionException {
		parseDefineName(d.getDefineName(), p);
		parseDefineValueType(d.getValueType(), p);
		parseDefineValue(d.getValue(), p);
	}

	/**
	 * Parse the particular value, which requires type possible coersion.
	 * 
	 * @param defineName
	 *            the value of the define object in the pom file, to set on the
	 *            {@link ParsedDefine} object
	 * @param p
	 *            the parsed define object
	 * @throws MojoExecutionException
	 *             if encountering an unsupported valueType for the defines
	 */
	private static void parseDefineName(final String defineName,
			final ParsedDefine p) throws MojoExecutionException {
		if (defineName == null) {
			throw new MojoExecutionException("defineName can not be empty");
		}
		String trimmedDefineName = defineName.trim();
		if (trimmedDefineName.matches(".*\\s.*")) {
			throw new MojoExecutionException("defineName [" + trimmedDefineName
					+ "] can not contain whitespace");
		}
		if (trimmedDefineName.matches(".*=.*")) {
			throw new MojoExecutionException("defineName [" + trimmedDefineName
					+ "] can not contain equal sign");
		}
		p.setDefineName(trimmedDefineName);
	}

	/**
	 * Parse the particular value, which requires type possible coersion.
	 * 
	 * @param value
	 *            the value of the define object in the pom file, to set on the
	 *            {@link ParsedDefine} object
	 * @param p
	 *            the parsed define object
	 * @throws MojoExecutionException
	 *             if encountering an unsupported valueType for the defines
	 */
	private static void parseDefineValue(final String value,
			final ParsedDefine p) throws MojoExecutionException {
		// parse define value
		if (value != null) {
			String trimmedValue = value.trim();
			if (p.getValueType().isAssignableFrom(String.class)) {
				p.setValue(trimmedValue);
			} else if (p.getValueType().isAssignableFrom(Boolean.class)) {
				p.setValue(Boolean.parseBoolean(trimmedValue));
			} else if (p.getValueType().isAssignableFrom(Double.class)) {
				p.setValue(Double.parseDouble(trimmedValue));
			} else if (p.getValueType().isAssignableFrom(Integer.class)) {
				p.setValue(Integer.parseInt(trimmedValue));
			} else {
				// should not happen
				throw new MojoExecutionException("unsupported valueType");
			}
		} else {
			if (p.getValueType().isAssignableFrom(String.class)) {
				// accept empty string
				p.setValue("");
			} else {
				throw new MojoExecutionException("value of define ["
						+ p.getDefineName() + "] can not be empty");
			}
		}
	}

	/**
	 * Parse the particular value, which requires type possible coersion.
	 * 
	 * @param valueType
	 *            the valueType of the define object in the pom file, to set on
	 *            the {@link ParsedDefine} object
	 * @param p
	 *            the parsed define object
	 * @throws MojoExecutionException
	 *             if encountering an unsupported valueType for the defines
	 */
	private static void parseDefineValueType(final String valueType,
			final ParsedDefine p) throws MojoExecutionException {
		if (valueType == null) {
			// assume string type by default
			p.setValueType(String.class);
		} else {
			String trimmedValueType = valueType.trim();
			ParsedDefine.Type type = ParsedDefine.Type
					.getByType(trimmedValueType);
			if (type != null) {
				switch (ParsedDefine.Type.valueOf(trimmedValueType
						.toUpperCase())) {
				case BOOLEAN:
					p.setValueType(Boolean.class);
					break;
				case DOUBLE:
					p.setValueType(Double.class);
					break;
				case INTEGER:
					p.setValueType(Integer.class);
					break;
				case STRING:
					p.setValueType(String.class);
					break;
				default:
					throw new MojoExecutionException(
							"The current ParsedValue ["
									+ trimmedValueType
									+ "] is fully supported for the 'define' tag.");
				}
			} else {
				throw new MojoExecutionException("unsupported valueType ["
						+ trimmedValueType + "] for the 'define' tag");
			}
		}
	}
}
