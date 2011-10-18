package com.github.jlgrock.javascriptframework.sourcearchive;

import org.apache.log4j.Logger;
import org.apache.maven.project.MavenProject;

/**
 * This Mojo will zip everything in the declared source directory into
 * a Source Archive file, which will be stored into the local
 * repository.
 * 
 * @author <a href="mailto:grantjl@umich.edu">Justin Grant</a>
 * @requiresProject
 * @goal java-source-archive
 * @inheritByDefault false
 * @threadSafe
 */
public class JavaSourceArchiveMojo extends AbstractArchiveMojo {

	/**
	 * The Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(JavaSourceArchiveMojo.class);
	
	/**
	 * The resource to assemble with.
	 */
	public static final String DESCRIPTOR_RESOURCE_NAME = "java-src-assembly.xml";
	
    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    @Override
    public final MavenProject getProject() {
        return project;
    }

    @Override
	protected final String getDescriptorResourceName() {
		return DESCRIPTOR_RESOURCE_NAME;
	}
}
