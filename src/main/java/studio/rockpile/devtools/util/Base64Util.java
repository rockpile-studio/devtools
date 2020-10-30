package studio.rockpile.devtools.util;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.codec.binary.Base64;

public class Base64Util {
	// 文件转化成base64字符串
	public static String encodeFile(String filePath) throws Exception {
		InputStream input = null;
		byte[] bytes = null;
		try {
			input = new FileInputStream(filePath);
			bytes = new byte[input.available()];
			input.read(bytes);
		} finally {
			input.close();
		}
		return Base64.encodeBase64String(bytes);
	}

	// base64字符串转化成文件，可以是JPEG、PNG、TXT和AVI等
	public static void decoderFile(String base64, String filePath) throws Exception {
		byte[] bytes = Base64.decodeBase64(base64);
		for (int i = 0; i < bytes.length; ++i) {
			if (bytes[i] < 0) {
				bytes[i] += 256; // 调整异常数据
			}
		}
		OutputStream output = null;
		InputStream input = new ByteArrayInputStream(bytes);
		try {
			output = new FileOutputStream(filePath);
			byte[] buff = new byte[1024];
			int len = 0;
			while ((len = input.read(buff)) != -1) {
				output.write(buff, 0, len);
			}
		} finally {
			output.flush();
			output.close();
		}
	}
}
