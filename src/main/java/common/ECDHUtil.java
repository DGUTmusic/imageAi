package common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * com.fengke.Ai
 *
 * @desc ECDH是一种在网络上安全交换密钥的算法
 * @author:Administrator
 * @year: 2018
 * @month: 07
 * @day: 25
 * @time: 2018/07/25 0025
 */
public class ECDHUtil {
    public static final byte[] prk = ByteUtils.fromHexString("308202290201003081de06072a8648ce3d02013081d2020101302906072a8648ce3d0101021e7fffffffffffffffffffffff7fffffffffff8000000000007fffffffffff3040041e7fffffffffffffffffffffff7fffffffffff8000000000007ffffffffffc041e6b016c3bdcf18941d0d654921475ca71a9db2fb27d1d37796185c2942c0a043d040ffa963cdca8816ccc33b8642bedf905c3d358573d3f27fbbd3b3cb9aaaf7debe8e4e90a5dae6e4054ca530ba04654b36818ce226b39fccb7b02f1ae021e7fffffffffffffffffffffff7fffff9e5e9a9f5d9071fbd1522688909d0b020101048201413082013d020101041e2e6b758f91ab0bc06f36172dbfe0ca2853ddf3d91ac5bae14c9a21c0e556a081d53081d2020101302906072a8648ce3d0101021e7fffffffffffffffffffffff7fffffffffff8000000000007fffffffffff3040041e7fffffffffffffffffffffff7fffffffffff8000000000007ffffffffffc041e6b016c3bdcf18941d0d654921475ca71a9db2fb27d1d37796185c2942c0a043d040ffa963cdca8816ccc33b8642bedf905c3d358573d3f27fbbd3b3cb9aaaf7debe8e4e90a5dae6e4054ca530ba04654b36818ce226b39fccb7b02f1ae021e7fffffffffffffffffffffff7fffff9e5e9a9f5d9071fbd1522688909d0b020101a140033e00045957eecbfaf9f8df7e3e460ee4e60bd0c0e1a9f3631ff5d81e7510fb052b3c8cfdb1e42b803aa9b1711d8ad07c11615cf74697b86ff8ef9307546111");
    public static final byte[] puk = ByteUtils.fromHexString("308201213081de06072a8648ce3d02013081d2020101302906072a8648ce3d0101021e7fffffffffffffffffffffff7fffffffffff8000000000007fffffffffff3040041e7fffffffffffffffffffffff7fffffffffff8000000000007ffffffffffc041e6b016c3bdcf18941d0d654921475ca71a9db2fb27d1d37796185c2942c0a043d040ffa963cdca8816ccc33b8642bedf905c3d358573d3f27fbbd3b3cb9aaaf7debe8e4e90a5dae6e4054ca530ba04654b36818ce226b39fccb7b02f1ae021e7fffffffffffffffffffffff7fffff9e5e9a9f5d9071fbd1522688909d0b020101033e00045957eecbfaf9f8df7e3e460ee4e60bd0c0e1a9f3631ff5d81e7510fb052b3c8cfdb1e42b803aa9b1711d8ad07c11615cf74697b86ff8ef9307546111");

    private static ThreadLocal<EcdhAlgorithm> ecdh = new ThreadLocal<EcdhAlgorithm>() {
        @Override
        protected EcdhAlgorithm initialValue() {
            return new EcdhAlgorithm();
        }
    };

    /** 生成秘钥对，PUK为公钥，PRK为私钥 */
    public static Map<String, byte[]> genKeyPair() {
        return ecdh.get().genKeyPair();
    }

    /** 签名 */
    public static byte[] sign(byte[] content, byte[] priKey) {
        try {
            return ecdh.get().signature(ecdh.get().convertPrivateKey(priKey), content);
        } catch (Exception e) {
            return null;
        }
    }

    /** 验签 */
    public static boolean verify(byte[] content, byte[] sign, byte[] pubKey) {
        try {
            return ecdh.get().verify(ecdh.get().convertPublicKey(pubKey), content, sign);
        } catch (Exception e) {
            return false;
        }
    }

    /** 根据己方私钥和对方公钥生成TEA加解秘钥 */
    public static byte[] genAuthKey(byte[] priKey, byte[] pubKey) {
        return Arrays.copyOf(ecdh.get().genAuthorKey(priKey, pubKey), 16);
    }

    /** 16位byte数组转换成4位int数组 */
    public static int[] chAuthKey(byte[] authKey) {
        return ByteUtils.byteToInt(authKey, 0);
    }

    /** 4位int数组转换成16位byte数组 */
    public static byte[] chAuthKey(int[] authKey) {
        return ByteUtils.intToByte(authKey, 0);
    }

    public static void main(String[] args) {
        int i =1;
        List<Map<String, byte[]>> keyList= new ArrayList<Map<String, byte[]>>();
        //生成5组
        while(i<=5){
            Map<String, byte[]> keyMap=genKeyPair();
            for (Map.Entry<String, byte[]> entry:keyMap.entrySet()) {
                System.out.println(entry.getKey()+"("+i+"):"+ByteUtils.toHexString(entry.getValue()));
            }
            keyList.add(keyMap);
            i++;
        }
        //将任意一对的公钥和私钥交换，计算出来的密钥都是相等的
        int m=1; int n=4;
        System.out.println("使用"+m+"，"+n+"两组公私钥进行协商：");
        System.out.println("加密串1："+ByteUtils.toHexString(genAuthKey(keyList.get(m).get("PRK"), keyList.get(n).get("PUK"))));
        System.out.println("加密串2："+ByteUtils.toHexString(genAuthKey(keyList.get(n).get("PRK"), keyList.get(m).get("PUK"))));
    }
}
