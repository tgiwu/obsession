package com.mine.obsession.models;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class SimpleZipFile extends ZipFile {
    private File file;
    public SimpleZipFile(String name) throws IOException {
        super(name);
    }

    public SimpleZipFile(File file, int mode) throws IOException {
        super(file, mode);
        this.file = file;
    }

    public SimpleZipFile(File file) throws ZipException, IOException {
        super(file);
        this.file = file;
    }

    public SimpleZipFile(File file, int mode, Charset charset) throws IOException {
        super(file, mode, charset);
        this.file = file;
    }

    public SimpleZipFile(String name, Charset charset) throws IOException {
        super(name, charset);
    }

    public SimpleZipFile(File file, Charset charset) throws IOException {
        super(file, charset);
        this.file = file;
    }

    public File getFile() {
        return file;
    }
}
