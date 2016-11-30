package com.heygays.jarvis.utils;

import android.os.Build;

/**
 * 获取设备信息，
 * 
 * @author Administrator
 *
 */
public class OSUtil {
	/**
	 * 下面列举常见设备信息
	 */
	/**
	 * Build
	 */
	// Build.BOARD // 主板
	// Build.BRAND // 系统定制商
	// Build.SUPPORTED_ABIS //CPU指令集
	// Build.DEVICE // 设备参数
	// Build.DISPLAY // 显示屏参数
	// Build.FINGDERPRINT // 唯一编号
	// Build.SERIAL // 硬件序列号
	// Build.ID // 修订版本列表
	// Build.MANUFACTURER // 硬件制造商
	// Build.MODEL //版本
	// Build.HARDWARE //硬件名
	// Build.PRODUCT //手机产品名
	// Build.TAGS // 描述build的标签
	// Build.TYPE // Builder类型
	// Build.VERSION.CODENAME //当前开发代号
	// Build.VERSION.INCREMENTAL //源码控制版本号
	// Build.VERSION.RELEASE //版本字符串
	// Build.VERSION.SDK_INT //版本号
	// Build.HOST // HOST值
	// Build.USER // User名
	// Build.TIME // 编译时间
	/**
	 * System.Property
	 */
	// os.version // OS版本号
	// os.name // OS名称
	// os.arch // OS架构
	// user.timezone //时区

	/**
	 * 获取主板信息
	 * 
	 * @return
	 */
	public static String getBoardInfo() {
		return Build.BOARD;
	}

	/**
	 * 获取OS版本号
	 * 
	 * @return
	 */
	public static String getOSVersionNum() {
		String osVersion = System.getProperty("os.version");
		return osVersion;
	}
}
