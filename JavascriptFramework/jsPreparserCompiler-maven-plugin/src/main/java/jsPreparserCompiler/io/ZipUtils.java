package namespaceclosure.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

public class ZipUtils {
	public static void unzipArchiveResource(Log log, String archiveResource, File outputDir) throws IOException {
		if (outputDir.exists()) {
			log.debug("deleting current directory: " + outputDir);
			FileUtils.deleteDirectory(outputDir);
		}
		log.debug("extracting archive resource: " + archiveResource);
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(cl.getResourceAsStream(archiveResource)));
		ZipEntry entry = null;
        try {
			while((entry = zis.getNextEntry()) != null) {
				ZipUtils.unzipEntry(zis, entry, outputDir);
	        }
        } finally {
        	zis.close();
        }
    }

    private static void unzipEntry(ZipInputStream zis, ZipEntry entry, File outputDir) throws IOException {

        if (entry.isDirectory()) {
        	ZipUtils.createDir(new File(outputDir, entry.getName()));
            return;
        }

        File outputFile = new File(outputDir, entry.getName());
        if (!outputFile.getParentFile().exists()){
        	ZipUtils.createDir(outputFile.getParentFile());
        }

        BufferedInputStream inputStream = new BufferedInputStream(zis);
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));

        IOUtils.copy(inputStream, outputStream);
        outputStream.flush();
    }

    private static void createDir(File dir) {
        if(!dir.mkdirs()) {
        	throw new RuntimeException("Can not create dir "+dir);
        }
    }
    
    public static void main(String[] args) throws URISyntaxException, IOException {
    	unzipArchiveResource(new SystemStreamLog(), "\\closure-library.zip", new File("C:\\Temp\\"));
    }
}