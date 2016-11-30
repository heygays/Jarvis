package com.heygays.jarvis.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.UUID;

/**
 * SharedPreference数据读写操作
 *
 * @author Administrator
 */
public class PreferenceHelper {
    /**
     * APP第一次启动文件名和KEY
     */
    public static final String FILE_FIRSTSTART = "FILE_FIRSTSTART";
    public static final String KEY_FIRSTSTART = "KEY_FIRSTSTART";
    /**
     * APP的UUID信息
     */
    public final static String FILE_UUID = "FILE_UUID";
    public final static String KEY_UUID = "KEY_UUID";

    /**
     * 保存第一次启动信息
     *
     * @param context
     * @param isFirstStart
     */
    public static void setIsFirstStart(Context context, boolean isFirstStart) {
        SharedPreferences sp = context.getSharedPreferences(FILE_FIRSTSTART, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(KEY_FIRSTSTART, isFirstStart);
        editor.commit();
    }

    /**
     * 获取是否第一次启动
     *
     * @param context
     * @return
     */
    public static boolean getIsFirstStart(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_FIRSTSTART, Context.MODE_PRIVATE);
        return sp.getBoolean(KEY_FIRSTSTART, false);
    }

    /**
     * 保存UUID
     *
     * @param context
     * @param uuid
     */
    public static void setUuid(Context context, String uuid) {
        SharedPreferences sp = context.getSharedPreferences(FILE_UUID, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(KEY_UUID, uuid);
        editor.commit();
    }

    /**
     * 获取UUID
     *
     * @param context
     * @return
     */
    public static String getUuid(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_UUID, Context.MODE_PRIVATE);
        String uuid = sp.getString(KEY_UUID, UUID.randomUUID().toString());
        setUuid(context, uuid);
        return uuid;
    }

    public static void saveData(Context context, String fileName, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getData(Context context, String fileName, String key) {
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String value = sp.getString(key, null);
        return value;
    }
}
