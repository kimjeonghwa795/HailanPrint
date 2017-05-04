package com.hailan.HaiLanPrint.utils;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;

import com.hailan.HaiLanPrint.constants.Constants;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Map;
import java.util.Map.Entry;

public class Tools {
	
	/**
	 * 获取设备唯一标识
	 * 
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String DEVICE_ID = tm.getDeviceId();
		return DEVICE_ID;
	}

	/**
	 * 获取设备Mac地址
	 * 
	 * @return
	 */
	public static String getDeviceMac() {
		String macSerial = null;
		String str = "";
		InputStreamReader ir = null;
		LineNumberReader input = null;
		try {
			Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
			ir = new InputStreamReader(pp.getInputStream());
			input = new LineNumberReader(ir);

			for (; null != str;) {
				str = input.readLine();
				if (str != null) {
					macSerial = str.trim();// 去空格
					break;
				}
			}
		} catch (IOException ex) {
			// 赋予默认值
			ex.printStackTrace();
		} finally {

			try {
				if (input != null) {
					input.close();
				}
				if (ir != null) {
					ir.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return macSerial;
	}

	/**
	 * 转换成Get形式的Url串
	 * 
	 * @return
	 */
	public static String toGetUrl(Map<String, String> params) {
		if (params == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (Entry<String, String> param : params.entrySet()) {
			sb.append(param.getKey());
			sb.append("=");
			sb.append(param.getValue());
			sb.append("&");
		}

		return sb.substring(0, sb.length() - 1);
	}

	/**
	 * 将资源文件的通配符用args替代
	 * 
	 * @param context
	 * @param resourceId
	 * @param args
	 * @return
	 */
	public static String getFormat(Context context, int resourceId, Object... args) {
		String str = context.getResources().getString(resourceId);
		return String.format(str, args);
	}

	public static String getFormat(Context context, String text, Object... args) {
		return String.format(text, args);
	}

	/** 取SD卡路径 **/
	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory(); // 获取根目录
		}
		if (sdDir != null) {
			return sdDir.toString();
		} else {
			return "";
		}
	}

	/**
	 * 获取sdcard根目录
	 * 
	 * @return
	 */
	public static String getAppPath() {
		File medicalPath = new File(getSDPath() + File.separator + Constants.APP_PATH);
		if (!medicalPath.exists()) {
			medicalPath.mkdir();
		}
		return medicalPath.getAbsolutePath();
	}

	public static String getPathFromUri(Context mContext, Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		CursorLoader loader = new CursorLoader(mContext, contentUri, proj, null, null, null);
		Cursor cursor = loader.loadInBackground();
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dp(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
