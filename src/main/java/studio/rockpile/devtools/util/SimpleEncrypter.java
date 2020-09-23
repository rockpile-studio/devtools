package studio.rockpile.devtools.util;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Random;

import org.apache.shiro.crypto.hash.Md5Hash;

import studio.rockpile.devtools.shiro.realm.CustomerAuthzRealm;

public class SimpleEncrypter {
	private static final int RADIX = 16;
	private static final String SEED = "0933910847463829827159347601486730416058";
	public static final int ENCRYPTED_PREFIX_LENGTH = 10;

	public static String shiroMd5Hash(String message) {
		Md5Hash md5 = new Md5Hash(message, CustomerAuthzRealm.MD5_SALT, CustomerAuthzRealm.HASH_ITERATIONS);
		return md5.toHex();
	}

	public static String getRandomString(int length) {
		StringBuilder buff = new StringBuilder();

		for (int i = 0; i < length; i++) {
			int type = new Random().nextInt(3);
			long random = 0;
			switch (type) {
			case 0:
				random = Math.round(Math.random() * 25 + 65);
				buff.append(String.valueOf((char) random));
				break;
			case 1:
				random = Math.round(Math.random() * 25 + 97);
				buff.append(String.valueOf((char) random));
				break;
			case 2:
				random = new Random().nextInt(10);
				buff.append(String.valueOf(random));
				break;
			}
		}
		return buff.toString();
	}

	public static final String encryptPassword(String password) {
		return SimpleEncrypter.encryptPassword(password, "UTF-8");
	}

	public static final String encryptPassword(String password, String charset) {
		if (password == null)
			return "";
		if (password.length() == 0) {
			return "";
		} else {
			String code = getRandomString(ENCRYPTED_PREFIX_LENGTH) + password;
			BigInteger bi_passwd = new BigInteger(code.getBytes(Charset.forName(charset)));
			BigInteger bi_r0 = new BigInteger(SEED);
			BigInteger bi_r1 = bi_r0.xor(bi_passwd);
			return bi_r1.toString(RADIX);
		}
	}

	public static final String decryptPassword(String encrypted) {
		return SimpleEncrypter.decryptPassword(encrypted, "UTF-8");
	}

	public static final String decryptPassword(String encrypted, String charset) {
		if (encrypted == null)
			return "";
		if (encrypted.length() == 0)
			return "";
		BigInteger bi_confuse = new BigInteger(SEED);
		try {
			BigInteger bi_r1 = new BigInteger(encrypted, RADIX);
			BigInteger bi_r0 = bi_r1.xor(bi_confuse);
			String decode = new String(bi_r0.toByteArray(), Charset.forName(charset));
			return decode.substring(ENCRYPTED_PREFIX_LENGTH);
		} catch (Exception e) {
			return "";
		}
	}
}
