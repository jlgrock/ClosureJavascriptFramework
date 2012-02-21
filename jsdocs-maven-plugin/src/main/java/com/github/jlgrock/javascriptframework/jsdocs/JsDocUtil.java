package com.github.jlgrock.javascriptframework.jsdocs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.SystemUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationOutputHandler;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.apache.maven.shared.invoker.PrintStreamHandler;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineUtils;

/**
 * The utility for creating a maven site using the jsdoc toolkit.
 */
public final class JsDocUtil {
	/** Error message when VM could not be started using invoker. */
	protected static final String ERROR_INIT_VM = "Error occurred during initialization of VM, " +
			"try to reduce the Java heap size for the MAVEN_OPTS "
			+ "environnement variable using -Xms:<size> and -Xmx:<size>.";

	/**
	 * Constructor.
	 */
	private JsDocUtil() {
	}

	/**
	 * @param log
	 *            a logger could be null
	 * @return the <code>JAVA_HOME</code> from System.getProperty( "java.home" )
	 *         By default,
	 *         <code>System.getProperty( "java.home" ) = JRE_HOME</code> and
	 *         <code>JRE_HOME</code> should be in the <code>JDK_HOME</code>
	 * @since 2.6
	 */
	private static File getJavaHome(final Log log) {
		File javaHome;
		if (SystemUtils.IS_OS_MAC_OSX) {
			javaHome = SystemUtils.getJavaHome();
		} else {
			javaHome = new File(SystemUtils.getJavaHome(), "..");
		}

		if (javaHome == null || !javaHome.exists()) {
			try {
				javaHome = new File(CommandLineUtils.getSystemEnvVars()
						.getProperty("JAVA_HOME"));
			} catch (IOException e) {
				if (log != null && log.isDebugEnabled()) {
					log.debug("IOException: " + e.getMessage());
				}
			}
		}

		if (javaHome == null || !javaHome.exists()) {
			if (log != null && log.isErrorEnabled()) {
				log.error("Cannot find Java application directory. Either specify \'java.home\' system property, or "
						+ "JAVA_HOME environment variable.");
			}
		}

		return javaHome;
	}

	/**
	 * @param log
	 *            a logger could be null
	 * @return the Maven home defined in the <code>maven.home</code> system
	 *         property or defined in <code>M2_HOME</code> system env variables
	 *         or null if never set.
	 * @since 2.6
	 */
	private static String getMavenHome(final Log log) {
		String mavenHome = System.getProperty("maven.home");
		if (mavenHome == null) {
			try {
				mavenHome = CommandLineUtils.getSystemEnvVars().getProperty(
						"M2_HOME");
			} catch (IOException e) {
				if (log != null && log.isDebugEnabled()) {
					log.debug("IOException: " + e.getMessage());
				}
			}
		}
		File m2Home = new File(mavenHome);
		if (!m2Home.exists()) {
			if (log != null && log.isErrorEnabled()) {
				log.error("Cannot find Maven application directory. Either specify \'maven.home\' system property, or "
						+ "M2_HOME environment variable.");
			}
		}

		return mavenHome;
	}

	/**
	 * Invoke Maven for the given project file with a list of goals and
	 * properties, the output will be in the invokerlog file. <br/>
	 * <b>Note</b>: the Maven Home should be defined in the
	 * <code>maven.home</code> Java system property or defined in
	 * <code>M2_HOME</code> system env variables.
	 * 
	 * @param log
	 *            a logger could be null.
	 * @param localRepositoryDir
	 *            the localRepository not null.
	 * @param projectFile
	 *            a not null project file.
	 * @param goals
	 *            a not null goals list.
	 * @param properties
	 *            the properties for the goals, could be null.
	 * @param invokerLog
	 *            the log file where the invoker will be written, if null using
	 *            <code>System.out</code>.
	 * @throws MavenInvocationException
	 *             if any
	 * @since 2.6
	 */
	protected static void invokeMaven(final Log log,
			final File localRepositoryDir, final File projectFile,
			final List<String> goals, final Properties properties,
			final File invokerLog) throws MavenInvocationException {
		if (projectFile == null) {
			throw new IllegalArgumentException(
					"projectFile should be not null.");
		}
		if (!projectFile.isFile()) {
			throw new IllegalArgumentException(projectFile.getAbsolutePath()
					+ " is not a file.");
		}
		if (goals == null || goals.size() == 0) {
			throw new IllegalArgumentException("goals should be not empty.");
		}
		if (localRepositoryDir == null || !localRepositoryDir.isDirectory()) {
			throw new IllegalArgumentException("localRepositoryDir '"
					+ localRepositoryDir + "' should be a directory.");
		}

		String mavenHome = getMavenHome(log);
		if (StringUtils.isEmpty(mavenHome)) {
			String msg = "Could NOT invoke Maven because no Maven Home is defined. You need to have set the M2_HOME "
					+ "system env variable or a maven.home Java system properties.";
			if (log != null) {
				log.error(msg);
			} else {
				System.err.println(msg);
			}
			return;
		}

		Invoker invoker = new DefaultInvoker();
		invoker.setMavenHome(new File(mavenHome));
		invoker.setLocalRepositoryDirectory(localRepositoryDir);

		InvocationRequest request = new DefaultInvocationRequest();
		request.setBaseDirectory(projectFile.getParentFile());
		request.setPomFile(projectFile);
		if (log != null) {
			request.setDebug(log.isDebugEnabled());
		} else {
			request.setDebug(true);
		}
		request.setGoals(goals);
		if (properties != null) {
			request.setProperties(properties);
		}
		File javaHome = getJavaHome(log);
		if (javaHome != null) {
			request.setJavaHome(javaHome);
		}

		if (log != null && log.isDebugEnabled()) {
			log.debug("Invoking Maven for the goals: "
					+ goals
					+ " with "
					+ (properties == null ? "no properties" : "properties="
							+ properties));
		}
		InvocationResult result = invoke(log, invoker, request, invokerLog,
				goals, properties, null);

		if (result.getExitCode() != 0) {
			String invokerLogContent = readFile(invokerLog, "UTF-8");

			// see DefaultMaven
			if (invokerLogContent != null
					&& (invokerLogContent.indexOf("Scanning for projects...") == -1 || invokerLogContent
							.indexOf(OutOfMemoryError.class.getName()) != -1)) {
				if (log != null) {
					log.error("Error occurred during initialization of VM, trying to use an empty MAVEN_OPTS...");

					if (log.isDebugEnabled()) {
						log.debug("Reinvoking Maven for the goals: " + goals
								+ " with an empty MAVEN_OPTS...");
					}
				}
				result = invoke(log, invoker, request, invokerLog, goals,
						properties, "");
			}
		}

		if (result.getExitCode() != 0) {
			String invokerLogContent = readFile(invokerLog, "UTF-8");

			// see DefaultMaven
			if (invokerLogContent != null
					&& (invokerLogContent.indexOf("Scanning for projects...") == -1 || invokerLogContent
							.indexOf(OutOfMemoryError.class.getName()) != -1)) {
				throw new MavenInvocationException(ERROR_INIT_VM);
			}

			throw new MavenInvocationException(
					"Error when invoking Maven, consult the invoker log file: "
							+ invokerLog.getAbsolutePath());
		}
	}

	/**
	 * @param log
	 *            could be null
	 * @param invoker
	 *            not null
	 * @param request
	 *            not null
	 * @param invokerLog
	 *            not null
	 * @param goals
	 *            not null
	 * @param properties
	 *            could be null
	 * @param mavenOpts
	 *            could be null
	 * @return the invocation result
	 * @throws MavenInvocationException
	 *             if any
	 * @since 2.6
	 */
	private static InvocationResult invoke(final Log log,
			final Invoker invoker, final InvocationRequest request,
			final File invokerLog, final List<String> goals,
			final Properties properties, final String mavenOpts)
			throws MavenInvocationException {
		PrintStream ps;
		OutputStream os = null;
		if (invokerLog != null) {
			if (log != null && log.isDebugEnabled()) {
				log.debug("Using " + invokerLog.getAbsolutePath()
						+ " to log the invoker");
			}

			try {
				if (!invokerLog.exists()) {
					invokerLog.getParentFile().mkdirs();
				}
				os = new FileOutputStream(invokerLog);
				ps = new PrintStream(os, true, "UTF-8");
			} catch (FileNotFoundException e) {
				if (log != null && log.isErrorEnabled()) {
					log.error("FileNotFoundException: " + e.getMessage()
							+ ". Using System.out to log the invoker.");
				}
				ps = System.out;
			} catch (UnsupportedEncodingException e) {
				if (log != null && log.isErrorEnabled()) {
					log.error("UnsupportedEncodingException: " + e.getMessage()
							+ ". Using System.out to log the invoker.");
				}
				ps = System.out;
			}
		} else {
			if (log != null && log.isDebugEnabled()) {
				log.debug("Using System.out to log the invoker.");
			}

			ps = System.out;
		}

		if (mavenOpts != null) {
			request.setMavenOpts(mavenOpts);
		}

		InvocationOutputHandler outputHandler = new PrintStreamHandler(ps,
				false);
		request.setOutputHandler(outputHandler);

		outputHandler.consumeLine("Invoking Maven for the goals: "
				+ goals
				+ " with "
				+ (properties == null ? "no properties" : "properties="
						+ properties));
		outputHandler.consumeLine("");
		outputHandler.consumeLine("M2_HOME=" + getMavenHome(log));
		outputHandler.consumeLine("MAVEN_OPTS=" + getMavenOpts(log));
		outputHandler.consumeLine("JAVA_HOME=" + getJavaHome(log));
		outputHandler.consumeLine("JAVA_OPTS=" + getJavaOpts(log));
		outputHandler.consumeLine("");

		try {
			return invoker.execute(request);
		} finally {
			IOUtil.close(os);
			ps = null;
		}
	}

	/**
	 * @param log
	 *            a logger could be null
	 * @return the <code>MAVEN_OPTS</code> env variable value
	 * @since 2.6
	 */
	private static String getMavenOpts(final Log log) {
		String mavenOpts = null;
		try {
			mavenOpts = CommandLineUtils.getSystemEnvVars().getProperty(
					"MAVEN_OPTS");
		} catch (IOException e) {
			if (log != null && log.isDebugEnabled()) {
				log.debug("IOException: " + e.getMessage());
			}
		}

		return mavenOpts;
	}

	/**
	 * @param log
	 *            a logger could be null
	 * @return the <code>JAVA_OPTS</code> env variable value
	 * @since 2.6
	 */
	private static String getJavaOpts(final Log log) {
		String javaOpts = null;
		try {
			javaOpts = CommandLineUtils.getSystemEnvVars().getProperty(
					"JAVA_OPTS");
		} catch (IOException e) {
			if (log != null && log.isDebugEnabled()) {
				log.debug("IOException: " + e.getMessage());
			}
		}

		return javaOpts;
	}

	/**
	 * Read the given file and return the content or null if an IOException
	 * occurs.
	 * 
	 * @param javaFile
	 *            not null
	 * @param encoding
	 *            could be null
	 * @return the content with unified line separator of the given javaFile
	 *         using the given encoding.
	 * @see FileUtils#fileRead(File, String)
	 * @since 2.6.1
	 */
	protected static String readFile(final File javaFile, final String encoding) {
		try {
			return FileUtils.fileRead(javaFile, encoding);
		} catch (IOException e) {
			return null;
		}
	}

}
