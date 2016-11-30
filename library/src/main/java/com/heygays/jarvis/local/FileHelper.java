package com.heygays.jarvis.local;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 文件存储服务
 *
 * @author Administrator
 */
public class FileHelper {

	/*
     * TODO InternalStorage 内部存储
	 */

    /**
     * 在内部存储目录（data/data/com.xx.xx）保存文件
     *
     * @param context
     * @param fileName
     * @param content
     */
    public static void setAtInternal(Context context, String fileName, String content) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(content.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从内部存储目录文件获取内容
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String getAtInternal(Context context, String fileName) {
        String result = null;
        try {
            FileInputStream inStream = context.openFileInput(fileName);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            byte[] data = outStream.toByteArray();
            result = new String(data);
            inStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 在内部缓存储存目录保存文件
     *
     * @param context
     * @param fileName
     * @param content
     */
    public static void setTempFile(Context context, String fileName, String content) {
        try {
            File file = File.createTempFile(fileName, null, context.getCacheDir());
            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(content.getBytes());
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从内部缓存存储目录取得文件内容
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String getTempFile(Context context, String fileName) {
        String result = null;
        File file = context.getCacheDir();
        try {
            FileInputStream inStream = new FileInputStream(file);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            byte[] data = outStream.toByteArray();
            result = new String(data);
            inStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

	/* TODO ExternalStorage 外部存储 */

    /**
     * 检查sd卡
     *
     * @return sd卡路径
     */
    public static boolean checkSDCardAvailable() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * 在sd卡保存数据
     *
     * @param path     保存路径
     * @param fileName 保存名称
     * @param mByte
     * @return
     */
    public static boolean saveInSDCard(String path, String fileName, byte[] mByte) {
        boolean isSaved;
        if (checkSDCardAvailable()) {
            isSaved = true;
            /*
             * API Level8之前，所有的文件都是建议放在Environment.getExternalStorageState()目录下的
			 * 从API
			 * Level8开始，对于应用程序的私有文件(private)应该放在Context.getExternalFilesDir目录下，
			 * 非私有的(public）
			 * 的文件应该放在目录下Environment.getExternalStoragePublicDirectory(String)
			 * 所指定的目录下。对于缓存文件应该放在Context.getExternalCacheDir()目录下
			 */
            // File fileDir = new
            // File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
            // albumName);
            // File fileDir = new
            // File(context.getExternalFilesDirs(Environment.DIRECTORY_PICTURES),
            // albumName);
            File fileDir = new File(path);
            if (!fileDir.exists()) {
                fileDir.mkdir();
            }
            File file = new File(fileDir, fileName);
            try {
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(mByte);
                outStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            isSaved = false;
        }
        return isSaved;
    }

    /**
     * 从sdcard获取file
     *
     * @param path
     * @param fileName
     * @return
     * @paramcontext
     */
    public static byte[] getAtSDCard(String path, String fileName) {
        byte[] data = null;
        if (checkSDCardAvailable()) {
            File file = new File(path, fileName);
            if (!file.exists()) {
                return null;
            } else {
                try {
                    FileInputStream inStream = new FileInputStream(file);
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = inStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, len);
                    }
                    data = outStream.toByteArray();
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            data = null;
        }
        return data;

    }
}
