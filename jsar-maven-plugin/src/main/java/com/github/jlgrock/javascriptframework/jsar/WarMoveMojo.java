package com.github.jlgrock.javascriptframework.jsar;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.github.jlgrock.javascriptframework.mavenutils.io.DirectoryIO;
import com.github.jlgrock.javascriptframework.mavenutils.mavenobjects.JsarRelativeLocations;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * A simple extension that copies the necessary files from the
 * "default directory" to the war staging directory prior to a war packaging.
 * This is only used in the war lifecycle.
 */
@Mojo( name = "war-move",
        defaultPhase = LifecyclePhase.PREPARE_PACKAGE,
        threadSafe = true,
        requiresDependencyResolution = ResolutionScope.RUNTIME)
public class WarMoveMojo extends AbstractMojo {
	/**
	 * The directory to place compiled files into.
	 */
    @Parameter(defaultValue = "${project.build.directory}${file.separator}javascriptFramework")
	private File frameworkTargetDirectory;

	/**
	 * The directory (where the webapp is built) that you want to place the
	 * necessary closure compiled files (including debug, assert, and compiled)
	 * javascript. This also includes the closure library and all dependencies.
	 * Default value is:
	 * ${project.build.directory}/${project.build.finalName}/javascript
	 * /generated.
	 */
    @Parameter(defaultValue = "${project.build.directory}/${project.build.finalName}/javascript/generated")
	private File warTargetDirectory;

	/**
	 * The directory (where the webapp is built) that you want to place the
	 * debug and assert javascript. This includes the closure library and all
	 * dependencies. Default value is:
	 * ${project.build.directory}/${project.build.finalName}/javascript/debug.
	 * 
	 * @deprecated - In 1.15.0 - This is no longer something that can be broken
	 *             out of the targetFrameworkDirectory. The compiled files will
	 *             always be in the compiled folder of the warTargetDirectory.
	 *             This is due to SourceMapping, which allows you to map
	 *             compiled code back to the original uncompiled code.
	 */
	private File debugDirectory;

	/**
	 * The directory (where the webapp is built) that you want to place the
	 * deploy javascript. This includes the compiled version of the code.
	 * 
	 * @deprecated - In 1.15.0 - This is no longer something that can be broken
	 *             out of the targetFrameworkDirectory. The compiled files will
	 *             always be in the compiled folder of the warTargetDirectory.
	 *             This is due to SourceMapping, which allows you to map
	 *             compiled code back to the original uncompiled code.
	 */
	private File compileDirectory;

	/**
	 * Whether or not to expand the debug and assertion library. This can be
	 * useful when including files for an api. When doing this, make sure to
	 * include the appropriate internal dependencies and closure compiler.
	 */
    @Parameter(defaultValue = "true")
	private boolean includeAssert;

	/**
	 * Whether or not to expand the debug and assertion library. This can be
	 * useful when including files for an api. When doing this, make sure to
	 * include the appropriate internal dependencies and closure compiler.
	 * 
	 * @deprecated In 1.15.0 - due to the addition of SourceMaps, debug must
	 *             always be provided. At this point, this will not be used.
	 */
    @Parameter(defaultValue = "true")
	private boolean includeDebug;

	/**
	 * Whether or not to expand the compressed library files. This can be useful
	 * when including files for an api. When doing this, make sure to include
	 * the appropriate internal dependencies and closure compiler.
	 */
    @Parameter(defaultValue = "true")
	private boolean includeCompiled;

	@Override
	public final void execute() throws MojoExecutionException,
			MojoFailureException {
		if (compileDirectory != null || debugDirectory != null) {
			throw new MojoExecutionException(
					"compileDirectory  and debugDirectory are no longer an accepted parameters.  Please remove.  This will " +
					"now be located within the warTargetDirectory.");
		}
		
		try {
			if (!warTargetDirectory.exists()) {
				warTargetDirectory.mkdirs();
			}

			DirectoryIO.copyDirectory(JsarRelativeLocations
					.getClosureLibraryLocation(frameworkTargetDirectory),
					JsarRelativeLocations
							.getClosureLibraryLocation(warTargetDirectory));

			if (JsarRelativeLocations.getInternsLocation(
					frameworkTargetDirectory).exists()) {
				DirectoryIO.copyDirectory(JsarRelativeLocations
						.getInternsLocation(frameworkTargetDirectory),
						JsarRelativeLocations
								.getInternsLocation(warTargetDirectory));
			}

			// Include Assert
			if (includeAssert) {
				DirectoryIO.copyDirectory(JsarRelativeLocations
						.getAssertDepsLocation(frameworkTargetDirectory),
						JsarRelativeLocations
								.getAssertDepsLocation(warTargetDirectory));
				DirectoryIO
						.copyDirectory(
								JsarRelativeLocations
										.getAssertionSourceLocation(frameworkTargetDirectory),
								JsarRelativeLocations
										.getAssertionSourceLocation(warTargetDirectory));
				DirectoryIO.copyDirectory(JsarRelativeLocations
						.getAssertRequiresLocation(frameworkTargetDirectory),
						JsarRelativeLocations
								.getAssertRequiresLocation(warTargetDirectory));
			}

			// Include Debug
			DirectoryIO.copyDirectory(JsarRelativeLocations
					.getDebugDepsLocation(frameworkTargetDirectory),
					JsarRelativeLocations
							.getDebugDepsLocation(warTargetDirectory));
			DirectoryIO.copyDirectory(JsarRelativeLocations
					.getDebugSourceLocation(frameworkTargetDirectory),
					JsarRelativeLocations
							.getDebugSourceLocation(warTargetDirectory));
			DirectoryIO.copyDirectory(JsarRelativeLocations
					.getDebugRequiresLocation(frameworkTargetDirectory),
					JsarRelativeLocations
							.getDebugRequiresLocation(warTargetDirectory));

			// Include Compiled
			if (includeCompiled) {
				DirectoryIO.copyDirectory(JsarRelativeLocations
						.getCompileLocation(frameworkTargetDirectory),
						JsarRelativeLocations
								.getCompileLocation(warTargetDirectory));
			}
		} catch (Exception e) {
			throw new MojoExecutionException(
					"Unable to move files to war archive directory: " + e);
		}
	}
}
