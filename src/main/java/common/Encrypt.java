package common;

public class Encrypt {

	public static byte[] _encrypt(byte[] content, int offset, int[] key, int times) {

		int[] tempInt = ByteUtils.byteToInt(content, offset);
		int y = tempInt[0], z = tempInt[1], sum = 0, i;
		int delta = 0x9e3779b9;
		int a = key[0], b = key[1], c = key[2], d = key[3];
		for (i = 0; i < times; i++) {
			sum += delta;
			y += ((z << 4) + a) ^ (z + sum) ^ ((z >>> 5) + b);
			z += ((y << 4) + c) ^ (y + sum) ^ ((y >>> 5) + d);
		}
		tempInt[0] = y;
		tempInt[1] = z;
		return ByteUtils.intToByte(tempInt, 0);
	}

	// 批量加密
	public static byte[] _encryptBatch(String info, int[] key) {
		byte[] temp = null;
		try {
			temp = info.getBytes("ISO-8859-1");//
		} catch (Exception e) {
			e.printStackTrace();
		}
		return _encryptBatch(temp, key);
	}

	public static byte[] _encryptBatch(byte[] temp, int[] key) {
		/**
		 *由于加密的字节数不确定是8的倍数，所以，我们需要对加密内容进行填充，
		 *填充字节数为1至8（第一个字节（1字节int）表示填充的字节数），并放
		 * 置在字节数组的最前面，解密时会将填充的字节数按字节数组的第一个字节
		 * （1字节int）去除掉，即可得到原明文
		 */
		int n = 8 - temp.length % 8;
		byte[] encryptStr = new byte[temp.length + n];
		encryptStr[0] = (byte) n;
		System.arraycopy(temp, 0, encryptStr, n, temp.length);
		byte[] result = new byte[encryptStr.length];
		for (int offset = 0; offset < result.length; offset += 8) {
			byte[] tempEncrpt = _encrypt(encryptStr, offset, key, 16);// 32
			System.arraycopy(tempEncrpt, 0, result, offset, 8);
		}
		return result;
	}

	public static void main(String[] args) {
		/**
		 * TEA加密算法是一种比较常用的简单加密算法，
		 * 算法会使用给的的密钥（key-16字节数据）对
		 * 一个8字节数数据进行多次加密运算，加密的次
		 * 数由我们自己控制，解密方法类似，用密钥对密
		 *文进行相同次数的解密即可得到原文比较推荐的是
		 * 32或64次运算，由于我们的单片机算力有限，我
		 * 们采用的是16次加密
		 */

		//定义密钥key,我们采用4个长度的4字节int来描述key
		int[] key ={312,432,32,2189};
		//这里可以随意定义
		byte[] origin_data=ByteUtils.fromHexString("025e683a8e5425e2c452fd8fed");
		//进行16轮TEA加密
		byte[] encrypt_data=_encryptBatch(origin_data,key);
		//进行16轮TEA解密
		byte[] decrypt_data=Decrypt._decryptBatch(encrypt_data,key);

		System.out.println("原数据："+ByteUtils.toHexString(origin_data));
		System.out.println("加密后数据："+ByteUtils.toHexString(encrypt_data));
		System.out.println("解密后数据："+ByteUtils.toHexString(decrypt_data));
	}
}
