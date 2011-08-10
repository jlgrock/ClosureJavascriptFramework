package jsPreparserCompiler.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class FileCopier {
	public static void copyFile(File from, File to) throws IOException {
		InputStream in = null;
		OutputStream out = null;
		 
		try {
		    // Ensure folder is created
		    FileUtils.forceMkdir(to.getParentFile());
		 
		    in = new FileInputStream(from);
		    out = new FileOutputStream(to);
		 
		    IOUtils.copy(in, out);
		 
		} finally {
		    IOUtils.closeQuietly(in);
		    IOUtils.closeQuietly(out);
		}
	}
}
