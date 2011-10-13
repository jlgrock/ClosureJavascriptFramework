package org.mojo.javascriptframework.closurecompiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Represents a dependency that is used to build and walk a tree.  This is a direct
 * port from the google python script.
 *
 */
public class CalcDeps {
	private static final Logger logger = Logger.getLogger( CalcDeps.class );
	
	/**
	 * Build a list of dependencies from a list of files.
	 * Takes a list of files, extracts their provides and requires, and builds
	 * out a list of dependency objects.
	 * 
	 * @param files a list of files to be parsed for goog.provides and goog.requires.
	 * @throws IOException 
	 * @returns A list of dependency objects, one for each file in the files argument.
	 */
	private static HashMap<File, DependencyInfo> buildDependenciesFromFiles(Collection<File> files) throws IOException {
		HashMap<File, DependencyInfo> result = new HashMap<File, DependencyInfo>();
		Set<File> searchedAlready = new HashSet<File>();
		for (File file:files) {
			if (!searchedAlready.contains(file)) {
				DependencyInfo dep = AnnotationFileReader.parseForDependencyInfo(file);
				result.put(file, dep);
				searchedAlready.add(file);
			}
		}
		return result;
	}
	
	/**
	 * Calculates the dependencies for given inputs.
	 * 
	 * This method takes a list of paths (files, directories) and builds a 
	 * searchable data structure based on the namespaces that each .js file
	 * provides.  It then parses through each input, resolving dependencies
	 * against this data structure.  The final output is a list of files,
	 * including the inputs, that represent all of the code that is needed to
	 * compile the given inputs.
	 *     
	 * @param paths the references (files, directories) that are used to build the
	 * dependency hash.
	 * @param inputs the inputs (files, directories, namespaces) that have dependencies
	 * that need to be calculated.
	 * @return A list of all files, including inputs, that are needed to compile the 
	 * given inputs.
	 * @throws MojoExecutionException 
	 * @throws Exception if a provided input is invalid.
	 */
	private static List<DependencyInfo> calculateDependencies(final File base_js, final Set<File> inputs) throws IOException, MojoExecutionException {
		HashSet<File> temp = new HashSet<File>();
		temp.addAll(inputs);
		HashMap<File, DependencyInfo> search_hash = CalcDeps.buildDependenciesFromFiles(temp);
		ArrayList<DependencyInfo> result_list = new ArrayList<DependencyInfo>();
		
		// All files depend on base.js, so put it first.
    	result_list.add(new DependencyInfo(base_js));

    	//add dependencies in a sorted way
    	result_list.addAll(search_hash.values());
    	Collections.sort(result_list);
    	
    	return result_list;
	}
	
	/**
	 * Takes a given requirement and resolves all of the dependencies for it.
	 * 
	 * A given requirement may require other dependencies.  This method
	 * recursively resolves all dependencies for the given requirement.
	 * @param seen_list 
	 * @param result_list 
	 * @param search_hash 
	 * @param require 
	 * 
	 * @return
	 * @throws MojoExecutionException 
	 * @throws Exception when require does not exist in the search_hash.
	 */
//	private static void resolveDependencies(final String require, final HashMap<File, DependencyInfo> search_hash, final Set<File> result_list, final Set<File> seen_list) throws IOException, MojoExecutionException {
//		DependencyInfo requireDep = CalcDeps.requireFromDependency(search_hash, require);
//		if (requireDep == null) {
//			throw new MojoExecutionException("Missing provider for (" + require + ")");
//		}
//		File requireFile = requireDep.getFile();
//		
//		if (!seen_list.contains(requireFile)) {
//			seen_list.add(requireFile);
//			for (String sub_require : requireDep.getRequires()) {
//				resolveDependencies(sub_require, search_hash, result_list, seen_list);
//			}
//			result_list.add(requireFile);
//		}
//	}
	
//	private static DependencyInfo requireFromDependency(
//			HashMap<File, DependencyInfo> search_hash, String require) {
//		DependencyInfo returnVal = null;
//		for (DependencyInfo di: search_hash.values()) {
//			if (di.getProvides().contains(require)) {
//				returnVal = di;
//				break;
//			}
//		}
//		return returnVal;
//	}

	/**
	 * Print out a deps.js file from a list of source paths.
	 * 
	 * @param source_paths: Paths that we should generate dependency info for.
	 * @param deps: Paths that provide dependency info. Their dependency info should
	 * not appear in the deps file.
	 * @param out: The output file.
	 * @throws Exception 
	 * @returns True on success, false if it was unable to find the base path 
	 * to generate deps relative to.
	 */
	private static boolean outputDeps(final List<DependencyInfo> deps, final File outputFile) throws IOException {
		FileWriter fw = new FileWriter(outputFile);
		BufferedWriter buff = new BufferedWriter(fw);
		
		buff.append("\n// This file was autogenerated by CalcDeps.java\n");
		for (DependencyInfo file_dep : deps) {
			if (file_dep != null) {
				buff.write(file_dep.toString(outputFile));
				buff.write("\n");
				buff.flush();
			}
	    
		}
		return true;
		
	}
	
	public static void executeCalcDeps(final File googleBaseFile, final Set<File> inputs, final File outputFile) throws IOException, MojoExecutionException {
		logger.debug("Finding Closure dependencies...");
		List<DependencyInfo> deps = calculateDependencies(googleBaseFile, inputs);
		
		//create deps file
		logger.debug("Outputting Closure dependency file...");
		outputDeps(deps, outputFile);
		
		logger.debug("Closure dependencies created");
	}
}
