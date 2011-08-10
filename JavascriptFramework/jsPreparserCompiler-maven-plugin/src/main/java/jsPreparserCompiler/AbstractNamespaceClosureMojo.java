package jsPreparserCompiler;

import java.io.File;

import jsPreparserCompiler.mojotools.PackagingType;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

public abstract class AbstractNamespaceClosureMojo extends AbstractMojo {

	/**
	 * The file produced after running the dependencies and files through
	 * the compiler
	 * 
	 * @parameter default-value="${project.build.finalName}.js"
	 */
	protected String compiledFilename;

	/**
	 * The file produced after running the dependencies and files through
	 * the assertion preprocessor and the google compiler to remove google
	 * assert statements
	 * 
	 * @parameter default-value="${project.build.finalName}-assert.js"
	 */
	protected String assertFilename;
	
	/**
	 * If true, build the assert file.  If false, do not.
	 * 
	 * @parameter default-value="true"
	 */
	protected boolean buildAssertFile;
	
	/**
	 * The file produced after running through the files through the 
	 * concatenation and namespacing procedure
	 * 
	 * @parameter default-value="${project.build.finalName}-concat.js"
	 */
	protected String concatenatedFilename;
	
	/**
	 * The file produced after running the code through the minification
	 * object.
	 * 
	 * @parameter default-value="${project.build.finalName}-min.js"
	 */
	protected String minifiedFileName;

	/**
	 * The default directory to extract files to.  This likely shouldn't be 
	 * changed unless there is a conflict with another plugin.
	 * 
	 * @parameter default-value="${project.build.directory}${file.separator}namespaceClosureStaging"
	 */
	protected File stagingDirectory;

	/**
	 * The directory to place compiled files into.
	 * 
	 * @parameter default-value="${project.build.directory}${file.separator}namespaceClosureStaging${file.separator}compiled"
	 */
	protected File compileDirectory;
	
	/**
	 * The directory to place compiled files into.
	 * 
	 * @parameter default-value="${project.build.directory}${file.separator}namespaceClosureStaging${file.separator}dependencies"
	 */
	protected File dependencyDirectory;

	/**
     * The directory to extract to when using the WAR packaging type (it otherwise is copied directly 
     * out of the compileDirectory into the jsjar)
     * 
     * @parameter default-value="${project.build.directory}${file.separator}${project.build.finalName}${file.separator}webapp${file.separator}WEB-INF${file.separator}js"
	 */
	protected File outputDirectory;
	
	/**
     * The Maven project.
     *
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;
    
    /**
     * Used for specifying the jsjar final
	* @parameter default-value="${project.build.finalName}"
	*/
	protected String finalName;
	
	
	
	public abstract void executeMojo() throws MojoExecutionException, MojoFailureException;

	public abstract void checkForErrors() throws MojoExecutionException, MojoFailureException;
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		checkErrorsForAll();
		checkForErrors();
		executeMojo();
	}
	
	/**
	 * Check to make sure that all parameters are set up properly
	 * @throws MojoExecutionException
	 */
	private void checkErrorsForAll() throws MojoExecutionException {
		if (project != null) {
			if (compiledFilename == null)
				throw new MojoExecutionException("compiledFilename cannot be null");
		
			PackagingType packagingType = PackagingType.getByName(project.getPackaging());
			if (outputDirectory == null && packagingType.equals(PackagingType.JSJAR)) {
				//keep the dir as passed in
				outputDirectory = compileDirectory;
			} else if (outputDirectory == null && packagingType.equals(PackagingType.WAR)) {
				//adjust to the js dir
				outputDirectory = new File(project.getBuild().getDirectory() + File.separator + finalName 
					+ File.separator + "js");
			} else if (outputDirectory == null) {
				throw new MojoExecutionException("The outputDirectory cannot be null for this packaging type.");
			}
		}
	}
}