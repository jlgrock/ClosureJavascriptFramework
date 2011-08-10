package jsPreparserCompiler.minifier.google.calcdeps;

import java.io.File;
import java.io.IOException;

import jsPreparserCompiler.io.RelativePath;
import junit.framework.Assert;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RelativePath.class)
public class DependencyInfoTest {
	DependencyInfo di;
	DependencyInfo di2;
	File mockedFile = Mockito.mock(File.class);
	File mockedFile2 = Mockito.mock(File.class);
	File mockedPath = Mockito.mock(File.class);
	
	@Before
	public void setup() throws IOException {
		Mockito.when(mockedFile.toString()).thenReturn("C:\\temp\\temp.js");
		Mockito.when(mockedFile2.toString()).thenReturn("C:\\temp2\\temp.js");
		Mockito.when(mockedPath.toString()).thenReturn("C:\\temp");
		Mockito.when(mockedPath.getAbsolutePath()).thenReturn("C:\\temp2");

		PowerMockito.mockStatic(RelativePath.class);
		Mockito.when(RelativePath.getRelPathFromBase(mockedPath, mockedFile)).thenReturn("temp.js");
		Mockito.when(RelativePath.getRelPathFromBase(mockedPath, mockedFile2)).thenReturn("../temp2/temp.js");
		Mockito.when(RelativePath.getRelPathFromBase(mockedFile, mockedFile)).thenReturn("temp.js");
		Mockito.when(RelativePath.getRelPathFromBase(mockedFile, mockedFile2)).thenReturn("../temp2/temp.js");
		
		di = new DependencyInfo(mockedFile);
		
		di.addToProvides("Sparrow");
		di.addToProvides("Pheasant");
		di.addToRequires("Animal");
		di.addToRequires("Bird");

		di2 = new DependencyInfo(mockedFile2);
		di2.addToProvides("Lovebird");
		di2.addToProvides("Parrot");
		di2.addToRequires("Animal");
		di2.addToRequires("Bird");

	}
	
	@Test
	public void testGetProvideString() {
		Assert.assertEquals(true, di.getProvidesString().equalsIgnoreCase("['Sparrow', 'Pheasant']") || di.getProvidesString().equalsIgnoreCase("['Pheasant', 'Sparrow']"));
	}
	
	@Test
	public void testGetRequiresString() {
		Assert.assertEquals(true, di.getRequiresString().equalsIgnoreCase("['Animal', 'Bird']") || di.getRequiresString().equalsIgnoreCase("['Bird', 'Animal']"));
	}
	
	@Test
	public void testToString() throws IOException {
		//TODO make these work.  Needs to have relative pathing and the order of the provides/requires is undetermined.
		
//		Assert.assertEquals("goog.addDependency('temp.js', ['Sparrow', 'Pheasant'], ['Animal', 'Bird']);", di.toString(mockedPath));
//		Assert.assertEquals("goog.addDependency('../temp2/temp.js', ['Lovebird', 'Parrot'], ['Animal', 'Bird']);", di2.toString(mockedPath));
//		Assert.assertEquals("goog.addDependency('temp.js', ['Sparrow', 'Pheasant'], ['Animal', 'Bird']);", di.toString(mockedFile));
//		Assert.assertEquals("goog.addDependency('../temp2/temp.js', ['Lovebird', 'Parrot'], ['Animal', 'Bird']);", di2.toString(mockedFile));
	}
}
