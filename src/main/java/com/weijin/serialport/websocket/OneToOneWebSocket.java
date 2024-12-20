package com.weijin.serialport.websocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;

/**
 * 前后端交互的类实现消息的接收推送(自己发送给另一个人)
 *
 * @ServerEndpoint(value = "/test/oneToOne") 前端通过此URI 和后端交互，建立连接
 */
@Slf4j
@ServerEndpoint(value = "/test/oneToOne")
@Component
public class OneToOneWebSocket {

	/** 记录当前在线连接数 */
	private static AtomicInteger onlineCount = new AtomicInteger(0);

	/** 存放所有在线的客户端 */
	private static Map<String, Session> clients = new ConcurrentHashMap<>();

	/**
	 * 连接建立成功调用的方法
	 */
	@OnOpen
	public void onOpen(Session session) {
		onlineCount.incrementAndGet(); // 在线数加1
		clients.put(session.getId(), session);
		log.info("有新连接加入：{}，当前在线人数为：{}", session.getId(), onlineCount.get());
	}

	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
	public void onClose(Session session) {
		onlineCount.decrementAndGet(); // 在线数减1
		clients.remove(session.getId());
		log.info("有一连接关闭：{}，当前在线人数为：{}", session.getId(), onlineCount.get());
	}

	/**
	 * 收到客户端消息后调用的方法
	 *
	 * @param message
	 *            客户端发送过来的消息
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		log.info("服务端收到客户端[{}]的消息[{}]", session.getId(), message);
		try {
			JSONObject myMessage = JSON.parseObject(message);
			if (myMessage != null) {
				Session toSession = clients.get(myMessage.getString("Userid"));
				if (toSession != null) {
					this.sendMessage(myMessage.getString("Message"), toSession);
				}
			}
		} catch (Exception e) {
			log.error("解析失败：{}", e);
		}
	}

	@OnError
	public void onError(Session session, Throwable error) {
		log.error("发生错误");
		error.printStackTrace();
	}

	/**
	 * 服务端发送消息给客户端
	 */
	private void sendMessage(String message, Session toSession) {
		try {
			log.info("服务端给客户端[{}]发送消息[{}]", toSession.getId(), message);
			toSession.getBasicRemote().sendText(message);
		} catch (Exception e) {
			log.error("服务端发送消息给客户端失败：{}", e);
		}
	}

}
