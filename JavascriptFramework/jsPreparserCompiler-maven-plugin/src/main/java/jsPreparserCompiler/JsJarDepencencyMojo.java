package namespaceclosure;

import java.io.IOException;
import java.util.zip.ZipException;

import namespaceclosure.mojotools.ArtifactExtractor;
import namespaceclosure.mojotools.PackagingType;
import namespaceclosure.mojotools.ScopeType;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Get files.
 *
 * @goal jsjar-dependency
 * @phase generate-sources
 * @requiresDependencyResolution runtime
 */
public class JsJarDepencencyMojo extends AbstractNamespaceClosureMojo {	

	@Override
	public void executeMojo() throws MojoExecutionException, MojoFailureException {
		extractDependencies();
	}
	
	@SuppressWarnings("unchecked")
	private void extractDependencies() throws MojoExecutionException {
		stagingDirectory.mkdirs();
		try {
			ArtifactExtractor extractJSArtifacts = new ArtifactExtractor(project.getDependencyArtifacts(), ScopeType.COMPILE, PackagingType.JSJAR);
			extractJSArtifacts.extract(dependencyDirectory);
		} catch (ZipException e) {
			throw new MojoExecutionException("Unable to access jsjar dependency object: " + e.getMessage());
		} catch (IOException e) {
			throw new MojoExecutionException("Unable to access jsjar dependency object: " + e.getMessage());
		}
	}

	@Override
	public void checkForErrors() throws MojoExecutionException,
			MojoFailureException {
		//Nothing additional to check as of now
	}
	
}
