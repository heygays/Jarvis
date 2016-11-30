package com.heygays.jarvis.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

/**
 * 跳转工具类
 *
 * @author Administrator
 */
public class JumpUtil {
    /**
     * 浏览器跳转
     *
     * @param context
     * @param url
     */
    public static void JumpBrowser(Context context, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        context.startActivity(intent);
    }


}
