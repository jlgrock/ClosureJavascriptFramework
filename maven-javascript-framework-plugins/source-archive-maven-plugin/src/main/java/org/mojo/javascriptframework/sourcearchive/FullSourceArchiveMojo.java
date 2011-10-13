package org.mojo.javascriptframework.sourcearchive;

import org.apache.log4j.Logger;
import org.apache.maven.project.MavenProject;

/**
 * This Mojo will zip everything in the declared source directory into
 * a "sar" (Source Archive) file, which will be stored into the local
 * repository.
 * 
 * @author <a href="mailto:grantjl@umich.edu">Justin Grant</a>
 * @requiresProject
 * @goal full-source-archive
 * @inheritByDefault false
 * @threadSafe
 */
public class FullSourceArchiveMojo extends AbstractArchiveMojo {

	static Logger logger = Logger.getLogger(FullSourceArchiveMojo.class);
	
	public static final String DESCRIPTOR_RESOURCE_NAME = "full-src-assembly.xml";
	
    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    @Override
    public MavenProject getProject()
    {
        return project;
    }

	protected String getDescriptorResourceName() {
		return DESCRIPTOR_RESOURCE_NAME;
	}

}