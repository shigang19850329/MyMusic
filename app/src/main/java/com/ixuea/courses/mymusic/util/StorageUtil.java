package com.ixuea.courses.mymusic.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by kaka
 * On 2019/6/24
 */
public class StorageUtil {
    public static final String MP3 = ".mp3";

    public static String getExternalPath(String title,String suffix){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),String.format("/MyMusic/Music/%s%s",title,suffix));
        if (!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        return file.getAbsolutePath();
    }
}
