package common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ByteUtils {

	// byte[]型数据转成int型数据
	public static int byteToInt(byte[] content) {
		return byteToInt(content, 0)[0];
	}

	// byte[]型数据转成int[]型数据
	public static int[] byteToInt(byte[] content, int offset) {
		int[] result = new int[content.length >> 2];// 除以2的n次方 == 右移n位 即
													// content.length / 4 ==
													// content.length >> 2
		for (int i = 0, j = offset; j < content.length; i++, j += 4) {
			result[i] = transform(content[j + 3]) | transform(content[j + 2]) << 8 | transform(content[j + 1]) << 16
					| (int) content[j] << 24;
		}
		return result;
	}

	// int型数据转成byte[]型数据
	public static byte[] intToByte(int content) {
		return intToByte(new int[] { content }, 0);
	}

	// int[]型数据转成byte[]型数据
	public static byte[] intToByte(int[] content, int offset) {
		byte[] result = new byte[content.length << 2];// 乘以2的n次方 == 左移n位 即
														// content.length * 4 ==
														// content.length << 2
		for (int i = 0, j = offset; j < result.length; i++, j += 4) {
			result[j + 3] = (byte) (content[i] & 0xff);
			result[j + 2] = (byte) ((content[i] >> 8) & 0xff);
			result[j + 1] = (byte) ((content[i] >> 16) & 0xff);
			result[j] = (byte) ((content[i] >> 24) & 0xff);
		}
		return result;
	}

	public static String toHexString(byte[] bytes) {
		return org.bouncycastle.pqc.math.linearalgebra.ByteUtils.toHexString(bytes);
	}

	public static byte[] fromHexString(String hexString) {
		return org.bouncycastle.pqc.math.linearalgebra.ByteUtils.fromHexString(hexString);
	}

	/** 拼接多个byte[] */
	public static byte[] stitch(byte[]... bs) {
		int len = 0;
		for (byte[] b : bs) {
			len += b.length;
		}
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream(len)) {
			for (byte[] b : bs) {
				baos.write(b);
			}
			return baos.toByteArray();
		} catch (IOException e) {
			return null;
		}

	}

	public static byte[] double2Bytes(double d) {
		long value = Double.doubleToRawLongBits(d);
		byte[] byteRet = new byte[8];
		for (int i = 0; i < 8; i++) {
			byteRet[7 - i] = (byte) ((value >> 8 * i) & 0xff);
		}
		return byteRet;
	}

	public static double bytes2Double(byte[] arr) {
		long value = 0;
		for (int i = 0; i < 8; i++) {
			value |= ((long) (arr[7 - i] & 0xff)) << (8 * i);
		}
		return Double.longBitsToDouble(value);
	}

	// 若某字节为负数则需将其转成无符号正数
	private static int transform(byte temp) {
		int tempInt = (int) temp;
		if (tempInt < 0) {
			tempInt += 256;
		}
		return tempInt;
	}
	

}
