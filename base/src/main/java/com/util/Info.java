package com.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.UUID;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/***
 * 应用信息相关
 * 
 * @author Duan
 *
 */
public class Info {

	@Nullable
    private static String sID = null;
	@Nullable
    private static String versionName = null;
	private static int versionCode = 0;
	private static final String INSTALLATION = "INSTALLATION";

	/***
	 * 程序是否在前台运行
	 */
	public static boolean isAppOnForeground(@NotNull Context context) {
		ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = context.getApplicationContext().getPackageName();
		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		if (appProcesses == null) {
			return false;
		}
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(packageName) && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取版本名
	 * 
	 * @return 当前应用的版本名
	 */
	@Nullable
    public static String getVersionName(@NotNull Context context) {
		if (versionName == null) {
			PackageManager manager = context.getPackageManager();
			try {
				PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
				versionName = info.versionName;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				return "none";
			}
		}
		return versionName;
	}

	/**
	 * 获取版本号
	 * 
	 * @return 当前应用的版本号
	 */
	public static int getVersionCode(@NotNull Context context) {
		if (versionCode == 0) {
			PackageManager manager = context.getPackageManager();
			try {
				PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
				versionCode = info.versionCode;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				return 0;
			}
		}
		return versionCode;
	}

	/***
	 * 生成手机唯一标识,先检查IMEI号是否合法，如不合法，采用谷歌推荐的方法生成一个
	 * 
	 * @param context
	 * @return
	 */
	@Nullable
    public synchronized static String getID(@NotNull Context context) {
		if (sID == null) {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String id = tm.getDeviceId();
			if (!TextUtils.isEmpty(id)) {
				sID = id;
				return sID;
			}
			File installation = new File(context.getFilesDir(), INSTALLATION);
			try {
				if (!installation.exists()) {
					writeInstallationFile(installation);
				}
				sID = readInstallationFile(installation);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return sID;
	}

	@NotNull
    private static String readInstallationFile(File installation) throws IOException {
		RandomAccessFile f = new RandomAccessFile(installation, "r");
		byte[] bytes = new byte[(int) f.length()];
		f.readFully(bytes);
		f.close();
		return new String(bytes);
	}

	private static void writeInstallationFile(@NotNull File installation) throws IOException {
		FileOutputStream out = new FileOutputStream(installation);
		String id = UUID.randomUUID().toString();
		out.write(id.getBytes());
		out.close();
	}
}
