package studio.rockpile.devtools.util;

import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.Security;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class AES128Encryptor {
	public final static String KEY_ALGORITHM = "AES";
	public final static String CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";
	public final static String DEFAULT_AES_KEY = "PIrV02rOap26f19m";
	public final static String DEFAULT_IV_PARAM = "d78av3L8PHRR200O";

	private static AlgorithmParameters generateIV(String ivParam) throws Exception {
		AlgorithmParameters params = AlgorithmParameters.getInstance(KEY_ALGORITHM);
		params.init(new IvParameterSpec(ivParam.getBytes()));
		return params;
	}

	// 加密
	public static String encrypt(String text, String aesKey, String ivParam) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		Key key = new SecretKeySpec(aesKey.getBytes(), KEY_ALGORITHM);
		AlgorithmParameters iv = generateIV(ivParam);

		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, key, iv);
		byte[] bytes = cipher.doFinal(text.getBytes());
		return SimpleStringEncryptor.bytesToHex(bytes);
	}

	// 解密
	public static String decrypt(String encrypt, String aesKey, String ivParam) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		Key key = new SecretKeySpec(aesKey.getBytes(), KEY_ALGORITHM);
		AlgorithmParameters iv = generateIV(ivParam);

		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, key, iv);
		byte[] bytes = cipher.doFinal(SimpleStringEncryptor.hexToBytes(encrypt));
		return new String(bytes);
	}

	public static void main(String[] args) {
		// 明文
		String text = "B5F7E21EAB2C6中文信息FE119 D& D%￥9#A！@C$5E38187A5";
		System.out.println("明文 : " + text);
		String aesKey = AES128Encryptor.DEFAULT_AES_KEY;
		String ivParam = AES128Encryptor.DEFAULT_IV_PARAM;
		try {
			String encrypt = AES128Encryptor.encrypt(text, aesKey, ivParam);
			System.out.println("加密 : " + encrypt);

			String decrypt = AES128Encryptor.decrypt(encrypt, aesKey, ivParam);
			System.out.println("解密 : " + decrypt);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
