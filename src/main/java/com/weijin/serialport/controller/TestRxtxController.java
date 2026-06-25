package com.weijin.serialport.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.weijin.serialport.jSerialComm.SerialCommPortService;
import com.weijin.serialport.nettyrxtx.NettyRxtxServer;
import com.weijin.serialport.rxtx.RXTXSerialService;

import io.swagger.annotations.ApiOperation;

@RestController
public class TestRxtxController {
	
	@Autowired
	private NettyRxtxServer nettyRxtxServer;

	@Autowired
	private RXTXSerialService rxtxSerialService;

	@Autowired
	private SerialCommPortService jSerialComm;

	private static final Logger logger = LoggerFactory.getLogger(TestRxtxController.class);

	@ApiOperation(value = "获取串口信息", notes = "获取串口信息")
	@RequestMapping(value = "/rxtx/SerialPorts", method = RequestMethod.GET)
	public String getSerialPorts() {
		List<String> datas = rxtxSerialService.findPortName();
		return JSONObject.toJSONString(datas);
	}

	@ApiOperation(value = "给串口发送信息", notes = "给串口发送信息")
	@RequestMapping(value = "/rxtx/SerialPorts/message", method = RequestMethod.POST)
	public String sendMessage(@RequestParam(value = "portname",required = false) String name, @RequestBody(required = true) String data) {
		rxtxSerialService.sendToPort(name, data);
		return "OK";
	}

	@ApiOperation(value = "给串口发送信息", notes = "给串口发送信息")
	@RequestMapping(value = "/rxtx/open", method = RequestMethod.GET)
	public int openNettyRxtx(@RequestParam(value = "portname", required = false) String name) {
		int i = rxtxSerialService.openSerial(name);
		return i;
	}

	@ApiOperation(value = "给串口发送信息", notes = "给串口发送信息")
	@RequestMapping(value = "/rxtx/close", method = RequestMethod.GET)
	public int closeNettyRxtx(@RequestParam(value = "portname", required = false) String name) {
		int i = rxtxSerialService.closeSerial(name);
		return i;
	}

	@ApiOperation(value = "获取串口信息", notes = "获取串口信息")
	@RequestMapping(value = "/jSerialComms", method = RequestMethod.GET)
	public String getSerialPortsjSerialComm() {
		List<String> datas = jSerialComm.findPorts();
		return JSONObject.toJSONString(datas);
	}

	@ApiOperation(value = "给串口发送信息", notes = "给串口发送信息")
	@RequestMapping(value = "/jSerialComm/message", method = RequestMethod.POST)
	public String sendMessagejSerialComm(@RequestParam(value = "portname", required = false) String name,
			@RequestBody(required = true) String data) {
		jSerialComm.sendStringToPort(name, data);
		return "OK";
	}

	@ApiOperation(value = "给串口发送信息", notes = "给串口发送信息")
	@RequestMapping(value = "/jSerialComm/open", method = RequestMethod.GET)
	public int openjSerialComm(@RequestParam(value = "portname", required = false) String name) {
		int i = jSerialComm.openSerial(name);
		return i;
	}

	@ApiOperation(value = "给串口发送信息", notes = "给串口发送信息")
	@RequestMapping(value = "/jSerialComm/close", method = RequestMethod.GET)
	public int closejSerialComm(@RequestParam(value = "portname", required = false) String name) {
		int i = jSerialComm.closeSerial(name);
		return i;
	}

	@ApiOperation(value = "给串口发送信息", notes = "给串口发送信息")
	@RequestMapping(value = "/jSerialComm/openTime", method = RequestMethod.GET)
	public int closejSerialComm(@RequestParam(value = "portname", required = false) String name,
			@RequestParam(value = "time", required = true) int time) {
		int i = jSerialComm.openTimeSerial(name, time);
		return i;
	}

	@ApiOperation(value = "给串口发送信息", notes = "给串口发送信息")
	@RequestMapping(value = "/jSerialComm/reset", method = RequestMethod.GET)
	public int resetjSerialComm(@RequestParam(value = "portname", required = false) String name) {
		int i = jSerialComm.resetSrial(name);
		return i;
	}

	@ApiOperation(value = "给串口发送信息", notes = "给串口发送信息")
	@RequestMapping(value = "/jSerialComm/openAll", method = RequestMethod.GET)
	public int openALLjSerialComm(@RequestParam(value = "portname", required = false) String name) {
		int i = jSerialComm.openAllSerial(name);
		return i;
	}

	@ApiOperation(value = "给串口发送信息", notes = "给串口发送信息")
	@RequestMapping(value = "/jSerialComm/closeAll", method = RequestMethod.GET)
	public int closeALLjSerialComm(@RequestParam(value = "portname", required = false) String name) {
		int i = jSerialComm.closeALLSerial(name);
		return i;
	}


	@ApiOperation(value = "给串口发送信息", notes = "给串口发送信息")
	@RequestMapping(value = "/jSerialComm/openDD", method = RequestMethod.GET)
	public int ddjSerialComm(@RequestParam(value = "portname", required = false) String name) {
		int i = jSerialComm.dianDongSerial(name);
		return i;
	}

	@ApiOperation(value = "给串口发送信息", notes = "给串口发送信息")
	@RequestMapping(value = "/jSerialComm/open2", method = RequestMethod.GET)
	public int openjSerialComm2(@RequestParam(value = "portname", required = false) String name) {
		int i = jSerialComm.openSerial2(name);
		return i;
	}

	@ApiOperation(value = "给串口发送信息", notes = "给串口发送信息")
	@RequestMapping(value = "/jSerialComm/close2", method = RequestMethod.GET)
	public int closejSerialComm2(@RequestParam(value = "portname", required = false) String name) {
		int i = jSerialComm.closeSerial2(name);
		return i;
	}

	@ApiOperation(value = "给串口发送信息", notes = "给串口发送信息")
	@RequestMapping(value = "/nettyRxtx/open", method = RequestMethod.GET)
	public int openRxtx(@RequestParam(value = "portname", required = false) String name) {
		int i = nettyRxtxServer.openSerial(name);
		return i;
	}

	@ApiOperation(value = "给串口发送信息", notes = "给串口发送信息")
	@RequestMapping(value = "/nettyRxtx/close", method = RequestMethod.GET)
	public int closeRxtx(@RequestParam(value = "portname", required = false) String name) {
		int i = nettyRxtxServer.closeSerial(name);
		return i;
	}


}
