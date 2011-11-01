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
	 *            "${project.build.directory}${file.separator}${project.build.finalName}-min.js"
	 * @required
	 */
	private File compiledFile;

	/**
	 * The file produced after running the dependencies and files through the
	 * compiler.
	 * 
	 * @parameter 
	 *            default-value="${project.build.directory}${file.separator}${project.build.finalName}-debug.js"
	 * @required
	 */
	private File generatedDepsJS;

	/**
	 * The default directory to extract files to. This likely shouldn't be
	 * changed unless there is a conflict with another plugin.
	 * 
	 * @parameter default-value=
	 *            "${project.build.directory}${file.separator}javascriptFramework${file.separator}processedJavascript"
	 * @required
	 */
	private File sourceDirectory;

	/**
	 * The directory to place compiled files into.
	 * 
	 * @parameter default-value=
	 *            "${project.build.directory}${file.separator}javascriptFramework${file.separator}dependencies"
	 * @required
	 */
	private File internalDependencies;

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
	 * The directory to place compiled files into.
	 * 
	 * @parameter default-value=
	 *            "${project.build.directory}${file.separator}javascriptFramework${file.separator}externalDependencies"
	 * @required
	 */
	private File externalDependencies;

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

	@Override
	public final void execute() throws MojoExecutionException,
			MojoFailureException {
		MojoLogAppender.beginLogging(this);
		try {
			LOGGER.info("Creating deps.js");

			LOGGER.info("Compiling source files and internal dependencies to file \""
					+ compiledFile.getAbsolutePath() + "\".");
			compile();

		} catch (Exception e) {
			throw new MojoExecutionException(
					"Unable to closure compile files: " + e.getMessage());
		} finally {
			MojoLogAppender.endLogging();
		}
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
	private void compile() throws MojoExecutionException, MojoFailureException,
			IOException {
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
		WarningLevel wLevel = WarningLevel.VERBOSE;
		wLevel.setOptionsForWarningLevel(compilerOptions);
		// WarningLevel.QUIET.setOptionsForWarningLevel(options)
		compilationLevel.setOptionsForCompilationLevel(compilerOptions);
		compilerOptions.setGenerateExports(generateExports);
		// compilerOptions.setWarningLevel(DiagnosticGroups.ACCESS_CONTROLS,
		// CheckLevel.ERROR);
		// compilerOptions.setWarningLevel(DiagnosticGroups.AMBIGUOUS_FUNCTION_DECL,
		// CheckLevel.ERROR);
		// compilerOptions.setWarningLevel(DiagnosticGroups.CHECK_REGEXP,
		// CheckLevel.ERROR);
		// compilerOptions.setWarningLevel(DiagnosticGroups.CHECK_TYPES,
		// CheckLevel.ERROR);
		// compilerOptions.setWarningLevel(DiagnosticGroups.CHECK_VARIABLES,
		// CheckLevel.ERROR);
		// compilerOptions.setWarningLevel(DiagnosticGroups.CONST,
		// CheckLevel.ERROR);
		// compilerOptions.setWarningLevel(DiagnosticGroups.CONSTANT_PROPERTY,
		// CheckLevel.ERROR);
		// compilerOptions.setWarningLevel(DiagnosticGroups.DEPRECATED,
		// CheckLevel.ERROR);
		// compilerOptions.setWarningLevel(DiagnosticGroups.DUPLICATE_VARS,
		// CheckLevel.ERROR);
		// compilerOptions.setWarningLevel(DiagnosticGroups.ES5_STRICT,
		// CheckLevel.ERROR);
		// compilerOptions.setWarningLevel(DiagnosticGroups.EXTERNS_VALIDATION,
		// CheckLevel.ERROR);
		// compilerOptions.setWarningLevel(DiagnosticGroups.FILEOVERVIEW_JSDOC,
		// CheckLevel.ERROR);
		// compilerOptions.setWarningLevel(DiagnosticGroups.GLOBAL_THIS,
		// CheckLevel.ERROR);
		// compilerOptions.setWarningLevel(DiagnosticGroups.INTERNET_EXPLORER_CHECKS,
		// CheckLevel.ERROR);
		// compilerOptions.setWarningLevel(DiagnosticGroups.INVALID_CASTS,
		// CheckLevel.ERROR);
		// compilerOptions.setWarningLevel(DiagnosticGroups.MISSING_PROPERTIES,
		// CheckLevel.ERROR);
		// compilerOptions.setWarningLevel(DiagnosticGroups.NON_STANDARD_JSDOC,
		// CheckLevel.ERROR);
		// compilerOptions.setWarningLevel(DiagnosticGroups.STRICT_MODULE_DEP_CHECK,
		// CheckLevel.ERROR);
		// compilerOptions.setWarningLevel(DiagnosticGroups.TWEAKS,
		// CheckLevel.ERROR);
		// compilerOptions.setWarningLevel(DiagnosticGroups.TYPE_INVALIDATION,
		// CheckLevel.ERROR);
		// compilerOptions.setWarningLevel(DiagnosticGroups.UNDEFINED_VARIABLES,
		// CheckLevel.ERROR);
		// compilerOptions.setWarningLevel(DiagnosticGroups.UNKNOWN_DEFINES,
		// CheckLevel.ERROR);
		// compilerOptions.setWarningLevel(DiagnosticGroups.CHECK_USELESS_CODE,
		// CheckLevel.ERROR);
		// compilerOptions.setWarningLevel(DiagnosticGroups.VISIBILITY,
		// CheckLevel.ERROR);
		// compilerOptions.setGenerateExports(true);

		PrintStream ps = new PrintStream(new Log4jOutputStream(LOGGER,
				Level.DEBUG));
		Compiler compiler = new Compiler(ps);
		List<JSSourceFile> internalJSSourceFiles = extractInternalFiles();
		List<JSSourceFile> externalJSSourceFiles = extractExternalFiles();

		for (JSSourceFile jsf : internalJSSourceFiles) {
			LOGGER.debug("source files: " + jsf.getOriginalPath());
		}

		Result result = null;
		try {
			result = compiler.compile(externalJSSourceFiles,
					internalJSSourceFiles, compilerOptions);
		} catch (Exception e) {
			LOGGER.error("There was a problem with the compile.  Please review input.");
			LOGGER.error("externalJSSourceFiles: " + externalJSSourceFiles);
			LOGGER.error("internalJSSourceFiles: " + internalJSSourceFiles);
			LOGGER.error("compilerOptions: " + compilerOptions); // TODO make
																	// this a
																	// string?
			e.printStackTrace();
			throw new MojoExecutionException(e.getMessage(), e);
		}

		listErrors(result);

		if (!result.success) {
			String message = "Google Closure Compilation failure.  Please review errors to continue.";
			LOGGER.error(message);
			throw new MojoFailureException(message);
		}

		Files.createParentDirs(compiledFile);
		Files.touch(compiledFile);
		Files.write(compiler.toSource(), compiledFile, Charsets.UTF_8);
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
	private List<File> createDepsJS(final Set<File> src,
			final Set<File> combinedInternal) throws MojoExecutionException,
			IOException {
		File baseLocation = getBaseLocation();

		List<File> sortedDeps = CalcDeps.executeCalcDeps(baseLocation, src,
				combinedInternal, generatedDepsJS);
		return sortedDeps;
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
	private List<JSSourceFile> extractInternalFiles()
			throws MojoExecutionException, IOException {
		File baseFile = getBaseLocation();
		
		Set<File> listSourceFiles = listFiles(sourceDirectory);
		LOGGER.debug("number of source files:" + listSourceFiles.size());

		Set<File> internalSourceFiles = listFiles(internalDependencies);
		LOGGER.debug("number of source files:" + internalSourceFiles.size());

		Set<File> closureLibFiles = listFiles(closureLibraryLocation);
		LOGGER.debug("number of google lib files:" + closureLibFiles.size());

		HashSet<File> combinedInternal = new HashSet<File>();

		combinedInternal.add(baseFile);
		combinedInternal.addAll(listSourceFiles);
		combinedInternal.addAll(internalSourceFiles);
		combinedInternal.addAll(closureLibFiles);
		List<File> sortedDeps = createDepsJS(listSourceFiles, combinedInternal);
		if (!generatedDepsJS.exists()) {
			LOGGER.error("The generated dependency does not exist.  This may cause dependency order not to resolve");
		}
		JSSourceFile generatedDepsJSSrcFile = JSSourceFile
				.fromFile(generatedDepsJS);

		ArrayList<JSSourceFile> combinedJsInternal = new ArrayList<JSSourceFile>();
		combinedJsInternal.add(JSSourceFile.fromFile(baseFile));
		combinedJsInternal.add(generatedDepsJSSrcFile);
		combinedJsInternal.addAll(convertToJSSourceFiles(sortedDeps));

		return combinedJsInternal;
	}

	/**
	 * Extract external dependency libraries to the location specified in the
	 * settings.
	 * 
	 * @return the list of the files that are extracted
	 * @throws IOException
	 *             if unable to read the default externs
	 */
	private List<JSSourceFile> extractExternalFiles() throws IOException {
		Set<File> externalSourceFiles = listFiles(externalDependencies);
		List<JSSourceFile> externalJSSourceFiles = convertToJSSourceFiles(externalSourceFiles);
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
