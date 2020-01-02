package common;

import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.KeyAgreement;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;
import java.util.HashMap;
import java.util.Map;

public class EcdhAlgorithm {
	private static EllipticCurve curve;
	private static ECParameterSpec ecSpec;

	static {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}

	/**
	 * 
	 * @return Map<"PRK", *****> Map<"PUK", *****>
	 */
	public Map<String, byte[]> genKeyPair() {
		Map<String, byte[]> maps = new HashMap<String, byte[]>();
		try {
			if (curve == null || ecSpec == null)
				init();
			KeyPairGenerator g = KeyPairGenerator.getInstance("ECDH", "BC");
			g.initialize(ecSpec, new SecureRandom());
			KeyPair keyPair = g.generateKeyPair();
			maps.put("PRK", keyPair.getPrivate().getEncoded());
			maps.put("PUK", keyPair.getPublic().getEncoded());
		} catch (Exception e) {
			return null;
		}
		return maps;
	}


	public byte[] genAuthorKey(byte[] prk, byte[] puk) {
		byte[] returnByte = null;
		try {
			PrivateKey privateKey = convertPrivateKey(prk);
			PublicKey publicKey = convertPublicKey(puk);
			KeyAgreement keyAgree = KeyAgreement.getInstance("ECDH", "BC");
			keyAgree.init(privateKey);
			keyAgree.doPhase(publicKey, true);
			MessageDigest hash = MessageDigest.getInstance("SHA1", "BC");
			returnByte = hash.digest(keyAgree.generateSecret());
		} catch (Exception e) {
		}

		return returnByte;
	}

	public PrivateKey convertPrivateKey(byte[] prk) throws Exception {
		KeySpec keySpec = new PKCS8EncodedKeySpec(prk);
		KeyFactory keyFactory = KeyFactory.getInstance("ECDH");
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
		return privateKey;
	}

	public PublicKey convertPublicKey(byte[] puk) throws Exception {
		KeySpec keySpec = new X509EncodedKeySpec(puk);
		KeyFactory keyFactory = KeyFactory.getInstance("ECDH");
		PublicKey publicKey = keyFactory.generatePublic(keySpec);
		return publicKey;
	}

	private void init() {
		curve = new EllipticCurve(
				new ECFieldFp(
						new BigInteger("883423532389192164791648750360308885314476597252960362792450860609699839")), // q
				new BigInteger("7fffffffffffffffffffffff7fffffffffff8000000000007ffffffffffc", 16), // a
				new BigInteger("6b016c3bdcf18941d0d654921475ca71a9db2fb27d1d37796185c2942c0a", 16)); // b
		ecSpec = new ECParameterSpec(curve,
				ECPointUtil.decodePoint(curve,
						Hex.decode("020ffa963cdca8816ccc33b8642bedf905c3d358573d3f27fbbd3b3cb9aaaf")), // G
				new BigInteger("883423532389192164791648750360308884807550341691627752275345424702807307"), // n
				1); // h
	}

	// ecdh签名
	public byte[] signature(PrivateKey prikey, byte[] inputData) {
		byte[] signatureBytes = null;
		try {
			Signature sig = Signature.getInstance("ECDSA", "BC");
			/**
			 * Initialize the sign and update data to be Sign
			 */
			sig.initSign(prikey);
			sig.update(inputData);
			/**
			 * SIGN the data using private key
			 */
			signatureBytes = sig.sign();
		} catch (Exception e) {
		}

		return signatureBytes;
	}

	// ecdh签名验证
	public boolean verify(PublicKey pubKey, byte[] inputData, byte[] signatureBytes) {
		boolean result = false;
		try {
			Signature sig = Signature.getInstance("ECDSA", "BC");
			/**
			 * Initialize the verify and update data to be Verify
			 */
			sig.initVerify(pubKey);
			sig.update(inputData);

			/**
			 * Check verify true or False
			 */
			result = sig.verify(signatureBytes);
		} catch (Exception e) {
		}
		return result;
	}

}
