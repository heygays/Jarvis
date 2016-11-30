package com.heygays.jarvis.exception;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: HeyGays
 * @description:追踪应用的Crash信息 &在sd卡缓存目录下Android/data/com.x.x/cache/CrashLog/crashxxxxxx.trace
 * @date: 2016-02-25
 * @time: 17:06
 */
public abstract class CrashHandler implements UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";
    private static final boolean DEBUG = true;

    private static final String DIRECTORY_NAME = "/CrashLog/";
    private static final String FILE_NAME_PREFIX = "Android_crash";
    private static final String FILE_NAME_SUFFIX = ".trace";

    private UncaughtExceptionHandler mDefaultCrashHandler;
    protected Context mContext;


    /**
     * 因为要弹出对话框，所以必须在BaseActivity OnCreate里实例化后并初始化
     *
     * @param context
     */
    public void init(Context context) {
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context;
    }


    /**
     * 核心方法，当程序中有未捕获的异常，系统会调用它，thread为出现异常的线程，ex是未捕获的异常
     *
     * @param thread
     * @param ex
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            //导出异常到sd卡
            saveExceptionToSDCard(ex);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //1.自定义友好提示
        showTipDialog();
//       // 2.不提示，如果系统提供了默认的异常处理机制，就交给系统去处理，否则自杀
//        if (mDefaultCrashHandler != null) {
//            mDefaultCrashHandler.uncaughtException(thread, ex);
//        } else {
//            android.os.Process.killProcess(Process.myPid());
//        }
    }

    /**
     * 保存异常到本地
     *
     * @param ex
     * @throws IOException
     */
    private void saveExceptionToSDCard(Throwable ex) throws IOException {
        if (getDiskCacheDir() == null) {
            return;
        }
        File dir = new File(getDiskCacheDir() + DIRECTORY_NAME);
        if (!dir.exists()) {
            dir.mkdir();
        }
        try {
            long current = System.currentTimeMillis();
            String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(current));
            File file = new File(dir, FILE_NAME_PREFIX + date + FILE_NAME_SUFFIX);
            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(getCrashInfo(ex).getBytes());
            outStream.close();
            //上次异常到服务器
            uploadExceptionToServer(file);
        } catch (Exception e) {
            Log.e(TAG, "保存异常信息失败" + e.getMessage());
        }
    }

    /**
     * 上传异常到服务器
     *
     * @throws IOException
     */
    public abstract void uploadExceptionToServer(File errorLog);

    /**
     * 获取sd卡缓存路径
     *
     * @return
     */
    private String getDiskCacheDir() {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = mContext.getExternalCacheDir().getPath();
        } else {
            if (DEBUG) {
                Log.w(TAG, "未发现SD卡，无法保存异常信息");
            }
        }
        return cachePath;
    }

    /**
     * 获取CrashInfo
     *
     * @param ex
     * @return
     * @throws PackageManager.NameNotFoundException
     */
    private String getCrashInfo(Throwable ex) throws PackageManager.NameNotFoundException {
        StringBuffer sb = new StringBuffer();
        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(current));
        //时间
        sb.append(time + "\n");
        //手机信息
        getPhoneInfo(sb);
        //异常信息
        sb.append(ex.toString() + "\n");
        StackTraceElement[] stackArray = ex.getStackTrace();
        for (int i = 0; i < stackArray.length; i++) {
            StackTraceElement element = stackArray[i];
            sb.append(element.toString() + "\n");
        }
        return sb.toString();
    }

    /**
     * 获取手机信息
     *
     * @param sb
     * @throws PackageManager.NameNotFoundException
     */
    private void getPhoneInfo(StringBuffer sb) throws PackageManager.NameNotFoundException {
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        //APP版本信息
        sb.append("APP Version:" + pi.versionName + "_" + pi.versionCode + "\n");
        //系统版本信息
        sb.append("OS Version:" + Build.VERSION.RELEASE + "_" + Build.VERSION.SDK_INT + "\n");
        //手机厂商
        sb.append("Vendor:" + Build.MANUFACTURER + "\n");
        //手机型号
        sb.append("Model:" + Build.MODEL + "\n");
        //cpu架构
        sb.append("CPU ABI:" + Build.CPU_ABI + "\n\n");
    }

    /**
     * 自定义友好提示
     */
    public abstract void showTipDialog();


}
