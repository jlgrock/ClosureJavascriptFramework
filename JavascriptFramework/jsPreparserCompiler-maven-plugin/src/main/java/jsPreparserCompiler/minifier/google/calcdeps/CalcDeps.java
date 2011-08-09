package namespaceclosure.minifier.google.calcdeps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import namespaceclosure.io.JSDirectoryWalker;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileList.FileName;

import com.google.javascript.jscomp.ant.CompileTask;

/**
 * Represents a dependency that is used to build and walk a tree.  This is a direct
 * port from the google python script.
 *
 */
public class CalcDeps {
	final CalcDepsOptions options;
	
	public CalcDeps(CalcDepsOptions options) throws Exception {
		this.options = options;
		this.options.getLog().info("options: " + options);
	}
	
	/**
	 * Expands any directory references into inputs.
	 * 
	 * Looks for any directories in the provided references.  Found directories
	 * are recursively searched for .js files, which are then added to the result
	 * list.
	 * 
	 * @param refs a list of references such as files, directories, and namespaces
	 * @return A list of references with directories removed and replaced by any
	 * .js files that are found in them. Also, the paths will be normalized.
	 * @throws IOException 
	 */
	public static Set<File> expandDirectories(Set<File> refs) throws IOException {
		Set<File> result = new HashSet<File>();
		Set<DirectoryWalkResult> dwrs = JSDirectoryWalker.multipleDirectorySearch(refs);
		for (DirectoryWalkResult dwr : dwrs) {
			for (File file : dwr.getFiles()) {
				result.add(file);
			}
		}
		return result;
	}
	
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
	 * @throws Exception if a provided input is invalid.
	 */
	private static ArrayList<File> calculateDependencies(File base_js_path, Set<File> paths, Set<File> inputs) throws Exception {
		HashSet<File> temp = new HashSet<File>();
		temp.addAll(paths);
		temp.addAll(inputs);
		HashMap<File, DependencyInfo> search_hash = CalcDeps.buildDependenciesFromFiles(temp);
		ArrayList<File> result_list = new ArrayList<File>();
		Set<File> seen_list = new HashSet<File>();

		// All files depend on base.js, so put it first.
    	result_list.add(base_js_path);

		for (File input_file:inputs) {
			DependencyInfo dep = AnnotationFileReader.parseForDependencyInfo(input_file);
		    seen_list.add(input_file);
		    for (String require:dep.getRequires()) {
		    	resolveDependencies(require, search_hash, result_list, seen_list);
		    }
		    if (!result_list.contains(input_file)){
		    	result_list.add(input_file);
		    }
		}
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
	 * @throws Exception when require does not exist in the search_hash.
	 */
	private static void resolveDependencies(final String require, final HashMap<File, DependencyInfo> search_hash, final ArrayList<File> result_list, final Set<File> seen_list) throws Exception {
		DependencyInfo requireDep = CalcDeps.requireFromDependency(search_hash, require);
		if (requireDep == null) {
			throw new Exception("Missing provider for (" + require + ")");
		}
		File requireFile = requireDep.getFile();
		
		if (!seen_list.contains(requireFile)) {
			seen_list.add(requireFile);
			for (String sub_require : requireDep.getRequires()) {
				resolveDependencies(sub_require, search_hash, result_list, seen_list);
			}
			result_list.add(requireFile);
		}
	}
	
	private static DependencyInfo requireFromDependency(
			HashMap<File, DependencyInfo> search_hash, String require) {
		DependencyInfo returnVal = null;
		for (DependencyInfo di: search_hash.values()) {
			if (di.getProvides().contains(require)) {
				returnVal = di;
				break;
			}
		}
		return returnVal;
	}

	
	
	private File getBasePathFromOptions() throws Exception {
		File base_path = options.getGoogleBaseFile();
		if (base_path == null || !base_path.exists()) {
			throw new Exception("can't find base.js in google library");
		}
		return base_path;
	}

	/**
	 * Filters the given files by the exlusions specified at the command line.
	 * 
	 * @param options The flags to calcdeps.
	 * @param files The files to filter.
	 * @return A list of files.
	 * @throws IOException 
	 */
	private Set<File> filterByExcludes(Collection<File> files) throws IOException {
		Set<File> excludes = getExcludesFromOptions();
		Set<File> returnVal = new HashSet<File>();
		for (File file:files) {
			if (!excludes.contains(file)) {
				returnVal.add(file);
			}
		}
		return returnVal;
	}
	
	private Set<File> getExcludesFromOptions() throws IOException {
		Set<File> excludes = null;
		if (options.getExcludes() != null) {
			excludes = CalcDeps.expandDirectories(options.getExcludes());
			excludes = options.getExcludes();
		}
		if (excludes == null) {
			excludes = new HashSet<File>();
		}
		return excludes;
	}
	/**
	 * Generates the path files from flag options.
	 * 
	 * @param The flags to calcdeps.
	 * @return A list of files in the specified paths. (strings).
	 * @throws IOException 
	 */
	private Set<File> getPathsFromOptions() throws IOException {
		Set<File> search_paths = options.getPaths();
		search_paths = CalcDeps.expandDirectories(search_paths);
		return filterByExcludes(search_paths);
	}
	/**
	 * Generates the inputs from flag options.
	 * 
	 * @param options The flags to calcdeps.
	 * @return A list of inputs (strings).
	 * @throws IOException 
	 */
	private Set<File> getInputsFromOptions() throws IOException {
		Set<File> inputs = options.getInputs();
		if (inputs == null) {
			options.getLog().info("No inputs specified. Reading from stdin...");
		    throw new IOException("Can't read from stdin, aborting");
		}
		options.getLog().info("Scanning files...");
		inputs = CalcDeps.expandDirectories(inputs);
		return filterByExcludes(inputs);
	}
	
	private Set<File> getDepsFromOptions() throws IOException {
		return CalcDeps.expandDirectories(options.getDeps());		
	}
	
	/**
	 * 
	 * @param source_paths The paths (in dependency order) to the files required.
	 * @throws BuildException
	 * @throws IOException
	 */
	private void compile(Collection<File> source_paths) throws BuildException, IOException {
		CompileTask compileTask = new CompileTask();
		compileTask.setCompilationLevel(options.getMinificationName());
    	for (File f:source_paths) {
			FileList sources = new FileList();
        	FileName fn = new FileName();
    		fn.setName(f.getName());
    		sources.setDir(f.getParentFile());
    		sources.addConfiguredFile(fn);
    		//options.getLog().info("***source - dir:" + f.getParent() + ", fn: " + f.getName());
    		compileTask.addSources(sources);
        }

		compileTask.setWarning("quiet");
		compileTask.setLocation(new Location(options.getCompileFile().getPath()));
		compileTask.setOutput(options.getCompileFile());
		compileTask.execute();
	}
	
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
	private static boolean printDeps(ArrayList<File> file_dependencies, File depsFile, Collection<File> source_paths, Collection<File> deps) throws Exception {
		Collection<File> temp = new ArrayList<File>();
		temp.addAll(source_paths);
		HashMap<File, DependencyInfo> search_hash = CalcDeps.buildDependenciesFromFiles(temp);
		
		
		FileWriter fw = new FileWriter(depsFile);
		BufferedWriter buff = new BufferedWriter(fw);
		Collection<File> excludesSet = new LinkedHashSet<File>();
		excludesSet.addAll(deps);
		
		buff.append("\n// This file was autogenerated by CalcDeps.java (based off of calcdeps.py)\n");
		//for (DependencyInfo dep : CalcDeps.buildDependenciesFromFiles(tempList).values()) {
		for (File file_dep : file_dependencies) {
			if (!excludesSet.contains(file_dep)) {
				DependencyInfo di = search_hash.get(file_dep);
				if (di != null) {
					buff.write(di.toString(depsFile));
					buff.write("\n");
					buff.flush();
				}
		    }
		}
		return true;
		
	}
	
	public void executeCalcDeps() throws Exception {
		Set<File> search_paths = getPathsFromOptions();
		Set<File> inputs = getInputsFromOptions();
		Set<File> inputdeps = getDepsFromOptions();

		options.getLog().info("Finding Closure dependencies...");
		ArrayList<File> deps = CalcDeps.calculateDependencies(getBasePathFromOptions(), search_paths, inputs);
		
		//create deps file
		boolean result = CalcDeps.printDeps(deps, options.getDepsOutputFile(), search_paths, inputdeps);
		
		//create compiled file
		compile(deps);

		//create assert file
		//TODO assertCompile(deps);
	}
}
