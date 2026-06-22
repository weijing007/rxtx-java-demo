package com.weijin.serialport.utils;

import java.nio.ByteOrder;
import java.text.DecimalFormat;

import cn.hutool.core.util.ByteUtil;

public class ByteUtils extends ByteUtil {

	private static final DecimalFormat df = new DecimalFormat("#.00");

	public static byte[] calculateCRC16Modbus(byte[] data) {
		int crc = 0x0000ffff;
		int polynomial = 0x0000A001;
		for (byte b : data) {
			crc ^= b & 0xFF;
			for (int i = 0; i < 8; i++) {
				if ((crc & 0x0001) != 0) {
					crc = (crc >> 1) ^ polynomial;
				} else {
					crc >>= 1;
				}
			}
		}
		byte[] crcBytes = new byte[2];
		crcBytes[0] = (byte) (crc & 0xFF);
		crcBytes[1] = (byte) ((crc >> 8) & 0xFF);
		return crcBytes;
	}

	/**
	 * 计算CRC16校验码
	 *
	 * @param bytes
	 * @return
	 */
	public static String getCRC(byte[] bytes) {
		int CRC = 0x0000ffff;
		int POLYNOMIAL = 0x0000a001;
		int i, j;
		for (i = 0; i < bytes.length; i++) {
			CRC ^= (bytes[i] & 0x000000ff);
			for (j = 0; j < 8; j++) {
				if ((CRC & 0x00000001) != 0) {
					CRC >>= 1;
					CRC ^= POLYNOMIAL;
				} else {
					CRC >>= 1;
				}
			}
		}
		return Integer.toHexString(CRC);
	}

	public static int[] parseRegisterValue(byte[] data, int startIndex, int byteCount) {
		int[] values = new int[byteCount / 2];
		for (int i = 0; i < values.length; i++) {

			int highByte = data[startIndex + i * 2] & 0xFF;
			int lowByte = data[startIndex + i * 2 + 1] & 0xFF;
			values[i] = (highByte << 8) | lowByte;
		}
		return values;
	}

	/**
	 * Hex字符串转byte
	 *
	 * @param inHex 待转换的Hex字符串
	 * @return 转换后的byte
	 */
	public static byte hexToByte(String inHex) {
		return (byte) Integer.parseInt(inHex, 16);
	}

	/**
	 * hex字符串转byte数组
	 *
	 * @param inHex 待转换的Hex字符串
	 * @return 转换后的byte数组结果
	 */
	public static byte[] hexToByteArray(String inHex) {
		int hexlen = inHex.length();
		byte[] result;
		if (hexlen % 2 != 0) {
			// 奇数
			hexlen++;
			result = new byte[(hexlen / 2)];
			inHex = "0" + inHex;
		} else {
			// 偶数
			result = new byte[(hexlen / 2)];
		}
		int j = 0;
		for (int i = 0; i < hexlen; i += 2) {
			result[j] = hexToByte(inHex.substring(i, i + 2));
			j++;
		}
		return result;
	}

	/**
	 * 数组转换成十六进制字符串
	 *
	 * @param bArray
	 * @return HexString
	 */
	public static String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2) {
				sb.append(0);
			}
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

	public static String byteArrayToHexString(byte[] msg) {
		StringBuilder sb = new StringBuilder();
		for (byte b : msg) {
			sb.append(String.format("%02X ", b));
		}
		return sb.toString().trim();
	}

	/**
	 * 16进制字符串转十进制字节数组 这是常用的方法，如某些硬件的通信指令就是提供的16进制字符串，发送时需要转为字节数组再进行发送
	 *
	 * @param strSource 16进制字符串，如 "455A432F5600"，每两位对应字节数组中的一个10进制元素
	 *                  默认会去除参数字符串中的空格，所以参数 "45 5A 43 2F 56 00" 也是可以的
	 * @return 十进制字节数组, 如 [69, 90, 67, 47, 86, 0]
	 */
	public static byte[] hexString2Bytes(String strSource) {
		if (strSource == null || "".equals(strSource.trim())) {
			return null;
		}
		strSource = strSource.replace(" ", "");
		int l = strSource.length() / 2;
		byte[] ret = new byte[l];
		for (int i = 0; i < l; i++) {
			ret[i] = Integer.valueOf(strSource.substring(i * 2, i * 2 + 2), 16).byteValue();
		}
		return ret;
	}


	/**
	 * 截取byte数组 不改变原数组
	 *
	 * @param b      原数组
	 * @param off    偏差值（索引）
	 * @param length 长度
	 * @return 截取后的数组
	 */
	public static byte[] subByte(byte[] b, int off, int length) {
		byte[] b1 = new byte[length];
		System.arraycopy(b, off, b1, 0, length);
		return b1;
	}

	public static double getDouble(float value) {
		return Double.parseDouble(df.format(value));
	}

	public static void main(String[] args) {
		// 01 04 10 04 00 02 34 CA
		byte[] bytes = new byte[] { 0x01, 0x04, 0x10, 0x04, 0x00, 0x02 };
		System.out.println(getCRC(bytes));
		System.out.println(byteArrayToHexString(calculateCRC16Modbus(bytes)));


		byte[] bytess = new byte[] { 0x3E, 0x39, (byte) 0xDB, 0x58 };
		System.out.println(ByteUtils.bytesToFloat(bytess, ByteOrder.BIG_ENDIAN) * 1000);

	}
}
