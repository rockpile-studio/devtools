package studio.rockpile.devtools;

import java.util.Calendar;

import org.junit.Test;

import studio.rockpile.devtools.util.SimpleBloomExecutor;
import studio.rockpile.devtools.util.SimpleEncrypter;

public class Temporary {
	
	@Test
	public void shiroMd5Hash() {
		String password = "pwd123";
		String encrypt = SimpleEncrypter.shiroMd5Hash(password);
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
