package com.weijin.serialport.nettyrxtx;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.weijin.serialport.jSerialComm.SerialCommPortListener;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.rxtx.RxtxChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.ReferenceCountUtil;

/**
 * 串口接收数据处理器
 *
 * @author 程就人生
 * @Date
 */
@Service("rxtxHandler")
@ChannelHandler.Sharable
public class NettyRxtxHandler extends SimpleChannelInboundHandler<String> {

	private static final Logger logger = LoggerFactory.getLogger(SerialCommPortListener.class);

	protected void initChannel(RxtxChannel rxtxChannel) {
		rxtxChannel.pipeline().addLast(
				// new LineBasedFrameDecoder(60000),
				// 文本形式发送编解码
				new StringEncoder(StandardCharsets.UTF_8), new StringDecoder(StandardCharsets.UTF_8),
				// 十六进制形式发送编解码
				new ByteArrayDecoder(), new ByteArrayEncoder()
				);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		// 文本方式编解码，String
		logger.info("接收到[" + msg.length() + "]:" + msg);
		// 十六进制发送编解码
		// 释放资源
		ReferenceCountUtil.release(msg);
	}

}
