package com.ixuea.courses.mymusic.util;

import java.util.UUID;

/**
 * Created by kaka
 * On 2019/7/29
 */
public class UUIDUtil {
    public static String getUUID(){
        String s = UUID.randomUUID().toString();
        /**
         * 去掉“-”符号
         * return s.substring(0,8)+s.substring(9,13)+s.substring(14,18)+s.substring(19,23)+s.substring(24);
         */
        return s.replace("-","");
    }
}
