package com.github.jlgrock.javascriptframework.mavenutils.logging;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;

/**
 *	This class acts as a wrapper to the Log4j implementation so that
 *  developers can use log4j rather than passing around the maven
 *  log. 
 *
 */
public class MojoLogAppender extends AppenderSkeleton {

	/**
	 * The log that will be used by the entire maven project.
	 */
	private static Log mavenLog;

	/**
	 * Use this to start logging in any mojo.  This should be the first thing
	 * instantiated in the Mojo
	 * 
	 * @param mojo The mojo to start logging within
	 */
	public static void beginLogging(final AbstractMojo mojo) {
		mavenLog = mojo.getLog();
	}

	/**
	 * Use this to stop logging in the mojo.  This should be the last thing run,
	 * preferably in a finally block so that it will always output to the console,
	 * regardless if exceptions have been thrown.
	 * 
	 */
	public static void endLogging() {
		mavenLog = null;
	}

	@Override
	protected final void append(final LoggingEvent event) {
		if (mavenLog == null) {
			return;
		}
		Level level = event.getLevel();
		if (Level.DEBUG.equals(level) && !(mavenLog.isDebugEnabled())) {
			return;
		}

		String text = this.layout.format(event);
		Throwable throwable = null;
		if (event.getThrowableInformation() != null) {
			throwable = event.getThrowableInformation().getThrowable();
		}

		if (Level.DEBUG.equals(level)) {
			if (throwable != null) {
				mavenLog.debug(text, throwable);
			} else {
				mavenLog.debug(text);
			}
		} else if (Level.INFO.equals(level)) {
			if (throwable != null) {
				mavenLog.info(text, throwable);
			} else {
				mavenLog.info(text);
			}
		} else if (Level.WARN.equals(level)) {
			if (throwable != null) {
				mavenLog.warn(text, throwable);
			} else {
				mavenLog.warn(text);
			}
		} else if (Level.ERROR.equals(level) || Level.FATAL.equals(level)) {
			if (throwable != null) {
				mavenLog.error(text, throwable);
			} else {
				mavenLog.error(text);
			}
		} else {
			if (throwable != null) {
				mavenLog.error(text, throwable);
			} else {
				mavenLog.error(text);
			}
		}
	}

	@Override
	public final void close() {
		mavenLog = null;
	}

	@Override
	public final boolean requiresLayout() {
		return true;
	}
}
