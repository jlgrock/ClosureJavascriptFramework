package jsPreparserCompiler.io;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;

public class DirectoryCopier {
	public static void copyDirectory(File srcDir, File destDir) throws IOException {
		IOFileFilter filter = FileFileFilter.FILE;
		filter = FileFilterUtils.or(DirectoryFileFilter.DIRECTORY,filter);
		filter = FileFilterUtils.and(HiddenFileFilter.VISIBLE, filter);
		FileUtils.copyDirectory(srcDir, destDir, filter);
	}
}
