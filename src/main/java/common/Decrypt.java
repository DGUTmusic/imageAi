package common;

import java.util.Arrays;

public class Decrypt {

	public static byte[] _decrypt(byte[] encryptContent, int offset, int[] key, int times) {
		int[] tempInt = ByteUtils.byteToInt(encryptContent, offset);
		int y = tempInt[0], z = tempInt[1], sum = 0, i;
		int delta = 0x9e3779b9;
		int a = key[0], b = key[1], c = key[2], d = key[3];
		if (times == 32)
			sum = 0xC6EF3720;
		else if (times == 16)
			sum = 0xE3779B90;
		else
			sum = delta * times;

		for (i = 0; i < times; i++) {
			z -= ((y << 4) + c) ^ (y + sum) ^ ((y >>> 5) + d);
			y -= ((z << 4) + a) ^ (z + sum) ^ ((z >>> 5) + b);
			sum -= delta;
		}
		tempInt[0] = y;
		tempInt[1] = z;

		return ByteUtils.intToByte(tempInt, 0);
	}

	// 批量解密
	public static byte[] _decryptBatch(byte[] secretInfo, int[] key) {
		byte[] decryptStr = null;
		byte[] tempDecrypt = new byte[secretInfo.length];
		for (int offset = 0; offset < secretInfo.length; offset += 8) {
			decryptStr = _decrypt(secretInfo, offset, key, 16);// 32
			System.arraycopy(decryptStr, 0, tempDecrypt, offset, 8);
		}
		int n = tempDecrypt[0];
		return Arrays.copyOfRange(tempDecrypt, n, decryptStr.length);//此处把加密时填充的字节去掉
	}

}
