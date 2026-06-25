package com.weijin.serialport.nettyrxtx;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.weijin.serialport.config.SerialConfigService;
import com.weijin.serialport.jSerialComm.SerialCommPortListener;
import com.weijin.serialport.utils.ByteUtils;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.rxtx.RxtxChannel;
import io.netty.channel.rxtx.RxtxChannelConfig;
import io.netty.channel.rxtx.RxtxDeviceAddress;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * 串口接收数据的服务端
 *
 * @author 程就人生
 * @Date
 */
@Component
public class NettyRxtxServer extends SerialConfigService {

	private static final Logger logger = LoggerFactory.getLogger(SerialCommPortListener.class);

	/**
	 * 主线程组数量
	 */
	@Value("${netty.bossThread:1}")
	private int bossThread;

	private RxtxChannel channel;

	@Autowired
	private NettyRxtxHandler rxtxHandler;

	public void start() {
		CompletableFuture.runAsync(() -> {
			try {
				// 阻塞的函数
				createRxtx();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, Executors.newSingleThreadExecutor());// 不传默认使用ForkJoinPool，都是守护线程
	}

	public void createRxtx() throws Exception {
		// 串口使用阻塞io
		EventLoopGroup group = new OioEventLoopGroup(this.bossThread);
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group).channelFactory(() -> {
				RxtxChannel rxtxChannel = new RxtxChannel();
				rxtxChannel.config().setBaudrate(baudrate) // 波特率
						.setDatabits(RxtxChannelConfig.Databits.DATABITS_8) // 数据位
						.setParitybit(RxtxChannelConfig.Paritybit.NONE) // 校验位
						.setStopbits(RxtxChannelConfig.Stopbits.STOPBITS_1); // 停止位
				return rxtxChannel;
			}).handler(new ChannelInitializer<RxtxChannel>() {
				@Override
				protected void initChannel(RxtxChannel rxtxChannel) {
					rxtxChannel.pipeline().addLast(
//                                    new LineBasedFrameDecoder(60000),
							// 文本形式发送编解码
							new StringEncoder(StandardCharsets.UTF_8), new StringDecoder(StandardCharsets.UTF_8),
							// 十六进制形式发送编解码
							new ByteArrayDecoder(), new ByteArrayEncoder(), rxtxHandler);
				}
			});
			ChannelFuture f = bootstrap.connect(new RxtxDeviceAddress(DefaultPORTNAME)).sync();
			f.addListener(connectedListener);
			f.channel().closeFuture().sync();
		} finally {
			group.shutdownGracefully();
		}
	}

	// 连接监听
	GenericFutureListener<ChannelFuture> connectedListener = (ChannelFuture f) -> {
		f.channel().eventLoop();
		if (!f.isSuccess()) {
			logger.info("连接失败");
		} else {
			channel = (RxtxChannel) f.channel();
			logger.info("连接成功");
			sendDataDefault();
		}
	};

	/**
	 * 发送数据
	 */
	public void sendDataDefault() {
		// 十六机制形式发送
		ByteBuf buf = Unpooled.buffer(2);
		buf.writeByte(3);
		buf.writeByte(2);
		channel.writeAndFlush(buf.array());
		// 文本形式发送
		// channel.writeAndFlush("connection success !");
	}

	/**
	 * 发送数据
	 */
	public boolean sendData(byte[] data) {
		// 十六机制形式发送
		ByteBuf buf = Unpooled.buffer(data.length);
		buf.writeBytes(data);
		ChannelFuture fu = channel.writeAndFlush(buf.array());
		String ss = ByteUtils.byteArrayToHexString(data);
		logger.info("向串口[{}]发送长度{} 数据：{}", DefaultPORTNAME, data.length, ss);
		return fu.isSuccess();
	}

	/**
	 * 往串口发送数据
	 *
	 * @param serialPort 串口对象
	 * @param content    待发送数据
	 */
	public int openSerial(String portName) {
		byte[] openbyte2 = new byte[] { (byte) 0xA0, 0x01, 0x01, (byte) 0XA2 };
		sendData(openbyte2);
		return openbyte2.length;
	}

	/**
	 * 往串口发送数据
	 *
	 * @param serialPort 串口对象
	 * @param content    待发送数据
	 */
	public int closeSerial(String portName) {
		byte[] closebyte2 = new byte[] { (byte) 0xA0, 0x01, 0x00, (byte) 0XA1 };
		sendData(closebyte2);
		return closebyte2.length;
	}
}