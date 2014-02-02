package com.github.jlgrock.javascriptframework.closurecompiler;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ParsedDefineTest {

	/**
	 * Use reflection API to create a new instance of
	 * {@link com.github.jlgrock.javascriptframework.closurecompiler.Define} and setup its private members.
	 *
	 * @param defineName
	 * @param value
	 * @param valueType
	 * @return
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 */
	private Define createNewDefineInstance(String defineName, String value, String valueType) throws IllegalAccessException, NoSuchFieldException {

		Field defineNameField = Define.class.getDeclaredField("defineName");
		Field valueField = Define.class.getDeclaredField("value");
		Field valueTypeField = Define.class.getDeclaredField("valueType");

		defineNameField.setAccessible(true);
		valueField.setAccessible(true);
		valueTypeField.setAccessible(true);

		Define d = new Define();

		defineNameField.set(d, defineName);
		valueField.set(d, value);
		valueTypeField.set(d, valueType);

		return d;
	}

	/**
	 * Test that we can create a new instance of Define and setup private members via reflection API.
	 *
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	@Test
	public void testNewDefineInstance() throws NoSuchFieldException, IllegalAccessException {

		String defineName = "goog.DEBUG";
		String value = "true";
		String valueType = "boolean";

		Define d = createNewDefineInstance(defineName, value, valueType);

		assertEquals(defineName, d.getDefineName());
		assertEquals(value, d.getValue());
		assertEquals(valueType, d.getValueType());
	}

	@Test(expected = org.apache.maven.plugin.MojoExecutionException.class)
	public void defineWithNullDefineNameShouldThrowException() throws NoSuchFieldException, IllegalAccessException, MojoExecutionException {

		String valueNotImportant = "";

		Define d = createNewDefineInstance(null, valueNotImportant, valueNotImportant);
		ParsedDefine p = new ParsedDefine();
		ParsedDefine.parseDefine(d, p);
	}

	@Test
	public void shouldNotAcceptWhiteSpaceInsideDefineName() throws NoSuchFieldException, IllegalAccessException, MojoExecutionException {

		String defineName;

		try {
			// space
			defineName = "x x";
			Define d = createNewDefineInstance(defineName, null, null);
			ParsedDefine p = new ParsedDefine();
			ParsedDefine.parseDefine(d, p);
			fail("define name with space should not be accepted");
		} catch (org.apache.maven.plugin.MojoExecutionException e) {
			assertTrue(e.getMessage().contains("can not contain whitespace"));
		}

		try {
			// tabs
			defineName = "x		x";
			Define d = createNewDefineInstance(defineName, null, null);
			ParsedDefine p = new ParsedDefine();
			ParsedDefine.parseDefine(d, p);
			fail("define name with tab should not be accepted");
		} catch (org.apache.maven.plugin.MojoExecutionException e) {
			assertTrue(e.getMessage().contains("can not contain whitespace"));
		}

	}

	@Test
	public void shouldNotAcceptEqualSignInsideDefineName() throws NoSuchFieldException, IllegalAccessException, MojoExecutionException {
		try {
			// space
			String defineName = "=";
			Define d = createNewDefineInstance(defineName, null, null);
			ParsedDefine p = new ParsedDefine();
			ParsedDefine.parseDefine(d, p);
			fail("define name with equals sign should not be accepted");
		} catch (org.apache.maven.plugin.MojoExecutionException e) {
			assertTrue(e.getMessage().contains("can not contain equal sign"));
		}
	}

	@Test
	public void shouldFallbackToDefaults() throws NoSuchFieldException, IllegalAccessException, MojoExecutionException {

		String defineName = "goog.DEBUG";

		Define d = createNewDefineInstance(defineName, null, null);
		ParsedDefine p = new ParsedDefine();
		ParsedDefine.parseDefine(d, p);

		assertEquals(defineName, p.getDefineName());
		assertEquals("", p.getValue());
		assertEquals(String.class, p.getValueType());
	}

	@Test(expected = org.apache.maven.plugin.MojoExecutionException.class)
	public void shouldNotAllowEmptyDefineName() throws NoSuchFieldException, IllegalAccessException, MojoExecutionException {
		Define d = createNewDefineInstance("", null, null);
		ParsedDefine p = new ParsedDefine();
		ParsedDefine.parseDefine(d, p);
	}

	@Test(expected = org.apache.maven.plugin.MojoExecutionException.class)
	public void shouldComplainAboutUnsupportedValueType() throws NoSuchFieldException, IllegalAccessException, MojoExecutionException {
		Define d = createNewDefineInstance("my.define", "", "long");
		ParsedDefine p = new ParsedDefine();
		ParsedDefine.parseDefine(d, p);
	}

	@Test(expected = java.lang.NumberFormatException.class)
	public void shouldComplainIfCannotConvertValueToInteger() throws NoSuchFieldException, IllegalAccessException, MojoExecutionException {
		Define d = createNewDefineInstance("my.define", "NaN", "integer");
		ParsedDefine p = new ParsedDefine();
		ParsedDefine.parseDefine(d, p);
	}

	@Test
	public void shouldParseDefine() throws NoSuchFieldException, IllegalAccessException, MojoExecutionException {
		Define d = createNewDefineInstance("goog.DEBUG", "true", "boolean");
		ParsedDefine p = new ParsedDefine();
		ParsedDefine.parseDefine(d, p);

		assertEquals(p.getDefineName(), "goog.DEBUG");
		assertEquals(p.getValue(), Boolean.TRUE);
		assertEquals(p.getValueType(), Boolean.class);
	}
}
