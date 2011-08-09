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
 * @goal copySrcDependency
 * @requiresProject
 * @threadSafe
 * @requiresDependencyResolution runtime
 */
public class CopySrcDependency extends AbstractDependencyOverlay{

	
	public void execute() throws MojoExecutionException, MojoFailureException {
		//extract files to webapp
		extractDependencies();
	}
	
	
	protected void extractDependencies() throws MojoExecutionException {
		try {
			// extract the dependencies to the directory where the webapp is built
			String extractDest = project.getBasedir().getAbsolutePath() + File.separator + "target/overlayStaging";
			ExtractSrcAftifactsUtils.extract(project.getDependencyArtifacts(), scope, extractDest);
		} catch (ZipException e) {
			throw new MojoExecutionException("Unable to access artifact dependency object: " + e.getMessage());
		} catch (IOException e) {
			throw new MojoExecutionException("Unable to access artifact dependency object: " + e.getMessage());
		}
	}
}
