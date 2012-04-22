package com.github.jlgrock.javascriptframework.mavenutils.io;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

/**
 * A simple Visitor nio extension that moves a directory from point a to point
 * b.
 */
public class MoveDirVisitor extends SimpleFileVisitor<Path> {
	/**
	 * The path of the directory to copy from.
	 */
	private Path fromPath;
	/**
	 * The path of the directory to copy to.
	 */
	private Path toPath;
	/**
	 * In the event of a conflict, overwrite.
	 */
	private StandardCopyOption moveOption = StandardCopyOption.REPLACE_EXISTING;

	/**
	 * Constructor.
	 * 
	 * @param fromPathIn
	 *            the path of the directory to copy from
	 * @param toPathIn
	 *            the path of the directory to copy to
	 */
	public MoveDirVisitor(final Path fromPathIn, final Path toPathIn) {
		fromPath = fromPathIn;
		toPath = toPathIn;
	}

	@Override
	public final FileVisitResult preVisitDirectory(final Path dir,
			final BasicFileAttributes attrs) throws IOException {
		Objects.requireNonNull(dir);
        Objects.requireNonNull(attrs);
        Path targetPath = toPath.resolve(fromPath.relativize(dir));
		if (!Files.exists(targetPath)) {
			Files.createDirectory(targetPath);
		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public final FileVisitResult visitFile(final Path file,
			final BasicFileAttributes attrs) throws IOException {
        Objects.requireNonNull(file);
        Objects.requireNonNull(attrs);
        Files.move(file, toPath.resolve(fromPath.relativize(file)), moveOption);
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(final Path dir, IOException exc)
			throws IOException {
		Objects.requireNonNull(dir);
		if (exc != null)
			throw exc;
		Files.delete(dir);
		return FileVisitResult.CONTINUE;
	}
}
