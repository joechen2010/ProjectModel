package cn.jcenterhome.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.CometEvent;
import org.apache.catalina.CometProcessor;

import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.JavaCenterHome;

public class CometServlet extends HttpServlet implements CometProcessor {

	private static final long serialVersionUID = 1L;
	
	private final int TIMEOUT = Integer.MAX_VALUE;
	private PostHandler postHandler = null;

	public void init() throws ServletException {
		postHandler = PostHandler.getInstance();
		Thread postHandlerThread = new Thread(postHandler, "postHandler[only]");
		postHandlerThread.setDaemon(true);
		postHandlerThread.start();
	}

	public void destroy() {
		postHandler.getConnections().clear();
		postHandler.stop();
		postHandler = null;
	}
	
	public void event(CometEvent event) throws IOException, ServletException {
		HttpServletRequest request = event.getHttpServletRequest();
		HttpServletResponse response = event.getHttpServletResponse();
		Map<Integer, HttpServletResponse> connections = postHandler.getConnections();
		int currUserId = 0;
		try {
			currUserId = Integer.parseInt(request.getParameter("supe_uid"));
		} catch(NumberFormatException e) {
			//Ignore
		}
		if(currUserId > 0) {
			if (event.getEventType() == CometEvent.EventType.BEGIN) {
				event.setTimeout(TIMEOUT);
				response.setCharacterEncoding(JavaCenterHome.JCH_CHARSET);
				PrintWriter out = response.getWriter();
				out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
				out.println("<head>");
				out.println("  <title>Comet php backend</title>");
				out.println("  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
				out.println("</head>");
				out.println("<body>");
				out.println("  <script type=\"text/javascript\">");
				out.println("    var is_khtml = navigator.appName.match(\"Konqueror\") || navigator.appVersion.match(\"KHTML\");");
				out.println("    if(is_khtml) {");
				out.println("      var prototypejs = document.createElement('script');");
				out.println("      prototypejs.setAttribute('type','text/javascript');");
				out.println("      prototypejs.setAttribute('src', '"+Common.getSiteUrl(request)/*request.get*/+"source/prototype.js');");
				out.println("      var head = document.getElementsByTagName('head');");
				out.println("      head[0].appendChild(prototypejs);");
				out.println("    }");
				out.println("    var comet = window.parent.comet;");
				out.println("  </script>");
				synchronized(connections) {
					connections.put(currUserId, response);
				}
			} else if (event.getEventType() == CometEvent.EventType.ERROR) {
				synchronized (connections) {
					connections.remove(currUserId);
				}
				response.getWriter().close();
				event.close();
			} else if (event.getEventType() == CometEvent.EventType.END) {
				synchronized (connections) {
					connections.remove(currUserId);
				}
				event.close();
			} else if (event.getEventType() == CometEvent.EventType.READ) {
				//Ignore
			}
		}
	}


}
