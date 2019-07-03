package com.ixuea.courses.mymusic.util;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by kaka
 * On 2019/7/2
 */
public class KeyboardUtil {
    public static void hideKeyboard(Activity activity){
        if (activity.getCurrentFocus()!=null){
            ((InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        }
    }
}
