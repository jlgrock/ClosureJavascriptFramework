package org.mojo.javascriptframework.closurecompiler;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.mojo.javascriptframework.mavenutils.pathing.RelativePath;


public class DependencyInfo implements Comparable<DependencyInfo> {
	private File file;
	private Set<String> provides;
	private Set<String> requires;
	
	public DependencyInfo(File file) {
		this.file = file;
		this.provides = new HashSet<String>();
		this.requires = new HashSet<String>();
	}
	
	@Override
	public String toString() {
		String s = null;
		try {
			s = toString(null);
		} catch (IOException e) {
			//can't happen
		}
		return s;
	}
	
	public String toString(File basePath) throws IOException {
		String filePath = "";
		if (basePath==null) {
			filePath = getFile().getCanonicalPath();
		} else {
			try {
				filePath = RelativePath.getRelPathFromBase(basePath, getFile());
			} catch (IOException e) {
				filePath = ""; //If there is an exception, set the file path to be blank
			}
		}
		return "goog.addDependency('" + filePath + "', " + getProvidesString() + ", " + getRequiresString() + ");";
	}

	public void addToProvides(String s) {
		provides.add(s);
	}
	
	public void addToRequires(String s) {
		requires.add(s);
	}

	protected File getFile() {
		return file;
	}

	protected String getFilename() {
		return file.getName();
	}
	
	protected Collection<String> getProvides() {
		return provides;
	}

	protected String getProvidesString() {
		StringBuilder providesString = new StringBuilder();
		int i = 0;
		providesString.append("[");
		for (String provide : getProvides()) {
			if (i > 0)
				providesString.append(", ");
			providesString.append("'");
			providesString.append(provide);
			providesString.append("'");
			i++;
		}
		providesString.append("]");
		return providesString.toString();
	}
	
	protected Collection<String> getRequires() {
		return requires;
	}

	protected String getRequiresString() {
		StringBuilder requiresString = new StringBuilder();
		int i = 0;
		requiresString.append("[");
		for (String require : getRequires()) {
			if (i > 0)
				requiresString.append(", ");
			requiresString.append("'");
			requiresString.append(require);
			requiresString.append("'");
			i++;
		}
		requiresString.append("]");
		return requiresString.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		result = prime * result
				+ ((provides == null) ? 0 : provides.hashCode());
		result = prime * result
				+ ((requires == null) ? 0 : requires.hashCode());
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
		DependencyInfo other = (DependencyInfo) obj;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		if (provides == null) {
			if (other.provides != null)
				return false;
		} else if (!provides.equals(other.provides))
			return false;
		if (requires == null) {
			if (other.requires != null)
				return false;
		} else if (!requires.equals(other.requires))
			return false;
		return true;
	}

	@Override
	public int compareTo(DependencyInfo o) {
		//if you require something of me, you are less than me
		for (String provide:this.getProvides()) {
			if (o.getRequires().contains(provide)) {
				return -1;
			}
		}
		return 0;
	}

}
