package org.quickperf.testlauncher;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

//TODO:: to be tested with Java 11 (modules activated)
public class ClassPathUtil {

    private static final int MAX_CLASS_PATH_LENGTH = 1024 * 2;
    private static final String ATTRIBUTE_MANIFEST_VERSION = "1.0";
    private static final String MANIFEST_CLASS_PATH_SEPARATOR = " ";

    public static String retrieveCurrentClassPath(String workingFolderPath) throws IOException {

        String fullClassPath = System.getProperty("java.class.path", "");

        if(fullClassPath.length() < MAX_CLASS_PATH_LENGTH) {
            return fullClassPath;
        } else {
            File classPathJar = new File(workingFolderPath + File.separator + "class_path.jar");
            ClassPathUtil.createClassPathJarFile(fullClassPath, classPathJar);
            return classPathJar.getAbsolutePath();
        }
    }

    public static void createClassPathJarFile(String fullClassPath, File classPathJar) throws IOException {
        String[] paths = fullClassPath.split(File.pathSeparator);
        String classPath = "";
        for (String path : paths) {
            if (classPath.length() > 0) {
                classPath += MANIFEST_CLASS_PATH_SEPARATOR;
            }
            classPath += new File(path).toURI().toURL().toString();
        }

        Manifest manifest = new Manifest();
        Attributes mainAttributes = manifest.getMainAttributes();
        mainAttributes.put(Attributes.Name.MANIFEST_VERSION, ATTRIBUTE_MANIFEST_VERSION);
        mainAttributes.put(Attributes.Name.CLASS_PATH, classPath);

        try (
                FileOutputStream fos = new FileOutputStream(classPathJar);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                JarOutputStream jar = new JarOutputStream(bos, manifest);
        ) {
            //inspection IO resource opened are safely closed
            jar.flush();
        } catch (IOException io) {
            throw io;
        }
    }
}
