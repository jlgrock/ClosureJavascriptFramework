package com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents;


/**
 * The location.
 * 
 * The line that reads something like one of the following:
 * 
 * /C:/Workspaces/maven-plugins/testDependencyResolution/target/javascriptFramework/testSuite/test.html
 * 
 */
public class TestCaseLocation implements IParsedDivObject {
	
	/**
	 * Constructor.
	 * 
	 * @param testCaseFilenameIn the filename of the test case
	 */
	public TestCaseLocation(final String testCaseFilenameIn) {
		this.testCaseFilename = testCaseFilenameIn;
	}

	/**
	 * @return the testCaseFilename
	 */
	public final String getTestCaseFilename() {
		return testCaseFilename;
	}

	/**
	 * The absolute filename for the test case.
	 */
	private String testCaseFilename;
}
