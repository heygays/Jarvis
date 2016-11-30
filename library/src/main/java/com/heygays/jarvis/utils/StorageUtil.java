package com.heygays.jarvis.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 存储工具类
 * 
 * @author Administrator
 *
 */
public class StorageUtil {
	/**
	 * 检查sd卡
	 *
	 * @return sd卡路径
	 */
	public static boolean checkSDCardAvailable() {
		return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}

	/**
	 * 获取内置SD卡的路径 eg."/storage/emulated/0"
	 * 
	 * @return null sd卡不存在
	 */
	public static String getInternalSDPath() {
		if (checkSDCardAvailable()) {
			return (Environment.getExternalStorageDirectory()).toString();
		}
		return null;
	}

	/**
	 * 获取外置SD卡的路径 eg. "/storage/sdcard1"
	 */
	public static String getExternalSDPath(Context context) {
		String exSDPath = null;
		String[] path = getVolumePaths(context);
		if (path != null && path.length > 1) {
			for (int i = 0; i < path.length; i++) {
				if (!path[i].equalsIgnoreCase(getInternalSDPath())) {
					exSDPath = path[i];
					break;
				}
			}
		}
		return exSDPath;
	}

	/**
	 * 获取全部存储目录内置SD卡和外置SD卡
	 * 
	 * @param context
	 * @return
	 */
	public static String[] getVolumePaths(Context context) {
		if (android.os.Build.VERSION.SDK_INT >= 14) {// 4.0以上的api
			return getVolumePathsFor14(context);
		} else if (Environment.MEDIA_MOUNTED.equals(Environment// 4.0以下的
				.getExternalStorageState())) {
			return new String[] { Environment.getExternalStorageDirectory().getAbsolutePath() };
		}
		return null;
	}

	private static String[] getVolumePathsFor14(Context context) {
		String[] paths = null;
		try {
			StorageManager mStorageManager = (StorageManager) context.getSystemService(Activity.STORAGE_SERVICE);
			Method mMethodGetPaths = mStorageManager.getClass().getMethod("getVolumePaths");
			paths = (String[]) mMethodGetPaths.invoke(mStorageManager);
		} catch (IllegalArgumentException e) {

		} catch (IllegalAccessException e) {

		} catch (InvocationTargetException e) {

		} catch (Exception e) {

		}
		return paths;
	}

	/**
	 * 根据文件路径获得文件名,可以是网络路径或者本地路径
	 *
	 * @param filePath
	 *            传入的文件路径
	 * @return
	 */
	public static String getFileName(String filePath) {
		if (filePath.contains("/")) {
			return filePath.substring(filePath.lastIndexOf("/") + 1);
		}
		return filePath;
	}
}
