package studio.rockpile.devtools.netty.procedure;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;
import studio.rockpile.devtools.netty.constant.IntfServerTypeEnum;

import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcedureChannelAdapter extends ChannelInboundHandlerAdapter {
	public final static String URI_PREFIX = "/rockpile/studio/intf/ext/";
	private static final Logger logger = LoggerFactory.getLogger(ProcedureChannelAdapter.class);
	private HttpRequest httpRequest = null;
	private StringBuilder message = new StringBuilder();

	// netty服务器读取客户端消息处理方法
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		String content = null;
		try {
			if (msg instanceof HttpRequest) {
				httpRequest = (HttpRequest) msg;
				logger.debug("http请求uri : {}", httpRequest.getUri());

				String uri = httpRequest.getUri().trim();
				if (StringUtils.isEmpty(uri) || uri.startsWith(URI_PREFIX) == false) {
					throw new Exception("未知的表单外部接口服务调用，uri=" + uri);
				}
			}

			if (msg instanceof HttpContent) {
				HttpContent httpContent = (HttpContent) msg;
				ByteBuf buffer = httpContent.content();
				content = buffer.toString(CharsetUtil.UTF_8);
				// 解决httpRequest半包问题
				if (StringUtils.isEmpty(content) == false) {
					message.append(content);
				}
				buffer.release();

				if (msg instanceof LastHttpContent) {
					content = message.toString();
					message.delete(0, message.length());
					logger.debug("http请求content : {}", content);

					URI uri = new URI(httpRequest.getUri());
					String[] uriPaths = uri.getPath().substring(URI_PREFIX.length()).split("/");

					IntfServerTypeEnum type = IntfServerTypeEnum.getType(uriPaths[0]);
					if (type == null) {
						throw new Exception("未知的接口服务类型，uri=" + httpRequest.getUri());
					}
					BaseServiceProcedure procedure = IntfServerTypeEnum.buildProcedure(type);
					String data = procedure.call(content, uriPaths);

					FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
							Unpooled.wrappedBuffer(data.getBytes("UTF-8")));
					response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/json");
					response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
					if (HttpHeaders.isKeepAlive(httpRequest)) {
						response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
					}
					ctx.write(response);
					ctx.flush();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("http请求 : {} 应答异常 : {}", content, e.getMessage());
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		SocketChannel channel = (SocketChannel) ctx.channel();
		logger.debug("exception caught {}:{}", channel.remoteAddress().getAddress().getHostAddress(),
				channel.remoteAddress().getPort());
		logger.debug("exception message : {}", cause.getMessage());
		ctx.close();
	}
}
