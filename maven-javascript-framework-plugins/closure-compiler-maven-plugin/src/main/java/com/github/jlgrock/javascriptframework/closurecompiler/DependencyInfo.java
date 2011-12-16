package com.github.jlgrock.javascriptframework.closurecompiler;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.github.jlgrock.javascriptframework.mavenutils.pathing.RelativePath;

/**
 * A file that has been scanned for dependency information. This will keep
 * information about provides, requires, and namespace objects.
 * 
 */
public class DependencyInfo implements Comparable<DependencyInfo> {
	/**
	 * The Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(DependencyInfo.class);
	/**
	 * The file that was scanned.
	 */
	private File file;
	/**
	 * The provides statements found within the file.
	 */
	private Set<String> provides;
	/**
	 * The requires statements found within the file.
	 */
	private Set<String> requires;
	/**
	 * The namespace statements found within the file.
	 */
	private Set<String> namespaces;

	/**
	 * Constructor.
	 * 
	 * @param parsedFile the file to scan
	 */
	public DependencyInfo(final File parsedFile) {
		this.file = parsedFile;
		this.provides = new HashSet<String>();
		this.requires = new HashSet<String>();
	}

	/**
	 * the file, relative path from the base.
	 * 
	 * @param basePath
	 *            the base to build the relative string off of
	 * @return the relative path
	 * @throws IOException
	 *             if either of the files cannot be found
	 */
	public final String toDepsString(final File basePath) throws IOException {
		String filePath = "";
		if (basePath == null) {
			filePath = getFile().getCanonicalPath();
		} else {
			try {
				filePath = RelativePath.getRelPathFromBase(basePath, getFile());
			} catch (IOException e) {
				filePath = ""; // If there is an exception, set the file path to
								// be blank
			}
		}
		return "goog.addDependency('" + filePath + "', " + getProvidesString()
				+ ", " + getRequiresString() + ");";
	}

	/**
	 * the file, relative path from the base.
	 * 
	 * @param basePath
	 *            the base to build the relative string off of
	 * @return the relative path
	 * @throws IOException
	 *             if either of the files cannot be found
	 */
	public final String toRequiresString(final File basePath) throws IOException {
		return "goog.requires(\"" + getProvidesString() + "\");";
	}
	/**
	 * Add a provides to the current file scan.
	 * 
	 * @param namespace
	 *            the provides namespace to add
	 */
	public final void addToProvides(final String namespace) {
		provides.add(namespace);
	}

	/**
	 * Add a requires to the current file scan.
	 * 
	 * @param namespace
	 *            the requires namespace to add
	 */
	public final void addToRequires(final String namespace) {
		requires.add(namespace);
	}

	/**
	 * Return the file that is scanned.
	 * 
	 * @return the file that that is scanned
	 */
	public final File getFile() {
		return file;
	}

	/**
	 * Return the filename of the file that is scanned.
	 * 
	 * @return the filename of the file that is scanned
	 */
	public final String getFilename() {
		return file.getName();
	}

	/**
	 * Get the provides objects.
	 * 
	 * @return a collections of strings that represent provides namespace
	 *         statements
	 */
	public final Collection<String> getProvides() {
		return provides;
	}

	/**
	 * Build a string that can be used to show all requirements. This will match
	 * the google standard for dependency files.
	 * 
	 * @return the string of provides
	 */
	public final String getProvidesString() {
		StringBuilder providesString = new StringBuilder();
		int i = 0;
		providesString.append("[");
		for (String provide : getProvides()) {
			if (i > 0) {
				providesString.append(", ");
			}
			providesString.append("'");
			providesString.append(provide);
			providesString.append("'");
			i++;
		}
		providesString.append("]");
		return providesString.toString();
	}

	/**
	 * Get the requires objects.
	 * 
	 * @return a collections of strings that represent require namespaces
	 */
	public final Collection<String> getRequires() {
		return requires;
	}

	/**
	 * Build a string that can be used to show all requirements. This will match
	 * the google standard for dependency files.
	 * 
	 * @return the string of requirements
	 */
	public final String getRequiresString() {
		StringBuilder requiresString = new StringBuilder();
		int i = 0;
		requiresString.append("[");
		for (String require : getRequires()) {
			if (i > 0) {
				requiresString.append(", ");
			}
			requiresString.append("'");
			requiresString.append(require);
			requiresString.append("'");
			i++;
		}
		requiresString.append("]");
		return requiresString.toString();
	}

	@Override
	public final int compareTo(final DependencyInfo o) {
		// if you require something of me, you are less than me
		int returnVal = 0;
		for (String require : this.getRequires()) {
			if (o.getProvides().contains(require)) {
				returnVal = -1;
			}
		}
		for (String provide : this.getProvides()) {
			if (o.getRequires().contains(provide)) {
				returnVal = 1;
			}
		}
		// TODO we haven't factored this in yet...
		// for (String namespace : this.getNamespaces()) {
		// if (o.getRequires().contains(namespace)) {
		// returnVal = 1;
		// }
		// }
		return returnVal;
	}

	/**
	 * Add a namespace to the current file scan.
	 * 
	 * @param namespace
	 *            the namespace to add
	 */
	public final void addToNamespaces(final String namespace) {
		namespaces.add(namespace);
	}

	/**
	 * Build a string that can be used to show all namespaces.
	 * 
	 * @return the string of namespaces
	 */
	public final String getNamespaceString() {
		StringBuilder namepaceString = new StringBuilder();
		int i = 0;
		namepaceString.append("[");
		for (String namespace : getNamespaces()) {
			if (i > 0) {
				namepaceString.append(", ");
			}
			namepaceString.append("'");
			namepaceString.append(namespace);
			namepaceString.append("'");
			i++;
		}
		namepaceString.append("]");
		return namepaceString.toString();
	}

	/**
	 * Get the namespace objects.
	 * 
	 * @return a collections of strings that represent namespace statements
	 */
	public final Set<String> getNamespaces() {
		return namespaces;
	}
}
