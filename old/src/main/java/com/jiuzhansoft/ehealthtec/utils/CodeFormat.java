package com.jiuzhansoft.ehealthtec.utils;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class CodeFormat {

	static String dataOne;

	private static String hexString = "0123456789ABCDEF";

	public static String encode(String str) {
		dataOne = str;
		byte[] bytes = str.getBytes();
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
			sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0) + " ");
		}

		return sb.toString();

	}

	public static String decode(String bytes) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream(
				bytes.length() / 2);
		for (int i = 0; i < bytes.length(); i += 2)
			baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
					.indexOf(bytes.charAt(i + 1))));
		return new String(baos.toByteArray());

	}

	public static String StringFilter(String str) throws PatternSyntaxException {
        String regEx = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}


	public static String bytesToHexString(byte[] src) {

		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {

			return null;

		}

		for (int i = 0; i < 20; i++) {

			int v = src[i] & 0xFF;

			String hv = Integer.toHexString(v);

			if (hv.length() < 2) {

				stringBuilder.append(0);
				System.out.println(stringBuilder);
			}

			stringBuilder.append(hv);

		}

		return stringBuilder.toString();

	}

	public static final int[] bytesToHexStringTwo(byte[] bArray, int count) {
		int[] fs = new int[count];
		for (int i = 0; i < count; i++) {
			fs[i] = (0xFF & bArray[i]);
		}
		return fs;
	}

	public static String Stringspace(String str) {

		String temp = "";
		String temp2 = "";
		for (int i = 0; i < str.length(); i++) {

			if (i % 2 == 0) {
				temp = str.charAt(i) + "";
				temp2 += temp;
				System.out.println(temp);
			} else {
				temp2 += str.charAt(i) + " ";
			}

		}
		return temp2;
	}

	/**
	 * Byte -> Hex
	 * 
	 * @param bytes
	 * @return
	 */
	public static String byteToHex(byte[] bytes, int count) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < count; i++) {
			String hex = Integer.toHexString(bytes[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex).append(" ");
		}
		return sb.toString();
	}

	/**
	 * String -> Hex
	 * 
	 * @param s
	 * @return
	 */
	public static String stringToHex(String s) {
		String str = "";
		for (int i = 0; i < s.length(); i++) {
			int ch = (int) s.charAt(i);
			String s4 = Integer.toHexString(ch);
			if (s4.length() == 1) {
				s4 = '0' + s4;
			}
			str = str + s4 + " ";
		}
		return str;
	}

}
