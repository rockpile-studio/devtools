package studio.rockpile.devtools.netty.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.netty.server")
public class NettyServerConfig {
	private int workThreadNum = 0; // 配置0，则EventLoopGroup默认的线程数是cpu核数的2倍
	private String host = null;
	private int port = 25030;
	private int backlog = 100;
	private int maxContentLength = 65536;

	@Override
	public String toString() {
		return "NettyServerConfig [workThreadNum=" + workThreadNum + ", host=" + host + ", port=" + port + ", backlog="
				+ backlog + ", maxContentLength=" + maxContentLength + "]";
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getWorkThreadNum() {
		return workThreadNum;
	}

	public void setWorkThreadNum(int workThreadNum) {
		this.workThreadNum = workThreadNum;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getBacklog() {
		return backlog;
	}

	public void setBacklog(int backlog) {
		this.backlog = backlog;
	}

	public int getMaxContentLength() {
		return maxContentLength;
	}

	public void setMaxContentLength(int maxContentLength) {
		this.maxContentLength = maxContentLength;
	}

}
