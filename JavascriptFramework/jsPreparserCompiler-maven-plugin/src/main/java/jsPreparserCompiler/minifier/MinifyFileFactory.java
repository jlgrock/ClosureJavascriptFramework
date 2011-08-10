package jsPreparserCompiler.minifier;

import java.io.File;
import java.io.IOException;

import jsPreparserCompiler.minifier.google.GoogleEnumConverter;
import jsPreparserCompiler.minifier.google.GoogleMinifier;
import jsPreparserCompiler.minifier.google.calcdeps.CalcDepsOptions;
import jsPreparserCompiler.minifier.google.calcdeps.DiscoverGoogleBase;

import org.apache.maven.plugin.logging.SystemStreamLog;

public class MinifyFileFactory {
	private MinifyFileFactory() {
		//DO NOT USE - use determineFramwork
	}
	
	public static MinifyFramework determineFramework(MinificationOptions minificationOptions) throws MinificationException {
		MinifyFramework returnFmk = null;
		switch(minificationOptions.getMinificationType()) {
		case GOOGLE_ADVANCED:
		case GOOGLE_SIMPLE:
		case GOOGLE_WHITESPACE:
			CalcDepsOptions calcDepsOptions = new CalcDepsOptions();
			try {
				//File baseFile = new File(minificationOptions.getCompileDir().getPath() + File.separator 
				//		+ "closure-library" + File.separator + "closure" + File.separator + "goog" 
				//		+ File.separator + "base.js");
				File depsFile = new File(minificationOptions.getCompileDir().getPath() + File.separator + "deps.js");
				calcDepsOptions.setLog(minificationOptions.getLog());
				calcDepsOptions.setCompileDir(minificationOptions.getCompileDir());
				calcDepsOptions.setMinificationType(GoogleEnumConverter.convert(minificationOptions.getMinificationType()));
				File googleBaseDir = DiscoverGoogleBase.discover(minificationOptions.getCompileDir());
				calcDepsOptions.setGoogleBaseHome(googleBaseDir);
				calcDepsOptions.calculatePathsAndInputs(minificationOptions.getCompileDir());
				calcDepsOptions.getLog().info("input files size:" + calcDepsOptions.getInputs().size());
				calcDepsOptions.setCompileFile(minificationOptions.getCompileDir(), minificationOptions.getCompileFilename());
				calcDepsOptions.setDepsOutputFile(depsFile);
				//if (minificationOptions.isCreateAssertFile()) {
					calcDepsOptions.setAssertFile(minificationOptions.getCompileDir(), minificationOptions.getAssertFilename());
				//}
			} catch(IOException ioe) {
				minificationOptions.getLog().info("ERROR");
				throw new MinificationException(ioe);
			}
			returnFmk = new GoogleMinifier(calcDepsOptions);
			break;
		case YUI:
			//returnFmk = new YuiMinifier(log, inputFiles, outputFile);
			break;
		case NONE:
			returnFmk = new NoMinifier();
			break;
		default:
			throw new MinificationException("The minification type is not valid.");
		}
		return returnFmk;
	}
	
	public static void main(String[] args) throws MinificationException {
		SystemStreamLog log = new SystemStreamLog();
		log.info("Starting...");
		MinificationOptions minificationOptions = new MinificationOptions();
		minificationOptions.setAssertFilename("O4Common.assert.js");
		minificationOptions.setCompileFilename("O4Common.min.js");
		minificationOptions.setLog(log);
		minificationOptions.setMinificationType(MinificationType.GOOGLE_ADVANCED);
		minificationOptions.setCreateAssertFile(true);
		minificationOptions.setCompileDir(new File("C:\\Workspaces\\Tahoe\\trunk\\synapseCT\\SynapseCore\\war\\target\\namespaceClosureStaging\\compiled"));
		MinifyFramework minificationFramework = MinifyFileFactory.determineFramework(minificationOptions);
		minificationFramework.minify();
	}
}
