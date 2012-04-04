package com.github.jlgrock.javascriptframework.jsdocs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.ManifestException;

import com.github.jlgrock.javascriptframework.mavenutils.io.ZipUtils;

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
	 * Includes all generated JsDoc files.
	 */
	private static final String[] DEFAULT_INCLUDES = new String[] { "**/**" };

	/**
	 * Excludes all processing files.
	 * 
	 * @see AbstractJavadocMojo#DEBUG_JAVADOC_SCRIPT_NAME
	 * @see AbstractJavadocMojo#OPTIONS_FILE_NAME
	 * @see AbstractJavadocMojo#PACKAGES_FILE_NAME
	 * @see AbstractJavadocMojo#ARGFILE_FILE_NAME
	 * @see AbstractJavadocMojo#FILES_FILE_NAME
	 */
	private static final String[] DEFAULT_EXCLUDES = new String[] {};

	// ///////////////////////////////////////////////////////////////////////
	// Maven Specific Variables
	// ///////////////////////////////////////////////////////////////////////

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
	 * Specifies the filename that will be used for the generated jar file.
	 * Please note that <code>-jsdocs</code> or <code>-test-jsdocs</code> will
	 * be appended to the file name.
	 * 
	 * @parameter expression="${project.build.finalName}"
	 */
	private String finalName;

	/**
	 * @return finalName
	 */
	protected final String getFinalName() {
		return finalName;
	}

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

	/**
	 * The classifier that will be used for the file name.
	 * 
	 * @return the classifier
	 */
	protected abstract String getClassifier();

	/**
	 * Path to the default MANIFEST file to use. It will be used if
	 * <code>useDefaultManifestFile</code> is set to <code>true</code>.
	 * 
	 * @parameter 
	 *            expression="${project.build.outputDirectory}/META-INF/MANIFEST.MF"
	 * @required
	 * @readonly
	 */
	private File defaultManifestFile;

	/**
	 * @return defaultManifestFile
	 */
	public final File getDefaultManifestFile() {
		return defaultManifestFile;
	}

	/**
	 * Set this to <code>true</code> to enable the use of the
	 * <code>defaultManifestFile</code>. <br/>
	 * 
	 * @parameter default-value="false"
	 */
	private boolean useDefaultManifestFile;

	/**
	 * @return useDefaultManifestFile
	 */
	public final boolean isUseDefaultManifestFile() {
		return useDefaultManifestFile;
	}

	/**
	 * The Maven Project Object.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * @return project
	 */
	public final MavenProject getProject() {
		return project;
	}

	/**
	 * The archive configuration to use. See <a
	 * href="http://maven.apache.org/shared/maven-archiver/index.html">Maven
	 * Archiver Reference</a>.
	 * 
	 * @parameter
	 */
	private MavenArchiveConfiguration archive = new MavenArchiveConfiguration();

	/**
	 * @return archive
	 */
	public final MavenArchiveConfiguration getArchive() {
		return archive;
	}

	/**
	 * The Jar archiver.
	 * 
	 * @component role="org.codehaus.plexus.archiver.Archiver" roleHint="jar"
	 * @since 2.5
	 */
	private JarArchiver jarArchiver;

	/**
	 * @return archive
	 */
	public final JarArchiver getJarArchiver() {
		return jarArchiver;
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
	public final File getOutputDirectory() {
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
				+ getOutputDirectory().getAbsolutePath() + "'.");
		args.add("-d=" + getOutputDirectory().getAbsolutePath());

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
	 * Archive the directory of the jsdocs.
	 * 
	 * @param archiveDir
	 *            the location to write the archive
	 * @throws IOException
	 *             If there is a problem unzipping to the directory
	 */
	protected final void archive(final File archiveDir) throws IOException {
		File archiveFile = new File(archiveDir, getFinalName() + "-"
				+ getClassifier() + ".jsar");
		ZipUtils.zipFolder(getOutputDirectory(), archiveFile);
		LOGGER.info("archive completed.");
	}

	/**
	 * This will get a listing of all of the files that should be used to create
	 * the jsdocs.
	 * 
	 * @return the set of files
	 */
	public abstract Set<File> getSourceFiles();

	/**
	 * Method that creates the jar file.
	 * 
	 * @param mojo
	 *            the mojo to receive maven specific variables from.
	 * @param javadocFiles
	 *            the directory where the generated jar file will be put
	 * @param jarFileName
	 *            the filename of the generated jar file
	 * @return a File object that contains the generated jar file
	 * @throws ArchiverException
	 *             if any
	 * @throws IOException
	 *             if any
	 */
	public static File generateArchive(final AbstractJsDocsMojo mojo,
			final File javadocFiles, final String jarFileName)
			throws ArchiverException, IOException {
		File javadocJar = new File(mojo.getArchiveOutputDirectory(),
				jarFileName);

		if (javadocJar.exists()) {
			javadocJar.delete();
		}

		MavenArchiver archiver = new MavenArchiver();
		archiver.setArchiver(mojo.getJarArchiver());
		archiver.setOutputFile(javadocJar);

		File contentDirectory = javadocFiles;
		if (!contentDirectory.exists()) {
			mojo.getLog().warn(
					"JAR will be empty - no content was marked for inclusion!");
		} else {
			archiver.getArchiver().addDirectory(contentDirectory,
					DEFAULT_INCLUDES, DEFAULT_EXCLUDES);
		}

		List<Resource> resources = mojo.getProject().getBuild().getResources();

		for (Resource r : resources) {
			if (r.getDirectory().endsWith("maven-shared-archive-resources")) {
				archiver.getArchiver().addDirectory(new File(r.getDirectory()));
			}
		}

		if (mojo.isUseDefaultManifestFile()
				&& mojo.getDefaultManifestFile().exists()
				&& mojo.getArchive().getManifestFile() == null) {
			mojo.getLog().info(
					"Adding existing MANIFEST to archive. Found under: "
							+ mojo.getDefaultManifestFile().getPath());
			mojo.getArchive().setManifestFile(mojo.getDefaultManifestFile());
		}

		try {
			// we don't want Maven stuff
			mojo.getArchive().setAddMavenDescriptor(false);
			archiver.createArchive(mojo.getProject(), mojo.getArchive());
		} catch (ManifestException e) {
			throw new ArchiverException("ManifestException: " + e.getMessage(),
					e);
		} catch (DependencyResolutionRequiredException e) {
			throw new ArchiverException(
					"DependencyResolutionRequiredException: " + e.getMessage(),
					e);
		}

		return javadocJar;
	}

	/**
	 * return the file location of where the archive of the jsdocs should be
	 * placed. This should return null if no archive is expected to be created.
	 * 
	 * @return the output directory or null in the case that it does not archive
	 */
	public abstract File getArchiveOutputDirectory();
}
