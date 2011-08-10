/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jsdoc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.doxia.site.renderer.SiteRenderer;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.util.DirectoryScanner;

/**
 * @version $Id$
 * @author manos
 */
public abstract class AbstractBaseJstoolsReport extends AbstractMavenReport{
	
	/**
	 * The output directory.
	 * 
	 * @parameter expression="${project.build.directory}/site/"
	 */
	protected String outputBaseDirectory;

	/**
	 * <i>Maven Internal</i>: The Project descriptor.
	 * 
	 * @parameter expression="${project}"
	 * @readonly
	 */
	protected MavenProject project;

	/**
	 * <i>Maven Internal</i>: A Project Helper instance.
	 * 
	 * @component
	 */
	protected MavenProjectHelper helper;

	/**
	 * <i>Maven Internal</i>: The Doxia Site Renderer.
	 * 
	 * @component
	 */
	protected SiteRenderer siteRenderer;

	/**
	 * <i>Maven Internal</i>: To look up Archiver/UnArchiver implementations
	 * 
	 * @parameter expression="${component.org.codehaus.plexus.archiver.manager.ArchiverManager}"
	 * @required
	 * @readonly
	 */
	protected ArchiverManager archiverManager;

	/**
	 * <i>Maven Internal</i>: The build directory. Default is
	 * ${project.build.directory}
	 * 
	 * @parameter expression="${project.build.directory}"
	 * @required
	 */
	protected String buildDir;

	/**
	 * <i>Maven Internal</i>: The base directory. Default is ${basedir}
	 * 
	 * @parameter expression="${basedir}"
	 * @required
	 */
	protected File baseDir;

	/**
	 * The path to the JavaScript source directory (appended to ${basedir}).
	 * Default is src/main/js
	 * 
	 * @parameter expression="src/main/js"
	 */
	private String jsDir;

	/**
	 * The include pattern used to select javascript files for processing. Default is all (recursive) files with a .js
	 * extention
     * @parameter
	 */
	protected String includes;

	/**
	 * The excluded files pattern. Default is empty.
     * @parameter expression=""
	 */
	protected String excludes;
	
	/**
	 * Whether the file selection patterns should be case sensitive. Default is <code>true</code>.
	 * @parameter expression="true"
	 */
	protected boolean caseSensitive;

	/**
	 * Use package name along with a file name in the report. Default is false (using old style)
	 * @parameter expression="false"
	 */
	private boolean useNamespacedFiles;
	
	/**
	 * @return
	 */
	public abstract String getBundleKey();

	/**
	 * {@inheritDoc}
	 * @see org.apache.maven.reporting.AbstractMavenReport#getSiteRenderer()
	 */
	protected SiteRenderer getSiteRenderer() {
		return this.siteRenderer;
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.maven.reporting.AbstractMavenReport#getDescription(java.util.Locale)
	 */
	public String getDescription(Locale locale) {
		return this.getBundle(locale).getString(
				"report." + getBundleKey() + ".description");
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.maven.reporting.AbstractMavenReport#getName(java.util.Locale)
	 */
	public String getName(Locale locale) {
		return this.getBundle(locale).getString(
				"report." + getBundleKey() + ".name");
	}

	/**
	 * @see org.apache.maven.reporting.AbstractMavenReport#getOutputName()
	 */
	public String getOutputName() {
		return getBundleKey() + "/index";
	}

	/**
	 * @see org.apache.maven.reporting.AbstractMavenReport#getOutputDirectory()
	 */
	protected String getOutputDirectory() {
		return this.outputBaseDirectory + "/" + getBundleKey();
	}

	/**
	 * Loads the locale dependent mojo configuration
	 * 
	 * @param locale
	 * @return the bundle corresponding to the given locale if available or the
	 *         default locale otherwise
	 */
	protected ResourceBundle getBundle(Locale locale) {
		return ResourceBundle.getBundle("jstools", locale, this.getClass()
				.getClassLoader());
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.maven.reporting.AbstractMavenReport#getProject()
	 */
	protected MavenProject getProject() {
		return this.project;
	}

	/**
	 * Call the subclass implementations of setUp(java.util.Locale),
	 * doGenerateReport(java.util.Locale) and tearDown(java.util.Locale)
	 * @see org.apache.maven.reporting.AbstractMavenReport#executeReport(java.util.Locale)
	 */
	public final void executeReport(Locale defaultLocale)
			throws MavenReportException {
		if (this.canGenerateReport()) {
			// call subclass methods
			this.setUp(defaultLocale);
			this.doGenerateReport(defaultLocale);
			this.tearDown(defaultLocale);
		}
	}

	/**
	 * Subclasses must implement this method instead of
	 * org.apache.maven.reporting.AbstractMavenReport#executeReport(java.util.Locale)
	 */
	public abstract void doGenerateReport(Locale defaultLocale)
			throws MavenReportException;

	/**
	 * @return the text of a file as a String
	 * @throws IOException
	 */
	protected String getFileAsString(String path) throws IOException {
		String s = "";
		// Read jslint code
		FileInputStream f = new FileInputStream(path);
		int x = f.available();
		byte b[] = new byte[x];
		f.read(b);
		s = new String(b);
		return s;
	}

	/**
	 * Gets file namespace.
	 * 
	 * @param file the File object on which action is performed.
	 * @param omitSlahes indicates whether slasher should be present or replaced it with an empty string.
	 * @return file name if useNamespacedFiles is false,
	 *   otherwise namespaced file name (e.g. my/pkg/MyFile.js or mypkgMyFile.js depending on omitSlahes value)
	 */
	protected String getFileNamespace(File file, Boolean omitSlahes) {
		// use an old naming style if useNamespacedFiles is set to false
		if (!this.useNamespacedFiles) {
			return file.getName();
		}

		// cut off the base path if exists, otherwise use an old naming style
		String[] pathParts = file.getAbsolutePath().split(this.jsDir);
		if (pathParts.length != 2) {
			return file.getName();
		}

		String namespace = pathParts[1];

		// replace slashes with an empty string if requested
		if (omitSlahes) {
			namespace = namespace.replaceAll("\\\\", "").replaceAll("/", "");
		} else if (namespace.charAt(0) == '\\' || namespace.charAt(0) == '/') {
			// remove the first char if it is a slash
			namespace = namespace.substring(1);
		}

		return namespace;
	}

	/**
	 * Get the list of JavaScript source files for this project
	 * @returnvthe list of JavaScript source files for this project
	 * @throws MavenReportException
	 */
	protected Set<File> getJavaScriptFiles(){ 
		DirectoryScanner ds = new DirectoryScanner();
		// base dir
		if(this.jsDir == null || this.jsDir.length() == 0){
			this.jsDir = "src/main/js";
		}
		ds.setBasedir(this.jsDir);
		// includes
		if(this.includes == null || this.includes.length() == 0){
			this.includes = "**/*.js";
		}
		ds.setIncludes(this.includes.split(" "));
		// excludes
		if(this.excludes != null && this.excludes.length() > 0){
			ds.setExcludes(this.excludes.split(" "));
		}
		ds.addDefaultExcludes();
		// case sensitivity
	   ds.setCaseSensitive(true);
	   // scan
	   ds.scan();
	   // build a file set.	   
	   Set<File> files = new HashSet<File>();
	   // paths are relative to baseDir
	   String[] relPaths = ds.getIncludedFiles();
	   for(int i=0;i < relPaths.length;i++){
		   File file = new File(this.jsDir, relPaths[i]);
		   files.add(file);
		   this.getLog().debug("File to process: "+file.getAbsolutePath());
	   }
	   return files;
    }
	/**
	 * Perform any required initialization
	 * @param defaultLocale
	 * @throws MavenReportException
	 */
	protected abstract void setUp(Locale defaultLocale)
			throws MavenReportException;

	/**
	 * Perform any required cleanup
	 * @param defaultLocale
	 * @throws MavenReportException
	 */
	protected abstract void tearDown(Locale defaultLocale)
			throws MavenReportException;
}
