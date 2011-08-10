package jsPreparserCompiler.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class FileListBuilder {
	private FileListBuilder() {
	}
	
	public static Collection<File> buildList(File root) {
        File[] list = root.listFiles();
        Collection<File> fileList = new ArrayList<File>();
        
        for ( File f : list ) {
            if ( f.isDirectory() ) {
            	fileList.addAll(buildList(f.getAbsoluteFile()));
            }
            else {
            	fileList.add(f);
            }
        }
		return fileList;
	}
}
