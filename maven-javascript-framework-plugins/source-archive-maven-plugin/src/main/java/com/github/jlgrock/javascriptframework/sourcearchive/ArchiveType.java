package org.mojo.javascriptframework.sourcearchive;

public enum ArchiveType {
	SOURCE("zip"),
	GENERATED_JAVASCRIPT("jsar");
	
	private final String fileExtension;
	
	ArchiveType(final String fileExtension) {
		this.fileExtension = fileExtension;
	}
	
	public String getFileExtension() {
		return fileExtension;
	}
}
