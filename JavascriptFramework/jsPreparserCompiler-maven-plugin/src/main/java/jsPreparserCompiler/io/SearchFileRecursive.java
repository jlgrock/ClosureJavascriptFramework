package namespaceclosure.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

public class SearchFileRecursive {
	public static File findAbsoluteFile(String filename, String startingDirectory) throws FileNotFoundException {
		File returnFile = null;
		try {
			File startingDirFile = new File(startingDirectory);
			Collection<File> files = FileUtils.listFiles(startingDirFile,
					FileFilterUtils.nameFileFilter(filename),
					TrueFileFilter.INSTANCE);

			if (files.size() == 0) {
				throw new FileNotFoundException("file '" + filename + "' not found in directory '" + startingDirectory);
			} else if (files.size() > 1) {
				throw new FileNotFoundException("multiple files with filename '" + filename + "' found in directory '" + startingDirectory);
			} else {
				for (File f:files)
					returnFile = f;
			}
		} catch (FileNotFoundException fnfe) {
			throw fnfe;
		} catch (Exception e) {
			throw new FileNotFoundException("'" + filename + "' not found in directory '" + startingDirectory + "'");
		}
		return returnFile;
	}
}
