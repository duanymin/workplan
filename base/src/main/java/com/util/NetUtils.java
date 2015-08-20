package com.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;

import java.util.UUID;

/***
 * 网络公共方法
 * 
 * @author Duan
 *
 */
public class NetUtils {
	/***
	 * 是否有网络连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected( Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/***
	 * 判断WIFI网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifiConnected( Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/***
	 * 根据url得到一个文件名
	 * 
	 * @param url
	 * @return
	 */
	public static String getFileNameFromUrl( String url) {
		// 通过 ‘？’ 和 ‘/’ 判断文件名
		int index = url.lastIndexOf('?');
		String filename;
		if (index > 1) {
			filename = url.substring(url.lastIndexOf('/') + 1, index);
		} else {
			filename = url.substring(url.lastIndexOf('/') + 1);
		}

		if (filename == null || "".equals(filename.trim())) {// 如果获取不到文件名称
			filename = UUID.randomUUID() + ".apk";// 默认取一个文件名
		}
		return filename;
	}

	/**
	 * 是否cmwap
	 */
	public static boolean isCMWAPMobileNet( Context paramContext) {
		if (isWifi(paramContext)) {
			return false;
		}
		ConnectivityManager localConnectivityManager = (ConnectivityManager) paramContext.getSystemService("connectivity");
		if (localConnectivityManager != null) {
			NetworkInfo localNetworkInfo = localConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (localNetworkInfo != null) {
				String str = localNetworkInfo.getExtraInfo();
				if ("cmwap".equals(str)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 是否wifi
	 * 
	 * @param paramContext
	 * @return
	 */
	public static boolean isWifi( Context paramContext) {
		NetworkInfo localNetworkInfo = ((ConnectivityManager) paramContext.getSystemService("connectivity")).getActiveNetworkInfo();
		if ((localNetworkInfo != null) && (localNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI)) {
			return true;
		}
		return false;

	}

	/**
	 * 得到网络状态
	 * 
	 * @param paramContext
	 * @return
	 */
	public static int getNetType( Context paramContext) {
		NetworkInfo localNetworkInfo = ((ConnectivityManager) paramContext.getSystemService("connectivity")).getActiveNetworkInfo();
		if (localNetworkInfo != null) {
			return localNetworkInfo.getType();
		}
		return -1;
	}

	/***
	 * TrafficStats这个类，用getUidRxBytes这个方法来查找我这个程序使用的流量。这个方法需要传递一个UID（进程ID）
	 * 下面是通过程序包名来转换UID的方法,但是这个方法统计的流量是从开机一直到关机的流量
	 * 
	 * @param context
	 * @return
	 */
	public static long getUidRxBytes( Context context) { // 获取总的接受字节数，包含Mobile和WiFi等
		PackageManager pm = context.getPackageManager();
		ApplicationInfo ai = null;
		try {
			ai = pm.getApplicationInfo("com.cnlive.bbt", PackageManager.GET_ACTIVITIES);
			return TrafficStats.getUidRxBytes(ai.uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}
