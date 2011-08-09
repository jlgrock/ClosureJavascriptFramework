package dependencyOverlay;

import org.apache.maven.plugin.AbstractMojo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

public abstract class AbstractDependencyOverlay extends AbstractMojo{

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
    protected File srcDirectory;
    
    /**
     * The scope in which the plugin is executes.
     *
     * @parameter default-value="compile"
     */
     protected String scope;
    
    
	public abstract void execute() throws MojoExecutionException, MojoFailureException;


}
