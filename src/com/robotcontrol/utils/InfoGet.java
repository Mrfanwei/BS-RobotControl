package com.robotcontrol.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.util.Log;

public class InfoGet {

	public static String getMac() {
		String macSerial = null;
		String str = "";
		try {
			Process pp = Runtime.getRuntime().exec(
					" cat /sys/class/net/wlan0/address ");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);

			for (; null != str;) {
				str = input.readLine();
				if (str != null) {
					macSerial = str.trim();// 去空�?
					break;
				}
			}
		} catch (IOException ex) {
			// 赋予默认�?
			ex.printStackTrace();
		}
		Log.i("IIIIIIIIIIIIIIIII", macSerial);
		return macSerial;
	}

	public static String MD5(String str) {
		try {
			MessageDigest msg = MessageDigest.getInstance("MD5");
			msg.update(str.getBytes());
			byte[] bs = msg.digest();
			return getstring(bs);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public static String getCPUSerial() {
		String str = "", strCPU = "", cpuAddress = "0000000000000000";
		try {
			// 读取CPU信息
			Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			// 查找CPU序列�?
			for (int i = 1; i < 100; i++) {
				str = input.readLine();
				if (str != null) {
					// 查找到序列号�?在行
					if (str.indexOf("Serial") > -1) {
						// 提取序列�?
						strCPU = str.substring(str.indexOf(":") + 1,
								str.length());
						// 去空�?
						cpuAddress = strCPU.trim();
						break;
					}
				} else {
					// 文件结尾
					break;
				}
			}
		} catch (IOException ex) {
			// 赋予默认�?
			ex.printStackTrace();
		}
		Log.i("CCCCCCCCCCCCCCC", cpuAddress);
		return cpuAddress;
	}

	private static String getstring(byte[] bs) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bs.length; i++) {
			sb.append(bs[i]);
		}
		return sb.toString();
	}

	public static byte[] int2Byte(int intValue) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (intValue >> 8 * (3 - i) & 0xFF);
			// System.out.print(Integer.toBinaryString(b[i])+"");
			// System.out.print((b[i]& 0xFF) + " ");
		}

		return b;
	}

}
