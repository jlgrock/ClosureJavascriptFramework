package dependencyOverlay;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import dependencyOverlay.io.ExtractSrcAftifactsUtils;


/**
 * The Maven project.
 *
 * @goal Overlay
 * @requiresProject
 * @threadSafe
 * @requiresDependencyResolution runtime
 */
public class DoOverlay extends AbstractDependencyOverlay{

	
	public void execute() throws MojoExecutionException, MojoFailureException {
		File fromDir = new File(project.getBasedir().getAbsolutePath() + File.separator + "src");
		File toDir = new File(project.getBasedir().getAbsolutePath() + File.separator + "target/overlayStaging/src");
		
		try {
			ExtractSrcAftifactsUtils.copyDirectory(fromDir, toDir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

