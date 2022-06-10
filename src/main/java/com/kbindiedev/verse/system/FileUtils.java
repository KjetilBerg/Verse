package com.kbindiedev.verse.system;

import java.io.File;

public class FileUtils {

    public static String getFileExtension(File file) {
        String path = file.getName();
        int i = path.lastIndexOf(".");
        if (i == -1) return "";
        return path.substring(i+1);
    }

}
