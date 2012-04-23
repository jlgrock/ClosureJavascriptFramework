package com.github.jlgrock.javascriptframework.jsar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.Manifest;
import org.codehaus.plexus.archiver.jar.ManifestException;

/**
 * Custom archiver for ClosureJavascriptFramework dependencies, packaged as "jsar".
 */
public class JavascriptArchiver extends JarArchiver {
	/**
     * Constructor.
     */
	public JavascriptArchiver() {
		super();
		archiveType = "jsar";
	}

	/**
     * How to create the default manifest.
	 * @param project
	 *            the maven project
	 * @throws ManifestException
	 *            when there is a problem with the manifest
	 * @throws IOException
	 *            when there is a problem writing the files
	 * @throws ArchiverException
	 *            when there is a problem creating an archive
     */
	public final void createDefaultManifest(final MavenProject project)
			throws ManifestException, IOException, ArchiverException {
		Manifest manifest = new Manifest();
		Manifest.Attribute attr = new Manifest.Attribute("Created-By",
				"Apache Maven");
		manifest.addConfiguredAttribute(attr);
		attr = new Manifest.Attribute("Implementation-Title", project.getName());
		manifest.addConfiguredAttribute(attr);
		attr = new Manifest.Attribute("Implementation-Version",
				project.getVersion());
		manifest.addConfiguredAttribute(attr);
		attr = new Manifest.Attribute("Implementation-Vendor-Id",
				project.getGroupId());
		manifest.addConfiguredAttribute(attr);
		if (project.getOrganization() != null) {
			String vendor = project.getOrganization().getName();
			attr = new Manifest.Attribute("Implementation-Vendor", vendor);
			manifest.addConfiguredAttribute(attr);
		}
		attr = new Manifest.Attribute("Built-By",
				System.getProperty("user.name"));
		manifest.addConfiguredAttribute(attr);

		File mf = File.createTempFile("maven", ".mf");
		mf.deleteOnExit();
		PrintWriter writer = new PrintWriter(new FileWriter(mf));
		manifest.write(writer);
		writer.close();
		setManifest(mf);
	}
}