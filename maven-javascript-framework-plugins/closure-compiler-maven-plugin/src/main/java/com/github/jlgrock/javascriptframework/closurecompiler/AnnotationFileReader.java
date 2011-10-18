package com.github.jlgrock.javascriptframework.closurecompiler;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * A class that will scan all files provided and check for namespace,
 * goog.require, and goog.provides statements.
 */
public final class AnnotationFileReader {

	/**
	 * Empty Constructor for utility class.
	 */
	private AnnotationFileReader() {}
	
	/**
	 * The Logger.
	 */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger
			.getLogger(AnnotationFileReader.class);

	/**
	 * Regular expression to match the goog.requires statement.
	 */
	public static final Pattern REQ_REGEX = Pattern
			.compile("goog\\.require\\s*\\(\\s*[\\'\\\"]([^\\)]+)[\\'\\\"]\\s*\\)");

	/**
	 * Regular expression to match the goog.provides statement.
	 */
	public static final Pattern PROV_REGEX = Pattern
			.compile("goog\\.provide\\s*\\(\\s*[\\'\\\"]([^\\)]+)[\\'\\\"]\\s*\\)");

	/**
	 * Regular expression to match the namespace (ns) statement.
	 */
	public static final Pattern NS_REGEX = Pattern
			.compile("^ns:((\\w+\\.)*(\\w+))$");

	/**
	 * Parse a file for the google reqquires, provides, and namespace statements.
	 * 
	 * @param file
	 *            the file to scan
	 * @return the dependency info object, populated with the information
	 * @throws IOException if file does not exist or parsing is not possible
	 */
	public static DependencyInfo parseForDependencyInfo(final File file)
			throws IOException {
		DependencyInfo dep = new DependencyInfo(file);
		FileInputStream filestream = null;
		try {
			if (!file.exists() || !file.isFile()) {
				throw new IOException("the File at location "
						+ file.getCanonicalPath() + " does not exist");
			}
			filestream = new FileInputStream(file);
			DataInputStream in = new DataInputStream(filestream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String strLine;
			while ((strLine = br.readLine()) != null) {
				Matcher m;
				// goog.provides
				m = PROV_REGEX.matcher(strLine);
				if (m.lookingAt()) {
					for (int i = 0; i < m.groupCount(); i++) {
						String s = m.group(1);
						dep.addToProvides(s);
					}
				}
				// goog.requires
				m = REQ_REGEX.matcher(strLine);
				if (m.lookingAt()) {
					String s = m.group(1);
					dep.addToRequires(s);
				}

				// goog namespace
				m = NS_REGEX.matcher(strLine);
				if (m.lookingAt()) {
					String s = m.group(1);
					dep.addToNamespaces(s);
				}

			}
		} finally {
			if (filestream != null) {
				filestream.close();
			}
		}
		return dep;
	}
}
