package namespaceclosure.framework;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;

public class InitObject extends AbstractFrameworkObject {
	private static String EXECUTE_END = ".initialize();";
	private String namespace;
	
	public InitObject(String namespace) {
		this.namespace = namespace;
	}
	
	public ArrayList<BufferedReader> toBufferedReader() {
		StringBuffer sb = new StringBuffer();
		sb.append(NEWLINE);
		sb.append(namespace);
		sb.append(EXECUTE_END);
		sb.append(NEWLINE);
		ArrayList<BufferedReader> readers = new ArrayList<BufferedReader>();
		readers.add(new BufferedReader(new StringReader(sb.toString())));
		return readers;
	}

}
