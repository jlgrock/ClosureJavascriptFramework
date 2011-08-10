package jsPreparserCompiler.mojotools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.maven.artifact.Artifact;

public class ArtifactExtractor {
	final int BUFFER = 2048;
	
	private final Set<Artifact> artifacts;
	private final ScopeType scope;
	private final PackagingType packagingType;
	
	public ArtifactExtractor(final Set<Artifact> artifacts, final ScopeType scope, final PackagingType packagingType) {
		this.artifacts = artifacts;
		this.scope = scope;
		this.packagingType = packagingType;
	}

	public void extract(final File outputDirectory) throws ZipException, IOException {
		System.out.println("unfiltered artifacts size: " + artifacts.size());
		Set<Artifact> filteredArtifacts = filterArtifactList(artifacts);
		System.out.println("filtered artifacts size (" + packagingType + "/" + scope + "): " + filteredArtifacts.size());
		extractSet(filteredArtifacts, outputDirectory);
	}
	
	private void extractSet(Set<Artifact> filteredArtifacts, final File outputDirectory) throws ZipException, IOException {
		for (Artifact artifact:filteredArtifacts) {
			System.out.println("Processing artifact" + artifact.getArtifactId());
			if (PackagingType.getByName(artifact.getType()).equals(packagingType)) {
				System.out.println("Extracting " + scope + " Package: " + artifact.getArtifactId());
				File file = artifact.getFile();
				ZipFile zipFile=new ZipFile(file);
				Enumeration<? extends ZipEntry> e = zipFile.entries();
				while (e.hasMoreElements()) {
					ZipEntry zipEntry = (ZipEntry) e.nextElement();
					
					if (!(zipEntry.getName().equalsIgnoreCase("META-INF") && zipEntry.isDirectory())) {
						extractEntry(artifact, zipEntry, zipFile, outputDirectory);
					}
		        }
				zipFile.close();
			}
		}
	}

	public void extractEntry(Artifact artifact, ZipEntry zipEntry, ZipFile zipFile, final File outputDirectory) throws IOException {
		BufferedInputStream is = new BufferedInputStream(zipFile.getInputStream(zipEntry));
        int count;
        byte data[] = new byte[BUFFER];
        
        String dependencyDirectoryString = outputDirectory.getAbsoluteFile() + File.separator + artifact.getGroupId() + File.separator + artifact.getArtifactId();
        File dependencyDirectory = new File(dependencyDirectoryString);
        dependencyDirectory.mkdirs();
        
        if (!zipEntry.isDirectory() && !zipEntry.getName().contains("META-INF")) {
        	File newFile = new File(dependencyDirectory + File.separator + zipEntry.getName());
        	
        	newFile.getParentFile().mkdirs();
        	
        	FileOutputStream fos = new FileOutputStream(newFile);
        	BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
        	while ((count = is.read(data, 0, BUFFER)) != -1) {
        	   dest.write(data, 0, count);
        	}
        	dest.flush();
        	dest.close();
        	is.close();
        	//System.out.println("placed-entry: " + newFile.getAbsolutePath());
		}
	}
	
	public int countArtifacts() {
		int artifactCount = 0;
		if (artifacts == null || artifacts.isEmpty() || packagingType == null) {
			return artifactCount;
		}
		for (Artifact a : artifacts) {
			if (a.getType().equalsIgnoreCase(packagingType.toString())) {
				artifactCount++;
			}
		}
		return artifactCount;
	}
	
	private Set<Artifact> filterArtifactList(final Set<Artifact> artifacts) {
		Set<Artifact> returnArtifacts = new LinkedHashSet<Artifact>();
		for (Artifact a:artifacts) {
			ScopeType scopeType = ScopeType.getByName(a.getScope());
			PackagingType packagingType = PackagingType.getByName(a.getType());
			if (scopeType.equals(scope) && packagingType.equals(packagingType)) {
				returnArtifacts.add(a);
			}
		}
		return returnArtifacts;
	}
}
