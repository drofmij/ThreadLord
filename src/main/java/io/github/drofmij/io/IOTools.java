package io.github.drofmij.io;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * IOTools has some simple IO shortcuts
 *
 * @author drofmij
 */
public class IOTools {

    /**
     * builds buffered reader for given input stream and returns it
     *
     * @param is InputStream to read from
     * @return buffered reader for inputstream
     * @throws IOException if an I/O error occurs during reading.
     */
    public static BufferedReader bread(InputStream is) throws IOException {
        InputStreamReader instr = new InputStreamReader(is);
        BufferedReader bread = new BufferedReader(instr);
        return bread;
    }

    /**
     * builds a buffered writer for given output file path
     *
     * @param outFile file to BufferdWriter points to
     * @return BufferedWriter pointing to provided file path
     * @throws IOException if an I/O error occurs during writing.
     */
    public static BufferedWriter bwrite(String outFile) throws IOException {
        BufferedWriter bwrite = null;
        File file = new File(outFile);
        file.getParentFile().mkdirs();
        if(file.getParentFile().exists()) {
            FileWriter fwrite = new FileWriter(file);
            bwrite = new BufferedWriter(fwrite);
        } else {
            throw new IOException("Error: could not write to file " + outFile);
        }
        return bwrite;
    }

    /**
     * private recursive helper for findFilesInDir()
     * WARNING: if you have circular directory links this might result in an infinite loop scenario.
     *
     * @param files Set passed in the resulting file objects will be stored in
     * @param file to be checked, if regular file add to files set, if directory check for files in the directory and recursively descend
     */
    private static void findFilesInDir(Set<File> files, File file) {
        if(!file.isDirectory()) {
            files.add(file);
        } else {
            for(File fileInDir : file.listFiles()) {
                findFilesInDir(files, file);
            }
        }
    }

    /**
     * find all files in directory tree starting at provided path using recursion
     * WARNING: if you have circular directory links this might result in an infinite loop scenario.
     *
     * @param dir directory to start looking in
     * @return Set of file objects found
     */
    public static Set<File> findFilesInDir(String dir) {
        Set<File> files = new HashSet<>();
        File dirFile = new File(dir);
        findFilesInDir(files, dirFile);
        return files;
    }
}
