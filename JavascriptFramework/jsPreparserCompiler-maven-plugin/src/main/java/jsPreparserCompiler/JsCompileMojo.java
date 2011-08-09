package namespaceclosure;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import namespaceclosure.framework.DependencyObjects;
import namespaceclosure.framework.DirectivesObject;
import namespaceclosure.framework.InitObject;
import namespaceclosure.framework.NamespaceObject;
import namespaceclosure.io.ConcatenateStreams;
import namespaceclosure.io.DirectoryCopier;
import namespaceclosure.io.FileListBuilder;
import namespaceclosure.mojotools.PackagingType;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Takes everything from the staging directory and compiles it.  This has many options including the following:
 * - concatenating the files
 * - wrapping the files in a namespace
 * - removing assertion blocks
 * 
 * @goal jscompile
 * @phase compile 
 */
public class JsCompileMojo extends AbstractNamespaceClosureMojo {

	/**
     * Will concatenate all files from the source directory before copying 
     * them to the staging directory
     *
     * @parameter default-value="false"
     */
    protected boolean concatenateFiles;
	
	/**
     * Will not put the namespace around the files during concatenation.  Will automatically
     * turn on concatenation if not set.
     *
     * @parameter default-value="false"
     */
    protected boolean useNamespace;
    
    /**
	 * @parameter default-value="directives.js"
	 */
	protected String directivesFilename;
	
	/**
	 * @parameter default-value="${basedir}${file.separator}src${file.separator}main${file.separator}javascript"
	 */
	protected String directivesFilePath;
	
	
	/**
	 * @parameter default-value="${basedir}${file.separator}src${file.separator}main${file.separator}javascript"
	 */
	protected File sourceDirectory;
	
	/**
	 * @parameter default-value="${project.name}"
	 */
	protected String namespace;

	/**
     * Will go through the file and replace each tab with 4 spaces, to match lint standards
     *
     * @parameter default-value="true"
     */
    protected boolean replaceTabsWithSpaces;
    
    
	@Override
	public void checkForErrors() throws MojoExecutionException,
			MojoFailureException {
		if (useNamespace) {
			if (namespace == null)
				throw new MojoExecutionException("If useNamespace=true, namespace parameter must be set.");
			if (directivesFilePath == null)
				throw new MojoExecutionException("directivesFilePath cannot be null");
			if (directivesFilename == null)
				throw new MojoExecutionException("directivesFilename cannot be null");
		}
	}
	
	@Override
	public void executeMojo() throws MojoExecutionException, MojoFailureException {
		try {
			PackagingType packagingType = PackagingType.getByName(project.getPackaging());
			if (packagingType != null) {
				if (useNamespace || concatenateFiles) {
					compileDirectory.mkdirs();
					getLog().debug("Path = namespacing/concatenating");
					ArrayList<BufferedReader> streams = gatherStreams();
					concatStreamsToFile(streams, compileDirectory, compiledFilename);
				} else {
					//TODO currently doesn't do much - needs to be adjusted to do compile tasking.
					//The end of the compile task should be placing all compile files into the compile
					//directory
					if (compileDirectory.exists()) {
						getLog().debug("deleting current compileDirectory: " + compileDirectory);
						FileUtils.deleteDirectory(compileDirectory);
					}
					compileDirectory.mkdirs();
					//unpack closure-library
					//it is assumed that the google dependencies are requires, so copy them into the dependency dir
					//ZipUtils.unzipArchiveResource(getLog(), "closure-library.zip", compileDirectory);
					getLog().debug("Path = Copying dependencies and source directories");
					if (dependencyDirectory.exists()) {
						DirectoryCopier.copyDirectory(dependencyDirectory, compileDirectory);
					}
					if (sourceDirectory.exists()) {
						File sourceCompileDir = new File(compileDirectory.getAbsolutePath() + File.separator + "source");
						//TODO update here to do more than copy
						DirectoryCopier.copyDirectory(sourceDirectory, sourceCompileDir);
					}
				}
			}
		} catch (MojoExecutionException mee) {
			throw mee;
		} catch (Exception e) {
			getLog().error(e);
			throw new MojoExecutionException(e.getMessage());
		}
	}

	protected ArrayList<BufferedReader> gatherStreams() throws IOException, MojoExecutionException {
		ArrayList<BufferedReader> streams = new ArrayList<BufferedReader>();
		
		getLog().debug("starting dependencyStream compilation.");
		ArrayList<BufferedReader> dependencyStream = new DependencyObjects(stagingDirectory).toBufferedReader();
		streams.addAll(dependencyStream);
		
		getLog().debug("starting directives stream (if applicable).");
		try {
			ArrayList<BufferedReader> directivesStream = new DirectivesObject(new File(directivesFilePath + File.separator + directivesFilename)).toBufferedReader();
			streams.addAll(directivesStream);
		} catch(Exception e) {
			getLog().debug("No directives file found.  This should be defined for variables global to the namespace.");
		}

		Collection<File> sourceIncludes = FileListBuilder.buildList(sourceDirectory);
		
		getLog().debug("starting concatenation of files stream with namespacing (if applicable).");
		ArrayList<BufferedReader> namespaceStream = new NamespaceObject(namespace, 
				sourceIncludes, useNamespace).toBufferedReader();
		streams.addAll(namespaceStream);

		getLog().debug("starting init object stream (if namespace wrapped).");
		if (useNamespace) {
			ArrayList<BufferedReader> initStream = new InitObject(namespace).toBufferedReader();
			streams.addAll(initStream);
		}
		return streams;
	}
	
	protected void concatStreamsToFile(ArrayList<BufferedReader> streams, File compileDir, String Filename) throws IOException, MojoExecutionException {
		ConcatenateStreams concatenatedStreams = new ConcatenateStreams(streams.toArray(new BufferedReader[streams.size()]));
    	File concatFile = new File (compileDir.getAbsolutePath() + File.separatorChar + Filename);
		getLog().info("creating compiled file at location: " + compileDir.getAbsolutePath() + File.separator + Filename);
    	concatenatedStreams.createOutput(concatFile);
	}
	
	
}
