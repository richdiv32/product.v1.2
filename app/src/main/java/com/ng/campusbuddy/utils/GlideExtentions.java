package com.ng.campusbuddy.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

public class GlideExtentions {

    public static boolean isValidContextForGlide(final Context context){
        if (context == null){
            return false;
        }
        else if (context instanceof Activity){
            final  Activity activity =(Activity) context;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (activity.isDestroyed() || activity.isFinishing()){
                    return false;
                }
            }
        }
        return true;
    }
}
