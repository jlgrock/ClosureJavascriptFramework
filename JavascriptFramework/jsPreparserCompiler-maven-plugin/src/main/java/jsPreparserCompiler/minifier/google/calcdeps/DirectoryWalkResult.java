package jsPreparserCompiler.minifier.google.calcdeps;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class DirectoryWalkResult {
	File directory;
	Set<DirectoryWalkResult> subdirs;
	Set<File> files;
	
	public DirectoryWalkResult() {
		subdirs = new HashSet<DirectoryWalkResult>();
		files = new HashSet<File>();
	}
	
	public File getDirectory() {
		return directory;
	}
	public void setDirectory(File directory) {
		this.directory = directory;
	}
	public Set<DirectoryWalkResult> getSubdirs() {
		return subdirs;
	}
	public void addSubdir(DirectoryWalkResult subdir) {
		this.subdirs.add(subdir);
	}
	public void addSubdirs(Set<DirectoryWalkResult> subdirs) {
		this.subdirs.addAll(subdirs);
	}
	public Set<File> getFiles() {
		return files;
	}
	public void addFile(File file) {
		this.files.add(file);
	}
	public void addFiles(Set<File> files) {
		this.files.addAll(files);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((directory == null) ? 0 : directory.hashCode());
		result = prime * result + ((files == null) ? 0 : files.hashCode());
		result = prime * result + ((subdirs == null) ? 0 : subdirs.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DirectoryWalkResult other = (DirectoryWalkResult) obj;
		if (directory == null) {
			if (other.directory != null)
				return false;
		} else if (!directory.equals(other.directory))
			return false;
		if (files == null) {
			if (other.files != null)
				return false;
		} else if (!files.equals(other.files))
			return false;
		if (subdirs == null) {
			if (other.subdirs != null)
				return false;
		} else if (!subdirs.equals(other.subdirs))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "directory: " + directory + ", subdirs: " + subdirs + ", files: " + files;
	}
}
