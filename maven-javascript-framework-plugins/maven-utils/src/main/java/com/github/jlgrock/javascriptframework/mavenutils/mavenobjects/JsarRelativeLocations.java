package com.github.jlgrock.javascriptframework.mavenutils.mavenobjects;

import java.io.File;

public class JsarRelativeLocations {
	/**
	 * The location of the extern files in the jsar
	 */
	public static final String JSAR_TEST_LOCATION = "testDependencies";

	/**
	 * The location of the output of the test generation in the jsar
	 */
	public static final String JSAR_TEST_SUITE_LOCATION = "testSuite";

	/**
	 * The location of the extern files in the jsar
	 */
	public static final String JSAR_EXTERN_LOCATION = "externDependencies";

	/**
	 * The location of the assertion source files in the jsar
	 */
	public static final String JSAR_ASSERTION_SOURCE_LOCATION = "assertionSource";
	
	/**
	 * The location of the source files (with assertions removed) in the jsar
	 */
	public static final String JSAR_PROCESSED_SOURCE_LOCATION = "debugSource";
	
	/**
	 * The location of the intern files in the jsar
	 */
	public static final String JSAR_INTERN_LOCATION = "internDependencies";
	
	/**
	 * The location of the source files in the jsar
	 */
	public static final String JSAR_OUTPUT_LOCATION = "output";
	
	/**
	 * The location of the source files in the jsar
	 */
	public static final String JSAR_COMPILE_LOCATION = "compiled";
	
	/**
	 * The location of the generated assert deps file
	 */
	public static final String JSAR_ASSERT_LOCATION = "assert";
	
	/**
	 * The location of the generated debug deps file
	 */
	public static final String JSAR_DEBUG_LOCATION = "debug";

	/**
	 * private Constructor for utility class.
	 */
	private JsarRelativeLocations() {
	}

	public static final File getDebugDepsLocation(final File frameworkLocation) {
		return new File(new File(frameworkLocation, JSAR_OUTPUT_LOCATION), JSAR_DEBUG_LOCATION);
	}
	
	public static final File getAssertDepsLocation(final File frameworkLocation) {
		return new File(new File(frameworkLocation, JSAR_OUTPUT_LOCATION), JSAR_ASSERT_LOCATION);
	}
	
	public static final File getTestLocation(final File frameworkLocation) {
		return new File(frameworkLocation, JSAR_TEST_LOCATION);
	}

	public static final File getTestSuiteLocation(final File frameworkLocation) {
		return new File(frameworkLocation, JSAR_TEST_SUITE_LOCATION);
	}
	
	public static final File getAssertionSourceLocation(final File frameworkLocation) {
		return new File(new File(frameworkLocation, JSAR_OUTPUT_LOCATION), JSAR_ASSERTION_SOURCE_LOCATION);
	}
	
	public static final File getDebugSourceLocation(final File frameworkLocation) {
		return new File(new File(frameworkLocation, JSAR_OUTPUT_LOCATION), JSAR_PROCESSED_SOURCE_LOCATION);
	}

	public static final File getExternsLocation(final File frameworkLocation) {
		return new File(new File(frameworkLocation, JSAR_OUTPUT_LOCATION), JSAR_EXTERN_LOCATION);
	}
	
	public static final File getInternsLocation(final File frameworkLocation) {
		return new File(frameworkLocation, JSAR_INTERN_LOCATION);
	}
	
	public static final File getOutputLocation(final File frameworkLocation) {
		return new File(frameworkLocation, JSAR_OUTPUT_LOCATION);
	}
	public static final File getCompileLocation(final File frameworkLocation) {
		return new File(new File(frameworkLocation, JSAR_OUTPUT_LOCATION), JSAR_COMPILE_LOCATION);
	}
}
