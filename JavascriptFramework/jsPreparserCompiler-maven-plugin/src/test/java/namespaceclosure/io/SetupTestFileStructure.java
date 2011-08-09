package namespaceclosure.io;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.HashSet;

import namespaceclosure.minifier.google.calcdeps.DirectoryWalkResult;

import org.mockito.Mockito;

public class SetupTestFileStructure {
	/* file structure:
	 *         1
	 *      /     \
	 *    2_1     2_2
	 *   /   \     |
	 * 3_1   3_2  3_3
	 *
	 */
	File root = Mockito.mock(File.class);
	File dirLevel1 = Mockito.mock(File.class);
	File dirLevel2_1 = Mockito.mock(File.class);
	File dirLevel2_2 = Mockito.mock(File.class);
	File fileLevel1_1 = Mockito.mock(File.class);
	File fileLevel1_2 = Mockito.mock(File.class);
	File fileLevel2_1 = Mockito.mock(File.class);
	File fileLevel2_2 = Mockito.mock(File.class);
	File fileLevel2_3 = Mockito.mock(File.class);
	Collection<File> combinedList;
	
	public void setupThreeLevel() throws Exception {
		setupThreeLevelStructure();
		
		setupThreeLevelFilePaths();
		
		combinedList = new HashSet<File>();
		combinedList.add(fileLevel1_1);
		combinedList.add(fileLevel1_2);
		combinedList.add(fileLevel2_2);
	}

	public void setupThreeLevelStructure() {
		Collection<File> rootLevel = new HashSet<File>();
		rootLevel.add(root);
		
		Collection<File> level1 = new HashSet<File>();
		level1.add(dirLevel2_1);
		level1.add(dirLevel2_2);
		level1.add(fileLevel1_1);
		level1.add(fileLevel1_2);
		
		Collection<File> level2_1 = new HashSet<File>();
		level2_1.add(fileLevel2_1);
		level2_1.add(fileLevel2_2);

		Collection<File> level2_2 = new HashSet<File>();
		level2_2.add(fileLevel2_3);
		Mockito.when(dirLevel1.listFiles()).thenReturn(level1.toArray(new File[level1.size()]));
		Mockito.when(dirLevel1.listFiles(Mockito.any(FileFilter.class))).thenReturn(level1.toArray(new File[level1.size()]));
		Mockito.when(dirLevel1.listFiles(Mockito.any(FilenameFilter.class))).thenReturn(level1.toArray(new File[level1.size()]));
		Mockito.when(dirLevel2_1.listFiles()).thenReturn(level2_1.toArray(new File[level2_1.size()]));
		Mockito.when(dirLevel2_1.listFiles(Mockito.any(FileFilter.class))).thenReturn(level2_1.toArray(new File[level2_1.size()]));
		Mockito.when(dirLevel2_1.listFiles(Mockito.any(FilenameFilter.class))).thenReturn(level2_1.toArray(new File[level2_1.size()]));
		Mockito.when(dirLevel2_2.listFiles()).thenReturn(level2_2.toArray(new File[level2_2.size()]));
		Mockito.when(dirLevel2_2.listFiles(Mockito.any(FileFilter.class))).thenReturn(level2_2.toArray(new File[level2_2.size()]));
		Mockito.when(dirLevel2_2.listFiles(Mockito.any(FilenameFilter.class))).thenReturn(level2_2.toArray(new File[level2_2.size()]));

	}
	
	public void setupThreeLevelFilePaths() throws Exception {
		//directories
		Mockito.when(dirLevel1.toString()).thenReturn("C:\\1");
		Mockito.when(dirLevel1.getPath()).thenReturn("C:\\1");
		Mockito.when(dirLevel1.getAbsolutePath()).thenReturn("C:\\1");
		Mockito.when(dirLevel1.getCanonicalPath()).thenReturn("C:\\1");
		Mockito.when(dirLevel1.getName()).thenReturn("1");
		Mockito.when(dirLevel1.getParent()).thenReturn("C:");
		Mockito.when(dirLevel1.getParentFile()).thenReturn(root);
		Mockito.when(dirLevel1.isDirectory()).thenReturn(true);
		Mockito.when(dirLevel1.exists()).thenReturn(true);
		Mockito.when(dirLevel2_1.toString()).thenReturn("C:\\1\\2_1");
		Mockito.when(dirLevel2_1.getPath()).thenReturn("C:\\1\\2_1");
		Mockito.when(dirLevel2_1.getCanonicalPath()).thenReturn("C:\\1\\2_1");
		Mockito.when(dirLevel2_1.getAbsolutePath()).thenReturn("C:\\1\\2_1");
		Mockito.when(dirLevel1.getName()).thenReturn("2_1");
		Mockito.when(dirLevel1.getParent()).thenReturn("1");
		Mockito.when(dirLevel2_1.getParentFile()).thenReturn(dirLevel1);
		Mockito.when(dirLevel2_1.isDirectory()).thenReturn(true);
		Mockito.when(dirLevel2_1.exists()).thenReturn(true);
		Mockito.when(dirLevel2_2.toString()).thenReturn("c:\\1\\2_2");
		Mockito.when(dirLevel2_2.getPath()).thenReturn("c:\\1\\2_2");
		Mockito.when(dirLevel2_2.getCanonicalPath()).thenReturn("c:\\1\\2_2");
		Mockito.when(dirLevel2_2.getAbsolutePath()).thenReturn("c:\\1\\2_2");
		Mockito.when(dirLevel1.getName()).thenReturn("2_2");
		Mockito.when(dirLevel1.getParent()).thenReturn("1");
		Mockito.when(dirLevel2_2.getParentFile()).thenReturn(dirLevel1);
		Mockito.when(dirLevel2_2.isDirectory()).thenReturn(true);
		Mockito.when(dirLevel2_2.exists()).thenReturn(true);

		//files (level 1)
		Mockito.when(fileLevel1_1.toString()).thenReturn("C:\\1\\1_1.js");
		Mockito.when(fileLevel1_1.getPath()).thenReturn("C:\\1");
		Mockito.when(fileLevel1_1.getCanonicalPath()).thenReturn("C:\\1");
		Mockito.when(fileLevel1_1.getAbsolutePath()).thenReturn("C:\\1");
		Mockito.when(dirLevel1.getName()).thenReturn("1_1.js");
		Mockito.when(dirLevel1.getParent()).thenReturn("1");
		Mockito.when(fileLevel1_1.getParentFile()).thenReturn(dirLevel1);
		Mockito.when(fileLevel1_1.isDirectory()).thenReturn(false);
		Mockito.when(fileLevel1_1.exists()).thenReturn(true);
		Mockito.when(fileLevel1_2.toString()).thenReturn("C:\\1\\1_2.js");
		Mockito.when(fileLevel1_2.getPath()).thenReturn("C:\\1");
		Mockito.when(fileLevel1_2.getCanonicalPath()).thenReturn("C:\\1");
		Mockito.when(fileLevel1_2.getAbsolutePath()).thenReturn("C:\\1");
		Mockito.when(fileLevel1_2.getName()).thenReturn("1_2.js");
		Mockito.when(fileLevel1_2.getParent()).thenReturn("1");
		
		//files (level 2)
		Mockito.when(fileLevel1_2.isDirectory()).thenReturn(false);
		Mockito.when(fileLevel1_2.exists()).thenReturn(true);
		Mockito.when(fileLevel1_2.getParentFile()).thenReturn(dirLevel1);
		Mockito.when(fileLevel2_1.toString()).thenReturn("C:\\1\\2_1\\2_1.xml");
		Mockito.when(fileLevel2_1.getPath()).thenReturn("C:\\1\\2_1");
		Mockito.when(fileLevel2_1.getCanonicalPath()).thenReturn("C:\\1\\2_1");
		Mockito.when(fileLevel2_1.getAbsolutePath()).thenReturn("C:\\1\\2_1");
		Mockito.when(fileLevel2_1.getName()).thenReturn("2_1.xml");
		Mockito.when(fileLevel2_1.getParent()).thenReturn("2_1");
		Mockito.when(fileLevel2_1.isDirectory()).thenReturn(false);
		Mockito.when(fileLevel2_1.exists()).thenReturn(true);
		Mockito.when(fileLevel2_1.getParentFile()).thenReturn(dirLevel2_1);
		Mockito.when(fileLevel2_2.toString()).thenReturn("c:\\1\\2_1\\2_2.js");
		Mockito.when(fileLevel2_2.getPath()).thenReturn("c:\\1\\2_1");
		Mockito.when(fileLevel2_2.getCanonicalPath()).thenReturn("c:\\1\\2_1");
		Mockito.when(fileLevel2_2.getAbsolutePath()).thenReturn("c:\\1\\2_1");
		Mockito.when(fileLevel2_2.getName()).thenReturn("2_2.js");
		Mockito.when(fileLevel2_2.getParent()).thenReturn("2_1");
		Mockito.when(fileLevel2_2.getParentFile()).thenReturn(dirLevel2_1);
		Mockito.when(fileLevel2_2.isDirectory()).thenReturn(false);
		Mockito.when(fileLevel2_2.exists()).thenReturn(true);
		Mockito.when(fileLevel2_3.toString()).thenReturn("c:\\1\\2_2\\2_3.dll");
		Mockito.when(fileLevel2_3.getPath()).thenReturn("c:\\1\\2_2");
		Mockito.when(fileLevel2_3.getCanonicalPath()).thenReturn("c:\\1\\2_2");
		Mockito.when(fileLevel2_3.getAbsolutePath()).thenReturn("c:\\1\\2_2");
		Mockito.when(fileLevel2_3.getName()).thenReturn("2_3.dll");
		Mockito.when(fileLevel2_3.getParent()).thenReturn("2_2");
		Mockito.when(fileLevel2_3.getParentFile()).thenReturn(dirLevel2_2);
		Mockito.when(fileLevel2_3.isDirectory()).thenReturn(false);
		Mockito.when(fileLevel2_3.exists()).thenReturn(true);
	}

	public File getDirLevel1() {
		return this.dirLevel1;
	}

	public Collection<File> getCombinedList() {
		return this.combinedList;
	}

	public Collection<DirectoryWalkResult> createDWRs() {
		Collection<DirectoryWalkResult> results = new HashSet<DirectoryWalkResult>();
		DirectoryWalkResult dwr1 = new DirectoryWalkResult();
		dwr1.setDirectory(dirLevel1);
		dwr1.addFile(fileLevel1_1);
		dwr1.addFile(fileLevel1_2);

		DirectoryWalkResult dwr2_1 = new DirectoryWalkResult();
		dwr2_1.setDirectory(dirLevel2_1);
		dwr2_1.addFile(fileLevel2_1);
		dwr2_1.addFile(fileLevel2_2);
		dwr1.addSubdir(dwr2_1);

		DirectoryWalkResult dwr2_2 = new DirectoryWalkResult();
		dwr2_2.setDirectory(dirLevel2_2);
		dwr2_2.addFile(fileLevel2_3);
		dwr1.addSubdir(dwr2_2);

		results.add(dwr1);
		results.add(dwr2_1);
		results.add(dwr2_2);
		return results;
	}

}
