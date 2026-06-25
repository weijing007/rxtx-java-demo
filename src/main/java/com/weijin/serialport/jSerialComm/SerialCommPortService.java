package com.weijin.serialport.jSerialComm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fazecast.jSerialComm.SerialPort;
import com.weijin.serialport.config.SerialConfigService;
import com.weijin.serialport.utils.ByteUtils;

import cn.hutool.core.util.StrUtil;

@Component
public class SerialCommPortService extends SerialConfigService {

	private static final Logger logger = LoggerFactory.getLogger(SerialCommPortService.class);

	private static final byte[] openbyte = new byte[] { (byte) 0xA0, 0x01, 0x01 };

	private static final byte[] closebyte = new byte[] { (byte) 0xA0, 0x01, 0x00 };

	public Map<String, SerialPort> serialPorts = new HashMap<String, SerialPort>();

	/**
	 * 定时检测客户端/服务的链接状态，未链接成功重新链接
	 */
	@Scheduled(cron = "0/15 * * * * ?")
	public void checkConnect() {
		findPorts().forEach(f -> {
			int size = serialHeatBelt(f);
			if (size > 0) {
				return;
			}
			reOpenPort(f);
		});
	}

	public void start() {
		findPorts().forEach(portName -> {
			boolean issuccess = openPort(portName);
			logger.info("开启串口：" + portName + " : " + issuccess);
		});

	}

	// 查找所有可用端口
	public List<String> findPorts() {
		// 获得当前所有可用串口
		SerialPort[] serialPorts = SerialPort.getCommPorts();
		List<String> portNameList = new ArrayList<String>();
		// 将可用串口名添加到List并返回该List
		for (SerialPort serialPort : serialPorts) {
			portNameList.add(serialPort.getSystemPortName());
		}
		// 去重
		portNameList = portNameList.stream().distinct().collect(Collectors.toList());
		return portNameList;
	}

	/**
	 * 打开串口
	 *
	 * @param portName 端口名称
	 * @param baudRate 波特率
	 * @return 串口对象
	 */
	private boolean openPort(String portName) {
		SerialPort serialPort = SerialPort.getCommPort(portName);
		// 设置一下串口的波特率等参数
		// 数据位：8
		// 停止位：1
		// 校验位：None
		serialPort.setBaudRate(baudrate);
		serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
		serialPort.setComPortParameters(baudrate, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
		serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 1000, 1000);
		boolean issuccesss = serialPort.openPort();
		addListener(serialPort);
		serialPorts.put(portName, serialPort);
		return issuccesss;
	}

	/**
	 * 打开串口
	 *
	 * @param portName 端口名称
	 * @param baudRate 波特率
	 * @return 串口对象
	 */
	private boolean reOpenPort(String portName) {
		SerialPort serialPort = serialPorts.get(portName);
		boolean close = closePort(serialPort);
		boolean open = openPort(portName);
		logger.info("重新开启串口[" + portName + "] close:" + close + " , open:" + open);
		return open;
	}

	/**
	 * 关闭串口
	 * 
	 * @param serialPort 待关闭的串口对象
	 */
	private boolean closePort(SerialPort serialPort) {
		if (serialPort != null) {
			return serialPort.closePort();
		}
		return false;
	}

	/**
	 * 往串口发送数据
	 * 
	 * @param serialPort 串口对象
	 * @param content    待发送数据
	 */
	private int sendToPort(SerialPort serialPort, byte[] content) {
		return serialPort.writeBytes(content, content.length);
	}

	/**
	 * 往串口发送数据
	 *
	 * @param serialPort 串口对象
	 * @param content    待发送数据
	 */
	public int sendStringToPort(String portName, String str) {
		SerialPort serialPort = serialPorts.get(portName);
		if (serialPort == null) {
			return 0;
		}
		String sendmess = str + "\n";
		byte[] content = sendmess.getBytes();
		int i = sendToPort(serialPort, content);
		logger.info("向串口[{}]发送长度{} 数据：{}", portName, i, str);
		return i;
	}

	/**
	 * 往串口发送数据
	 *
	 * @param serialPort 串口对象
	 * @param content    待发送数据
	 */
	private int sendToPort(String portName, byte[] content) {
		SerialPort serialPort = serialPorts.get(portName);
		if (serialPort == null) {
			return 0;
		}
		int i = sendToPort(serialPort, content);
		return i;
	}

	/**
	 * 往串口发送数据
	 *
	 * @param serialPort 串口对象
	 * @param content    待发送数据
	 */
	private int sendToPortAll(String portName, byte[] content) {
		int size = 0;
		if (StrUtil.isEmpty(portName)) {
			for (String portname : serialPorts.keySet()) {
				SerialPort serialPort = serialPorts.get(portname);
				if (serialPort == null) {
					continue;
				}
				int i = sendToPort(serialPort, content);
				size += i;
				String ss = ByteUtils.byteArrayToHexString(content);
				logger.info("向串口[{}]发送长度{} 数据：{}", portname, i, ss);
			}
		} else {
			SerialPort serialPort = serialPorts.get(portName);
			if (serialPort == null) {
				return 0;
			}
			size = sendToPort(serialPort, content);
			String ss = ByteUtils.byteArrayToHexString(content);
			logger.info("向串口[{}]发送长度{} 数据：{}", portName, size, ss);
		}
		return size;
	}

	/**
	 * 添加监听器
	 * 
	 * @param serialPort 串口对象
	 * @param listener   串口存在有效数据监听
	 */
	private void addListener(SerialPort serialPort) {
		try {
			// 给串口添加监听器
			serialPort.addDataListener(new SerialCommPortListener(serialPort));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 往串口发送数据
	 *
	 * @param serialPort 串口对象
	 * @param content    待发送数据
	 */
	public int openSerial(String portName) {
		return sendToPortAll(portName, openbyte);
	}

	public int openAllSerial(String portName) {
		byte[] bytes = new byte[] { (byte) 0xD0, 0x01 };
		return sendToPortAll(portName, bytes);
	}

	/**
	 * 往串口发送数据
	 *
	 * @param serialPort 串口对象
	 * @param content    待发送数据
	 */
	public int closeSerial(String portName) {
		return sendToPortAll(portName, closebyte);
	}

	public int closeALLSerial(String portName) {
		byte[] bytes = new byte[] { (byte) 0xD0, 0x00 };
		return sendToPortAll(portName, bytes);
	}

	/**
	 * 往串口发送数据
	 *
	 * @param serialPort 串口对象
	 * @param content    待发送数据
	 */
	public int openTimeSerial(String portName, int second) {
		byte time = ByteUtils.intToHexByte(second);
		byte[] bytes = new byte[] { (byte) 0xB0, 0x01, time };
		return sendToPortAll(portName, bytes);
	}

	/**
	 * 往串口发送数据
	 *
	 * @param serialPort 串口对象
	 * @param content    待发送数据
	 */
	public int dianDongSerial(String portName) {
		byte[] bytes = new byte[] { (byte) 0xE1, 0x01 };
		return sendToPortAll(portName, bytes);
	}

	/**
	 * 往串口发送数据
	 *
	 * @param serialPort 串口对象
	 * @param content    待发送数据
	 */
	public int resetSrial(String portName) {
		byte[] bytes = new byte[] { (byte) 0xF1, 0x01 };
		return sendToPortAll(portName, bytes);
	}

	/**
	 * 往串口发送数据
	 *
	 * @param serialPort 串口对象
	 * @param content    待发送数据
	 */
	public int serialHeatBelt(String portName) {
		byte[] bytes = new byte[] { (byte) 0x00, 0x00 };
		int i = sendToPort(portName, bytes);
		return i;
	}

	/**
	 * 往串口发送数据
	 *
	 * @param serialPort 串口对象
	 * @param content    待发送数据
	 */
	public int openSerial2(String portName) {
		byte[] openbyte2 = new byte[] { (byte) 0xA0, 0x01, 0x01, (byte) 0XA2 };
		return sendToPortAll(portName, openbyte2);
	}

	/**
	 * 往串口发送数据
	 *
	 * @param serialPort 串口对象
	 * @param content    待发送数据
	 */
	public int closeSerial2(String portName) {
		byte[] closebyte2 = new byte[] { (byte) 0xA0, 0x01, 0x00, (byte) 0XA1 };
		return sendToPortAll(portName, closebyte2);
	}

	/**
	 * 往串口发送数据
	 *
	 * @param serialPort 串口对象
	 * @param content    待发送数据
	 */
	public int openTimeSerial2(String portName, int second) {
		byte time = ByteUtils.intToHexByte(second);
		byte crc = (byte) (0xB0 + 0x01 + time);
		byte[] bytes = new byte[] { (byte) 0xB0, 0x01, time, crc };
		return sendToPortAll(portName, bytes);
	}

	/**
	 * 往串口发送数据
	 *
	 * @param serialPort 串口对象
	 * @param content    待发送数据
	 */
	public int resetSrial2(String portName) {
		byte[] bytes = new byte[] { (byte) 0xF1, 0x01 };
		return sendToPortAll(portName, bytes);
	}

	public static void main(String[] args) {
		byte crc = (byte) (0xA0 + 0x01 + 0x01);
		System.out.println(crc);
		String aa = Integer.toHexString(crc & 0xFF);
		System.out.println(aa);
	}

}