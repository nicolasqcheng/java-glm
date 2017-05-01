package com.xschen.works.glm.utils;

import java.io.File;


/**
 * Created by xschen on 12/8/15.
 */
public class FileUtils {
    public static File getResourceFile(String fileName) {

        //StringBuilder result = new StringBuilder("");

        //Get file from resources folder
        ClassLoader classLoader = FileUtils.class.getClassLoader();
        return new File(classLoader.getResource(fileName).getFile());

    }
}
