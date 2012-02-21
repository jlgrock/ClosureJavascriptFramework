package com.github.jlgrock.javascriptframework.sourcearchive;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.assembly.AssemblerConfigurationSource;
import org.apache.maven.plugin.assembly.InvalidAssemblerConfigurationException;
import org.apache.maven.plugin.assembly.archive.ArchiveCreationException;
import org.apache.maven.plugin.assembly.archive.AssemblyArchiver;
import org.apache.maven.plugin.assembly.format.AssemblyFormattingException;
import org.apache.maven.plugin.assembly.io.AssemblyReadException;
import org.apache.maven.plugin.assembly.io.AssemblyReader;
import org.apache.maven.plugin.assembly.model.Assembly;
import org.apache.maven.plugin.assembly.utils.AssemblyFormatUtils;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.maven.shared.filtering.MavenFileFilter;
import org.codehaus.plexus.configuration.PlexusConfiguration;

import com.github.jlgrock.javascriptframework.mavenutils.io.DirectoryIO;
import com.github.jlgrock.javascriptframework.mavenutils.io.ResourceIO;
import com.github.jlgrock.javascriptframework.mavenutils.logging.MojoLogAppender;

/**
 * Essentially the same as the Assembly Plugin Mojo, with a convenience function
 * to have the src assembly xml files set up for you already. Unfortunately
 * there is no way to extend from one plugin space to another within the plexus
 * system, so I copied the entire Mojo.
 * 
 * This is just to help automate and minimize the amount of code in the poms.
 * 
 * @threadSafe
 */
public abstract class AbstractArchiveMojo extends AbstractMojo implements
		AssemblerConfigurationSource {

	/**
	 * The Logger.
	 */
	static final Logger LOGGER = Logger.getLogger(AbstractArchiveMojo.class);

	/**
	 * Location to get the assembly descriptor.
	 * 
	 * @parameter default-value=
	 *            "${project.build.directory}${file.separator}assemblyDescriptor${file.separator}assembly.xml"
	 * @required
	 */
	protected String assemblyDescriptorLocation;

	/**
	 * Remove target directories before starting work.
	 * 
	 * @throws IOException if unable to delete
	 */
	protected final void cleanup() throws IOException {
		File assemblyDesc = new File(assemblyDescriptorLocation);
		if (assemblyDesc.exists()) {
			if (assemblyDesc.isDirectory()) {
				DirectoryIO.recursivelyDeleteDirectory(assemblyDesc);
			} else {
				assemblyDesc.delete();
			}
		}
	}

	/**
	 * Copy the descriptor over to do the type of archiving required.
	 * 
	 * @throws IOException if there is a problem copying
	 */
	protected final void copyDescriptorResource() throws IOException {
		LOGGER.info("copying default descriptor to \""
				+ assemblyDescriptorLocation + "\"");
		if (assemblyDescriptorLocation == null) {
			LOGGER.error("assemblyDescriptorLocation cannot be null");
		}
		File descLoc = new File(assemblyDescriptorLocation);
		ResourceIO.copyResource(getDescriptorResourceName(), descLoc);
	}

	/**
	 * Accessor to the descriptor resource name that will be defined in children
	 * objects.
	 * 
	 * @return the descriptor resource name
	 */
	protected abstract String getDescriptorResourceName();

	/**
	 * Flag allowing one or more executions of the assembly plugin to be
	 * configured as skipped for a particular build. This makes the assembly
	 * plugin more controllable from profiles.
	 * 
	 * @parameter expression="${assembly.skipAssembly}" default-value="false"
	 */
	private boolean skipAssembly;

	/**
	 * If this flag is set, everything up to the call to
	 * Archiver.createArchive() will be executed.
	 * 
	 * @parameter expression="${assembly.dryRun}" default-value="false"
	 */
	private boolean dryRun;

	/**
	 * If this flag is set, the ".dir" suffix will be suppressed in the output
	 * directory name when using assembly/format == 'dir' and other formats that
	 * begin with 'dir'. <br/>
	 * <b>NOTE:</b> Since 2.2-beta-3, the default-value for this is true, NOT
	 * false as it used to be.
	 * 
	 * @parameter default-value="true"
	 */
	private boolean ignoreDirFormatExtensions;

	/**
	 * Local Maven repository where artifacts are cached during the build
	 * process.
	 * 
	 * @parameter default-value="${localRepository}"
	 * @required
	 * @readonly
	 */
	private ArtifactRepository localRepository;

	/**
	 * @parameter default-value="${project.remoteArtifactRepositories}"
	 * @required
	 * @readonly
	 */
	private List<ArtifactRepository> remoteRepositories;

	/**
	 * Contains the full list of projects in the reactor.
	 * 
	 * @parameter default-value="${reactorProjects}"
	 * @required
	 * @readonly
	 */
	private List<MavenProject> reactorProjects;

	/**
	 * The output directory of the assembled distribution file.
	 * 
	 * @parameter default-value="${project.build.directory}"
	 * @required
	 */
	private File outputDirectory;

	/**
	 * The filename of the assembled distribution file.
	 * 
	 * @parameter default-value="${project.build.finalName}-src"
	 * @required
	 */
	private String finalName;

	/**
	 * Directory to unpack JARs into if needed.
	 * 
	 * @parameter default-value="${project.build.directory}/assembly/work"
	 * @required
	 */
	private File workDirectory;

	/**
	 * This is the artifact classifier to be used for the resultant assembly
	 * artifact. Normally, you would use the assembly-id instead of specifying
	 * this here.
	 * 
	 * @parameter expression="${classifier}"
	 * @deprecated Please use the Assembly's id for classifier instead
	 */
	@Deprecated
	@SuppressWarnings("unused")
	private String classifier;

	/**
	 * A list of descriptor files to generate from.
	 * 
	 * @parameter
	 */
	private String[] descriptors;

	/**
	 * A list of references to assembly descriptors available on the plugin's
	 * classpath. The default classpath includes these built-in descriptors:
	 * <code>bin</code>, <code>jar-with-dependencies</code>, <code>src</code>,
	 * and <code>project</code>. You can add others by adding dependencies to
	 * the plugin.
	 * 
	 * @parameter
	 */
	private String[] descriptorRefs;

	/**
	 * Directory to scan for descriptor files in. <b>NOTE:</b> This may not work
	 * correctly with assembly components.
	 * 
	 * @parameter
	 */
	private File descriptorSourceDirectory;

	/**
	 * This is the base directory from which archive files are created. This
	 * base directory pre-pended to any <code>&lt;directory&gt;</code>
	 * specifications in the assembly descriptor. This is an optional parameter.
	 * 
	 * @parameter
	 */
	private File archiveBaseDirectory;

	/**
	 * Predefined Assembly Descriptor Id's. You can select bin,
	 * jar-with-dependencies, or src.
	 * 
	 * @parameter expression="${descriptorId}"
	 * @deprecated Please use descriptorRefs instead
	 */
	@Deprecated
	protected String descriptorId;

	/**
	 * Assembly XML Descriptor file. This must be the path to your customized
	 * descriptor file.
	 * 
	 * @parameter expression="${descriptor}"
	 * @deprecated Please use descriptors instead
	 */
	@Deprecated
	protected String descriptor;

	/**
	 * Sets the TarArchiver behavior on file paths with more than 100 characters
	 * length. Valid values are: "warn" (default), "fail", "truncate", "gnu", or
	 * "omit".
	 * 
	 * @parameter expression="${assembly.tarLongFileMode}" default-value="warn"
	 */
	private String tarLongFileMode;

	/**
	 * Base directory of the project.
	 * 
	 * @parameter default-value="${project.basedir}"
	 * @required
	 * @readonly
	 */
	private File basedir;

	/**
	 * Maven ProjectHelper.
	 * 
	 * @component
	 */
	private MavenProjectHelper projectHelper;

	/**
	 * Maven shared filtering utility.
	 * 
	 * @component
	 */
	private MavenFileFilter mavenFileFilter;

	/**
	 * The Maven Session Object.
	 * 
	 * @parameter default-value="${session}"
	 * @required
	 * @readonly
	 */
	private MavenSession mavenSession;

	/**
	 * Temporary directory that contain the files to be assembled.
	 * 
	 * @parameter default-value="${project.build.directory}/archive-tmp"
	 * @required
	 * @readonly
	 */
	private File tempRoot;

	/**
	 * Directory for site generated.
	 * 
	 * @parameter default-value="${project.reporting.outputDirectory}"
	 * @readonly
	 */
	private File siteDirectory;

	/**
	 * Set to true to include the site generated by site:site goal.
	 * 
	 * @parameter expression="${includeSite}" default-value="false"
	 * @deprecated Please set this variable in the assembly descriptor instead
	 */
	@Deprecated
	private boolean includeSite;

	/**
	 * Set to false to exclude the assembly id from the assembly final name.
	 * 
	 * @parameter expression="${assembly.appendAssemblyId}"
	 *            default-value="false"
	 */
	protected boolean appendAssemblyId;

	/**
	 * Set to true in order to not fail when a descriptor is missing.
	 * 
	 * @parameter expression="${assembly.ignoreMissingDescriptor}"
	 *            default-value="false"
	 */
	protected boolean ignoreMissingDescriptor;

	/**
	 * This is a set of instructions to the archive builder, especially for
	 * building .jar files. It enables you to specify a Manifest file for the
	 * jar, in addition to other options.
	 * 
	 * @parameter
	 */
	private MavenArchiveConfiguration archive;

	/**
	 * @parameter
	 */
	protected List<String> filters;

	/**
	 * Controls whether the assembly plugin tries to attach the resulting
	 * assembly to the project.
	 * 
	 * @parameter expression="${assembly.attach}" default-value="true"
	 * @since 2.2-beta-1
	 */
	private boolean attach;

	/**
	 * @component
	 */
	private AssemblyArchiver assemblyArchiver;

	/**
	 * @component
	 */
	private AssemblyReader assemblyReader;

	/**
	 * Allows additional configuration options that are specific to a particular
	 * type of archive format. This is intended to capture an XML configuration
	 * that will be used to reflectively setup the options on the archiver
	 * instance. <br/>
	 * For instance, to direct an assembly with the "ear" format to use a
	 * particular deployment descriptor, you should specify the following for
	 * the archiverConfig value in your plugin configuration: <br/>
	 * 
	 * <pre>
	 * &lt;appxml&gt;${project.basedir}/somepath/app.xml&lt;/appxml&gt;
	 * </pre>
	 * 
	 * @parameter
	 * @since 2.2-beta-3
	 */
	private PlexusConfiguration archiverConfig;

	/**
	 * This will cause the assembly to run only at the top of a given module
	 * tree. That is, run in the project contained in the same folder where the
	 * mvn execution was launched.
	 * 
	 * @parameter expression="${assembly.runOnlyAtExecutionRoot}"
	 *            default-value="false"
	 * @since 2.2-beta-4
	 */
	private boolean runOnlyAtExecutionRoot;

	/**
	 * This will cause the assembly to only update an existing archive, if it
	 * exists.
	 * 
	 * @parameter expression="${assembly.updatOnly}" default-value="false"
	 * @since 2.2
	 */
	private boolean updateOnly;

	/**
	 * <p>
	 * will use the jvm chmod, this is available for user and all level group
	 * level will be ignored.
	 * </p>
	 * 
	 * @parameter expression="${assembly.useJvmChmod}" default-value="false"
	 * @since 2.2
	 */
	private boolean useJvmChmod;

	/**
	 * <p>
	 * Set to <code>true</code> in order to avoid all chmod calls.
	 * </p>
	 * 
	 * <p>
	 * <b>NOTE:</b> This will cause the assembly plugin to <b>DISREGARD</b> all
	 * fileMode/directoryMode settings in the assembly descriptor, and all file
	 * permissions in unpacked dependencies!
	 * </p>
	 * 
	 * @parameter expression="${assembly.ignorePermissions}"
	 *            default-value="false"
	 * @since 2.2
	 */
	private boolean ignorePermissions;

	@Override
	public final void execute() throws MojoExecutionException, MojoFailureException {
		MojoLogAppender.beginLogging(this);
		try {
			// TODO this should only really be extending the
			// SimpleAssemblyPlugin, but
			// Maven/Plexus doesn't support extending a separate plug-in
			//
			// **********************************************************/
			// CODE ADDED TO ASSEMBLY PLUGIN
			// **********************************************************/
			LOGGER.info("initial cleanup");
			cleanup();
			LOGGER.info("copying descriptor resource for assembly.");
			copyDescriptorResource();
			String[] descLocs = { assemblyDescriptorLocation };
			setDescriptors(descLocs);
			LOGGER.info("executing assembly.");

			// this is a convenience mojo anyways, so I have automatically
			// stopped the
			// appending of id on the source code bundles
			this.setAppendAssemblyId(false);

			// **********************************************************/
			// END CODE ADDED TO ASSEMBLY PLUGIN
			// **********************************************************/

			if (skipAssembly) {
				LOGGER.info("Assemblies have been skipped per configuration of the skipAssembly parameter.");
				return;
			}

			// run only at the execution root.
			if (runOnlyAtExecutionRoot && !isThisTheExecutionRoot()) {
				LOGGER.info("Skipping the assembly in this project because it's not the Execution Root");
				return;
			}

			List<Assembly> assemblies;
			try {
				assemblies = assemblyReader.readAssemblies(this);
			} catch (final AssemblyReadException e) {
				throw new MojoExecutionException("Error reading assemblies: "
						+ e.getMessage(), e);
			} catch (final InvalidAssemblerConfigurationException e) {
				throw new MojoFailureException(assemblyReader, e.getMessage(),
						"Mojo configuration is invalid: " + e.getMessage());
			}

			// TODO: include dependencies marked for distribution under certain
			// formats
			// TODO: how, might we plug this into an installer, such as NSIS?

			boolean warnedAboutMainProjectArtifact = false;
			for (final Iterator<Assembly> assemblyIterator = assemblies
					.iterator(); assemblyIterator.hasNext();) {
				final Assembly assembly = assemblyIterator.next();
				try {

					final String fullName = AssemblyFormatUtils
							.getDistributionName(assembly, this);

					for (final String format : assembly.getFormats()) {
						final File destFile = assemblyArchiver.createArchive(
								assembly, fullName, format, this);

						final MavenProject project = getProject();
						final String classifier = getClassifier();
						final String type = project.getArtifact().getType();

						if (attach && destFile.isFile()) {
							if (isAssemblyIdAppended()) {
								projectHelper.attachArtifact(project, format,
										assembly.getId(), destFile);
							} else if (classifier != null) {
								projectHelper.attachArtifact(project, format,
										classifier, destFile);
							} else if (!"pom".equals(type)
									&& format.equals(type)) {
								if (!warnedAboutMainProjectArtifact) {
									final StringBuffer message = new StringBuffer();

									message.append("Configuration options: 'appendAssemblyId' is set to false, and 'classifier' is missing.");
									message.append(
											"\nInstead of attaching the assembly file: ")
											.append(destFile)
											.append(", it will become the file for main project artifact.");
									message.append("\nNOTE: If multiple descriptors or descriptor-formats are provided for this project, the value of this file will be non-deterministic!");

									LOGGER.warn(message);
									warnedAboutMainProjectArtifact = true;
								}

								final File existingFile = project.getArtifact()
										.getFile();
								if ((existingFile != null)
										&& existingFile.exists()) {
									LOGGER.warn("Replacing pre-existing project main-artifact file: "
											+ existingFile
											+ "\nwith assembly file: "
											+ destFile);
								}

								project.getArtifact().setFile(destFile);
							} else {
								projectHelper.attachArtifact(project, format,
										null, destFile);
							}
						} else if (attach) {
							LOGGER.warn("Assembly file: "
									+ destFile
									+ " is not a regular file (it may be a directory). It cannot be attached to the project build for installation or deployment.");
						}
					}
					// **********************************************************/
					// CODE ADDED TO ASSEMBLY PLUGIN
					// **********************************************************/

				} catch (final ArchiveCreationException e) {
					throw new MojoExecutionException(
							"Failed to create assembly: " + e.getMessage(), e);
				} catch (final AssemblyFormattingException e) {
					throw new MojoExecutionException(
							"Failed to create assembly: " + e.getMessage(), e);
				} catch (final InvalidAssemblerConfigurationException e) {
					throw new MojoFailureException(assembly,
							"Assembly is incorrectly configured: "
									+ assembly.getId(), "Assembly: "
									+ assembly.getId()
									+ " is not configured correctly: "
									+ e.getMessage());
				}
			}
		} catch (Exception e) {
			LOGGER.error(
					"Full Source Archive has encountered an error.  Please check stack trace for details. : "
							+ e.getMessage(), e);
			throw new MojoExecutionException(e.getMessage(), e);
		} finally {
			MojoLogAppender.endLogging();
		}
	}

	/**
	 * Returns true if the current project is located at the Execution Root
	 * Directory (where mvn was launched).
	 * 
	 * @return execution root
	 */
	protected final boolean isThisTheExecutionRoot() {
		LOGGER.debug("Root Folder:" + mavenSession.getExecutionRootDirectory());
		LOGGER.debug("Current Folder:" + basedir);
		final boolean result = mavenSession.getExecutionRootDirectory()
				.equalsIgnoreCase(basedir.toString());
		if (result) {
			LOGGER.debug("This is the execution root.");
		} else {
			LOGGER.debug("This is NOT the execution root.");
		}

		return result;
	}

	/**
	 * Accessor.
	 * 
	 * @return assembly archiver
	 */
	protected final AssemblyArchiver getAssemblyArchiver() {
		return assemblyArchiver;
	}

	/**
	 * Accessor.
	 * 
	 * @return assembly reader
	 */
	protected final AssemblyReader getAssemblyReader() {
		return assemblyReader;
	}

	@Override
	public final File getBasedir() {
		return basedir;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated This has been replaced by {@link #getDescriptors()}
	 */
	@Deprecated
	public final String getDescriptor() {
		return descriptor;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated This has been replaced by {@link #getDescriptorReferences()}
	 */
	@Deprecated
	public final String getDescriptorId() {
		return descriptorId;
	}

	@Override
	public final String[] getDescriptorReferences() {
		return descriptorRefs;
	}

	@Override
	public final File getDescriptorSourceDirectory() {
		return descriptorSourceDirectory;
	}

	@Override
	public final String[] getDescriptors() {
		return descriptors;
	}

	@Override
	public abstract MavenProject getProject();

	@Override
	public final File getSiteDirectory() {
		return siteDirectory;
	}

	@Override
	public final boolean isSiteIncluded() {
		return includeSite;
	}

	@Override
	public final String getFinalName() {
		return finalName;
	}

	@Override
	public final boolean isAssemblyIdAppended() {
		return appendAssemblyId;
	}

	@Override
	public final String getTarLongFileMode() {
		return tarLongFileMode;
	}

	@Override
	public final File getOutputDirectory() {
		return outputDirectory;
	}

	@Override
	public final MavenArchiveConfiguration getJarArchiveConfiguration() {
		return archive;
	}

	@Override
	public final File getWorkingDirectory() {
		return workDirectory;
	}

	@Override
	public final ArtifactRepository getLocalRepository() {
		return localRepository;
	}

	@Override
	public final File getTemporaryRootDirectory() {
		return tempRoot;
	}

	@Override
	public final File getArchiveBaseDirectory() {
		return archiveBaseDirectory;
	}

	@Override
	public final List<String> getFilters() {
		if (filters == null) {
			filters = getProject().getBuild().getFilters();
			if (filters == null) {
				filters = Collections.emptyList();
			}
		}
		return filters;
	}

	@Override
	public final List<MavenProject> getReactorProjects() {
		return reactorProjects;
	}

	@Override
	public final String getClassifier() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return projectHelper
	 */
	protected final MavenProjectHelper getProjectHelper() {
		return projectHelper;
	}

	/**
	 * Mutator.
	 * 
	 * @param appendAssemblyIdIn the value to change to
	 */
	public final void setAppendAssemblyId(final boolean appendAssemblyIdIn) {
		this.appendAssemblyId = appendAssemblyIdIn;
	}

	/**
	 * Mutator.
	 * 
	 * @param archiveIn the value to change to
	 */
	public final void setArchive(final MavenArchiveConfiguration archiveIn) {
		this.archive = archiveIn;
	}

	/**
	 * Mutator.
	 * 
	 * @param archiveBaseDirectoryIn the value to change to
	 */
	public final void setArchiveBaseDirectory(final File archiveBaseDirectoryIn) {
		this.archiveBaseDirectory = archiveBaseDirectoryIn;
	}

	/**
	 * Mutator.
	 * 
	 * @param assemblyArchiverIn the value to change to
	 */
	public final void setAssemblyArchiver(final AssemblyArchiver assemblyArchiverIn) {
		this.assemblyArchiver = assemblyArchiverIn;
	}

	/**
	 * Mutator.
	 * 
	 * @param assemblyReaderIn the value to change to
	 */
	public final void setAssemblyReader(final AssemblyReader assemblyReaderIn) {
		this.assemblyReader = assemblyReaderIn;
	}

	/**
	 * Mutator.
	 * 
	 * @param basedirIn the value to change to
	 */
	public final void setBasedir(final File basedirIn) {
		this.basedir = basedirIn;
	}

	/**
	 * Mutator.
	 * 
	 * @param classifierIn the value to change to
	 */
	public final void setClassifier(final String classifierIn) {
		this.classifier = classifierIn;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated This has been replaced by {@link #setDescriptors(String[])}
	 */
	@Deprecated
	public final void setDescriptor(final String descriptorIn) {
		this.descriptor = descriptorIn;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated This has been replaced by
	 *             {@link #setDescriptorRefs(String[])}
	 */
	@Deprecated
	public final void setDescriptorId(final String descriptorIdIn) {
		this.descriptorId = descriptorIdIn;
	}

	/**
	 * Mutator.
	 * 
	 * @param descriptorRefsIn  the value to change to
	 */
	public final void setDescriptorRefs(final String[] descriptorRefsIn) {
		this.descriptorRefs = descriptorRefsIn;
	}

	/**
	 * Mutator.
	 * 
	 * @param descriptorsIn the value to change to
	 */
	public final void setDescriptors(final String[] descriptorsIn) {
		this.descriptors = descriptorsIn;
	}

	/**
	 * Mutator.
	 * 
	 * @param descriptorSourceDirectoryIn the value to change to
	 */
	public final void setDescriptorSourceDirectory(
			final File descriptorSourceDirectoryIn) {
		this.descriptorSourceDirectory = descriptorSourceDirectoryIn;
	}

	/**
	 * Mutator.
	 * 
	 * @param filtersIn the value to change to
	 */
	public final void setFilters(final List<String> filtersIn) {
		this.filters = filtersIn;
	}

	/**
	 * Mutator.
	 * 
	 * @param finalNameIn the value to change to
	 */
	public final void setFinalName(final String finalNameIn) {
		this.finalName = finalNameIn;
	}

	
	/**
	 * Mutator.
	 * 
	 * @param tempRootIn the value to change to
	 */
	public final void setTempRoot(final File tempRootIn) {
		this.tempRoot = tempRootIn;
	}

	/**
	 * Mutator.
	 * 
	 * @param workDirectoryIn the value to change to
	 */
	public final void setWorkDirectory(final File workDirectoryIn) {
		this.workDirectory = workDirectoryIn;
	}

	@Override
	public final List<ArtifactRepository> getRemoteRepositories() {
		return remoteRepositories;
	}

	@Override
	public final boolean isDryRun() {
		return dryRun;
	}

	@Override
	public final boolean isIgnoreDirFormatExtensions() {
		return ignoreDirFormatExtensions;
	}

	@Override
	public final boolean isIgnoreMissingDescriptor() {
		return ignoreMissingDescriptor;
	}

	/**
	 * Mutator.
	 * 
	 * @param ignoreMissingDescriptorIn the value to change to
	 */
	public final void setIgnoreMissingDescriptor(final boolean ignoreMissingDescriptorIn) {
		this.ignoreMissingDescriptor = ignoreMissingDescriptorIn;
	}

	@Override
	public final MavenSession getMavenSession() {
		return mavenSession;
	}

	@Override
	public final String getArchiverConfig() {
		return archiverConfig == null ? null : archiverConfig.toString();
	}

	@Override
	public final MavenFileFilter getMavenFileFilter() {
		return mavenFileFilter;
	}

	@Override
	public final boolean isUpdateOnly() {
		return updateOnly;
	}

	@Override
	public final boolean isUseJvmChmod() {
		return useJvmChmod;
	}

	@Override
	public final boolean isIgnorePermissions() {
		return ignorePermissions;
	}
}
