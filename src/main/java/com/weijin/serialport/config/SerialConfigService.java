package com.weijin.serialport.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.netty.channel.rxtx.RxtxChannelConfig;
import io.netty.channel.rxtx.RxtxChannelConfig.Databits;
import io.netty.channel.rxtx.RxtxChannelConfig.Paritybit;
import io.netty.channel.rxtx.RxtxChannelConfig.Stopbits;

@Component
public class SerialConfigService {

	private static final Logger logger = LoggerFactory.getLogger(SerialConfigService.class);

	public static final String DefaultPORTNAME = "COM3";

	/**
	 * 波特率
	 */
	@Value("${serial.baudrate:9600}")
	public static Integer baudrate = new Integer(9600);

	/**
	 * 串口数据位
	 */
	@Value("${serial.datebits:8}")
	public int databits;

	/**
	 * 停止位
	 */
	@Value("${serial.stopbits:1}")
	public int stopbits;

	/**
	 * 校验位
	 */
	@Value("${serial.parity:0}")
	public int parity;
	/**
	 * 数据位 默认8位
	 * 可以设置的值：SerialPort.DATABITS_5、SerialPort.DATABITS_6、SerialPort.DATABITS_7、SerialPort.DATABITS_8
	 */
	public Databits dataBits = RxtxChannelConfig.Databits.DATABITS_8;
	/**
	 * 停止位
	 * 可以设置的值：SerialPort.STOPBITS_1、SerialPort.STOPBITS_2、SerialPort.STOPBITS_1_5
	 */
	public Stopbits stopBits = RxtxChannelConfig.Stopbits.STOPBITS_1;
	/**
	 * 校验位
	 * 可以设置的值：SerialPort.PARITY_NONE、SerialPort.PARITY_ODD、SerialPort.PARITY_EVEN、SerialPort.PARITY_MARK、SerialPort.PARITY_SPACE
	 */
	public Paritybit paritybit = RxtxChannelConfig.Paritybit.NONE;

}