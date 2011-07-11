package cn.jcenterhome.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PostHandler implements Runnable {

	private boolean running = true;
	private ArrayList<String> messages = new ArrayList<String>();
	private Map<Integer, HttpServletResponse> connections = new HashMap<Integer, HttpServletResponse>();
	private static PostHandler instance = new PostHandler();
	private Integer toUid = 0;
	
	private PostHandler() {
	}
	
	public static PostHandler getInstance() {
		return instance;
	}

	public void stop() {
		running = false;
	}

	public void send(int toUid, String message) {
		synchronized (messages) {
			synchronized(this.toUid) {
				this.toUid = toUid;
			}
			messages.add("<script type=\"text/javascript\">comet.showServerData('"+message+"')</script>");
			messages.notify();
		}
	}
	
	public Map<Integer, HttpServletResponse> getConnections() {
		return connections;
	}
	
	

	public void run() {
		while (running) {
			if (messages.size() == 0) {
				try {
					synchronized (messages) {
						messages.wait();
					}
				} catch (InterruptedException e) {
				}
			}
			synchronized (connections) {
				String[] pendingMessages = null;
				int pendingToUid = 0;
				synchronized (messages) {
					pendingMessages = messages.toArray(new String[0]);
					messages.clear();
					synchronized(this.toUid) {
						pendingToUid = this.toUid;
						this.toUid = 0;
					}
				}
				try {
					HttpServletResponse connection = connections.get(pendingToUid);
					if(connection != null) {
						PrintWriter writer = connection.getWriter();
						writer.println(pendingMessages[0]);
						
						writer.flush();
					}
				} catch (IOException e) {
				}
			}
		}
	}

}
