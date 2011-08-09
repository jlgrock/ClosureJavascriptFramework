package namespaceclosure;

import java.io.File;

import namespaceclosure.utils.JsJarArchiver;

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.jar.JarArchiver;

/**
 * Build a JSJAR package from the current project.
 *
 * @goal jsjar
 * @phase package
 * @requiresProject
 * @threadSafe
 * @requiresDependencyResolution runtime
 */
public class JsJarMojo extends AbstractNamespaceClosureMojo {
	/**
     * Classifier to add to the artifact generated. If given, the artifact will be an attachment instead.
     *
     * @parameter
     */
    private String classifier;

    protected String getClassifier()
    {
        return classifier;
    }

    /**
     * @return type of the generated artifact
     */
    protected String getType()
    {
        return "jsjar";
    }

    /**
     * Return the main classes directory, so it's used as the root of the jar.
     */
    protected File getClassesDirectory()
    {
    	getLog().info("retrieving file from: " + compileDirectory);
        return compileDirectory;
    }
    
    
    private static final String[] DEFAULT_EXCLUDES = new String[] { "**/package.html" };

    private static final String[] DEFAULT_INCLUDES = new String[] { "**/**" };

    /**
     * List of files to include. Specified as fileset patterns which are relative to the input directory whose contents
     * is being packaged into the JAR.
     *
     * @parameter
     */
    private String[] includes;

    /**
     * List of files to exclude. Specified as fileset patterns which are relative to the input directory whose contents
     * is being packaged into the JAR.
     *
     * @parameter
     */
    private String[] excludes;

    /**
     * Directory containing the generated JAR.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File outputDirectory;

    /**
     * Name of the generated JAR.
     *
     * @parameter alias="jarName" expression="${jar.finalName}" default-value="${project.build.finalName}"
     * @required
     */
    private String finalName;

    /**
     * The JSJar archiver.
     *
     * @component role="org.codehaus.plexus.archiver.Archiver" roleHint="jar"
     */
    private JarArchiver jsjarArchiver;
   
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

    protected final MavenProject getProject()
    {
        return project;
    }

    protected static File getJarFile( File basedir, String finalName, String classifier )
    {
        if ( classifier == null )
        {
            classifier = "";
        }
        else if ( classifier.trim().length() > 0 && !classifier.startsWith( "-" ) )
        {
            classifier = "-" + classifier;
        }

        return new File( basedir, finalName + classifier + ".jsjar" );
    }

    /**
     * Default Manifest location. Can point to a non existing file.
     * Cannot return null.
     */
    protected File getDefaultManifestFile()
    {
        return defaultManifestFile;
    }


    /**
     * Generates the JAR.
     *
     * @todo Add license files in META-INF directory.
     */
    public File createArchive()
        throws MojoExecutionException
    {
        File jarFile = getJarFile( outputDirectory, finalName, getClassifier() );

        JsJarArchiver archiver = new JsJarArchiver();

        archiver.setArchiver( jsjarArchiver );
        
        archiver.setOutputFile( jarFile );

        archive.setForced( forceCreation );

        try
        {
            File contentDirectory = getClassesDirectory();
            if ( !contentDirectory.exists() )
            {
                getLog().warn( "JSJAR will be empty - no content was marked for inclusion!" );
            }
            else
            {
                archiver.getArchiver().addDirectory( contentDirectory, getIncludes(), getExcludes() );
            }

            File existingManifest = getDefaultManifestFile();

            if ( useDefaultManifestFile && existingManifest.exists() && archive.getManifestFile() == null )
            {
                getLog().info( "Adding existing MANIFEST to archive. Found under: " + existingManifest.getPath() );
                archive.setManifestFile( existingManifest );
            }
            archiver.createArchive( project, archive, debugFilename );

            return jarFile;
        }
        catch ( Exception e )
        {
            // TODO: improve error handling
            throw new MojoExecutionException( "Error assembling JAR", e );
        }
    }

    /**
     * Generates the JAR.
     *
     * @todo Add license files in META-INF directory.
     */
    public void executeMojo() throws MojoExecutionException, MojoFailureException 
    {
        File jarFile = createArchive();

        String classifier = getClassifier();
        if ( classifier != null )
        {
            projectHelper.attachArtifact( getProject(), getType(), classifier, jarFile );
        }
        else
        {
            getProject().getArtifact().setFile( jarFile );
        }
    }

    private String[] getIncludes()
    {
        if ( includes != null && includes.length > 0 )
        {
            return includes;
        }
        return DEFAULT_INCLUDES;
    }

    private String[] getExcludes()
    {
        if ( excludes != null && excludes.length > 0 )
        {
            return excludes;
        }
        return DEFAULT_EXCLUDES;
    }

	@Override
	public void checkForErrors() throws MojoExecutionException,
			MojoFailureException {
		// shouldn't need any additional checks
		
	}
}
