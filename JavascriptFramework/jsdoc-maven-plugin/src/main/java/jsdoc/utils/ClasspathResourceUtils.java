/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jsdoc.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.log4j.Logger;

/**
 * Utility class to load classpath resources
 * @author manos
 */
public class ClasspathResourceUtils {
    
	private static Logger log = Logger.getLogger(ClasspathResourceUtils.class);

    /**
     * Get the content of the classpath resource as an InputStream.
     * @param path the path representing the classpath resource
     * @return the resource as an InputStream 
     * @throws ClasspathResourceDiscoveryException if the resource cannot be found
     */
    public static InputStream getResourceAsStream(String path) {
        InputStream is = getResourceAsStreamOrNull(path);
        if (is == null) {
            throw new ClasspathResourceDiscoveryException("Could not find resource in classpath: "
                    + path);
        }
        return is;
    }

    /**
     * Get the content of the classpath resource as an InputStream.
     * @param path the path representing the classpath resource
     * @return the resource as an InputStream or null
     */
    public static byte[] getResourceBytesOrNull(String path){
    	InputStream is = getResourceAsStreamOrNull(path);
    	byte[] data = null;
    	if(is != null){
    		ByteArrayOutputStream os = new ByteArrayOutputStream();
        	int readP;
            byte[] bufferP=new byte[1024];
            try {
    			while((readP=is.read(bufferP))>-1) {
    				os.write(bufferP,0,readP);
    			}
    		} catch (IOException e) {
    			throw new ClasspathResourceDiscoveryException("Could not read resource into memory");
    		}
    		data = os.toByteArray();
    	}
    	return data;
    }
    
    /**
     * Get the content of the classpath resource as an InputStream.
     * @param path the path representing the classpath resource
     * @return the resource as an InputStream or null
     */
    private static InputStream getResourceAsStreamOrNull(String path) {
    	// The Thread Context Classloader (TCL) should be associated 
    	// with any particular deployment, scoped or not.
        return  Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(path);
    }

    /**
     * Get the content of the text-based classpath resource as a String.
     * @param path
     * @return The content of the resource as a String or null
     */
    public static String getResourceAsStringOrNull(String path) {
        String s = null;
        InputStream is = getResourceAsStreamOrNull(path);
        if(log.isDebugEnabled()){
            log.debug("Result stream of path ["+path+"]: "+is);
        }
        if (is != null) {
            StringBuffer sb;
            try {
                sb = inputStreamToStringBuffer(is);
            } catch (IOException e) {
                throw new ClasspathResourceDiscoveryException("Could not load classpath resource into memory", e);
            }
            s = sb.toString();
        }
        return s;
    }

    /**
     * Construct a StringBuffer from the text content of the 
     * classpath resource represented by the given path
     * @param is
     * @return
     * @throws IOException
     */
    private static StringBuffer inputStreamToStringBuffer(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is));
        StringBuffer sb = new StringBuffer();
        // reading the content of the file within a char buffer allow to
        // keep the correct line endings
        char[] charBuffer = new char[4096];
        int nbCharRead = 0;
        while ((nbCharRead = reader.read(charBuffer)) != -1) {
            // appends buffer
            sb.append(charBuffer, 0, nbCharRead);
        }
        reader.close();
        return sb;
    }
}
