package org.mojo.javascriptframework.mavenutils.mavenobjects;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;
import org.apache.maven.artifact.Artifact;
import org.mojo.javascriptframework.mavenutils.io.ZipUtils;

/**
 * This will extract the artifacts specified by the scope and packagingType
 * and will store these files in a directory that matches their artifact
 * coordinates followed by package information
 * 
 * @author jgrant
 *
 */
public class ArtifactExtractor {
	private static final Logger logger = Logger.getLogger( ArtifactExtractor.class );

	final int BUFFER = 2048;
	
	private final Set<Artifact> artifacts;
	
	public ArtifactExtractor(final Set<Artifact> artifacts) {
		this.artifacts = artifacts;
	}

	public void extract(final PackagingType packagingType, 
			final ScopeType scope, final ClassifierType classifierType, 
			final File outputDirectory) throws ZipException, IOException {
		logger.debug("unfiltered artifacts size (" + packagingType + ":" + scope + ":" + classifierType + ") : " + artifacts.size());
		
		//filter artifacts
		Set<Artifact> filteredArtifacts = filterArtifactList(packagingType, scope, classifierType);
		logger.debug("filtered artifacts size (" + packagingType + "/" + scope + "): " + filteredArtifacts.size());
		//unzip to directory
		extractSet(filteredArtifacts, outputDirectory);
	}
	
	private void extractSet(Set<Artifact> filteredArtifacts, final File outputDirectory) throws ZipException, IOException {
		for (Artifact artifact:filteredArtifacts) {
			logger.debug("Processing artifact" + artifact.getArtifactId());
			File file = artifact.getFile();
			InputStream is = new FileInputStream(file);
			ZipInputStream zis = new ZipInputStream(is);
			ZipUtils.unzip(zis, outputDirectory);
		}
	}

	private Set<Artifact> filterArtifactList(final PackagingType packagingType, 
			final ScopeType scopeType, final ClassifierType classifierType) {
		Set<Artifact> returnArtifacts = new LinkedHashSet<Artifact>();
		for (Artifact a:artifacts) {
			
			ScopeType artifactScopeType = ScopeType.getByName(a.getScope());
			if (artifactScopeType == null) {
				artifactScopeType = ScopeType.COMPILE;
			}
			PackagingType artifactPackagingType = PackagingType.getByName(a.getType());
			if (artifactPackagingType == null) {
				artifactPackagingType = PackagingType.JAR;
			}
			ClassifierType artifactClassifierType = ClassifierType.getByName(a.getClassifier());
			if (artifactClassifierType == null) {
				artifactClassifierType = ClassifierType.INTERNAL;
			}
			if (scopeType.equals(artifactScopeType) && packagingType.equals(artifactPackagingType) 
					&& artifactClassifierType.equals(artifactClassifierType)) {
				returnArtifacts.add(a);
			}
		}
		return returnArtifacts;
	}
}
