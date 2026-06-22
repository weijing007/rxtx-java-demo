package com.weijin.serialport.jSerialComm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.weijin.serialport.utils.ByteUtils;

/**
 * 串口监听
 */
public class SerialCommPortListener implements SerialPortDataListener {

	private static final Logger logger = LoggerFactory.getLogger(SerialCommPortListener.class);

	private SerialPort serialPort;
	

	public SerialCommPortListener(SerialPort serialPort) {
		this.serialPort = serialPort;
	}

	@Override
    public int getListeningEvents() {//必须是return这个才会开启串口工具的监听
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }

	@Override
	public void serialEvent(SerialPortEvent serialPortEvent) {
		// TODO Auto-generated method stub
		String data = "";
		if (serialPortEvent.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE){
			return;//判断事件的类型
        }
		while(serialPort.bytesAvailable()!=0) {
			byte[] newData = new byte[serialPort.bytesAvailable()];
			serialPort.readBytes(newData, newData.length);
			data = ByteUtils.byteArrayToHexString(newData);
			// data = StrUtil.removeAllLineBreaks(data);
			logger.info("收到串口[" + serialPort.getSystemPortName() + "]:" + data);
			try {
				// webSockte.sendAllMessage(data); 处理数据
				Thread.sleep(20);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//同样使用循环读取法读取所有数据
        //由于这里是监听函数，所以也可以不使用循环读取法，在监听器外创建一个全局变量，然后将每次读取到的数据添加到全局变量里
	}
}