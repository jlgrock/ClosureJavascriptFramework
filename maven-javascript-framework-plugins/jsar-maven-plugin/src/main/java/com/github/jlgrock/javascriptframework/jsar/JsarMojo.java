package com.github.jlgrock.javascriptframework.jsar;

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.myfaces.buildtools.maven2.plugin.javascript.jmt.archive.JavascriptArchiver;

import com.github.jlgrock.javascriptframework.mavenutils.mavenobjects.JsarRelativeLocations;

/**
 * Build a JSAR package from the current project.
 * 
 * This is essentially a modified JAR plugin, since Plexus offers no way to extend
 * classes from other packages from maven dependencies, this is copied over in
 * its entirety and edited.
 *
 * @goal jsar
 * @phase package
 * @requiresProject
 * @threadSafe
 * @requiresDependencyResolution runtime
 */
public class JsarMojo extends AbstractMojo {
	private static final Logger LOGGER = Logger.getLogger(JsarMojo.class);
	
	/**
	 * The directory to place compiled files into.
	 * 
	 * @parameter default-value="${project.build.directory}${file.separator}javascriptFramework"
	 */
	protected File frameworkTargetDirectory;
	
	/**
     * Classifier to add to the artifact generated. If given, the artifact will be an attachment instead.
     *
     * @parameter
     */
    private String classifier;

    protected String getClassifier() {
        return classifier;
    }

    /**
     * @return type of the generated artifact
     */
    protected String getType() {
        return "jsar";
    }

    private static final String[] DEFAULT_EXCLUDES = new String[] { "**/package.html" };

    private static final String[] DEFAULT_INCLUDES = new String[] { "**/**" };

    /**
     * List of files to include. Specified as fileset patterns which are relative to the input directory whose contents
     * is being packaged into the JSAR.
     *
     * @parameter
     */
    private String[] includes;

    /**
     * List of files to exclude. Specified as fileset patterns which are relative to the input directory whose contents
     * is being packaged into the JSAR.
     *
     * @parameter
     */
    private String[] excludes;

    /**
     * Directory containing the generated JSAR.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File outputDirectory;

    /**
     * Name of the generated JSAR.
     *
     * @parameter alias="jsarName" expression="${jar.finalName}" default-value="${project.build.finalName}"
     * @required
     */
    private String finalName;

    /**
     * The JSAR archiver.
     *
     * @component role="org.codehaus.plexus.archiver.Archiver" roleHint="javascript"
     */
    private JavascriptArchiver jsarArchiver;
   
    /**
     * The Maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * The archive configuration to use.
     * See <a href="http://maven.apache.org/shared/maven-archiver/index.html">Maven Archiver Reference</a>.
     *
     * @parameter
     */
    private MavenArchiveConfiguration archive = new MavenArchiveConfiguration();

    /**
     * Path to the default MANIFEST file to use. It will be used if
     * <code>useDefaultManifestFile</code> is set to <code>true</code>.
     *
     * @parameter expression="${project.build.outputDirectory}/META-INF/MANIFEST.MF"
     * @required
     * @readonly
     * @since 2.2
     */
    private File defaultManifestFile;

    /**
     * Set this to <code>true</code> to enable the use of the <code>defaultManifestFile</code>.
     *
     * @parameter expression="${jar.useDefaultManifestFile}" default-value="false"
     *
     * @since 2.2
     */
    private boolean useDefaultManifestFile;

    /**
     * @component
     */
    private MavenProjectHelper projectHelper;

    /**
     * Whether creating the archive should be forced.
     *
     * @parameter expression="${jar.forceCreation}" default-value="false"
     */
    private boolean forceCreation;
    
    /**
	 * This will default to compiledFilename + "-debug" if not overridden
	 * @parameter default-value="${project.build.finalName}-debug.js"
	 */
	protected String debugFilename;

	/**
	 * @return project
	 */
    protected final MavenProject getProject() {
        return project;
    }

    /**
     * 
     * @param basedir
     * @param finalName
     * @param classifier
     * @return
     */
    protected static File getJsarFile( File basedir, String finalName, String classifier ) {
        if ( classifier == null ) {
            classifier = "";
        }
        else if ( classifier.trim().length() > 0 && !classifier.startsWith( "-" ) ) {
            classifier = "-" + classifier;
        }

        return new File( basedir, finalName + classifier + ".jsar" );
    }

    /**
     * Default Manifest location. Can point to a non existing file.
     * Cannot return null.
     */
    protected File getDefaultManifestFile() {
        return defaultManifestFile;
    }


    /**
     * Generates the JSAR.
     *
     * @todo Add license files in META-INF directory.
     */
    public File createArchive() throws MojoExecutionException {
        File jsarFile = getJsarFile( outputDirectory, finalName, getClassifier() );
        
        MavenArchiver archiver = new MavenArchiver();
        archiver.setArchiver( jsarArchiver );
        archiver.setOutputFile( jsarFile );
        archive.setForced( forceCreation );

        try
        {
        	// Add all output code
            File compiledDirectory = JsarRelativeLocations.getOutputLocation(frameworkTargetDirectory);
            
            archiver.getArchiver().addDirectory( compiledDirectory );
            
            //add manifest
            File existingManifest = getDefaultManifestFile();

            if ( useDefaultManifestFile && existingManifest.exists() && archive.getManifestFile() == null ) {
            	LOGGER.info( "Adding existing MANIFEST to archive. Found under: " + existingManifest.getPath() );
                archive.setManifestFile( existingManifest );
            }
            archiver.createArchive( project, archive );

            return jsarFile;
        }
        catch ( Exception e )
        {
            // TODO: improve error handling
            throw new MojoExecutionException( "Error assembling JSAR", e );
        }
    }

    /**
     * Generates the JSAR.
     *
     * @todo Add license files in META-INF directory.
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        File jsarFile = createArchive();

        String classifier = getClassifier();
        if ( classifier != null ) {
            projectHelper.attachArtifact( getProject(), getType(), classifier, jsarFile );
        } else {
            getProject().getArtifact().setFile( jsarFile );
        }
    }

    private String[] getIncludes() {
        if ( includes != null && includes.length > 0 ) {
            return includes;
        }
        return DEFAULT_INCLUDES;
    }

    private String[] getExcludes() {
        if ( excludes != null && excludes.length > 0 ) {
            return excludes;
        }
        return DEFAULT_EXCLUDES;
    }
}