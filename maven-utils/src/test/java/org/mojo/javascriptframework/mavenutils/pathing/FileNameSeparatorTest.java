package org.mojo.javascriptframework.mavenutils.pathing;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;

import com.github.jlgrock.javascriptframework.mavenutils.pathing.FileNameSeparator;

public class FileNameSeparatorTest {

	@Test
	public void testSplitting() {
		File file = new File("C:\\test\\mytest.java");
		FileNameSeparator fns = new FileNameSeparator(file);
		Assert.assertEquals("java", fns.getExtension());
		Assert.assertEquals("mytest", fns.getName());
		Assert.assertEquals("C:\\test", fns.getPath());
		
	}
}
