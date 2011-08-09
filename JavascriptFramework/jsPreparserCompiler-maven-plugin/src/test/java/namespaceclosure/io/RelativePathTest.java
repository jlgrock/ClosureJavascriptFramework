package namespaceclosure.io;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RelativePath.class)
public class RelativePathTest {

	File file1 = Mockito.mock(File.class);
	File file2 = Mockito.mock(File.class);
	File dir1 = Mockito.mock(File.class);
	File dir2 = Mockito.mock(File.class);
	
	@Before
	public void setup() throws IOException {
		Mockito.when(file1.getAbsolutePath()).thenReturn("C:" + File.separator + "temp" + File.separator + "temp.js");
		Mockito.when(file2.getAbsolutePath()).thenReturn("C:" + File.separator + "temp2" + File.separator + "temp.js");
		Mockito.when(dir1.getAbsolutePath()).thenReturn("C:" + File.separator + "temp");
		Mockito.when(dir2.getAbsolutePath()).thenReturn("C:" + File.separator + "temp2");

		Mockito.when(file1.getCanonicalPath()).thenReturn("C:" + File.separator + "temp" + File.separator + "temp.js");
		Mockito.when(file2.getCanonicalPath()).thenReturn("C:" + File.separator + "temp2" + File.separator + "temp.js");
		Mockito.when(dir1.getCanonicalPath()).thenReturn("C:" + File.separator + "temp");
		Mockito.when(dir2.getCanonicalPath()).thenReturn("C:" + File.separator + "temp2");
		
	}
	
	@Test
	public void testRelativeFromPathToPath() throws IOException {
		Assert.assertEquals(".." + File.separator + "temp2", RelativePath.getRelPathFromBase(dir1, dir2));
	}

	@Test
	public void testRelativeFromPathtoFile() throws IOException {
		Assert.assertEquals(".." + File.separator + "temp2" + File.separator + "temp.js", RelativePath.getRelPathFromBase(dir1, file2));
	}

	@Test
	public void testRelativeFromFiletoPath() throws IOException {
		Assert.assertEquals(".." + File.separator + "temp2", RelativePath.getRelPathFromBase(file1, dir2));
	}

	@Test
	public void testRelativeFromFiletoFile() throws IOException {
		Assert.assertEquals(".." + File.separator + "temp2" + File.separator + "temp.js", RelativePath.getRelPathFromBase(file1, file2));
	}
}
