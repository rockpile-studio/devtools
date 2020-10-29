package studio.rockpile.devtools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import org.junit.Test;
import org.springframework.util.DigestUtils;

import studio.rockpile.devtools.util.SimpleBloomExecutor;
import studio.rockpile.devtools.util.SimpleEncryptor;

public class Temporary {

	@Test
	public void encryptByAKSK() {
		try {
			@SuppressWarnings("unused")
			String accessKey = "ufnszc43";
			String securityKey = "pkdeoiv3";
			String fillId = "db3980e0f5a4447a843fa0d7abd16d49";
			String instanceId = "862731";
			StringBuilder text = new StringBuilder();
			text.append(fillId).append(",").append(instanceId).append(",").append(securityKey);

			MessageDigest digest = MessageDigest.getInstance("MD5");
			System.out.println("MD5摘要长度：" + digest.getDigestLength());
			byte[] bytes = digest.digest(text.toString().getBytes());
			String md5 = SimpleEncryptor.bytesToHex(bytes);
			System.out.println("... md5 : " + md5);

			md5 = DigestUtils.md5DigestAsHex(text.toString().getBytes());
			System.out.println("... md5 : " + md5);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void shiroMd5Hash() {
		String password = "pwd123";
		String encrypt = SimpleEncryptor.shiroMd5Hash(password);
		System.out.println("encrypt : " + encrypt);
	}

	@Test
	public void testBloom() {
		try {
			SimpleBloomExecutor exec = new SimpleBloomExecutor(5000L, 0.005d);
			long dura = Calendar.getInstance().getTimeInMillis();
			// String uuid = UUID.randomUUID().toString().replace("-", "");
			String uuid = "38780ac854174852bade8e1444a0894d";
			exec.hashOffset(uuid);
			dura = Calendar.getInstance().getTimeInMillis() - dura;
			System.out.println("dura : " + dura);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
