package com.xxg.chatroom;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import javax.servlet.http.HttpServletResponse;

public class MessageSender implements Runnable {
	
	// 所有正在等待响应的HTTP长连接
	private ArrayList<HttpServletResponse> connections;
	
	// 未发送给客户端的消息集合
	public static ArrayBlockingQueue<String> messages = new ArrayBlockingQueue<String>(10);

	public MessageSender(ArrayList<HttpServletResponse> connections) {
		this.connections = connections;
	}
	
	public void run() {
		
		while(true) {
			
			// 消息阻塞队列中获取一条消息，如果队列为空则阻塞
			String message = null;
			try {
				message = messages.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// 给每个客户端发送消息
			synchronized (connections) {
            	
				for(HttpServletResponse response : connections) {
					try {
						PrintWriter out = response.getWriter();
                        
						// 输出一段脚本，调用JS将消息显示在页面上
						out.println("<script>parent.addMsg('" + message + "<br>')</script>"); 
						out.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}