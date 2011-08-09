package namespaceclosure.io;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;
import namespaceclosure.minifier.google.calcdeps.DirectoryWalkResult;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest(JSDirectoryWalker.class)
public class JSDirectoryWalkerTest {
	SetupTestFileStructure stfs;
	
	@Before
	public void setup() throws Exception {
		stfs = new SetupTestFileStructure();
		stfs.setupThreeLevel();
	}
	
	
	@Test
	public void testWalkWithNoFilter() throws Exception {
		//TODO works sometimes... order problem?

		Set<File> list = new HashSet<File>();
		list.add(stfs.getDirLevel1());
		
		//System.out.println("cl: " + stfs.getCombinedList());
	
		JSDirectoryWalker jsdw =  Whitebox.invokeConstructor(JSDirectoryWalker.class);
		Collection<DirectoryWalkResult> results = new HashSet<DirectoryWalkResult>();
		
		Whitebox.invokeMethod(jsdw, "walk", stfs.getDirLevel1(), results);
		//Whitebox.invokeMethod(jsdw, "walk", new File("C:\\Workspaces\\Tahoe\\trunk\\synapseCT\\SynapseCore\\war\\target\\namespaceClosureStaging\\compiled\\sources"), results);
		System.out.println("walk results: " + results);
		
		//TODO Assert.assertEquals(stfs.createDWRs(), results); has an issue with the compares...
	}
	
	
}
