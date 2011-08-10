package jsPreparserCompiler.io;

import java.io.*;

public class FilteredReader extends BufferedReader {
	private final String original;
	private final String replacement;
	
	static final int DEFAULT_BUFFER_SIZE = 8192;
	
	public FilteredReader(Reader in, String original, String replacement) {
		this(in, original, replacement, DEFAULT_BUFFER_SIZE);
	}

	public FilteredReader(Reader in, String original, String replacement, int size) {
		super(in, size);
		this.original = original;
		this.replacement = replacement;
	}

	@Override
	public String readLine() throws IOException {
		String unfiltered = super.readLine();
		if (unfiltered == null)
			return null;
		return unfiltered.replaceAll(original, replacement);
	}
	
	
}