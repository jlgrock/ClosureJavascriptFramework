package org.mojo.javascriptframework.closurecompiler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.mojo.javascriptframework.mavenutils.logging.MojoLogAppender;
import org.mojo.javascriptframework.mavenutils.pathing.FileListBuilder;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.JSSourceFile;
import com.google.javascript.jscomp.Result;
/**
 * 
 * @goal js-closure-compile
 * @phase compile
 */
public class JsClosureCompileMojo extends AbstractMojo {
	private static final Logger logger = Logger.getLogger( JsClosureCompileMojo.class );
	
	/**
	 * The file produced after running the dependencies and files through
	 * the compiler
	 * 
	 * @parameter default-value="${project.build.directory}${file.separator}${project.build.finalName}-min.js"
	 * @required
	 */
	protected File compiledFile;
	
	/**
	 * The file produced after running the dependencies and files through
	 * the compiler
	 * 
	 * @parameter default-value="${project.build.directory}${file.separator}deps.js"
	 * @required
	 */
	protected File generatedDepsJS;

	/**
	 * The default directory to extract files to.  This likely shouldn't be 
	 * changed unless there is a conflict with another plugin.
	 * 
	 * @parameter default-value="${project.build.directory}${file.separator}javascriptFramework${file.separator}processedJavascript"
	 * @required
	 */
	protected File sourceDirectory;

	/**
	 * The directory to place compiled files into.
	 * 
	 * @parameter default-value="${project.build.directory}${file.separator}javascriptFramework${file.separator}dependencies"
	 * @required
	 */
	protected File internalDependencies;
	
	/**
	 * The location of the closure library.  By default, this is expected in the ${project.build.directory}${file.separator}javascriptFramework${file.separator}closure-library${file.separator}closure${file.separator}goog
	 * directory, as this is where the jsdependency plugin will put it by default.  If you would like to override this and use your 
	 * own location, this can be done by changing this path.
	 * 
	 * @parameter default-value="${project.build.directory}${file.separator}javascriptFramework${file.separator}closure-library"
	 * @required
	 */
	protected File closureLibraryLocation;
	
	/**
	 * The directory to place compiled files into.
	 * 
	 * @parameter default-value="${project.build.directory}${file.separator}javascriptFramework${file.separator}externalDependencies"
	 * @required
	 */
	protected File externalDependencies;

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
     * Please see the <a href="http://code.google.com/closure/compiler/docs/compilation_levels.html">Google Compiler levels page</a> for more details.
	 *
	 * @parameter default-value="ADVANCED_OPTIMIZATIONS"
	 * @required
	 */
	protected String compileLevel;
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		MojoLogAppender.beginLogging(this);
		try {
			logger.info("Creating base.js");
			
			logger.info("Compiling source files and internal dependencies to file \"" + compiledFile.getAbsolutePath() + "\".");
			compile();
			
		} catch (Exception e) {
			throw new MojoExecutionException("Unable to closure compile files: " + e.getMessage());
		} finally {
			MojoLogAppender.endLogging();
		}
	}

	private void compile() throws MojoExecutionException, MojoFailureException, IOException {
		CompilationLevel compilationLevel = null;
		try {
			compilationLevel = CompilationLevel.valueOf(compileLevel);
			logger.info("Compler set to optimization level \"" + compileLevel + "\".");
		} catch (IllegalArgumentException e) {
			logger.error("Compilation level invalid.  Aborting.");
			throw new MojoExecutionException("Compilation level invalid.  Aborting.");
		}

		
		CompilerOptions compilerOptions = new CompilerOptions();
		compilationLevel.setOptionsForCompilationLevel(compilerOptions);

		Compiler compiler = new Compiler();
		
		List<JSSourceFile> internalJSSourceFiles = extractInternalFiles();
		List<JSSourceFile> externalJSSourceFiles = extractExternalFiles();
		
		Result result = compiler.compile(externalJSSourceFiles, internalJSSourceFiles, compilerOptions);

		listErrors(result);

		if (!result.success) {
			String message = "Google Closure Compilation failure.  Please review errors to continue.";
			logger.error(message);
			throw new MojoFailureException(message);
		}

		Files.createParentDirs(compiledFile);
		Files.touch(compiledFile);
		Files.write(compiler.toSource(), compiledFile, Charsets.UTF_8);
	}
	
	private void createDepsJS(Set<File> combinedInternal) throws MojoExecutionException, IOException {
		File baseLocation = new File(closureLibraryLocation.getAbsoluteFile() + File.separator + "closure" 
				+ File.separator + "goog" + File.separator + "base.js");
		if (!baseLocation.exists()) {
			throw new MojoExecutionException("Could not locate \"base.js\" at location \"" 
					+ baseLocation.getParentFile().getAbsolutePath() + "\"");
		}
		CalcDeps.executeCalcDeps(baseLocation, combinedInternal, generatedDepsJS);
	}

	private void listErrors(Result result) {
		for (JSError warning : result.warnings) {
			logger.warn("[Goog.WARN]: " + warning.toString());
		}

		for (JSError error : result.errors) {
			logger.error("[Goog.ERROR]: " + error.toString());
		}		
	}

	private List<JSSourceFile> extractInternalFiles() throws MojoExecutionException, IOException {
		Set<File> listSourceFiles = listFiles(sourceDirectory);
		logger.debug("number of source files:" + listSourceFiles.size());
		
		Set<File> internalSourceFiles = listFiles(internalDependencies);
		logger.debug("number of source files:" + internalSourceFiles.size());
		
		Set<File> closureLibFiles = listFiles(closureLibraryLocation);
		logger.debug("number of google lib files:" + closureLibFiles.size());
		
		HashSet<File> combinedInternal = new HashSet<File>();
		
		JSSourceFile generatedBaseJSSrcFile = JSSourceFile.fromFile(generatedDepsJS);

		combinedInternal.addAll(listSourceFiles);
		combinedInternal.addAll(internalSourceFiles);
		//combinedInternal.addAll(closureLibFiles);
		
		createDepsJS(combinedInternal);
		
		ArrayList<JSSourceFile> combinedJsInternal = new ArrayList<JSSourceFile>();
		combinedJsInternal.add(generatedBaseJSSrcFile);
		combinedJsInternal.addAll(convertToJSSourceFiles(combinedInternal));
		
		return combinedJsInternal;
	}

	private List<JSSourceFile> extractExternalFiles() {
		Set<File> externalSourceFiles = listFiles(externalDependencies);
		List<JSSourceFile> externalJSSourceFiles = convertToJSSourceFiles(externalSourceFiles);
		logger.debug("number of external files:" + externalJSSourceFiles.size());
		return externalJSSourceFiles;
	}
	
	private List<JSSourceFile> convertToJSSourceFiles(final Set<File> jsFiles) {
		List<JSSourceFile> jsSourceFiles = new ArrayList<JSSourceFile>();
		for (File f : jsFiles) {
			jsSourceFiles.add(JSSourceFile.fromFile(f));
		}
		return jsSourceFiles;
	}
	
	private Set<File> listFiles(final File directory) {
		return FileListBuilder.buildFilteredList(directory, "js");
	}
}
