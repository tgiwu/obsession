package com.mine.obsession;

import cn.hutool.core.io.FileUtil;

import java.io.File;

public class Utils {
    public static boolean createDirIfNeed(String path) {
        if (null == path || path.isEmpty()) return false;

        File dir = new File(path);

        if (!(dir.exists() && dir.isDirectory())) {
            return dir.mkdirs();
        } else {
            return true;
        }

    }

    public static boolean createDirIfNeed(File folder) {
        if (null == folder) return false;

        if (!(folder.exists() && folder.isDirectory())) {
            return folder.mkdirs();
        } else {
            return true;
        }

    }

    public static boolean delFileOrDir(File file) {
        if (null == file) return true;
        System.out.println("delete " + file.getAbsolutePath());
        if (!file.exists()) return true;
        if (file.isFile()) return FileUtil.del(file);
        if (file.isDirectory()) {

            File[] subFiles = file.listFiles();
            if (null != subFiles && subFiles.length > 0) {
                for (File f : subFiles) {
                    if (!delFileOrDir(f)) return false;
                }
            }
            return FileUtil.del(file);
        }
        return false;
    }

    public static boolean delFileOrDir(String filePath) {
        return delFileOrDir(new File(filePath));
    }
}
