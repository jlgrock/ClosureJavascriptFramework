package com.github.jlgrock.javascriptframework.jsdocs;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

import com.github.jlgrock.javascriptframework.mavenutils.logging.MojoLogAppender;
import com.github.jlgrock.javascriptframework.mavenutils.pathing.FileListBuilder;

//TODO take a look at http://code.google.com/p/jsdoctk-plugin/ to figure out how to run reports

/**
 * Generates javascript docs from the jsdoc-toolkit (the final version).
 * 
 * @goal jsdoc-report
 */
public class JsDocsReport extends AbstractMavenReport {
	/**
	 * The Logger.
	 */
	private static final Logger LOGGER = Logger
			.getLogger(JsDocsReport.class);
	
	/**
	 * The Maven Project Object.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;
	
	/**
	 * <i>Maven Internal</i>: The Doxia Site Renderer.
	 *
	 * @component
	 */
	private Renderer siteRenderer;
	
	/**
     * The target directory where you want the generated doc to end up.
     * Changing this to a path outside the reporting outputdir will invalidate
     * the site, the link to this report will be broken.
     *
     * @parameter expression="${project.reporting.outputDirectory}/jsdoc"
     */
	private File directory;
	
	/**
	 * The name with which this report will be displayed in the site:site results.
	 * 
	 * @parameter expression="jsdoc"
	 */
	private String name;
	
	/**
	 * Where to extract the jsdoc-toolkit javascript to.
	 * 
	 * @parameter default-value="${project.build.directory}/jsdoctoolkit24"
	 */
	private File toolkitExtractDirectory;
	
	/**
	 * @return the toolkitExtractDirectory
	 */
	public final File getToolkitExtractDirectory() {
		return toolkitExtractDirectory;
	}
	
	/**
	 * The description with which this report will be shown in the site:site results.
	 * 
	 * @parameter expression="This is a JavaDoc-like report for JavaScript files in this immediate project."
	 */
	private String description;
	
	/**
	 * The path to the JavaScript source directory). Default is
	 * src/main/javascript
	 * 
	 * @parameter
	 */
	private Set<File> sourceFiles;
	
	/**
	 * Use to identify the base directory of the project.
	 * 
	 * @parameter default-value="${basedir}"
	 * @readonly
	 */
	private File baseDir;

	/**
	 * @return the base directory
	 */
	protected final File getBaseDir() {
		return baseDir;
	}
	
	// ///////////////////////////////////////////////////////////////////////
	// JSDocToolkit Specific Variables
	// ///////////////////////////////////////////////////////////////////////

	/**
	 * Include all functions, even undocumented ones.
	 * 
	 * @parameter default-value="false"
	 */
	private boolean allFunctions;

	/**
	 * @return allFunctions parameter
	 */
	public final boolean isAllFunctions() {
		return allFunctions;
	}

	/**
	 * Ignore all code, only document comments with @name tags. Default value is
	 * false.
	 * 
	 * @parameter default-value="false"
	 */
	private boolean ignoreCode;

	/**
	 * @return ignoreCode
	 */
	public final boolean isIgnoreCode() {
		return ignoreCode;
	}

	/**
	 * Whether to suppress source code output. Default value is true.
	 * 
	 * @parameter default-value="true"
	 */
	private boolean includeSourceCode;

	/**
	 * @return includeSourceCode
	 */
	public final boolean isIncludeSourceCode() {
		return includeSourceCode;
	}

	/**
	 * Specifies the destination directory where javadoc saves the generated
	 * HTML files. <br/>
	 * See <a href=
	 * "http://download.oracle.com/javase/1.4.2/docs/tooldocs/windows/javadoc.html#d"
	 * >d</a>. <br/>
	 * 
	 * @parameter expression="${destDir}"
	 *            default-value="${project.build.directory}/apidocs"
	 * @required
	 */
	private File outputDirectory;

	/**
	 * @return outputDirectory
	 */
	public final File getOutputDirectoryFile() {
		return outputDirectory;
	}

	/**
	 * Include symbols tagged as private, underscored and inner symbols. Default
	 * is <code>false</code>.
	 * 
	 * @parameter default-value="false"
	 */
	private boolean includePrivate;

	/**
	 * @return the includePrivate
	 */
	public final boolean isIncludePrivate() {
		return includePrivate;
	}

	/**
	 * Descend into src directories. Default Value is 1.
	 * 
	 * @parameter default-value="1"
	 */
	private int recurseDepth;

	/**
	 * @return recurseDepth;
	 */
	public final int getrecurseDepth() {
		return recurseDepth;
	}

	/**
	 * The directory for the JSDoc template to use. The default is the JSDocs
	 * "jsdoc" that is defined by the jsdoc toolkit
	 * 
	 * @parameter default-value=
	 *            "${project.build.directory}/jsdoctoolkit24/templates/jsdoc"
	 */
	private File template;

	/**
	 * @return the template
	 */
	public final File getTemplate() {
		return template;
	}

	/**
	 * Force file names to be unique, but not based on symbol names.
	 * 
	 * @parameter default-value="false"
	 */
	private boolean forceUnique;

	/**
	 * @return forceUnique
	 */
	public final boolean isForceUnique() {
		return forceUnique;
	}

	/**
	 * Execute the mojo, this is what mvn calls to start this mojo.
	 * 
	 * @param localeIn
	 *            the Locale In
	 * @throws MavenReportException
	 *             TODO
	 */
	@Override
	protected final void executeReport(final Locale localeIn)
			throws MavenReportException {

		//Locale locale = Locale.getDefault();

		LOGGER.debug("starting report execution...");
		MojoLogAppender.beginLogging(this);
		try {
			ReportGenerator.extractJSDocToolkit(getToolkitExtractDirectory());
			Set<File> sources = getSourceFiles();
			List<String> args = createArgumentStack(sources);
			ReportGenerator.executeJSDocToolkit(args, getToolkitExtractDirectory());
		} catch (Exception e) {
			LOGGER.error("There was an error in the execution of the report: "
					+ e.getMessage(), e);
			throw new MavenReportException(e.getMessage(), e);
		} finally {
			MojoLogAppender.endLogging();
		}
		
		// Rename index.htm to index.html, otherwise the site:site goal won't
		// link properly...
		File index = new File(directory, "index.htm");
		if (index.exists()) {
			index.renameTo(new File(directory, "index.html"));
		}

	}

	@Override
	protected final String getOutputDirectory() {
		return getOutputDirectoryFile().getAbsolutePath();
	}

	@Override
	protected final MavenProject getProject() {
		return project;
	}

	@Override
	protected final Renderer getSiteRenderer() {
		return siteRenderer;
	}

	@Override
	public final String getDescription(final Locale locale) {
		return description;
	}

	@Override
	public final String getName(final Locale locale) {
		return name;
	}

	@Override
	public final String getOutputName() {
		return directory.getName() + "/index";
	}

	@Override
	public final boolean isExternalReport() {
		return true;
	}

	/**
	 * Create a list of arguments for the jsdoc toolkit.
	 * 
	 * @param files
	 *            the files to create jsdocs for
	 * @return the list of arguments
	 * @throws MavenReportException
	 *             whenever the necessary arguments have been passed in
	 *             incorrectly or if there is a problem with one of the
	 *             parameters sent in
	 */
	protected final List<String> createArgumentStack(final Set<File> files)
			throws MavenReportException {
		List<String> args = new ArrayList<String>();

		// tell run.js its path
		args.add(getToolkitExtractDirectory() + File.separator + "app"
				+ File.separator + "run.js");

		if (isAllFunctions()) {
			args.add("-a");
		}

		if (isIgnoreCode()) {
			args.add("-n");
		}

		args.add("-r=" + Integer.toString(recurseDepth));

		if (getLog().isDebugEnabled()) {
			args.add("-v");
		}

		if (!isIncludeSourceCode()) {
			args.add("-s");
		}

		if (isIncludePrivate()) {
			args.add("-p");
		}

		// set the output directory
		LOGGER.info("Running javadocs reports to location '"
				+ getOutputDirectoryFile().getAbsolutePath() + "'.");
		args.add("-d=" + getOutputDirectoryFile().getAbsolutePath());

		// add template
		if (!getTemplate().exists()) {
			throw new MavenReportException("The template specified at '"
					+ getTemplate().getAbsolutePath()
					+ "' does not exist.  Please correct before running.");
		}
		args.add("-t=" + getTemplate());

		if (isForceUnique()) {
			args.add("-u");
		}

		// add files argument(s)
		for (File f : files) {
			args.add(f.getAbsolutePath());
		}

		// TODO
		// -D="myVar:My value" or --define="myVar:My value"
		// Multiple. Define a variable, available in JsDoc as JSDOC.opt.D.myVar.
		//
		// -E="REGEX" or --exclude="REGEX"
		// Multiple. Exclude files based on the supplied regex.
		//

		args.add("-j=" + getToolkitExtractDirectory() + File.separator + "app"
				+ File.separator + "run.js");
		return args;
	}
	
	/**
	 * This will get a listing of all of the files that should be used to create
	 * the jsdocs.
	 * 
	 * @return the set of files
	 */
	public final Set<File> getSourceFiles() {
		Set<File> srcFiles = new HashSet<File>();
		if (sourceFiles == null) {
			srcFiles.addAll(FileListBuilder.buildFilteredList(new File(getBaseDir(), "src/main/javascript"), "js"));
		} else {
			srcFiles = sourceFiles;
		}
		return srcFiles;
	}
}
