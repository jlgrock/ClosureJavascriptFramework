package jsPreparserCompiler;

import java.io.IOException;
import java.util.List;

import jsPreparserCompiler.io.DirectoryCopier;
import jsPreparserCompiler.minifier.MinificationException;
import jsPreparserCompiler.minifier.MinificationOptions;
import jsPreparserCompiler.minifier.MinificationType;
import jsPreparserCompiler.minifier.MinifyFileFactory;
import jsPreparserCompiler.minifier.MinifyFramework;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Takes everything from the in the source directory and concatenates them together into one 
 * file and minifies the file.
 * 
 * @goal js-minify
 * @phase process-classes 
 */
public class JsMinifyMojo extends AbstractNamespaceClosureMojo {
	
	/**
	 * A directory where the final javascript files are placed
	 *
	 * AdvancedGoogleCompiler
	 * SimpleGoogleCompiler
	 * WhitespaceOnlyGoogleCompiler
	 * none
	 * @parameter expression="${js-minify.minificationType}" default-value="WhitespaceOnlyGoogleCompiler"
	 */
	protected String minificationType;
	
	/**
	 * 
	 * @parameter
	 */
	protected List<String> sourceIncludes;

	@Override
	public void executeMojo() throws MojoExecutionException,
			MojoFailureException {
		//build list of files in staging directory
		//Collection<File> fileList = FileListBuilder.buildList(stagingDirectory);
		
		//minify
		MinifyFramework minificationFramework;
		try {
			minificationFramework = createMinificationWithOptions();
			minificationFramework.minify();
			
			//copy compiled and minified files to war dir
			DirectoryCopier.copyDirectory(compileDirectory, outputDirectory);
			
		} catch (MinificationException e) {
			throw new MojoExecutionException("Problem in minification Process", e);
		} catch (IOException e) {
			throw new MojoExecutionException("Problem in minification Process", e);
		}
	}

	private MinifyFramework createMinificationWithOptions() throws MinificationException {
		MinifyFramework minificationFramework;
		MinificationOptions minificationOptions = new MinificationOptions();
		minificationOptions.setLog(getLog());
		minificationOptions.setCompileDir(compileDirectory);
		minificationOptions.setAssertFilename(assertFilename);
		minificationOptions.setCompileFilename(minifiedFileName);
		minificationOptions.setMinificationType(MinificationType.getByName(minificationType));
		minificationFramework = MinifyFileFactory.determineFramework(minificationOptions);
		return minificationFramework;
	}

	@Override
	public void checkForErrors() throws MojoExecutionException,
			MojoFailureException {
		switch(MinificationType.getByName(minificationType)) {
		case GOOGLE_ADVANCED:
		case GOOGLE_SIMPLE:
		case GOOGLE_WHITESPACE:
		case YUI:
		case NONE:
			break;
		default:
			throw new MojoExecutionException("The minification type is not valid.");
		}
	}
}
