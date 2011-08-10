package jsPreparserCompiler.framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

public class DirectivesObject extends AbstractFileObject {
	private static String INIT_OBJ = "var directives = {};";
	private static String FUNCTION_JSDOCS = "/**\nThis presets the environment " +
			"variables for use in the objects in the namespace\n*/";
	private static String DECLARE_FUNCTION_START = "(function (directives) {";
	private static String DECLARE_FUNCTION_END = "}(directives));";
	
	String deriveFilename;
	private FileInputStream deriveStream;
	
	public DirectivesObject(File deriveFile) throws FileNotFoundException {
		this.deriveFilename = deriveFile.getName();
		this.deriveStream = new FileInputStream(deriveFile);
	}

	@Override
	public ArrayList<BufferedReader> toBufferedReader() throws IOException {
		ArrayList<BufferedReader> readers = new ArrayList<BufferedReader>();
		readers.add(new BufferedReader(new StringReader(buildFileWrapper())));
		return readers;
	}
	
	private String buildFileWrapper() throws IOException {
		StringBuilder deriveObj = new StringBuilder();
		deriveObj.append(INIT_OBJ);
		deriveObj.append(NEWLINE);
		deriveObj.append(FUNCTION_JSDOCS);
		deriveObj.append(NEWLINE);
		deriveObj.append(DECLARE_FUNCTION_START);
		deriveObj.append(NEWLINE);
		deriveObj.append(buildFile(deriveFilename, deriveStream, true));
		deriveObj.append(NEWLINE);
		deriveObj.append(DECLARE_FUNCTION_END);
		deriveObj.append(NEWLINE);
		deriveObj.append(NEWLINE);
		return deriveObj.toString();
	}
}