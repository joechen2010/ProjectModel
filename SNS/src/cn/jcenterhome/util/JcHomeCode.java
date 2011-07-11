package cn.jcenterhome.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class JcHomeCode {
	private Map<String, Object> jcHomeCode = null;

	public JcHomeCode() {
		jcHomeCode = new HashMap<String, Object>();
		jcHomeCode.put("pcodecount", -1);
		jcHomeCode.put("codecount", 0);
		jcHomeCode.put("codehtml", null);
	}
	private String codeDisp(String code) {
		jcHomeCode.put("pcodecount", (Integer) jcHomeCode.get("pcodecount") + 1);
		code = code.replaceAll("(?is)^[\n\r]*(.+?)[\n\r]*$", "$1").replace("\\\"", "\"");
		Map temp = (Map) jcHomeCode.get("codehtml");
		if (temp == null) {
			temp = new HashMap();
		}
		temp.put(jcHomeCode.get("pcodecount"), tplCodeDisp(code));
		jcHomeCode.put("codehtml", temp);
		jcHomeCode.put("codecount", (Integer) jcHomeCode.get("codecount") + 1);
		return "[\tJCHOME_CODE_" + jcHomeCode.get("pcodecount") + "\t]";
	}

	
	public String complie(String message) {
		if (message == null || message.length() == 0) {
			return message;
		}
		Matcher matcher = null;
		if (message.indexOf("[/code]") >= 0) {
			matcher = Pattern.compile("(?is)\\s*\\[code\\](.+?)\\[\\/code\\]\\s*").matcher(message);
			StringBuffer b = new StringBuffer();
			while (matcher.find()) {
				matcher.appendReplacement(b, codeDisp(matcher.group(1)));
			}
			matcher.appendTail(b);
			message = b.toString();
		}
		if (message.indexOf("[/url]") >= 0) {
			matcher = Pattern
					.compile(
							"(?is)\\[url(=((https?|ftp|gopher|news|telnet|rtsp|mms|callto|bctp|ed2k|thunder|synacast){1}:\\/\\/|www\\.)([^\\[\\\"']+?))?\\](.+?)\\[\\/url\\]")
					.matcher(message);
			StringBuffer b = new StringBuffer();
			while (matcher.find()) {
				matcher.appendReplacement(b, parseURL(matcher.group(1), matcher.group(5)));
			}
			matcher.appendTail(b);
			message = b.toString();
		}
		if (message.indexOf("[/email]") >= 0) {
			matcher = Pattern
					.compile(
							"(?is)\\[email(=([a-z0-9\\-_.+]+)@([a-z0-9\\-_]+[.][a-z0-9\\-_.]+))?\\](.+?)\\[\\/email\\]")
					.matcher(message);
			StringBuffer b = new StringBuffer();
			while (matcher.find()) {
				matcher.appendReplacement(b, parseEmail(matcher.group(1), matcher.group(4)));
			}
			matcher.appendTail(b);
			message = b.toString();
		}
		String[] search = new String[] {"[/color]", "[/size]", "[/font]", "[/align]", "[b]", "[/b]", "[i]",
				"[/i]", "[u]", "[/u]", "[list]", "[list=1]", "[list=a]", "[list=A]", "[*]", "[/list]",
				"[indent]", "[/indent]", "[/float]"};
		String[] replace = new String[] {"</font>", "</font>", "</font>", "</p>", "<strong>", "</strong>",
				"<i>", "</i>", "<u>", "</u>", "<ul>", "<ul type=\"1\">", "<ul type=\"a\">",
				"<ul type=\"A\">", "<li>", "</ul>", "<blockquote>", "</blockquote>", "</span>"};
		for (int i = 0; i < replace.length; i++) {
			message = message.replace(search[i], replace[i]);
		}
		String[] search_exp = new String[] {"\\[color=([#\\w]+?)\\](?i)", "\\[size=(\\d+?)\\](?i)",
				"\\[size=(\\d+(\\.\\d+)?(px|pt|in|cm|mm|pc|em|ex|%)+?)\\](?i)",
				"\\[font=([^\\[\\<]+?)\\](?i)", "\\[align=(left|center|right)\\](?i)",
				"\\[float=(left|right)\\](?i)"};
		String[] replace_exp = new String[] {"<font color=\"$1\">", "<font size=\"$1\">",
				"<font style=\"font-size: $1\">", "<font face=\"$1 \">", "<p align=\"$1\">",
				"<span style=\"float: $1;\">"};
		for (int i = 0; i < replace_exp.length; i++) {
			message = message.replaceAll(search_exp[i], replace_exp[i]);
		}
		if (message.indexOf("[/quote]") >= 0) {
			message = message.replaceAll("\\s*\\[quote\\][\\n\\r]*(.+?)[\\n\\r]*\\[\\/quote\\]\\s*(?is)",
					tplQuote());
		}
		if (message.indexOf("[/img]") >= 0) {
			matcher = Pattern.compile("\\[img\\]\\s*([^\\[\\<\\r\\n]+?)\\s*\\[/img\\](?is)").matcher(message);
			StringBuffer b = new StringBuffer();
			while (matcher.find()) {
				matcher.appendReplacement(b, bbcodeURL(matcher.group(1),
						"<img src=\"%s\" border=\"0\" alt=\"\" />"));
			}
			matcher.appendTail(b);
			message = b.toString();
			matcher = Pattern.compile(
					"\\[img=(\\d{1,4})[x|\\,](\\d{1,4})\\]\\s*([^\\[\\<\\r\\n]+?)\\s*\\[/img\\](?is)")
					.matcher(message);
			b = new StringBuffer();
			while (matcher.find()) {
				matcher.appendReplacement(b, bbcodeURL(matcher.group(3), "<img width=\"" + matcher.group(1)
						+ "\" height=\"" + matcher.group(2) + "\" src=\"%s\" border=\"0\" alt=\"\" />"));
			}
			matcher.appendTail(b);
			message = b.toString();
		}
		int pcodeCount = (Integer) jcHomeCode.get("pcodecount");
		Map codeHtmlMap = (Map) jcHomeCode.get("codehtml");
		for (int i = 0; i <= pcodeCount; i++) {
			message = message.replace("[\tJCHOME_CODE_" + i + "\t]", (String) codeHtmlMap.get(i));
		}
		message = message.replace("\t", "&nbsp; &nbsp; &nbsp; &nbsp; ");
		message = message.replace("   ", "&nbsp; &nbsp;");
		message = message.replace("  ", "&nbsp;&nbsp;");
		return Common.nl2br(message);
	}

	private String tplCodeDisp(String code) {
		return "<div class=\"blockcode\"><code id=\"code" + jcHomeCode.get("codecount") + "\">" + code
				+ "</code></div>";
	}
	private String parseURL(String url, String text) {
		Matcher matcher = null;
		if (url == null) {
			Pattern pattern = Pattern
					.compile("((https?|ftp|gopher|news|telnet|rtsp|mms|callto|bctp|ed2k|thunder|synacast){1}:\\/\\/|www\\.)[^\\[\\\"']+(?i)");
			matcher = pattern.matcher(text.trim());
		}
		if (matcher != null && matcher.find()) {
			url = matcher.group(0);
			int length = 65;
			if (Common.strlen(url) > length) {
				text = url.substring(0, (int) (length * 0.5)) + " ... "
						+ url.substring(url.length() - (int) (length * 0.3), url.length());
			}
			return "<a href=\"" + (url.substring(0, 4).toLowerCase().equals("www.") ? "http://" + url : url)
					+ "\" target=\"_blank\">" + text + "</a>";
		} else {
			if (url == null) {
				url = "";
			} else {
				url = url.substring(1, url.length());
				if (url.substring(0, 4).toLowerCase().equals("www.")) {
					url = "http://" + url;
				}
			}
			return "<a href=\"" + url + "\" target=\"_blank\">" + text + "</a>";
		}
	}
	private String parseEmail(String email, String text) {
		Matcher matcher = null;
		if (email == null) {
			Pattern pattern = Pattern
					.compile("\\s*([a-z0-9\\-_.+]+)@([a-z0-9\\-_]+[.][a-z0-9\\-_.]+)\\s*(?i)");
			matcher = pattern.matcher(text);
		}
		if (matcher != null && matcher.find()) {
			email = matcher.group(0).trim();
			return "<a href=\"mailto:" + email + "\">" + email + "</a>";
		} else {
			return "<a href=\"mailto:" + (email == null ? "" : email.substring(1, email.length())) + "\">"
					+ text + "</a>";
		}
	}
	private String tplQuote() {
		return "<div class=\"quote\"><blockquote>$1</blockquote></div>";
	}
	private String bbcodeURL(String url, String tags) {
		if (!url.matches("<.+?>(?s)")) {
			int urlLength = url.length() >= 6 ? 6 : url.length();
			if (!Common.in_array(new String[] {"http:/", "https:", "ftp://", "rtsp:/", "mms://"}, url
					.substring(0, urlLength).toLowerCase())) {
				url = "http://" + url;
			}
			return String.format(tags, url, Common.addSlashes(url)).replace("submit", "").replace(
					"logging.jsp", "");
		} else {
			return "&nbsp;" + url;
		}
	}
}
