package jsPreparserCompiler.minifier.google.calcdeps;

import java.io.File;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class DirectoryWalkResultTest {
	File file1 = Mockito.mock(File.class);
	File file2 = Mockito.mock(File.class);
	File directory = Mockito.mock(File.class);

	@Before
	public void setup() {
	}
	
	@Test
	public void testCompareWithAdjustedOrder() {
		DirectoryWalkResult dwr1 = new DirectoryWalkResult();
		dwr1.setDirectory(directory);
		dwr1.addFile(file1);
		dwr1.addFile(file2);
		DirectoryWalkResult dwr2 = new DirectoryWalkResult();
		dwr2.setDirectory(directory);
		dwr2.addFile(file2);
		dwr2.addFile(file1);
		Assert.assertEquals(dwr1, dwr2);
	}
	
	@Test
	public void testCompareWithSubdirectories() {
		
	}

}