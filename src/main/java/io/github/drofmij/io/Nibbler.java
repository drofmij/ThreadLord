package io.github.drofmij.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Nibbler handles reading a file or input stream with a buffered reader one
 * line at a time
 *
 * @author drofmij
 */
public abstract class Nibbler implements Closeable {

    private String filename;

    private InputStream inputstream;

    public Nibbler() {
    }

    public Nibbler(String filename) {
        this.filename = filename;
    }

    public Nibbler(InputStream inputstream) {
        this.inputstream = inputstream;
    }
  
    public Nibbler withInputStream(InputStream inputstream) {
        this.inputstream = inputstream;
        return this;
    }

    public Nibbler withFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public void nibble() throws IOException {
        if (filename != null) {
            File file = new File(filename);
            if (file.exists()) {
                nibble(new FileInputStream(file));
            }
        } else if (inputstream != null) {
            nibble(inputstream);
        } else {
            throw new IOException("Error: No filename or inputstream is set");
        }
    }

    private void nibble(InputStream inputStream) throws IOException {
        try (InputStreamReader isr = new InputStreamReader(inputStream);
                BufferedReader br = new BufferedReader(isr);) {
            String line;
            while ((line = br.readLine()) != null) {
                handleLine(line);
            }
        }
    }

    @Override
    public void close() throws IOException {
        if(inputstream != null) {
            inputstream.close();
        }
    }

    public abstract void handleLine(String line);
}
