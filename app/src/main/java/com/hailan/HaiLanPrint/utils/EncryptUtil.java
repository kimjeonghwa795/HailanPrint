package com.hailan.HaiLanPrint.utils;

import android.util.Base64;

import com.hailan.HaiLanPrint.restfuls.bean.RequestData;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class EncryptUtil {

	public static final String KEY = "MD2O!50!";
	
	/**
	 * 加密
	 * @param message
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String message) throws Exception {
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		DESKeySpec desKeySpec = new DESKeySpec(KEY.getBytes("UTF-8"));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
		IvParameterSpec iv = new IvParameterSpec(KEY.getBytes("UTF-8"));
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
		byte[] encryptbyte = cipher.doFinal(message.getBytes());
		return new String(Base64.encode(encryptbyte, Base64.DEFAULT));
	}

	/**
	 * 解密数据
	 * @param message
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String message) throws Exception {
		byte[] bytesrc = Base64.decode(message.getBytes(), Base64.DEFAULT);
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		DESKeySpec desKeySpec = new DESKeySpec(KEY.getBytes("UTF-8"));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
		IvParameterSpec iv = new IvParameterSpec(KEY.getBytes("UTF-8"));
		cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
		byte[] retByte = cipher.doFinal(bytesrc);
		return new String(retByte);
	}

	/**
	 * 根据RequestData数据生成app签名
	 * @param data
	 * @return
     */
	public static String appSign(RequestData data) {
		Map<String, String> params = new TreeMap<String, String>(new Comparator<String>() {
			public int compare(String lhs, String rhs) {
				return lhs.compareToIgnoreCase(rhs);
			}

			;
		});

		params.put("Version", String.valueOf(data.getVersion()));
		params.put("Culture", data.getCulture());
		params.put("Platform", String.valueOf(data.getPlatform()));
		params.put("BusinessCode", String.valueOf(data.getBusinessCode()));
		params.put("FunctionName", data.getFunctionName());
		params.put("DeviceID", String.valueOf(data.getDeviceID()));
		params.put("UserID", String.valueOf(data.getUserID()));
		params.put("ServiceToken", data.getServiceToken());
		params.put("ActionTimeSpan", String.valueOf(data.getActionTimeSpan()));

		Set<String> keySet = params.keySet();
		Iterator<String> iter = keySet.iterator();

		StringBuffer sb = new StringBuffer();
		while (iter.hasNext()) {
			String key = iter.next();
			sb.append(key);
			sb.append(params.get(key));
		}
		sb.append("@2O!5"); // Hailan2O!6

		return MD5Util.MD5(sb.toString());
	}
}
