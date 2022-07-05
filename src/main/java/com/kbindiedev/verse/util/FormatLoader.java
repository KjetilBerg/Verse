package com.kbindiedev.verse.util;

import com.kbindiedev.verse.profiling.exceptions.UnsupportedFormatException;
import com.kbindiedev.verse.system.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public abstract class FormatLoader<T> {

    private HashMap<String, IFormatLoaderImplementation<T>> loaders;

    public FormatLoader() {
        loaders = new HashMap<>();
    }

    public void putLoader(String format, IFormatLoaderImplementation<T> loader) { loaders.put(format, loader); }
    public @Nullable IFormatLoaderImplementation<T> getLoader(String format) { return loaders.get(format); }

    public T load(File file) throws IOException {
        return load(file, FileUtils.getFileExtension(file));
    }

    public T load(File file, String format) throws IOException {
        throwIfUnknownFormat(format);
        try (FileInputStream stream = new FileInputStream(file)) {
            return load(stream, format);
        }
    }

    public T load(InputStream stream, String format) throws IOException {
        throwIfUnknownFormat(format);
        IFormatLoaderImplementation<T> loader = loaders.get(format);
        return loader.load(stream);
    }

    private void throwIfUnknownFormat(String format) {
        if (!loaders.containsKey(format)) {
            String message = String.format("FormatLoader: '%s' unknown format: %s", this.getClass().getCanonicalName(), format);
            throw new UnsupportedFormatException(message);
        }
    }

}