package com.company.properties;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {


    private static final char EXTENSION_SEPARATOR = '.';
    private static final char DIRECTORY_SEPARATOR = '/';


    /**
     * Creates a File if the file does not exist, or returns a
     * reference to the File if it already exists.
     */
    public static File createOrRetrieve(final String target) throws IOException{

        final Path path = Paths.get(target);

        if(Files.notExists(path)){
            //LOG.info("Target file \"" + target + "\" will be created.");
            return Files.createFile(Files.createDirectories(path)).toFile();
        }
        //LOG.info("Target file \"" + target + "\" will be retrieved.");
        return path.toFile();
    }

    public static String getPathOflastFileModified(String dir) {
        try {
            return getlastFileModified(dir).getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getNameOflastFileModified(String dir) {
        try {
            return getlastFileModified(dir).getName();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private static File getlastFileModified(String dir){
        File fl = new File(dir);
        File[] files = fl.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.isFile();
            }
        });
        long lastMod = Long.MIN_VALUE;
        File choice = null;
        for (File file : files) {
            if (file.lastModified() > lastMod) {
                choice = file;
                lastMod = file.lastModified();
            }
        }
        try {
            return choice;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Remove the file extension from a filename, that may include a path.
     *
     * e.g. /path/to/myfile.jpg -> /path/to/myfile
     */
    public static String removeExtension(String filename) {
        if (filename == null) {
            return null;
        }

        int index = indexOfExtension(filename);

        if (index == -1) {
            return filename;
        } else {
            return filename.substring(0, index);
        }
    }

    /**
     * Return the file extension from a filename, including the "."
     *
     * e.g. /path/to/myfile.jpg -> .jpg
     */
    public static String getExtension(String filename) {
        if (filename == null) {
            return null;
        }

        int index = indexOfExtension(filename);

        if (index == -1) {
            return filename;
        } else {
            return filename.substring(index);
        }
    }



    public static int indexOfExtension(String filename) {

        if (filename == null) {
            return -1;
        }

        // Check that no directory separator appears after the
        // EXTENSION_SEPARATOR
        int extensionPos = filename.lastIndexOf(EXTENSION_SEPARATOR);

        int lastDirSeparator = filename.lastIndexOf(DIRECTORY_SEPARATOR);

        if (lastDirSeparator > extensionPos) {
            //LogIt.w(FileSystemUtil.class, "A directory separator appears after the file extension, assuming there is no file extension");
            return -1;
        }

        return extensionPos;
    }

}
