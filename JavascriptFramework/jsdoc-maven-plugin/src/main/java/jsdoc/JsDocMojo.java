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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import jsdoc.io.ZipFolder;
import jsdoc.utils.FileSystemDirectoryUtils;

import org.apache.commons.io.IOUtils;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.mozilla.javascript.tools.shell.Main;

/**
 * Goal to generate JavaScript documentation using the JSDoc Toolkit
 * 
 * @goal jsdoc
 * @version $Id$
 * @author manos
 * 
 */
public class JsDocMojo extends AbstractBaseJstoolsReport {
	/**
	 * The JSDoc template to use. Valid values are template directory names in
	 * the JSDoc Toolkit Distribution used or paths to an appropriate template
	 * directory. The default is JSDocs' "jsdoc" template.
	 * 
	 * @parameter expression="jsdoc"
	 */
	private String template;

	/**
	 * A path pointing to a the desired JSDoc Toolkit directory (e.g.
	 * /home/username/stuff/jsdoc_toolkit-1.3.0). If no value is provided the
	 * plugin wil use its own internal version.
	 * 
	 * @parameter
	 */
	private String jsdocHome;
	
	/**
	 * Whether to include symbols tagged as private. Default is <code>false</code>.
	 * @parameter expression="false"
	 */
	private boolean includePrivate;
	
	/**
	 * Include all functions, even undocumented ones. Default is <code>false</code>.
	 * @parameter expression="false"
	 */
	private boolean includeUndocumented;
	
	/**
	 * Include all functions, even undocumented, underscored ones. Default is <code>false</code>.
	 * @parameter expression="false"
	 */
	private boolean includeUndocumentedUnderscored;
	
	/**
	 * Use the -j option, must be set to <code>false</code> for JSDoc Toolkit 1.x. or 
	 * <code>true</code> for JSDoc Toolkit version 2.0 and above. Default is <code>true</code>. 
	 * @parameter expression="true"
	 */
	private boolean jArgument;
	
	/**
	 * 
	 * @parameter default-value="true"
	 */
	private boolean zipJSDocs;
	
	/**
	 * 
	 * @parameter default-value="${project.build.directory}"
	 */
	private File zipDestFolder;
	
	/**
	 * 
	 * @parameter default-value="${project.name}-${project.version}-jsdocs.jar"
	 */
	private String zipDestFile;
	
	/**
	 * Use JSDoc to generate Javascript API documentation
	 * 
	 */
	public void doGenerateReport(Locale defaultLocale)
			throws MavenReportException {
		// set the working directory, needed by jsdoc, override temporarily if
		// needed
		// (not sure if this has any effect outside the context VM)
		String systemJsdocDir = System.getProperty("jsdoc.dir");
		System.setProperty("jsdoc.dir", this.jsdocHome);
		if (systemJsdocDir != null) {
			getLog().debug("Temporarily switched the system's 'jsdoc.dir' property to: "+System.getProperty("jsdoc.dir"));
		}
		// prepare the arguments to run.js
		List<String> args = new ArrayList<String>();
		// supress console output if level is INFO
		ByteArrayOutputStream baos = null;
		PrintStream ps = null;
		if(!this.getLog().isDebugEnabled()){
			baos = new ByteArrayOutputStream();
			ps = new PrintStream(baos);
			Main.setOut(ps);
		}
		String runJsPath = this.jsdocHome + File.separator + "app" + File.separator + "run.js";
		args.add(runJsPath);
		if(this.includeUndocumented){
			args.add( "-a" );
		}
		if(this.includeUndocumentedUnderscored){
			args.add( "-A" );
		}
		if(this.includePrivate){
			args.add( "-p" );
		}
		args.add("-d=" + this.getOutputDirectory());
		String targetTemplate = this.template.indexOf(File.separator) != -1 ? this.template
				: this.jsdocHome + File.separator + "templates" + File.separator + this.template;
		args.add("-t=" + targetTemplate);
		// add files argument
		Set<File> jsfiles = this.getJavaScriptFiles();
		for(Iterator<File> iter = jsfiles.iterator(); iter.hasNext();){
			args.add(((File) iter.next()).getAbsolutePath());
		}
		if(this.jArgument){
			// tell run.js where it actually is
			args.add("-j=" + runJsPath);
		}
		getLog().debug("Executing: '" + args.toString().replaceAll(",","") + "'");
		
		// tell Rhino to run JSDoc with the provided params
		// without calling System.exit
		Main.exec(args.toArray(new String[0]));
		// restore old system property (again, not sure if our change had any
		// effect outside the context VM)
		// fix .htm/.html
		try {
			File in = new File(this.getOutputDirectory() + File.separator + "index.htm");
			if (in.exists()) {
				File out = new File(this.getOutputDirectory() + File.separator
						+ "index.html");
				FileChannel sourceChannel = new FileInputStream(in)
						.getChannel();
				FileChannel destinationChannel = new FileOutputStream(out)
						.getChannel();
				sourceChannel.transferTo(0, sourceChannel.size(),
						destinationChannel);
				sourceChannel.close();
				destinationChannel.close();
			}
		} catch (IOException e) {
			throw new MavenReportException("Could not copy file");
		}

		//zipFile
		if (zipJSDocs) {
			this.getLog().info("Starting to zip jsdoc files in folder " + this.getOutputDirectory() + ".");
			if (this.getLog().isDebugEnabled()) {
				this.getLog().debug("checking output directory exists?: " + new File(this.getOutputDirectory()).exists());
				this.getLog().debug("ZipFile for output: " + zipDestFolder.getAbsolutePath() + File.separator + zipDestFile);
			}
			if (!zipDestFolder.exists()) {
				zipDestFolder.mkdirs();
			}
			ZipFolder zf = new ZipFolder(new File(this.getOutputDirectory()), zipDestFolder, zipDestFile, this.getLog());
			try {
				zf.zipFolder();
			} catch (IOException e) {
				throw new MavenReportException(e.getMessage());
			}
			this.getLog().info("zip file completed - written to file " + zipDestFile);
		}
		
		if (systemJsdocDir != null) {
			System.setProperty("jsdoc.dir", systemJsdocDir);
			getLog().debug("Switched the system's 'sdoc.dir' property back to original value: " + System.getProperty("jsdoc.dir"));
		}
		
	}

	/**
	 * Setup the plugin's internal JSDoc Toolkit version if no other is provided
	 * 
	 * @param defaultLocale
	 * @throws MavenReportException
	 */
	protected void setUp(Locale defaultLocale) throws MavenReportException {
		if (this.jsdocHome == null || this.jsdocHome.trim().length() == 0) {
			String jsdocDistFilename = this.getBundle(defaultLocale).getString(
					"jsdoc.dist.filename");
			this.jsdocHome = this.buildDir + File.separator + "jsdoc_run" + File.separator + jsdocDistFilename;
			String jsDocJar = this.baseDir + File.separator + "target" + File.separator + jsdocDistFilename
					+ ".zip";
			// copy the internal jsdoc distribution archive to target/
			try {
				InputStream input = this.getClass().getClassLoader().getResourceAsStream(
						jsdocDistFilename + ".zip");
				OutputStream output = new FileOutputStream(jsDocJar);
				IOUtils.copy(input, output);
				input.close();
				output.close();
			} catch (FileNotFoundException fe) {
				throw new MavenReportException("Cannot find "
						+ jsdocDistFilename + ".zip" + " in classpath", fe);
			} catch (IOException ioe) {
				throw new MavenReportException("Error copying "
						+ jsdocDistFilename + ".zip" + " to target dir", ioe);
			}
			// unpack the jsdoc distribution archive
			File jarFile = new File(jsDocJar);
			try {
				UnArchiver unArchiver = this.archiverManager
						.getUnArchiver(jarFile);
				unArchiver.setSourceFile(jarFile);
				File destDir = new File(this.jsdocHome).getParentFile();
				destDir.mkdirs();
				unArchiver.setDestDirectory(destDir);
				unArchiver.extract();
			} catch (NoSuchArchiverException ae) {
				throw new MavenReportException("Unknown archiver type", ae);
			} catch (ArchiverException ae) {
				throw new MavenReportException("Error unpacking " + jarFile
						+ ": " + ae.toString(), ae);
			} catch (IOException e) {
				throw new MavenReportException("Unknown io exception for " + jarFile
						+ ": " + e.toString(), e);
			}
		}
	}

	public boolean isExternalReport() {
		return true;
	}

	public String getBundleKey() {
		return "jsdoc";
	}

	protected void tearDown(Locale defaultLocale) throws MavenReportException {
		try {
			File runDir = new File(this.buildDir + File.separator + "jsdoc_run" +File.separator);
			if (runDir.exists()) {
				FileSystemDirectoryUtils.deleteDirectory(runDir);
			}
			File jsdocJar = new File(this.baseDir
					+ File.separator + "target" +File.separator 
					+ this.getBundle(defaultLocale).getString(
							"jsdoc.dist.filename") + ".zip");
			if(jsdocJar.exists()){
				jsdocJar.delete();
			}
		} catch (Exception ioe) {
			throw new MavenReportException("Error clearing up", ioe);
		}
	}
}
