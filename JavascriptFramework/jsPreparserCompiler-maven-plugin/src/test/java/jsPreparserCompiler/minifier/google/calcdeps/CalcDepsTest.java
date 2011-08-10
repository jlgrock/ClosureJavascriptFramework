package jsPreparserCompiler.minifier.google.calcdeps;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CalcDeps.class, AnnotationFileReader.class})
public class CalcDepsTest {
	Set<File> fileset = null;
	Set<File> fileset2 = null;
	Set<File> fileset3 = null;
	ArrayList<File> orderedResult;
	
	File file1 = Mockito.mock(File.class);
	File file2 = Mockito.mock(File.class);
	File file3 = Mockito.mock(File.class);
	File file4 = Mockito.mock(File.class);
	File base = new File("C:\\Temp\\base.js");

	DependencyInfo dep1;
	DependencyInfo dep2;
	DependencyInfo dep3;
	DependencyInfo dep4;
	Set<DependencyInfo> deplist;

	@Before
	public void setup() throws IOException {
		Mockito.when(file1.getCanonicalPath()).thenReturn("C:\\codebase\\smallbirds.js");
		Mockito.when(file2.getCanonicalPath()).thenReturn("C:\\codebase-core\\abstractbird.js");
		Mockito.when(file3.getCanonicalPath()).thenReturn("C:\\codebase\\bigbirds.js");
		Mockito.when(file4.getCanonicalPath()).thenReturn("C:\\x\\robots.js");
		Mockito.when(file1.toString()).thenReturn("C:\\codebase\\smallbirds.js");
		Mockito.when(file2.toString()).thenReturn("C:\\codebase-core\\abstractbird.js");
		Mockito.when(file3.toString()).thenReturn("C:\\codebase\\bigbirds.js");
		Mockito.when(file4.toString()).thenReturn("C:\\x\\robots.js");
		orderedResult = new ArrayList<File>();
		orderedResult.add(base);
		orderedResult.add(file2);
		orderedResult.add(file1);
		orderedResult.add(file3);
		
		fileset = new HashSet<File>();
		fileset.add(file3);
		fileset.add(file1);
		fileset.add(file2);
		
		fileset2 = new HashSet<File>();
		fileset2.add(file1);
		fileset2.add(file2);
		fileset2.add(file4);
		fileset2.add(file3);

		fileset3 = new HashSet<File>();
		fileset3.add(file1);
		fileset3.add(file3);

		dep1 = new DependencyInfo(file1);
		dep1.addToProvides("Lovebird");
		dep1.addToProvides("Sparrow");
		dep1.addToRequires("Bird");
		
		dep2 = new DependencyInfo(file2);
		dep2.addToProvides("Animal");
		dep2.addToProvides("Bird");
		
		dep3 = new DependencyInfo(file3);
		dep3.addToProvides("Eagle");
		dep3.addToProvides("Cardinal");
		dep3.addToRequires("Bird");
		
		dep4 = new DependencyInfo(file4);
		dep4.addToProvides("Robot");
		
		deplist = new HashSet<DependencyInfo>();
		deplist.add(dep1);
		deplist.add(dep2);
		deplist.add(dep3);
	}
	
	@Test
	public void testExpandDirectories() throws IOException {
		//Assert.assertEquals(combinedList, CalcDeps.expandDirectories(list));
	}
	
	 /* Build a list of dependencies from a list of files.
	 * Takes a list of files, extracts their provides and requires, and builds
	 * out a list of dependency objects.
	*/
	//	private Collection<DependencyInfo> buildDependenciesFromFiles(Collection<File> files) throws IOException {
	@Test
	public void testBuildDependenciesFromFiles() throws Exception {
		PowerMockito.mockStatic(AnnotationFileReader.class);
		Mockito.when(AnnotationFileReader.parseForDependencyInfo(file1)).thenReturn(dep1);
		Mockito.when(AnnotationFileReader.parseForDependencyInfo(file2)).thenReturn(dep2);
		Mockito.when(AnnotationFileReader.parseForDependencyInfo(file3)).thenReturn(dep3);
		HashMap<File, DependencyInfo> depHashMap = new HashMap<File, DependencyInfo>();
		for (DependencyInfo dep : deplist)
			depHashMap.put(dep.getFile(), dep);
		Assert.assertEquals(depHashMap, Whitebox.invokeMethod(CalcDeps.class, "buildDependenciesFromFiles", fileset));
	}
	
	//	private Collection<File> calculateDependencies(Collection<File> paths, Collection<File> inputs) throws Exception {
	@Test
	public void testCalculateDependencies() throws Exception {
		PowerMockito.mockStatic(AnnotationFileReader.class);
		Mockito.when(AnnotationFileReader.parseForDependencyInfo(file1)).thenReturn(dep1);
		Mockito.when(AnnotationFileReader.parseForDependencyInfo(file2)).thenReturn(dep2);
		Mockito.when(AnnotationFileReader.parseForDependencyInfo(file3)).thenReturn(dep3);
		Mockito.when(AnnotationFileReader.parseForDependencyInfo(file4)).thenReturn(dep4);
		
		ArrayList<File> calcDepsReturn = Whitebox.invokeMethod(CalcDeps.class, "calculateDependencies", base, fileset2, fileset3);
		Assert.assertEquals(true, calcDepsReturn.contains(file1));
		Assert.assertEquals(true, calcDepsReturn.contains(file2));
		Assert.assertEquals(true, calcDepsReturn.contains(file3));
		Assert.assertEquals(false, calcDepsReturn.contains(file4));
		Assert.assertEquals(4, calcDepsReturn.size()); //three files + base
	}
	
	//	private void resolveDependencies(final String require, final HashMap<File, DependencyInfo> search_hash, final ArrayList<File> result_list, final ArrayList<File> seen_list) throws Exception {
	@Test
	public void testResolveDependencies() {
		
	}
	
	//private boolean printDeps(Collection<File> source_paths, Collection<File> deps) throws Exception {
	@Test
	public void testPrintDeps() {
		
	}
	
	//	private Collection<File> filterByExcludes(Collection<File> files) throws IOException {
	@Test
	public void testFilterByExcludes() {
		
	}
	
	//private void compile(Collection<File> source_paths) throws BuildException, IOException {
	@Test
	public void testCompile() {
		
	}
	
	@Test
	public void testAssertCompile() {
		
	}
	
}
