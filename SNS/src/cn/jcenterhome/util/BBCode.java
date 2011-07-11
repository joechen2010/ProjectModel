package cn.jcenterhome.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BBCode {
	private static String img_exp = "(?is)\\[img\\]\\s*([^\\[\\<\\r\\n]+?)\\s*\\[\\/img\\]";
	private static String url_exp = "(?i)(?<=[^\\]a-z0-9-=\"'\\/])((https?|ftp|gopher|news|telnet|mms|rtsp):\\/\\/)([a-z0-9\\/\\-_+=.~!%@?#%&;:$\\\\()|]+)";
	private static String search_exp[] = {
			"(?is)(\\s*\\[quote\\][\\n\\r]*(.+?)[\\n\\r]*\\[\\/quote\\])",
			"(?i)\\[url\\]\\s*(https?:\\/\\/|ftp:\\/\\/|gopher:\\/\\/|news:\\/\\/|telnet:\\/\\/|rtsp:\\/\\/|mms:\\/\\/|callto:\\/\\/|ed2k:\\/\\/){1}([^\\[\"']+?)\\s*\\[\\/url\\]",
			"(?i)\\[em:(.+?):\\]"};
	private static String replace_exp[] = {"<div class=\"quote\"><span class=\"q\">$2</span></div>",
			"<a href=\"$1$2\" target=\"_blank\">$1$2</a>", "<img src=\"image/face/$1.gif\" class=\"face\">"};
	private static String search_str[] = {"[b]", "[/b]", "[i]", "[/i]", "[u]", "[/u]"};
	private static String replace_str[] = {"<b>", "</b>", "<i>", "</i>", "<u>", "</u>"};

	private static final String[] HTML_SEARCH_EXPRESSION = {
			"(?is)<div class=\"quote\"><span class=\"q\">(.*?)</span></div>",
			"(?is)<a href=\"(.+?)\".*?</a>", "(\r\n|\n|\r)", "(?is)<br.*?>",
			"(?is)[ \t]*<img src=\"image/face/(.+?).gif\".*?>[ \t]*", "(?is)\\s*<img src=\"(.+?)\".*?>\\s*"};
	private static final String[] HTML_REPLACE_EXPRESSION = {"[quote]$1[/quote]", "$1", "", "\n", "[em:$1:]",
			"\n[img]$1[/img]\n"};
	private static final String[] HTML_SEARCH_STRING = {"<b>", "</b>", "<i>", "</i>", "<u>", "</u>",
			"&nbsp; &nbsp; &nbsp; &nbsp; ", "&nbsp; &nbsp;", "&nbsp;&nbsp;", "&lt;", "&gt;", "&amp;"};
	private static final String[] HTML_REPLACE_STRING = {"[b]", "[/b]", "[i]", "[/i]", "[u]", "[/u]", "\t",
			"   ", "  ", "<", ">", "&"};

	
	public static String bbCode(String message, int parseurl) {
		if (parseurl == 2) {
			Matcher matcher = Pattern.compile(img_exp).matcher(message);
			StringBuffer sb = new StringBuffer();
			while (matcher.find()) {
				matcher.appendReplacement(sb, "<img src=\"" + Common.addSlashes(matcher.group(1)) + "\">");
			}
			matcher.appendTail(sb);
			message = sb.toString();
			message = parseURL(message);
		}
		for (int i = 0; i < search_exp.length; i++) {
			message = message.replaceAll(search_exp[i], replace_exp[i]);
		}
		for (int i = 0; i < search_str.length; i++) {
			message = message.replace(search_str[i], replace_str[i]);
		}
		message = message.replace("\t", "&nbsp; &nbsp; &nbsp; &nbsp; ");
		message = message.replace("   ", "&nbsp; &nbsp;");
		message = message.replace("  ", "&nbsp;&nbsp;");
		return Common.nl2br(message);
	}

	
	public static String html2bbcode(String html) {
		for (int i = 0; i < HTML_SEARCH_STRING.length; i++) {
			html = html.replace(HTML_SEARCH_STRING[i], HTML_REPLACE_STRING[i]);
		}
		for (int i = 0; i < HTML_SEARCH_EXPRESSION.length; i++) {
			html = html.replaceAll(HTML_SEARCH_EXPRESSION[i], HTML_REPLACE_EXPRESSION[i]);
		}
		html = Common.sHtmlSpecialChars(html).toString().trim();
		return html;
	}

	
	private static String parseURL(String message) {
		message = " " + message;
		return message.replaceAll(url_exp, "[url]$1$3[/url]");
	}

}