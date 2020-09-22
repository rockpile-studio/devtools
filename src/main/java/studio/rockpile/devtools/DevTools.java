package studio.rockpile.devtools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DevTools {
	private static final Logger logger = LoggerFactory.getLogger(DevTools.class);

	public static void main(String[] args) {
		SpringApplication.run(DevTools.class, args);
		logger.info("====== 启动成功 ======");
	}
}
