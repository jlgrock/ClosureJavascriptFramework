package com.github.jlgrock.javascriptframework.closuretesting.resultparsing.generators;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.github.jlgrock.javascriptframework.mavenutils.pathing.FileNameSeparator;
import com.github.jlgrock.javascriptframework.mavenutils.pathing.RelativePath;

/**
 * A Class that will generate a test case output container and write to file.
 */
public class TestCaseGenerator {

	/**
	 * the location of the sourcefile to generate the test off of.
	 */
	private final File generatorSourceFile;

	/**
	 * the location of the closure library.
	 */
	private final File generatorClosureLocation;

	/**
	 * the location of the generated dependency file.
	 */
	private final File generatorDepsLocation;

	/**
	 * the location of the external dependency files.
	 */
	private final List<File> testDeps;

	/**
	 * The preamble for the test.
	 */
	private final String preamble;

	/**
	 * The prologue for the test.
	 */
	private final String prologue;

	/**
	 * The epilogue for the test.
	 */
	private final String epilogue;

	/**
	 * Constructor.
	 * 
	 * @param closureLocation
	 *            the location of the closure library
	 * @param depsLocation
	 *            the location of the dependency file
	 * @param sourceFile
	 *            the location of the sourcefile to generate the test off of
	 * @param testDepsIn
	 *            the set of external dependencies for testing
	 * @param preambleIn
	 *            the preamble to the test case
	 * @param prologueIn
	 *            the prologue to the test case
	 * @param epilogueIn
	 *            the epilogue to the test case
	 */
	public TestCaseGenerator(final File closureLocation,
			final File depsLocation, final File sourceFile,
			final List<File> testDepsIn, final String preambleIn,
			final String prologueIn, final String epilogueIn) {
		generatorClosureLocation = closureLocation;
		generatorSourceFile = sourceFile;
		generatorDepsLocation = depsLocation;
		testDeps = testDepsIn;
		preamble = preambleIn;
		prologue = prologueIn;
		epilogue = epilogueIn;
	}

	/**
	 * Create the test case at the output directory specified.
	 * 
	 * @param sourceLocation
	 *            the location of the test source, used for relative pathing
	 * @param outputDirectory
	 *            the output directory to store the file
	 * @return the test case file
	 * @throws IOException
	 *             if unable to create the directory or write to the test case
	 *             file
	 */
	public final File createTestCase(final File sourceLocation,
			final File outputDirectory) throws IOException {
		FileNameSeparator fns = new FileNameSeparator(generatorSourceFile);
		String relPath = RelativePath.getRelPathFromBase(sourceLocation,
				new File(fns.getPath()));
		String outputFilePath = outputDirectory.getAbsoluteFile()
				+ File.separator + relPath + File.separator + fns.getName()
				+ ".html";
		File testCase = new File(outputFilePath);
		new TestCaseRef(generatorClosureLocation, generatorDepsLocation,
				generatorSourceFile, testCase, testDeps, preamble, prologue,
				epilogue).writeToFile();
		return testCase;
	}
}
