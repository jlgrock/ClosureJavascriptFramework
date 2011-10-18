package com.github.jlgrock.javascriptframework.mavenutils.io.readers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * A reader for parsing through a stream and replacing one thing with another.
 */
public class FilteredReader extends BufferedReader {
	/**
	 * The string to replace.
	 */
	private final String originalString;
	
	/**
	 * The string replacing the original string.
	 */
	private final String replacementString;

	/**
	 * The default buffer if none is provided.
	 */
	static final int DEFAULT_BUFFER_SIZE = 8192;

	/**
	 * Constructor.
	 * 
	 * @param in the reader to read from
	 * @param original the string to replace
	 * @param replacement the string replacing the original string
	 */
	public FilteredReader(final Reader in, final String original,
			final String replacement) {
		this(in, original, replacement, DEFAULT_BUFFER_SIZE);
	}

	/**
	 * Constructor.
	 * 
	 * @param in the reader to read from
	 * @param original the string to replace
	 * @param replacement the string replacing the original string
	 * @param size the size of the buffer, should the default not meet your needs
	 */
	public FilteredReader(final Reader in, final String original,
			final String replacement, final int size) {
		super(in, size);
		this.originalString = original;
		this.replacementString = replacement;
	}

	@Override
	public final String readLine() throws IOException {
		String unfiltered = super.readLine();
		if (unfiltered == null) {
			return null;
		}
		return unfiltered.replaceAll(originalString, replacementString);
	}
}
