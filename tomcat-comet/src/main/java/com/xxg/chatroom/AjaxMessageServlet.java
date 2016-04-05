package com.xxg.chatroom;

import java.io.UnsupportedEncodingException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/sendMsg")
public class AjaxMessageServlet extends HttpServlet {

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		
		request.setCharacterEncoding("UTF-8");
		try {
			// 这就相当于通知MessageSender线程发送消息给客户端
			MessageSender.messages.put("[" + request.getParameter("name") + "]: " + request.getParameter("msg"));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		doPost(request, response);
	}
}