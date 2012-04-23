package com.github.jlgrock.javascriptframework.jsar;

import java.io.InputStream;
import java.util.Collections;

import org.codehaus.plexus.archiver.ArchiveFileFilter;
import org.codehaus.plexus.archiver.ArchiveFilterException;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;

/**
 * Custom archiver for ClosureJavascriptFramework dependencies, packaged as
 * "jsar".
 * 
 * @plexus.component role="org.codehaus.plexus.archiver.UnArchiver"
 *                   role-hint="javascript"
 */
public class JavascriptUnArchiver extends ZipUnArchiver {
	/**
     *
     */
	public JavascriptUnArchiver() {
		super();
	}

	@Override
	public void execute() throws ArchiverException {
		setArchiveFilters(Collections.singletonList(new ArchiveFileFilter() {
			public boolean include(InputStream dataStream, String entryName)
					throws ArchiveFilterException {
				return !entryName.startsWith("META-INF");
			}
		}));
		super.execute();
	}
}