package dependencyOverlay.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.maven.artifact.Artifact;

public class ExtractSrcAftifactsUtils {

		public static void extract(final Set<Artifact> artifacts, final String scope, String outputDirectory) throws ZipException, IOException {
			Set<Artifact> filteredArtifacts = filterArtifactList(artifacts, scope, "zip");
			extractSet(filteredArtifacts, outputDirectory);
		}
		
		private static void extractSet(Set<Artifact> filteredArtifacts, String outputDirectory) throws ZipException, IOException {
			final int BUFFER = 2048;
			for (Artifact artifact:filteredArtifacts) {
				System.out.println("Extracting Artifact Dependencies. Type: "+ artifact.getType() + ", ID: " + artifact.getArtifactId());
				System.out.println("Extracting Artifact Dependencies. ID: " + artifact.getArtifactId());
				
				if ("zip".equals(artifact.getType())) {
					File file = artifact.getFile();
					//String path = "META-INF/maven/" + artifact.getGroupId() + "/" + artifact.getArtifactId() + "/js-ordered-list.xml";
					//Read through once to find ordered list file
					BufferedOutputStream dest = null;
			        BufferedInputStream is = null;
			        ZipFile zf=new ZipFile(file);
					ZipEntry ze = null;
					Enumeration e = zf.entries();
					System.out.println("entry: " + e.toString());
					
					while (e.hasMoreElements()) {
						ze = (ZipEntry) e.nextElement();
						
			            is = new BufferedInputStream(zf.getInputStream(ze));
			            int count;
			            byte data[] = new byte[BUFFER];
			            
			            File newFile = new File(outputDirectory + File.separator + ze.getName());
			            
			            if (!ze.isDirectory()) {
			            	newFile.getParentFile().mkdirs();
			            	FileOutputStream fos = new FileOutputStream(newFile);
							dest = new BufferedOutputStream(fos, BUFFER);
			            	while ((count = is.read(data, 0, BUFFER)) != -1) {
			            	   dest.write(data, 0, count);
			            	}
			            	dest.flush();
			            	dest.close();
			            	is.close();
			            	System.out.println("placed-entry: " + newFile.getAbsolutePath());
						}
			        }
					zf.close();
				}
			}
			//extractedPathList.add(); //add the path of the output file
		}
		
		private static Set<Artifact> filterArtifactList(final Set<Artifact> artifacts, final String filterScope, final String filterType) {
			Set<Artifact> returnArtifacts = new LinkedHashSet<Artifact>();
			for (Artifact a:artifacts) {
				System.out.println("Artifact Type: " + a.getType());
				if (a.getScope().equals(filterScope) && filterType.equals(a.getType())) {
					returnArtifacts.add(a);
				}
			}
			return returnArtifacts;
		}
		
		
		public static void copyDirectory(File srcPath, File dstPath) throws IOException{
			
			if (srcPath.isDirectory() && !srcPath.isHidden())
			{
				System.out.println("copying directory: " + srcPath);
				
				if (!dstPath.exists())
				{
					dstPath.mkdir();
				}

				String files[] = srcPath.list();
				for(int i = 0; i < files.length; i++)
				{
					copyDirectory(new File(srcPath, files[i]), new File(dstPath, files[i]));
				}
			}
			else
			{
				if(!srcPath.exists())
				{
					System.out.println("Dependency file or directory does not exist: " + srcPath);
					System.exit(0);
				}
				else
				{
					if ( !srcPath.isHidden() ) {
						System.out.println("Copying file: " + srcPath);
						
						InputStream in = new FileInputStream(srcPath);
				        OutputStream out = new FileOutputStream(dstPath);
		    
						// Transfer bytes from in to out
				        byte[] buf = new byte[1024];
						int len;
				        while ((len = in.read(buf)) > 0) {
							out.write(buf, 0, len);
						}
						in.close();
				        out.close();
					}
				}
			}
		}
}



