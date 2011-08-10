package jsPreparserCompiler.framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class NamespaceObject extends AbstractFileObject {
	private static String JSDOCS1 = "/**\nwrite these...\n";//TODO
	private static String JSDOCS2 = "\n*/";//TODO
	private static String FUNCTION_NS_DEF = "(function (win, ns, directives, nsStr) {\n"
		+ "\tif (!ns || !ns.loaded) {\n\t\twin[nsStr] = ns = {};\n";
	private static String END_FUNCTION = "\t\tns.loaded = true;\n\t}\n}(window, window.";
	private static String END_FUNCTION2 = ", directives, \"";
	private static String END_FUNCTION3 = "\"));";
	private static String FILE_LIST_HEADING = "//Compiled Namespace generated file at timestamp: ";
	
	private final String namespace;
	private final boolean useNamespacing;
	private final ArrayList<File> files;
	
	public NamespaceObject(final String namespace, final Collection<File> files, final boolean useNamespacing) throws FileNotFoundException {
		this.namespace = namespace;
		this.useNamespacing = useNamespacing;
		this.files = new ArrayList<File>();
		for (File f:files) {
			if (f == null) {
				throw new FileNotFoundException("One or more of the files is null.  Please correct for execution.");
			}
			if (!f.exists()){
				throw new FileNotFoundException("The file '" + f.getAbsolutePath() + "' is not found.  Please correct for execution.");
			}
			this.files.add(f);
		}
	}
	
	@Override
	public ArrayList<BufferedReader> toBufferedReader() throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append(generateFileListHeading());
		sb.append(NEWLINE);
		if (useNamespacing) {
			sb.append(JSDOCS1);
			sb.append("* @namespace " + namespace);
			sb.append(JSDOCS2);

			sb.append(NEWLINE);
			sb.append(FUNCTION_NS_DEF);
			sb.append(NEWLINE);
		}
		for (File file:files) {
			sb.append(buildFile(file.getName(), new FileInputStream(file), useNamespacing));
		}
		if (useNamespacing) {
			sb.append(NEWLINE);
			sb.append(END_FUNCTION);
			sb.append(namespace); 
			sb.append(END_FUNCTION2);
			sb.append(namespace); 
			sb.append(END_FUNCTION3);
			sb.append(NEWLINE);
		}
		
		ArrayList<BufferedReader> readers = new ArrayList<BufferedReader>();
		readers.add(new BufferedReader(new StringReader(sb.toString())));
		return readers;
	}

	private String generateFileListHeading() {
		StringBuffer sb = new StringBuffer();
		sb.append(HEADER_BORDER);
		sb.append(NEWLINE);
		sb.append(FILE_LIST_HEADING + new Date().toString());
		sb.append(NEWLINE);
		sb.append(HEADER_BORDER);
		sb.append(NEWLINE);
		
		sb.append("//File import order:\n");
		for (File f:files) {
			if (f.exists())
				sb.append("//\t*"+f.getName()+"\n");
		}
		sb.append("\n\n");
		return sb.toString();
	}
}
