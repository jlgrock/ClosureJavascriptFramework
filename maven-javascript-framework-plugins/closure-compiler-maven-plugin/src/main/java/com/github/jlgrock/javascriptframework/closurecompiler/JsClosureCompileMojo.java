package com.github.jlgrock.javascriptframework.closurecompiler;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.github.jlgrock.javascriptframework.mavenutils.logging.Log4jOutputStream;
import com.github.jlgrock.javascriptframework.mavenutils.logging.MojoLogAppender;
import com.github.jlgrock.javascriptframework.mavenutils.mavenobjects.JsarRelativeLocations;
import com.github.jlgrock.javascriptframework.mavenutils.pathing.FileListBuilder;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.javascript.jscomp.CommandLineRunner;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.JSSourceFile;
import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.WarningLevel;

/**
 * 
 * @goal js-closure-compile
 * @phase compile
 */
public class JsClosureCompileMojo extends AbstractMojo {
	/**
	 * The Logger.
	 */
	private static final Logger LOGGER = Logger
			.getLogger(JsClosureCompileMojo.class);
	/**
	 * The file produced after running the dependencies and files through the
	 * compiler.
	 * 
	 * @parameter default-value=
	 *            "${project.build.directory}${file.separator}javascriptFramework"
	 * @required
	 */
	private File frameworkTargetDirectory;

	/**
	 * The file produced after running the dependencies and files through the
	 * compiler.
	 * 
	 * @parameter default-value="${project.build.finalName}-min.js"
	 * @required
	 */
	private String compiledFilename;

	/**
	 * The file produced after running the dependencies and files through the
	 * compiler.
	 * 
	 * @parameter default-value="${project.build.finalName}-debug.js"
	 * @required
	 */
	private String generatedDepsJS;

	/**
	 * The default directory to extract files to. This likely shouldn't be
	 * changed unless there is a conflict with another plugin.
	 * 
	 * @parameter default-value=
	 *            "${project.build.directory}${file.separator}javascriptFramework${file.separator}output${file.separator}processedJavascript"
	 * @required
	 */
	private File sourceDirectory;

	/**
	 * The location of the closure library. By default, this is expected in the
	 * ${project.build.directory}${file.separator}javascriptFramework${file.
	 * separator}closure-library${file.separator}closure${file.separator}goog
	 * directory, as this is where the jsdependency plugin will put it by
	 * default. If you would like to override this and use your own location,
	 * this can be done by changing this path.
	 * 
	 * @parameter default-value=
	 *            "${project.build.directory}${file.separator}javascriptFramework${file.separator}closure-library"
	 * @required
	 */
	private File closureLibraryLocation;

	/**
	 * @parameter default-value="true"
	 */
	private boolean generateExports;

	/**
	 * Specifies the compiler level to use.
	 * 
	 * Possible values are:
	 * <ul>
	 * <li>WHITESPACE_ONLY
	 * <li>SIMPLE_OPTIMIZATIONS
	 * <li>ADVANCED_OPTIMIZATIONS
	 * </ul>
	 * <br/>
	 * 
	 * Please see the <a href=
	 * "http://code.google.com/closure/compiler/docs/compilation_levels.html"
	 * >Google Compiler levels page</a> for more details.
	 * 
	 * @parameter default-value="ADVANCED_OPTIMIZATIONS"
	 * @required
	 */
	private String compileLevel;

	/**
	 * Specifies how strict the compiler is at following the rules. The compiler
	 * is set to parameters matching SIMPLE by default, however, if you don't
	 * use WARNING or better, this isn't very useful. STRICT is set by default
	 * for this mojo.
	 * 
	 * Possible values are:
	 * <ul>
	 * <li>SIMPLE
	 * <li>WARNING
	 * <li>STRICT
	 * </ul>
	 * <br/>
	 * 
	 * @parameter default-value="STRICT"
	 */
	private String errorLevel;

	/**
	 * What to include based off of maven includes. If you are creating an API
	 * with an empty src folder, or you expect to use all of the maven
	 * dependencies as source, set this to ALL.
	 * 
	 * Possible values are:
	 * <ul>
	 * <li>ALL
	 * <li>WHEN_IN_SRCS
	 * </ul>
	 * <br/>
	 * 
	 * @parameter default-value="WHEN_IN_SRCS"
	 */
	private String inclusionStrategy;

	@Override
	public final void execute() throws MojoExecutionException,
			MojoFailureException {
		MojoLogAppender.beginLogging(this);
		try {
			LOGGER.info("Compiling source files and internal dependencies to location \""
					+ JsarRelativeLocations.getCompileLocation(
							frameworkTargetDirectory).getAbsolutePath() + "\".");
			// get sets of internal files
			Collection<File> sourceFiles = calculateSourceFiles();
			Collection<File> internFiles = calculateInternalFiles(sourceFiles);

			// gather externs
			Collection<JSSourceFile> externs = calculateExternFiles();

			// create deps.js
			List<File> depsFiles = createDepsJS(sourceFiles, internFiles);

			// create file collection for compilation
			List<File> compileFiles = new ArrayList<File>();
			compileFiles.add(getBaseLocation());
			compileFiles.add(getGeneratedDepsFile());
			compileFiles.addAll(depsFiles);

			// compile
			boolean result = compile(convertToJSSourceFiles(compileFiles),
					externs);

			if (!result) {
				String message = "Google Closure Compilation failure.  Please review errors to continue.";
				LOGGER.error(message);
				throw new MojoFailureException(message);
			}
		} catch (Exception e) {
			e.printStackTrace();
			e.printStackTrace(new PrintStream(new Log4jOutputStream(LOGGER,
					Level.DEBUG)));
			throw new MojoExecutionException(
					"Unable to closure compile files: " + e.getMessage());
		} finally {
			MojoLogAppender.endLogging();
		}
	}

	private File getGeneratedDepsFile() {
		return new File(
				JsarRelativeLocations
						.getCalcDepsLocation(frameworkTargetDirectory),
				generatedDepsJS);
	}

	/**
	 * Run the compiler on the calculated dependencies, input files, and
	 * external files.
	 * 
	 * @throws MojoExecutionException
	 *             if the options are set incorrectly for the compiler
	 * @throws MojoFailureException
	 *             if there is a problem executing the dependency creation or
	 *             the compiler
	 * @throws IOException
	 *             if there is a problem reading or writing to the files
	 */
	private boolean compile(Collection<JSSourceFile> allSources,
			Collection<JSSourceFile> externs) throws MojoExecutionException,
			MojoFailureException, IOException {
		CompilationLevel compilationLevel = null;
		try {
			compilationLevel = CompilationLevel.valueOf(compileLevel);
			LOGGER.info("Compiler set to optimization level \"" + compileLevel
					+ "\".");
		} catch (IllegalArgumentException e) {
			LOGGER.error("Compilation level invalid.  Aborting.");
			throw new MojoExecutionException(
					"Compilation level invalid.  Aborting.");
		}

		CompilerOptions compilerOptions = new CompilerOptions();
		if (ErrorLevel.getCompileLevelByName(errorLevel).equals(
				ErrorLevel.WARNING)) {
			WarningLevel wLevel = WarningLevel.VERBOSE;
			wLevel.setOptionsForWarningLevel(compilerOptions);
		} else if (ErrorLevel.getCompileLevelByName(errorLevel).equals(
				ErrorLevel.STRICT)) {
			StrictLevel sLevel = StrictLevel.VERBOSE;
			sLevel.setOptionsForWarningLevel(compilerOptions);
		}
		compilationLevel.setOptionsForCompilationLevel(compilerOptions);
		compilerOptions.setGenerateExports(generateExports);

		PrintStream ps = new PrintStream(new Log4jOutputStream(LOGGER,
				Level.DEBUG));
		Compiler compiler = new Compiler(ps);

		for (JSSourceFile jsf : allSources) {
			LOGGER.debug("source files: " + jsf.getOriginalPath());
		}

		Result result = null;
		try {
			LOGGER.debug("externJSSourceFiles: " + externs);
			LOGGER.debug("allSources: " + allSources);
			result = compiler.compile(
					externs.toArray(new JSSourceFile[externs.size()]),
					allSources.toArray(new JSSourceFile[allSources.size()]),
					compilerOptions);
		} catch (Exception e) {
			LOGGER.error("There was a problem with the compile.  Please review input.");
			e.printStackTrace();
			throw new MojoExecutionException(e.getMessage(), e);
		}

		listErrors(result);

		if (!result.success) {
			return false;
		}

		File compiledFile = new File(
				JsarRelativeLocations
						.getCompileLocation(frameworkTargetDirectory),
				compiledFilename);
		Files.createParentDirs(compiledFile);
		Files.touch(compiledFile);
		Files.write(compiler.toSource(), compiledFile, Charsets.UTF_8);
		return true;
	}

	private Set<File> calculateSourceFiles() {
		InclusionStrategy strategy = InclusionStrategy
				.getByType(inclusionStrategy);
		if (strategy == null) {
			strategy = InclusionStrategy.WHEN_IN_SRCS;
		}
		LOGGER.info("Calculating source files using Inclusion strategy: "
				+ strategy);
		Set<File> listSourceFiles = new HashSet<File>();
		if (strategy.equals(InclusionStrategy.WHEN_IN_SRCS)) {
			listSourceFiles.addAll(listFiles(sourceDirectory));
		} else {
			listSourceFiles.addAll(listFiles(sourceDirectory));
			listSourceFiles.addAll(listFiles(JsarRelativeLocations
					.getInternsLocation(frameworkTargetDirectory)));
		}
		LOGGER.debug("number of source files:" + listSourceFiles.size());
		return listSourceFiles;
	}

	/**
	 * Create the dependencies JS file.
	 * 
	 * @param src
	 *            the location of the source files
	 * @param combinedInternal
	 *            the set of combined internal files (internal maven
	 *            dependencies, closure library, and source files)
	 * @return the list of dependencies, in dependency order
	 * @throws MojoExecutionException
	 *             if the dependency generator is not able to make a file
	 * @throws IOException
	 *             if there is a problem reading or writing to any of the files
	 */
	private List<File> createDepsJS(final Collection<File> src,
			final Collection<File> interns) throws MojoExecutionException,
			IOException {

		File baseLocation = getBaseLocation();

		File depsFile = getGeneratedDepsFile();

		// TODO when they fix the visibility rules in the DepsGenerator, replace
		// it with Google's version
		LOGGER.debug("base location: " + baseLocation);
		LOGGER.debug("src files: " + src);
		LOGGER.debug("intern files: " + interns);
		LOGGER.debug("deps file location: " + depsFile);
		return CalcDeps.executeCalcDeps(baseLocation, src, interns, depsFile);
	}

	private File getBaseLocation() throws MojoExecutionException {
		File baseLocation = new File(closureLibraryLocation.getAbsoluteFile()
				+ File.separator + "closure" + File.separator + "goog"
				+ File.separator + "base.js");
		if (!baseLocation.exists()) {
			throw new MojoExecutionException(
					"Could not locate \"base.js\" at location \""
							+ baseLocation.getParentFile().getAbsolutePath()
							+ "\"");
		}
		return baseLocation;
	}

	/**
	 * List the errors that google is providing from the compiler output.
	 * 
	 * @param result
	 *            the results from the compiler
	 */
	private void listErrors(final Result result) {
		for (JSError warning : result.warnings) {
			LOGGER.warn("[Goog.WARN]: " + warning.toString());
		}

		for (JSError error : result.errors) {
			LOGGER.error("[Goog.ERROR]: " + error.toString());
		}
	}

	/**
	 * Extract internal dependency libraries, source to the location specified
	 * in the settings. Then create the deps to be loaded first.
	 * 
	 * @return the list of the files that are extracted (plus the generated deps
	 *         file)
	 * @throws MojoExecutionException
	 *             if there is a problem generating the dependency file
	 * @throws IOException
	 *             if there is a problem reading or extracting the files
	 */
	private Collection<File> calculateInternalFiles(
			final Collection<File> source) throws MojoExecutionException,
			IOException {
		Set<File> internalSourceFiles = listFiles(JsarRelativeLocations
				.getInternsLocation(frameworkTargetDirectory));
		LOGGER.debug("number of internal dependency files:"
				+ internalSourceFiles.size());

		Set<File> closureLibFiles = listFiles(closureLibraryLocation);
		LOGGER.debug("number of google lib files:" + closureLibFiles.size());

		HashSet<File> combinedInternal = new HashSet<File>();

		combinedInternal.addAll(source);
		combinedInternal.addAll(internalSourceFiles);
		combinedInternal.addAll(closureLibFiles);

		return combinedInternal;
	}

	/**
	 * Extract external dependency libraries to the location specified in the
	 * settings.
	 * 
	 * @return the list of the files that are extracted
	 * @throws IOException
	 *             if unable to read the default externs
	 */
	private List<JSSourceFile> calculateExternFiles() throws IOException {
		Set<File> externSourceFiles = listFiles(JsarRelativeLocations
				.getExternsLocation(frameworkTargetDirectory));
		List<JSSourceFile> externalJSSourceFiles = convertToJSSourceFiles(externSourceFiles);
		externalJSSourceFiles.addAll(CommandLineRunner.getDefaultExterns());
		LOGGER.debug("number of external files:" + externalJSSourceFiles.size());
		return externalJSSourceFiles;
	}

	/**
	 * A simple util to convert a collection of files to a list of closure
	 * JSSourceFiles.
	 * 
	 * @param jsFiles
	 *            the collection of files to convert
	 * @return the list of google formatted objects
	 */
	private static List<JSSourceFile> convertToJSSourceFiles(
			final Collection<File> jsFiles) {
		List<JSSourceFile> jsSourceFiles = new ArrayList<JSSourceFile>();
		for (File f : jsFiles) {
			jsSourceFiles.add(JSSourceFile.fromFile(f));
		}
		return jsSourceFiles;
	}

	/**
	 * List the javascript files in a directory.
	 * 
	 * @param directory
	 *            the directory to search
	 * @return the set of files with the ".js" extension
	 */
	private Set<File> listFiles(final File directory) {
		return FileListBuilder.buildFilteredList(directory, "js");
	}
}
