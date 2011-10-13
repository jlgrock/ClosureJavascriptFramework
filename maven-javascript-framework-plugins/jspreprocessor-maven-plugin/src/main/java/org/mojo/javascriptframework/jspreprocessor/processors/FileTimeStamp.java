package org.mojo.javascriptframework.jspreprocessor.processors;

import org.apache.maven.project.MavenProject;

public class FileTimeStamp {
	private MavenProject project;

	FileTimeStamp() {
		
	}
	
	public void execute() {
		//TODO add this information to the top of every file
		project.getVersion();
		project.getGroupId();
		project.getArtifactId();
		System.getProperty("java.version");
		System.getProperty("os.name");
		System.getProperty("user.name");
		//getCopyright();
		//getLicense();
	}
}