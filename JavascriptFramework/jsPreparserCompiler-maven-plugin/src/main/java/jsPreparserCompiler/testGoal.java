package namespaceclosure;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipException;

import namespaceclosure.mojotools.ArtifactExtractor;
import namespaceclosure.mojotools.PackagingType;
import namespaceclosure.mojotools.ScopeType;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * Remove code blocks
 *
 * @goal testGoal
 * @requiresProject
 * @threadSafe
 * @requiresDependencyResolution runtime
 */
public class testGoal extends AbstractMojo {

	/**
     * The Maven project.
     *
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;
    
    /**
     * The directory for the generated WAR.
     *
     * @parameter default-value="${project.build.directory}"
     * @required
     */
    private String outputDirectory;
    
    /**
    * The directory where the webapp is built.
    *
    * @parameter default-value="${project.build.directory}/${project.build.finalName}"
    * @required
    */
    private File webappDirectory;
    
	public void execute() throws MojoExecutionException, MojoFailureException {
		//extract files to webapp
		extractDependencies();
		try {
			copyDirectory(new File(project.getBasedir().getAbsolutePath() + File.separator + "target" + File.separator + "example2" + File.separator + "webapps"),
					webappDirectory);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void copyDirectory(File srcPath, File dstPath) throws IOException{
		if (srcPath.isDirectory())
		{
			if (!dstPath.exists())
			{
				dstPath.mkdir();
			}

			String files[] = srcPath.list();
			for(int i = 0; i < files.length; i++)
			{
				copyDirectory(new File(srcPath, files[i]), new File(dstPath, files[i]));
			}
		}
		else
		{
			if(!srcPath.exists())
			{
				System.out.println("File or directory does not exist.");
				System.exit(0);
			}
			else
			{
				InputStream in = new FileInputStream(srcPath);
		        OutputStream out = new FileOutputStream(dstPath);
    
				// Transfer bytes from in to out
		        byte[] buf = new byte[1024];
				int len;
		        while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
		        out.close();
			}
		}
	}

	protected void extractDependencies() throws MojoExecutionException {
		try {
			ArtifactExtractor extractJSArtifacts = new ArtifactExtractor(project.getDependencyArtifacts(), ScopeType.COMPILE, PackagingType.JSJAR);
			extractJSArtifacts.extract(new File(project.getBasedir().getAbsolutePath() + File.separator + "example2"));
		} catch (ZipException e) {
			throw new MojoExecutionException("Unable to access jsjar dependency object: " + e.getMessage());
		} catch (IOException e) {
			throw new MojoExecutionException("Unable to access jsjar dependency object: " + e.getMessage());
		}
	}
}

