package jsPreparserCompiler.framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Will gather all dependency objects from the staging directory
 * 
 */
public class DependencyObjects extends AbstractFrameworkObject {
	private File inputDirectory;

	public DependencyObjects(final File inputDirectory) {
		this.inputDirectory = inputDirectory;
	}
	
	@Override
	public ArrayList<BufferedReader> toBufferedReader() throws IOException {
		ArrayList<BufferedReader> list = new ArrayList<BufferedReader>();
		for (File f:inputDirectory.listFiles()) {
			BufferedReader br = new BufferedReader(new FileReader(f));
			list.add(br);
		}
		return list;
	}
}
