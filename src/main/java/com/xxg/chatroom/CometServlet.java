package com.xxg.chatroom;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.comet.CometEvent;
import org.apache.catalina.comet.CometProcessor;

@WebServlet("/comet")
public class CometServlet extends HttpServlet implements CometProcessor {
	
	// 所有正在等待响应的HTTP长连接
	private ArrayList<HttpServletResponse> connections = null;
	
	// 用于发送消息的线程
	private MessageSender messageSender = null;
	
	// 启动消息处理线程
	public void init() {
		connections = new ArrayList<HttpServletResponse>();
		messageSender = new MessageSender(connections);
		Thread messageSenderThread = new Thread(messageSender);
		messageSenderThread.start();
	}

	public void event(CometEvent event) throws IOException, ServletException {
		
		HttpServletResponse response = event.getHttpServletResponse();
		response.setCharacterEncoding("UTF-8");
		
		if (event.getEventType() == CometEvent.EventType.BEGIN) {
			System.out.println("BEGIN");
			
			// 一段大于1024的字符串，针对某些浏览器缓存
			PrintWriter out = response.getWriter();
			StringBuilder sb = new StringBuilder();  
	        for(int i = 0; i < 1024; i++) {  
	            sb.append('a');  
	        }  
	        out.println("<!-- " + sb.toString() + " -->"); // 注意加上HTML注释  
	        out.flush();
			
			synchronized(connections) {
                connections.add(response);
                System.out.println("当前在线用户：" + connections.size());
            }
			
		} else if (event.getEventType() == CometEvent.EventType.ERROR) {
			System.out.println("ERROR");
			
			synchronized(connections) {
                connections.remove(response);
                System.out.println("当前在线用户：" + connections.size());
            }
			event.close();
			
		} else if (event.getEventType() == CometEvent.EventType.END) {
			System.out.println("END");
			
			synchronized(connections) {
                connections.remove(response);
                System.out.println("当前在线用户：" + connections.size());
            }
			event.close();
			
		}
	}

}