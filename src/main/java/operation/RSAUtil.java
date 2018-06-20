package operation;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.Cipher;
import org.apache.commons.codec.binary.Base64;

/**
 * 非对称加密
 * @author LiuChengxiang
 * @time 2018年3月28日上午8:48:17
 *
 */
public class RSAUtil {
	private static final String ALGORITHM = "RSA";
	private static final int KEYSIZE = 1024;

	/**
	 * 生成RSA密钥对
	 * @author LiuChengxiang
	 * @time 2018年3月27日下午6:28:03
	 */
	public static KeyPair genKeyPair() {
		KeyPairGenerator keyPairGenerator = null;
		try {
			keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		keyPairGenerator.initialize(KEYSIZE);
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		return keyPair;
	}

	/**
	 * 使用RSA加密字符串
	 * @author LiuChengxiang
	 * @time 2018年3月27日下午6:48:13
	 * @param src
	 * @return
	 */
	public static String encode(String str,PublicKey publicKey) {
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			return Base64.encodeBase64String(cipher.doFinal(str.getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 使用RSA解密字符串
	 * @author LiuChengxiang
	 * @time 2018年3月27日下午7:06:20
	 * @param src
	 * @return
	 */
	public static String decode(String str,PrivateKey privateKey) {
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			return new String(cipher.doFinal(Base64.decodeBase64(str)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}