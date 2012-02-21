package com.github.jlgrock.javascriptframework.jsdocs;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.reporting.MavenReportException;
import org.mozilla.javascript.tools.shell.Main;

import com.github.jlgrock.javascriptframework.mavenutils.io.ResourceIO;
import com.github.jlgrock.javascriptframework.mavenutils.io.ZipUtils;
import com.github.jlgrock.javascriptframework.mavenutils.logging.Log4jOutputStream;
import com.github.jlgrock.javascriptframework.mavenutils.logging.MojoLogAppender;
import com.github.jlgrock.javascriptframework.mavenutils.pathing.FileListBuilder;

/**
 * The abstract jsdoc creation class.
 * 
 * @requiresDependencyResolution compile
 */
public abstract class AbstractJsDocsMojo extends AbstractMojo {

	/**
	 * The Logger.
	 */
	private static final Logger LOGGER = Logger
			.getLogger(AbstractJsDocsMojo.class);

	/**
	 * The name of the destination dir, which is where is is stored under the
	 * reporting directory.
	 * 
	 * @parameter default-value="jsapidocs"
	 */
	private String destDir;

	/**
	 * Where to extract the jsdoc-toolkit javascript to.
	 * 
	 * @parameter default-value="${project.build.directory}/jsdoctoolkit24"
	 */
	private File toolkitExtractDirectory;

	/**
	 * Whether the file selection patterns should be case sensitive. Default is
	 * <code>true</code>.
	 * 
	 * @parameter default-value="true"
	 */
	private boolean caseSensitive;

	/**
	 * Use package name along with a file name in the report. Default is false
	 * (using old style)
	 * 
	 * @parameter expression="false"
	 */
	private boolean useNamespacedFiles;

	/**
	 * The directory for the JSDoc template to use. The default is the JSDocs
	 * "jsdoc" that is defined by the jsdoc toolkit
	 * 
	 * @parameter 
	 *            expression="${project.build.directory}/jsdoctoolkit24/templates/jsdoc"
	 */
	private File template;

	/**
	 * Whether to include symbols tagged as private. Default is
	 * <code>false</code>.
	 * 
	 * @parameter default-value="false"
	 */
	private boolean includePrivate;

	/**
	 * Specifies whether the Jsdoc generation should be skipped.
	 * 
	 * @parameter default-value="false"
	 */
	private boolean skip;

	/**
	 * Specifies the filename that will be used for the generated jar file.
	 * Please note that <code>-jsdocs</code> or <code>-test-jsdocs</code> will
	 * be appended to the file name.
	 * 
	 * @parameter expression="${project.build.finalName}"
	 */
	private String finalName;

	/**
	 * Specifies the destination directory where javadoc saves the generated
	 * HTML files. <br/>
	 * See <a href=
	 * "http://download.oracle.com/javase/1.4.2/docs/tooldocs/windows/javadoc.html#d"
	 * >d</a>. <br/>
	 * 
	 * @parameter expression="${destDir}" alias="destDir"
	 *            default-value="${project.build.directory}/apidocs"
	 * @required
	 */
	private File outputDirectory;

	/**
	 * Use to identify the base directory of the project. This should not be
	 * modified. default-value="${basedir}"
	 */
	private File baseDir;

	/**
	 * @return the base directory
	 */
	protected final File getBaseDir() {
		return baseDir;
	}

	/**
	 * @return the caseSensitive
	 */
	public final boolean isCaseSensitive() {
		return caseSensitive;
	}

	/**
	 * @return the useNamespacedFiles
	 */
	public final boolean isUseNamespacedFiles() {
		return useNamespacedFiles;
	}

	/**
	 * @return the template
	 */
	public final File getTemplate() {
		return template;
	}

	/**
	 * @return the includePrivate
	 */
	public final boolean isIncludePrivate() {
		return includePrivate;
	}

	/**
	 * @return skip
	 */
	private boolean isSkip() {
		return skip;
	}

	/**
	 * @return the toolkitExtractDirectory
	 */
	public final File getToolkitExtractDirectory() {
		return toolkitExtractDirectory;
	}

	/**
	 * @return finalName
	 */
	protected final String getFinalName() {
		return finalName;
	}

	@Override
	public final void execute() throws MojoExecutionException, MojoFailureException {
		LOGGER.debug("starting report execution...");
		MojoLogAppender.beginLogging(this);
		try {
			createJsDocs();
		} catch (Exception e) {
			LOGGER.error("There was an error in the execution of the report: "
					+ e.getMessage(), e);
			throw new MojoExecutionException(e.getMessage(), e);
		} finally {
			MojoLogAppender.endLogging();
		}
	}

	/**
	 * Creates the js docs.
	 * 
	 * @throws MavenReportException if there is a problem in the creation of jsdocs
	 * @throws IOException if there is a problem writing the files
	 */
	protected final void createJsDocs() throws MavenReportException, IOException {
		// check params
		if (isSkip()) {
			LOGGER.info("Skipping javadoc generation");
			return;
		}
		// run the report
		ArrayList<File> sourceDirs = getSourceDirectories();
		Set<File> files = new HashSet<File>();
		for (File dir : sourceDirs) {
			LOGGER.debug("adding files from the " + dir.getAbsoluteFile());
			files.addAll(FileListBuilder.buildFilteredList(dir, "js"));
		}
		AbstractJsDocsMojo.extractJSDocToolkit(getToolkitExtractDirectory());
		if (!getTemplate().exists()) {
			throw new MavenReportException("The template specified at '"
					+ getTemplate().getAbsolutePath()
					+ "' does not exist.  Please correct before running.");
		}
		setConsoleOuput();
		if (isAggregator()) {
			// TODO do something to aggregate
		}
		LOGGER.info("Running javadocs reports to location '"
				+ getOutputDirectory().getAbsolutePath() + "'.");
		List<String> args = createArgumentStack(files);
		LOGGER.info("argument stack created.");
		executeJSDocs(args);
		LOGGER.info("JsDoc Reporting completed.");

		File archiveOutputDir = getArchiveOutputDirectory();
		if (archiveOutputDir != null) {
			LOGGER.debug("creating archive at "
					+ new File(archiveOutputDir, getFinalName()
							+ "-jsdocs.jsar").getAbsolutePath());
			ZipUtils.zipFolder(getOutputDirectory(), new File(archiveOutputDir,
					getFinalName() + "-jsdocs.jsar"));
			LOGGER.info("archive created.");
		}
	}

	/**
	 * Extract the jsdoc toolkit to a local dir.
	 * @param extractDirectory the directory to extract to
	 * @throws IOException if there is a problem extracting or writing files
	 */
	private static void extractJSDocToolkit(final File extractDirectory)
			throws IOException {
		ZipUtils.unzip(ResourceIO.getResourceAsZipStream("jsdoctoolkit.zip"),
				extractDirectory);
	}

	/**
	 * Execute the jsdoc toolkit executable.
	 * @param args the list of arguments for the jsdoc toolkit
	 * @throws MavenReportException for any reporting exception
	 */
	protected final void executeJSDocs(final List<String> args)
			throws MavenReportException {
		LOGGER.info("Executing with the following params: '"
				+ args.toString().replaceAll(",", "") + "'");
		Main.exec(args.toArray(new String[0]));
	}

	/**
	 * Create a list of arguments for the jsdoc toolkit.
	 * @param files the files to create jsdocs for
	 * @return the list of arguments
	 */
	protected final List<String> createArgumentStack(final Set<File> files) {
		List<String> args = new ArrayList<String>();

		// tell run.js its path
		args.add(getToolkitExtractDirectory() + File.separator + "app"
				+ File.separator + "run.js");

		// if you want to include private, set this flag.
		if (isIncludePrivate()) {
			args.add("-p");
		}

		// set the output directory
		args.add("-d=" + getOutputDirectory().getAbsolutePath()
				+ File.separator + getDestDir());

		// add template
		args.add("-t=" + getTemplate());

		// add files argument(s)
		for (File f : files) {
			args.add(f.getAbsolutePath());
		}

		args.add("-j=" + getToolkitExtractDirectory() + File.separator + "app"
				+ File.separator + "run.js");
		return args;
	}

	/**
	 * Will output the console jsdoc creation logs to log4j.
	 */
	protected final void setConsoleOuput() {
		Log4jOutputStream l4jos = new Log4jOutputStream(LOGGER, Level.INFO);
		PrintStream ps = new PrintStream(l4jos, true);
		Main.setOut(ps);
	}

	/**
	 * Archive the directory of the jsdocs.
	 * 
	 * @param archiveDir
	 *            the location to write the archive
	 * @throws IOException
	 * 			  If there is a problem unzipping to the directory
	 */
	protected final void archive(final File archiveDir) throws IOException {
		File archiveFile = new File(archiveDir, getFinalName() + "-"
				+ getClassifier() + ".jsar");
		ZipUtils.zipFolder(getOutputDirectory(), archiveFile);
		LOGGER.info("archive completed.");
	}

	/**
	 * @return the sourceDirectory
	 */
	public abstract ArrayList<File> getSourceDirectories();

	/**
	 * @return aggregator
	 */
	protected abstract boolean isAggregator();

	/**
	 * @return outputDirectory
	 */
	public final File getOutputDirectory() {
		return outputDirectory;
	}

	/**
	 * @param outputDirectoryIn
	 *            the outputDirectory to set it to
	 */
	public final void setOutputDirectory(final File outputDirectoryIn) {
		outputDirectory = outputDirectoryIn;
	}

	/**
	 * The classifier that will be used for the file name.
	 * 
	 * @return the classifier
	 */
	protected abstract String getClassifier();

	/**
	 * return the file location of where the archive of the jsdocs should be
	 * placed. This should return null if no archive is expected to be created.
	 * 
	 * @return the output directory or null
	 */
	protected abstract File getArchiveOutputDirectory();

	/**
	 * @return destDir
	 */
	public final String getDestDir() {
		return destDir;
	}

}
