package org.mojo.javascriptframework.mavenutils.mavenobjects;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;
import org.apache.maven.artifact.Artifact;

/**
 * A utility class to extract source maven artifacts.
 */
public final class ExtractSrcAftifacts {

	/**
	 * The Logger.
	 */
	private static final Logger LOGGER = Logger
			.getLogger(ExtractSrcAftifacts.class);

	/**
	 * The static buffer size.
	 */
	private static final int BUFFER = 2048;
	
	/**
	 * Private constructor for utility classes.
	 */
	private ExtractSrcAftifacts() {}
	
	/**
	 * Filter the artifacts, then extract the artifacts to a directory.
	 * 
	 * @param artifacts a set of artifacts to extract
	 * @param scope the scope that must match the artifact
	 * @param outputDirectory the directory to copy them to
	 * @throws IOException if there is a problem copying or unzipping the file
	 */
	public static void extract(final Set<Artifact> artifacts,
			final String scope, final File outputDirectory) throws IOException {
		Set<Artifact> filteredArtifacts = filterArtifactList(artifacts, scope,
				"zip");
		extractSet(filteredArtifacts, outputDirectory);
	}

	/**
	 * Extract the artifacts to a directory.
	 * 
	 * @param filteredArtifacts the already filtered artifacts
	 * @param outputDirectory the output directory to copy them to
	 * @throws IOException if there is a problem copying or unzipping the file
	 */
	private static void extractSet(final Set<Artifact> filteredArtifacts,
			final File outputDirectory) throws IOException {
		
		for (Artifact artifact : filteredArtifacts) {

			if ("zip".equals(artifact.getType())) {
				File file = artifact.getFile();

				BufferedOutputStream dest = null;
				BufferedInputStream is = null;
				ZipFile zf = new ZipFile(file);
				ZipEntry ze = null;
				Enumeration<? extends ZipEntry> e = zf.entries();

				while (e.hasMoreElements()) {
					ze = (ZipEntry) e.nextElement();

					is = new BufferedInputStream(zf.getInputStream(ze));
					int count;
					byte[] data = new byte[BUFFER];

					File newFile = new File(outputDirectory.getAbsolutePath()
							+ File.separator + ze.getName());

					if (!ze.isDirectory()) {
						newFile.getParentFile().mkdirs();
						FileOutputStream fos = new FileOutputStream(newFile);
						dest = new BufferedOutputStream(fos, BUFFER);
						while ((count = is.read(data, 0, BUFFER)) != -1) {
							dest.write(data, 0, count);
						}
						dest.flush();
						dest.close();
						is.close();
					}
				}
				zf.close();
			}
		}
	}

	/**
	 * Filter an artifact list based off of the possible criteria that was passed in.
	 * 
	 * @param artifacts the list of artifacts to filter
	 * @param filterScope the scope to filter by
	 * @param filterType the type to filter by
	 * @return the new set of filtered artifacts
	 */
	private static Set<Artifact> filterArtifactList(
			final Set<Artifact> artifacts, final String filterScope,
			final String filterType) {
		Set<Artifact> returnArtifacts = new LinkedHashSet<Artifact>();
		for (Artifact a : artifacts) {
			LOGGER.debug("Artifact Type: " + a.getType());
			if (a.getScope().equals(filterScope)
					&& filterType.equals(a.getType())) {
				returnArtifacts.add(a);
			}
		}
		return returnArtifacts;
	}

	/**
	 * Copy from one directory to another.
	 * 
	 * @param srcPath the source directory to copy from
	 * @param dstPath the directory to copy to
	 * @throws IOException if there is a problem copying files or the directory
	 */
	public static void copyDirectory(final File srcPath, final File dstPath)
			throws IOException {

		if (srcPath.isDirectory() && !srcPath.isHidden()) {
			LOGGER.debug("copying directory: " + srcPath);

			if (!dstPath.exists()) {
				dstPath.mkdir();
			}

			String[] files = srcPath.list();
			for (int i = 0; i < files.length; i++) {
				copyDirectory(new File(srcPath, files[i]), new File(dstPath,
						files[i]));
			}
		} else {
			if (!srcPath.exists()) {
				LOGGER.debug("Dependency file or directory does not exist: "
						+ srcPath);
				System.exit(0);
			} else {
				if (!srcPath.isHidden()) {
					LOGGER.debug("Copying file: " + srcPath);

					InputStream in = new FileInputStream(srcPath);
					OutputStream out = new FileOutputStream(dstPath);

					// Transfer bytes from in to out
					byte[] buf = new byte[BUFFER];
					int len;
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					in.close();
					out.close();
				}
			}
		}
	}
}
