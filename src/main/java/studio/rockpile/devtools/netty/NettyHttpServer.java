package studio.rockpile.devtools.netty;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import studio.rockpile.devtools.netty.config.NettyServerConfig;
import studio.rockpile.devtools.netty.procedure.ProcedureChannelAdapter;
import studio.rockpile.devtools.netty.util.ServiceCodeLoader;

@Component
public class NettyHttpServer {
	private static final Logger logger = LoggerFactory.getLogger(NettyHttpServer.class);
	private EventLoopGroup bossGroup = null;
	private EventLoopGroup workerGroup = null;
	private Channel channel = null;

	@Autowired
	private ServiceCodeLoader serviceCodeLoader;

	@Autowired
	private NettyServerConfig config;

	@PostConstruct
	public void bind() throws Exception {
		serviceCodeLoader.init();

		bossGroup = new NioEventLoopGroup();
		if (config.getWorkThreadNum() != 0) {
			workerGroup = new NioEventLoopGroup(config.getWorkThreadNum());
		} else {
			workerGroup = new NioEventLoopGroup();
		}

		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, config.getBacklog()).childOption(ChannelOption.SO_KEEPALIVE, true)
				.childOption(ChannelOption.TCP_NODELAY, true).childHandler(new ChildChannelHandler());

		String host = config.getHost();
		ChannelFuture futrue = null;
		if (host == null || "".equals(host)) {
			futrue = bootstrap.bind(config.getPort()).sync();
		} else {
			futrue = bootstrap.bind(host, config.getPort()).sync();
		}
		logger.debug("ServerSocket服务端，ip:{}, port:{}", host, config.getPort());
		channel = futrue.channel();
	}

	private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
		@Override
		protected void initChannel(SocketChannel channel) throws Exception {
			logger.debug("有客户端连接到本服务端，ip:{}, port:{}", channel.remoteAddress().getAddress().getHostAddress(),
					channel.remoteAddress().getPort());

			ChannelPipeline pipeline = channel.pipeline();
			// 设置server端发送协议为httpResponse，使用HttpResponseEncoder进行编码
			pipeline.addLast(new HttpResponseEncoder());
			// 设置server端接收协议为httpRequest，使用HttpRequestDecoder进行解码
			pipeline.addLast(new HttpRequestDecoder());
			// HttpObjectAggregator目的是将多个消息转换为单一的request或者response对象
			pipeline.addLast("aggregator", new HttpObjectAggregator(config.getMaxContentLength())); 
			// 支持异步大文件传输
			pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
			pipeline.addLast(new ProcedureChannelAdapter());
		}
	}

	@PreDestroy
	public void dispose() {
		if (channel != null) {
			try {
				// 等待服务监听端口关闭
				channel.closeFuture().sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (bossGroup != null) {
			bossGroup.shutdownGracefully();
			bossGroup = null;
		}
		if (workerGroup != null) {
			workerGroup.shutdownGracefully();
			workerGroup = null;
		}
	}
}
