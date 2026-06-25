package com.weijin.serialport.rxtx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weijin.serialport.utils.ByteUtils;

/**
 * @author steel datetime 2021/1/15 11:18
 */
public class RXTXSerialPortDataHandle implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(RXTXSerialPortDataHandle.class);

	private final RXTXSerialPortEventListener listener;
	// 线程控制标识
	private volatile boolean flag = true;

	public RXTXSerialPortDataHandle(RXTXSerialPortEventListener listener) {
		this.listener = listener;
	}

	@Override
	public void run() {
		try {
			LOGGER.info("串口线程已运行");
			while (flag) {
				// 如果堵塞队列中存在数据就将其输出
				// take() 取走BlockingQueue里排在首位的对象
				// 若BlockingQueue为空，阻断进入等待状态直到Blocking有新的对象被加入为止
				byte[] message = take();
				LOGGER.info("处理信息：" + ByteUtils.bytesToHexString(message));
			}
		} catch (InterruptedException e) {
			LOGGER.error("线程执行异常", e);
		}
	}

	public void stop() {
		this.flag = false;
	}

	public byte[] take() throws InterruptedException {
		return listener.MSG_QUEUE.take();
	}

}
