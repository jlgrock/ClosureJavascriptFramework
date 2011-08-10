package jsPreparserCompiler.io;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;

public class RelativePath {
	public static String getRelPathFromBase(File from, File to) throws IOException {
		String normalizedTargetPath = FilenameUtils.normalizeNoEndSeparator(to.getCanonicalPath());
        String normalizedBasePath = FilenameUtils.normalizeNoEndSeparator(from.getCanonicalPath());

        // Undo the changes to the separators made by normalization
        if (File.separator.equals("/")) {
            normalizedTargetPath = FilenameUtils.separatorsToUnix(normalizedTargetPath);
            normalizedBasePath = FilenameUtils.separatorsToUnix(normalizedBasePath);

        } else if (File.separator.equals("\\")) {
            normalizedTargetPath = FilenameUtils.separatorsToWindows(normalizedTargetPath);
            normalizedBasePath = FilenameUtils.separatorsToWindows(normalizedBasePath);
        } else {
            throw new IllegalArgumentException("Unrecognised dir separator '" + File.separator + "'");
        }

        String[] base = normalizedBasePath.split(Pattern.quote(File.separator));
        String[] target = normalizedTargetPath.split(Pattern.quote(File.separator));

        // First get all the common elements. Store them as a string,
        // and also count how many of them there are.
        StringBuffer common = new StringBuffer();

        int commonIndex = 0;
        while (commonIndex < target.length && commonIndex < base.length
                && target[commonIndex].equals(base[commonIndex])) {
            common.append(target[commonIndex] + File.separator);
            commonIndex++;
        }

        if (commonIndex == 0) {
            // No single common path element. This most
            // likely indicates differing drive letters, like C: and D:.
            // These paths cannot be relativized.
            throw new IOException("No common path element found for '" + normalizedTargetPath + "' and '" + normalizedBasePath
                    + "'");
        }   

        // The number of directories we have to backtrack depends on whether the base is a file or a dir
        // For example, the relative path from
        //
        // /foo/bar/baz/gg/ff to /foo/bar/baz
        // 
        // ".." if ff is a file
        // "../.." if ff is a directory
        //
        // The following is a heuristic to figure out if the base refers to a file or dir. It's not perfect, because
        // the resource referred to by this path may not actually exist, but it's the best I can do
        boolean baseIsFile = true;
        File baseResource = new File(normalizedBasePath);
        if (baseResource.exists()) {
            baseIsFile = baseResource.isFile();
        }/* else if (from.endsWith(File.separator)) {
            baseIsFile = false;
        }*/

        StringBuffer relative = new StringBuffer();

        if (base.length != commonIndex) {
            int numDirsUp = baseIsFile ? base.length - commonIndex - 1 : base.length - commonIndex;

            for (int i = 0; i < numDirsUp; i++) {
                relative.append(".." + File.separator);
            }
        }
        relative.append(normalizedTargetPath.substring(common.length()-1));
        return relative.toString().replace(File.separator + File.separator, File.separator);
	}
}
