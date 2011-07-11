package cn.jcenterhome.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.mail.smtp.SMTPTransport;


public class Mail {
	private final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
	private boolean mailUserName = true;
	private String host = null;
	private int port = 25;
	private boolean auth = true;
	private String username = null;
	private String password = null;
	private HttpServletRequest request = null;
	private Session session = null;
	ThreadPoolExecutor executor = null;
	{
		executor = new ThreadPoolExecutor(1, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(5));
	}

	@SuppressWarnings("unchecked")
	public Mail(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		Map<String, Object> globalMail = Common.getCacheDate(request, response, "/data/cache/cache_mail.jsp",
				"globalMail");
		if ((Integer) globalMail.get("mailusername") == 0) {
			mailUserName = false;
		}
		host = globalMail.get("server").toString();
		port = (Integer) globalMail.get("port");
		if (port == 0) {
			port = 25;
		}
		if ((Integer) globalMail.get("auth") == 0) {
			auth = false;
		}
		username = globalMail.get("auth_username").toString();
		password = globalMail.get("auth_password").toString();
	}

	
	private synchronized void createSession() {
		Properties mailProps = new Properties();
		mailProps.setProperty("mail.transport.protocol", "smtp");
		mailProps.setProperty("mail.smtp.host", host);
		mailProps.setProperty("mail.smtp.port", String.valueOf(port));
		if ("smtp.gmail.com".equals(host)) {
			mailProps.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
			mailProps.setProperty("mail.smtp.socketFactory.fallback", "false");
			mailProps.setProperty("mail.smtp.socketFactory.port", String.valueOf(port));
		}
		if (auth) {
			mailProps.put("mail.smtp.auth", "true");
		}
		session = Session.getDefaultInstance(mailProps, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
	}

	private MimeMessage createMimeMessage() {
		if (session == null) {
			createSession();
		}
		return new MimeMessage(session);
	}

	
	public boolean sendMessage(String from, String toEmail, String subject, String body) {
		String charset = JavaCenterHome.JCH_CHARSET;
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		String siteName = (String) sConfig.get("sitename");
		int timestamp = (Integer) sGlobal.get("timestamp");
		String timeoffset = Common.getTimeOffset(sGlobal, sConfig);
		body = Common.getMessage(request, "template_sendmail", siteName, body, Common.getSiteUrl(request),
				Common.gmdate("yyyy-MM-dd HH:mm", timestamp, timeoffset));
		try {
			if (from == null) {
				from = MimeUtility.encodeText(siteName, charset, "B") + " <" + sConfig.get("adminemail")
						+ ">";
			} else {
				List<String> mats = Common.pregMatch(from, "^(.+?) \\<(.+?)\\>$");
				if (mats.size() == 2) {
					from = MimeUtility.encodeText(mats.get(0), charset, "B") + " <" + mats.get(1) + ">";
				}
			}
			List<String> mats = Common.pregMatch(toEmail, "^(.+?) \\<(.+?)\\>$");
			if (mats.size() == 2) {
				toEmail = mailUserName ? MimeUtility.encodeText(mats.get(0), charset, "B") + " <"
						+ mats.get(1) + ">" : mats.get(1);
			}
			subject = MimeUtility.encodeText(("[" + siteName + "] " + subject).replaceAll("[\r|\n]", ""),
					charset, "B");
			body = body.replaceAll("\n\r", "\r").replaceAll("\r\n", "\n").replaceAll("\r", "\n").replaceAll(
					"\n", "\r\n");

			String encoding = MimeUtility.mimeCharset(charset);
			String toEmails[] = toEmail.split(",");
			Address to[] = new Address[toEmails.length];
			for (int i = 0; i < toEmails.length; i++) {
				String sTo = toEmails[i];
				if (sTo.matches("^.*<.*>$")) {
					int index = sTo.indexOf("<");
					to[i] = new InternetAddress(sTo.substring(index + 1, sTo.length() - 1),
							mailUserName ? sTo.substring(0, index) : "", encoding);
				} else {
					to[i] = new InternetAddress(sTo, "", encoding);
				}
			}
			String fromName = "";
			String fromEmail;
			if (from.matches("^.*<.*>$")) {
				int index = from.indexOf("<");
				if (mailUserName) {
					fromName = from.substring(0, index);
				}
				fromEmail = from.substring(index + 1, from.length() - 1);
			} else {
				fromEmail = from;
			}
			Address fromAddress = new InternetAddress(fromEmail, fromName, encoding);
			MimeMessage message = createMimeMessage();
			message.setHeader("X-Priority", "3");
			message.setHeader("Date", Common.gmdate("EEE, dd MMM yyyy HH:mm:ss Z", timestamp, timeoffset));
			message.setHeader("Content-Transfer-Encoding", "8bit");
			message.setRecipients(Message.RecipientType.TO, to);
			message.setFrom(fromAddress);
			message.setSubject(subject, encoding);
			MimeMultipart content = new MimeMultipart("alternative");
			if (body != null) {
				MimeBodyPart html = new MimeBodyPart();
				html.setContent(body, "text/html;charset=" + encoding);
				html.setDisposition(Part.INLINE);
				content.addBodyPart(html);
			}
			message.setContent(content);
			message.setDisposition(Part.INLINE);
			sendMessages(message);
		} catch (Exception e) {
			FileHelper.writeLog(request, "SMTP", "(" + this.host + ":" + this.port + ") END - "
					+ e.getMessage());
			return false;
		}
		return true;
	}

	private void sendMessages(MimeMessage message) {
		Collection<MimeMessage> messages = Collections.singletonList(message);
		if (messages.size() == 0) {
			return;
		}
		executor.execute(new EmailTask(messages));
	}

	
	
	private class EmailTask implements Runnable {
		private Collection<MimeMessage> messages;
		private int timestamp;
		private String timeoffset;
		private String onlineIP;
		private int supe_uid;
		private String requestURI;

		public EmailTask(Collection<MimeMessage> messages) {
			this.messages = messages;
			Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
			Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
			this.timestamp = (Integer) sGlobal.get("timestamp");
			this.timeoffset = Common.getTimeOffset(sGlobal, sConfig);
			this.onlineIP = Common.getOnlineIP(request);
			this.supe_uid = (Integer) sGlobal.get("supe_uid");
			this.requestURI = (String) request.getAttribute("requestURI");
		}

		public void run() {
			try {
				sendMessages();
			} catch (MessagingException me) {
				String message = me.getMessage();
				if (message == null) {
					message = "";
				}
				message = "(" + host + ":" + port + ") CONNECT - Unable to connect to the SMTP server¡£"
						+ message.replace("\t", "");
				FileHelper.writeLog(timestamp, timeoffset, onlineIP, supe_uid, requestURI, "SMTP", message);
			}
		}

		public void sendMessages() throws MessagingException {
			Transport transport = null;
			try {
				URLName url = new URLName("smtp", host, port, "", username, password);
				transport = new SMTPTransport(session, url);
				transport.connect(host, port, username, password);
				for (MimeMessage message : messages) {
					transport.sendMessage(message, message.getRecipients(MimeMessage.RecipientType.TO));
				}
			} finally {
				if (transport != null) {
					transport.close();
				}
			}
		}
	}
}