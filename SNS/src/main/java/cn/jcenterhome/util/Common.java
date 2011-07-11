
package cn.jcenterhome.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.struts.Globals;
import org.apache.struts.util.MessageResources;

import cn.jcenterhome.service.BlockService;
import cn.jcenterhome.service.CacheService;
import cn.jcenterhome.service.CpService;
import cn.jcenterhome.service.DataBaseService;
import cn.jcenterhome.vo.MessageVO;


public class Common {
	private static MessageResources mr = null;
	private static DataBaseService dataBaseService = (DataBaseService) BeanFactory.getBean("dataBaseService");
	private static final String[] notes = {"notenum", "pokenum", "addfriendnum", "mtaginvitenum",
			"eventinvitenum", "myinvitenum"};
	private static final char[] pregChars = {'.', '\\', '+', '*', '?', '[', '^', ']', '$', '(', ')', '{',
			'}', '=', '!', '<', '>', '|', ':'};
	private static final String randChars = "0123456789abcdefghigklmnopqrstuvtxyzABCDEFGHIGKLMNOPQRSTUVWXYZ";
	private static Random random = new Random();
	private static Map<String, String[]> timeZoneIDs = new LinkedHashMap<String, String[]>(32);
	static {
		timeZoneIDs.put("-12", new String[] {"GMT-12:00", "(GMT -12:00) Eniwetok, Kwajalein"});
		timeZoneIDs.put("-11", new String[] {"GMT-11:00", "(GMT -11:00) Midway Island, Samoa"});
		timeZoneIDs.put("-10", new String[] {"GMT-10:00", "(GMT -10:00) Hawaii"});
		timeZoneIDs.put("-9", new String[] {"GMT-09:00", "(GMT -09:00) Alaska"});
		timeZoneIDs.put("-8", new String[] {"GMT-08:00",
				"(GMT -08:00) Pacific Time (US &amp; Canada), Tijuana"});
		timeZoneIDs.put("-7", new String[] {"GMT-07:00",
				"(GMT -07:00) Mountain Time (US &amp; Canada), Arizona"});
		timeZoneIDs.put("-6", new String[] {"GMT-06:00",
				"(GMT -06:00) Central Time (US &amp; Canada), Mexico City"});
		timeZoneIDs.put("-5", new String[] {"GMT-05:00",
				"(GMT -05:00) Eastern Time (US &amp; Canada), Bogota, Lima, Quito"});
		timeZoneIDs.put("-4", new String[] {"GMT-04:00",
				"(GMT -04:00) Atlantic Time (Canada), Caracas, La Paz"});
		timeZoneIDs.put("-3.5", new String[] {"GMT-03:30", "(GMT -03:30) Newfoundland"});
		timeZoneIDs.put("-3", new String[] {"GMT-03:00",
				"(GMT -03:00) Brassila, Buenos Aires, Georgetown, Falkland Is"});
		timeZoneIDs.put("-2", new String[] {"GMT-02:00",
				"(GMT -02:00) Mid-Atlantic, Ascension Is., St. Helena"});
		timeZoneIDs.put("-1", new String[] {"GMT-01:00", "(GMT -01:00) Azores, Cape Verde Islands"});
		timeZoneIDs.put("0", new String[] {"GMT",
				"(GMT) Casablanca, Dublin, Edinburgh, London, Lisbon, Monrovia"});
		timeZoneIDs.put("1", new String[] {"GMT+01:00",
				"(GMT +01:00) Amsterdam, Berlin, Brussels, Madrid, Paris, Rome"});
		timeZoneIDs.put("2", new String[] {"GMT+02:00",
				"(GMT +02:00) Cairo, Helsinki, Kaliningrad, South Africa"});
		timeZoneIDs.put("3", new String[] {"GMT+03:00", "(GMT +03:00) Baghdad, Riyadh, Moscow, Nairobi"});
		timeZoneIDs.put("3.5", new String[] {"GMT+03:30", "(GMT +03:30) Tehran"});
		timeZoneIDs.put("4", new String[] {"GMT+04:00", "(GMT +04:00) Abu Dhabi, Baku, Muscat, Tbilisi"});
		timeZoneIDs.put("4.5", new String[] {"GMT+04:30", "(GMT +04:30) Kabul"});
		timeZoneIDs.put("5", new String[] {"GMT+05:00",
				"(GMT +05:00) Ekaterinburg, Islamabad, Karachi, Tashkent"});
		timeZoneIDs
				.put("5.5", new String[] {"GMT+05:30", "(GMT +05:30) Bombay, Calcutta, Madras, New Delhi"});
		timeZoneIDs.put("5.75", new String[] {"GMT+05:45", "(GMT +05:45) Katmandu"});
		timeZoneIDs.put("6", new String[] {"GMT+06:00", "(GMT +06:00) Almaty, Colombo, Dhaka, Novosibirsk"});
		timeZoneIDs.put("6.5", new String[] {"GMT+06:30", "(GMT +06:30) Rangoon"});
		timeZoneIDs.put("7", new String[] {"GMT+07:00", "(GMT +07:00) Bangkok, Hanoi, Jakarta"});
		timeZoneIDs.put("8", new String[] {"GMT+08:00",
				"(GMT +08:00) Beijing, Hong Kong, Perth, Singapore, Taipei"});
		timeZoneIDs
				.put("9", new String[] {"GMT+09:00", "(GMT +09:00) Osaka, Sapporo, Seoul, Tokyo, Yakutsk"});
		timeZoneIDs.put("9.5", new String[] {"GMT+09:30", "(GMT +09:30) Adelaide, Darwin"});
		timeZoneIDs.put("10", new String[] {"GMT+10:00",
				"(GMT +10:00) Canberra, Guam, Melbourne, Sydney, Vladivostok"});
		timeZoneIDs.put("11", new String[] {"GMT+11:00",
				"(GMT +11:00) Magadan, New Caledonia, Solomon Islands"});
		timeZoneIDs.put("12", new String[] {"GMT+12:00",
				"(GMT +12:00) Auckland, Wellington, Fiji, Marshall Island"});
	}

	
	@SuppressWarnings("unchecked")
	public static boolean empty(Object obj) {
		if (obj == null) {
			return true;
		} else if (obj instanceof String && (obj.equals("") || obj.equals("0"))) {
			return true;
		} else if (obj instanceof Number && ((Number) obj).doubleValue() == 0) {
			return true;
		} else if (obj instanceof Boolean && !((Boolean) obj)) {
			return true;
		} else if (obj instanceof Collection && ((Collection) obj).isEmpty()) {
			return true;
		} else if (obj instanceof Map && ((Map) obj).isEmpty()) {
			return true;
		} else if (obj instanceof Object[] && ((Object[]) obj).length == 0) {
			return true;
		}
		return false;
	}

	
	public static String trim(String text) {
		if (text == null) {
			return "";
		}
		return text.trim();
	}

	
	public static boolean in_array(Object source, Object ext) {
		return in_array(source, ext, false);
	}

	
	public static boolean in_array(Object source, Object ext, boolean strict) {
		if (source == null || ext == null) {
			return false;
		}
		if (source instanceof Collection) {
			for (Object s : (Collection) source) {
				if (s.toString().equals(ext.toString())) {
					if (strict) {
						if ((s.getClass().getName().equals(ext.getClass().getName()))) {
							return true;
						}
					} else {
						return true;
					}
				}
			}
		} else {
			for (Object s : (Object[]) source) {
				if (s.toString().equals(ext.toString())) {
					if (strict) {
						if ((s.getClass().getName().equals(ext.getClass().getName()))) {
							return true;
						}
					} else {
						return true;
					}
				}
			}
		}
		return false;
	}

	
	public static String urlEncode(String s) {
		return urlEncode(s, JavaCenterHome.JCH_CHARSET);
	}

	
	public static String urlEncode(String s, String enc) {
		if (!empty(s)) {
			try {
				return URLEncoder.encode(s, enc);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return s;
	}

	
	public static String urlDecode(String s) {
		return urlDecode(s, JavaCenterHome.JCH_CHARSET);
	}

	
	public static String urlDecode(String s, String enc) {
		if (!empty(s)) {
			try {
				return URLDecoder.decode(s, enc);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return s;
	}

	
	public static int rand(int max) {
		return rand(0, max);
	}

	
	public static int rand(int min, int max) {
		if (min < max) {
			return random.nextInt(max - min + 1) + min;
		} else {
			return min;
		}
	}

	
	public static String md5(String arg0) {
		return Md5Util.encode(arg0);
	}

	
	@SuppressWarnings("unchecked")
	public static String implode(Object data, String separator) {
		if (data == null) {
			return "";
		}
		StringBuffer out = new StringBuffer();
		if (data instanceof Object[]) {
			boolean flag = false;
			for (Object obj : (Object[]) data) {
				if (flag) {
					out.append(separator);
				} else {
					flag = true;
				}
				out.append(obj);
			}
		} else if (data instanceof Map) {
			Map temp = (Map) data;
			Set<Object> keys = temp.keySet();
			boolean flag = false;
			for (Object key : keys) {
				if (flag) {
					out.append(separator);
				} else {
					flag = true;
				}
				out.append(temp.get(key));
			}
		} else if (data instanceof Collection) {
			boolean flag = false;
			for (Object obj : (Collection) data) {
				if (flag) {
					out.append(separator);
				} else {
					flag = true;
				}
				out.append(obj);
			}
		} else {
			return data.toString();
		}
		return out.toString();
	}

	
	public static String sImplode(Object ids) {
		return "'" + implode(ids, "','") + "'";
	}

	
	public static int range(Object value, int max, int min) {
		if (value instanceof String) {
			return Math.min(max, Math.max(intval((String) value), min));
		} else {
			return Math.min(max, Math.max((Integer) value, min));
		}
	}

	
	public static int intval(String s) {
		return intval(s, 10);
	}

	
	public static int intval(String s, int radix) {
		if (s == null || s.length() == 0) {
			return 0;
		}
		if (radix == 0) {
			radix = 10;
		} else if (radix < Character.MIN_RADIX) {
			return 0;
		} else if (radix > Character.MAX_RADIX) {
			return 0;
		}
		int result = 0;
		int i = 0, max = s.length();
		int limit;
		int multmin;
		int digit;
		boolean negative = false;
		if (s.charAt(0) == '-') {
			negative = true;
			limit = Integer.MIN_VALUE;
			i++;
		} else {
			limit = -Integer.MAX_VALUE;
		}
		if (i < max) {
			digit = Character.digit(s.charAt(i++), radix);
			if (digit < 0) {
				return 0;
			} else {
				result = -digit;
			}
		}
		multmin = limit / radix;
		while (i < max) {
			digit = Character.digit(s.charAt(i++), radix);
			if (digit < 0) {
				break;
			}
			if (result < multmin) {
				result = limit;
				break;
			}
			result *= radix;
			if (result < limit + digit) {
				result = limit;
				break;
			}
			result -= digit;
		}
		if (negative) {
			if (i > 1) {
				return result;
			} else {
				return 0;
			}
		} else {
			return -result;
		}
	}

	
	public static Map<String, String[]> getTimeZoneIDs() {
		return timeZoneIDs;
	}

	
	public static int time() {
		return (int) (System.currentTimeMillis() / 1000);
	}

	public static SimpleDateFormat getSimpleDateFormat(String format, String timeoffset) {
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
		sdf.setTimeZone(TimeZone.getTimeZone(timeZoneIDs.get(timeoffset)[0]));
		return sdf;
	}

	
	public static String gmdate(SimpleDateFormat sdf, int timestamp) {
		return sdf.format(timestamp * 1000l);
	}

	
	public static String gmdate(String format, int timestamp, String timeoffset) {
		return getSimpleDateFormat(format, timeoffset).format(timestamp * 1000l);
	}

	
	public static String sgmdate(HttpServletRequest request, String dateformat, int timestamp) {
		return sgmdate(request, dateformat, timestamp, false);
	}

	
	@SuppressWarnings("unchecked")
	public static String sgmdate(HttpServletRequest request, String dateformat, int timestamp, boolean format) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		if (timestamp == 0) {
			timestamp = (Integer) sGlobal.get("timestamp");
		}
		String timeoffset = Common.getTimeOffset(sGlobal, sConfig);
		String result = null;
		if (format) {
			int time = (Integer) sGlobal.get("timestamp") - timestamp;
			if (time > 86400) {
				result = gmdate(dateformat, timestamp, timeoffset);
			} else if (time > 3600) {
				result = (time / 3600) + Common.getMessage(request, "hour")
						+ Common.getMessage(request, "before");
			} else if (time > 60) {
				result = (time / 60) + Common.getMessage(request, "minute")
						+ Common.getMessage(request, "before");
			} else if (time > 0) {
				result = time + Common.getMessage(request, "second") + Common.getMessage(request, "before");
			} else {
				result = Common.getMessage(request, "now");
			}
		} else {
			result = gmdate(dateformat, timestamp, timeoffset);
		}
		return result;
	}

	
	public static boolean matches(String content, String regex) {
		boolean flag = false;
		try {
			flag = new Perl5Matcher().contains(content, new Perl5Compiler().compile(regex));
		} catch (MalformedPatternException e) {
			e.printStackTrace();
		}
		return flag;
	}

	
	public static String getRandStr(int length, boolean isOnlyNum) {
		int size = isOnlyNum ? 10 : 62;
		StringBuffer hash = new StringBuffer(length);
		for (int i = 0; i < length; i++) {
			hash.append(randChars.charAt(random.nextInt(size)));
		}
		return hash.toString();
	}

	
	public static String stripTags(String content) {
		return content == null ? "" : content.replaceAll("<[\\s\\S]*?>", "");
	}

	
	public static String htmlSpecialChars(String string) {
		return htmlSpecialChars(string, 1);
	}

	
	public static String htmlSpecialChars(String text, int quotestyle) {
		if (text == null || text.equals("")) {
			return "";
		}
		StringBuffer sb = new StringBuffer(text.length() * 2);
		StringCharacterIterator iterator = new StringCharacterIterator(text);
		char character = iterator.current();
		while (character != StringCharacterIterator.DONE) {
			switch (character) {
				case '&':
					sb.append("&amp;");
					break;
				case '<':
					sb.append("&lt;");
					break;
				case '>':
					sb.append("&gt;");
					break;
				case '"':
					if (quotestyle == 1 || quotestyle == 2) {
						sb.append("&quot;");
					} else {
						sb.append(character);
					}
					break;
				case '\'':
					if (quotestyle == 2) {
						sb.append("&#039;");
					} else {
						sb.append(character);
					}
					break;
				default:
					sb.append(character);
					break;
			}
			character = iterator.next();
		}
		return sb.toString();
	}

	
	public static String addSlashes(String text) {
		if (text == null || text.equals("")) {
			return "";
		}
		StringBuffer sb = new StringBuffer(text.length() * 2);
		StringCharacterIterator iterator = new StringCharacterIterator(text);
		char character = iterator.current();
		while (character != StringCharacterIterator.DONE) {
			switch (character) {
				case '\'':
				case '"':
				case '\\':
					sb.append("\\");
				default:
					sb.append(character);
					break;
			}
			character = iterator.next();
		}
		return sb.toString();
	}

	
	public static String addCSlashes(String text, char[] characters) {
		if (text == null || text.equals("")) {
			return "";
		}
		StringBuffer sb = new StringBuffer(text.length() * 2);
		StringCharacterIterator iterator = new StringCharacterIterator(text);
		char character = iterator.current();
		while (character != StringCharacterIterator.DONE) {
			for (char c : characters) {
				if (character == c) {
					sb.append("\\");
					break;
				}
			}
			sb.append(character);
			character = iterator.next();
		}
		return sb.toString();
	}

	
	public static String stripSlashes(String text) {
		if (text == null || text.equals("")) {
			return "";
		}
		StringBuffer sb = new StringBuffer(text.length());
		StringCharacterIterator iterator = new StringCharacterIterator(text);
		char character = iterator.current();
		while (character != StringCharacterIterator.DONE) {
			switch (character) {
				case '\'':
					sb.append("'");
					break;
				case '"':
					sb.append('"');
					break;
				case '\\':
					sb.append(iterator.next());
					break;
				default:
					sb.append(character);
					break;
			}
			character = iterator.next();
		}
		return sb.toString();
	}

	
	public static String stripCSlashes(String text) {
		if (text == null || text.equals("")) {
			return "";
		}
		StringBuffer sb = new StringBuffer(text.length());
		StringCharacterIterator iterator = new StringCharacterIterator(text);
		char character = iterator.current();
		boolean flag = true;
		while (character != StringCharacterIterator.DONE) {
			if (character == '\\' && flag) {
				flag = false;
			} else {
				flag = true;
				sb.append(character);
			}
			character = iterator.next();
		}
		return sb.toString();
	}

	
	@SuppressWarnings("unchecked")
	public static Object sAddSlashes(Object obj) {
		if (obj instanceof String) {
			return addSlashes((String) obj);
		} else if (obj instanceof Map) {
			Map temp = (Map) obj;
			Set<Object> keys = temp.keySet();
			for (Object key : keys) {
				temp.put(key, sAddSlashes(temp.get(key)));
			}
			return temp;
		} else if (obj instanceof List) {
			List temp = new ArrayList();
			for (Object str : (List) obj) {
				temp.add(sAddSlashes(str));
			}
			return temp;
		} else if (obj instanceof Object[]) {
			Object[] temp = (Object[]) obj;
			for (int i = 0; i < temp.length; i++) {
				temp[i] = sAddSlashes(temp[i]);
			}
			return temp;
		} else {
			return obj;
		}
	}

	
	@SuppressWarnings("unchecked")
	public static Object sStripSlashes(Object obj) {
		if (obj instanceof String) {
			return stripSlashes((String) obj);
		} else if (obj instanceof Map) {
			Map temp = (Map) obj;
			Set<Object> keys = temp.keySet();
			for (Object key : keys) {
				temp.put(key, sStripSlashes(temp.get(key)));
			}
			return temp;
		} else if (obj instanceof List) {
			List temp = new ArrayList();
			for (Object str : (List) obj) {
				temp.add(sStripSlashes(str));
			}
			return temp;
		} else if (obj instanceof Set) {
			Set temp = new HashSet();
			for (Object str : (Set) obj) {
				temp.add(sStripSlashes(str));
			}
			return temp;
		} else if (obj instanceof Object[]) {
			Object[] temp = (Object[]) obj;
			for (int i = 0; i < temp.length; i++) {
				temp[i] = sStripSlashes(temp[i]);
			}
			return temp;
		} else {
			return obj;
		}
	}

	
	@SuppressWarnings("unchecked")
	public static Object sHtmlSpecialChars(Object obj) {
		if (obj instanceof String) {
			return htmlSpecialChars((String) obj).replaceAll(
					"&amp;((#(\\d{3,5}|x[a-fA-F0-9]{4})|[a-zA-Z][a-z0-9]{2,5});)", "&$1");
		} else if (obj instanceof Map) {
			Map temp = (Map) obj;
			Set<Object> keys = temp.keySet();
			for (Object key : keys) {
				temp.put(key, sHtmlSpecialChars(temp.get(key)));
			}
			return temp;
		} else if (obj instanceof List) {
			List temp = new ArrayList();
			for (Object str : (List) obj) {
				temp.add(sHtmlSpecialChars(str));
			}
			return temp;
		} else if (obj instanceof Object[]) {
			Object[] temp = (Object[]) obj;
			for (int i = 0; i < temp.length; i++) {
				temp[i] = sHtmlSpecialChars(temp[i]);
			}
			return temp;
		} else {
			return obj;
		}
	}

	
	public static boolean isArray(Object obj) {
		if (obj instanceof Object[]) {
			return true;
		} else if (obj instanceof Collection) {
			return true;
		} else if (obj instanceof Map) {
			return true;
		} else {
			return false;
		}
	}

	
	public static int strlen(String text) {
		return strlen(text, JavaCenterHome.JCH_CHARSET);
	}

	
	public static int strlen(String text, String charsetName) {
		if (text == null || text.length() == 0) {
			return 0;
		}
		int length = 0;
		try {
			length = text.getBytes(charsetName).length;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return length;
	}

	
	public static String cutstr(String text, int length) {
		return cutstr(text, length, " ...");
	}

	
	public static String cutstr(String text, int length, String dot) {
		int strBLen = strlen(text);
		if (strBLen <= length) {
			return text;
		}
		int temp = 0;
		StringBuffer sb = new StringBuffer(length);
		char[] ch = text.toCharArray();
		for (char c : ch) {
			sb.append(c);
			if (c > 256) {
				temp += 2;
			} else {
				temp += 1;
			}
			if (temp >= length) {
				if (dot != null) {
					sb.append(dot);
				}
				break;
			}
		}
		return sb.toString();
	}

	
	public static boolean isNumeric(Object obj) {
		if (obj instanceof String && !obj.equals("")) {
			String temp = (String) obj;
			if (temp.endsWith("d") || temp.endsWith("f")) {
				return false;
			} else {
				try {
					Double.parseDouble(temp);
				} catch (Exception e) {
					return false;
				}
			}
			return true;
		} else if (obj instanceof Number) {
			return true;
		} else {
			return false;
		}
	}

	
	public static boolean isEmail(String email) {
		return Common.strlen(email) > 6 && email.matches("^[\\w\\-\\.]+@[\\w\\-\\.]+(\\.\\w+)+$");
	}

	
	public static String pregQuote(String text, char... delimiter) {
		StringBuffer sb = new StringBuffer(text.length() * 2);
		StringCharacterIterator iterator = new StringCharacterIterator(text);
		char character = iterator.current();
		while (character != StringCharacterIterator.DONE) {
			boolean flag = false;
			for (char c : pregChars) {
				if (character == c) {
					flag = true;
					break;
				}
			}
			if (!flag && delimiter != null) {
				for (char d : delimiter) {
					if (character == d) {
						flag = true;
						break;
					}
				}
			}
			if (flag) {
				sb.append('\\');
			}
			sb.append(character);
			character = iterator.next();
		}
		return sb.toString();
	}

	
	public static String nl2br(String text) {
		if (text == null || text.length() == 0) {
			return text;
		}
		StringBuffer sb = new StringBuffer(text.length() * 2);
		StringCharacterIterator iterator = new StringCharacterIterator(text);
		char character = iterator.current();
		while (character != StringCharacterIterator.DONE) {
			switch (character) {
				case '\r':
					sb.append("<br/>");
					sb.append(character);
					character = iterator.next();
					if (character == '\n') {
						character = iterator.next();
					}
					break;
				case '\n':
					sb.append("<br/>");
					sb.append(character);
					character = iterator.next();
					if (character == '\r') {
						sb.append(character);
						character = iterator.next();
					}
					break;
				default:
					sb.append(character);
					character = iterator.next();
					break;
			}
		}
		return sb.toString();
	}

	
	public static String sprintf(String format, double number) {
		return new DecimalFormat(format).format(number);
	}

	
	public static String formatSize(long dataSize) {
		dataSize = Math.abs(dataSize);
		if (dataSize >= 1099511627776d) {
			return (Math.round(dataSize / 1099511627776d * 100) / 100d) + " TB";
		} else if (dataSize >= 1073741824) {
			return (Math.round(dataSize / 1073741824d * 100) / 100d) + " GB";
		} else if (dataSize >= 1048576) {
			return (Math.round(dataSize / 1048576d * 100) / 100d) + " MB";
		} else if (dataSize >= 1024) {
			return (Math.round(dataSize / 1024d * 100) / 100d) + " KB";
		} else if (dataSize > 0) {
			return dataSize + " B ";
		} else {
			return "   0 B ";
		}
	}

	
	public static String getOnlineIP(HttpServletRequest request) {
		return getOnlineIP(request, false);
	}

	
	@SuppressWarnings("unchecked")
	public static String getOnlineIP(HttpServletRequest request, boolean format) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		String onlineip = (String) sGlobal.get("onlineip");
		if (onlineip == null) {
			onlineip = request.getHeader("x-forwarded-for");
			if (Common.empty(onlineip) || "unknown".equalsIgnoreCase(onlineip)) {
				onlineip = request.getHeader("X-Real-IP");
			}
			if (Common.empty(onlineip) || "unknown".equalsIgnoreCase(onlineip)) {
				onlineip = request.getRemoteAddr();
			}
			onlineip = onlineip != null && onlineip.matches("^[\\d\\.]{7,15}$") ? onlineip : "unknown";
			sGlobal.put("onlineip", onlineip);
		}
		if (format) {
			String[] ips = onlineip.split("\\.");
			String stip = "000";
			StringBuffer temp = new StringBuffer();
			for (int i = 0; i < 3; i++) {
				int ip = 0;
				if (i < ips.length) {
					ip = intval(ips[i]);
				}
				temp.append(Common.sprintf(stip, ip));
			}
			return temp.toString();
		} else {
			return onlineip;
		}
	}

	
	public static String authCode(String string, String operation, String key, int expiry) {
		long currentTime = System.currentTimeMillis();
		int time = (int) (currentTime / 1000);
		try {
			int ckey_length = 4;

			key = md5((key == null ? JavaCenterHome.jchConfig.get("JC_KEY") : key));
			String keya = md5(key.substring(0, 16));
			String keyb = md5(key.substring(16, 32));
			String keyc = ckey_length > 0 ? ("DECODE".equals(operation) ? string.substring(0, ckey_length)
					: md5(String.valueOf(currentTime)).substring(32 - ckey_length)) : "";

			String cryptkey = keya + md5(keya + keyc);
			int key_length = cryptkey.length();

			string = "DECODE".equals(operation) ? Base64.decode(string.substring(ckey_length),
					JavaCenterHome.JCH_CHARSET) : (expiry > 0 ? expiry + time : "0000000000")
					+ md5(string + keyb).substring(0, 16) + string;
			int string_length = string.length();

			StringBuffer result = new StringBuffer(string_length);
			int range = 128;

			int[] rndkey = new int[range];
			for (int i = 0; i < range; i++) {
				rndkey[i] = cryptkey.charAt(i % key_length);
			}
			int tmp;
			int[] box = new int[range];
			for (int i = 0; i < range; i++) {
				box[i] = i;
			}
			for (int i = 0, j = 0; i < range; i++) {
				j = (j + box[i] + rndkey[i]) % range;
				tmp = box[i];
				box[i] = box[j];
				box[j] = tmp;
			}
			for (int a = 0, i = 0, j = 0; i < string_length; i++) {
				a = (a + 1) % range;
				j = (j + box[a]) % range;
				tmp = box[a];
				box[a] = box[j];
				box[j] = tmp;
				result.append((char) ((int) string.charAt(i) ^ (box[(box[a] + box[j]) % range])));
			}

			if ("DECODE".equals(operation)) {
				int resulttime = Common.intval(result.substring(0, 10));
				if ((resulttime == 0 || resulttime - time > 0)
						&& result.substring(10, 26).equals(md5(result.substring(26) + keyb).substring(0, 16))) {
					return result.substring(26);
				} else {
					return "";
				}
			} else {
				return keyc
						+ (Base64.encode(result.toString(), JavaCenterHome.JCH_CHARSET)).replaceAll("=", "");
			}
		} catch (Exception e) {
			return "";
		}
	}

	
	public static boolean censoruser(String content, String censoruser) {
		if (content != null && content.length() > 0) {
			String censorexp = censoruser.replaceAll("\\*", ".*");
			censorexp = censorexp.replaceAll("(\r\n|\r|\n)", "|");
			censorexp = censorexp.replaceAll("\\s", "");
			censorexp = "^(" + censorexp + ")";
			String guestexp = "\\xA1\\xA1|^Guest|^\\xD3\\xCE\\xBF\\xCD|\\xB9\\x43\\xAB\\xC8";
			if (Common.matches(content, "^\\s*$|^c:\\con\\con$|[%,\\\\'\\*\"\\s\\t\\<\\>\\&]|" + guestexp)
					|| (censoruser.length() > 0 && Common.matches(content, censorexp))) {
				return true;
			}
		}
		return false;
	}

	
	public static String getSiteUrl(HttpServletRequest request) {
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Object siteAllURL = null;
		if (sConfig != null) {
			siteAllURL = sConfig.get("siteallurl");
		}
		if (Common.empty(siteAllURL)) {
			int port = request.getServerPort();
			return request.getScheme() + "://" + request.getServerName() + (port == 80 ? "" : ":" + port)
					+ request.getContextPath() + "/";
		} else {
			return siteAllURL.toString();
		}
	}

	
	@SuppressWarnings("unchecked")
	public static String checkClose(HttpServletRequest request, HttpServletResponse response, int supe_uid) {
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		if ((Integer) sConfig.get("close") > 0 && !ckFounder(supe_uid)
				&& !checkPerm(request, response, "closeignore")) {
			String closereason = (String) sConfig.get("closereason");
			if (empty(closereason)) {
				return "site_temporarily_closed";
			} else {
				return closereason;
			}
		}
		String customerIP = getOnlineIP(request);
		if ((!ipAccess(customerIP, sConfig.get("ipaccess")) || ipBanned(customerIP, sConfig.get("ipbanned")))
				&& !ckFounder(supe_uid) && !checkPerm(request, response, "closeignore")) {
			return "ip_is_not_allowed_to_visit";
		}
		return null;
	}

	
	@SuppressWarnings("unchecked")
	public static boolean ckFounder(int uid) {
		Map<String, String> jchConfig = JavaCenterHome.jchConfig;
		String founder = jchConfig.get("founder");
		if (uid > 0 && !empty(founder)) {
			return in_array(founder.split(","), uid);
		} else {
			return false;
		}
	}

	
	@SuppressWarnings("unchecked")
	public static boolean checkPerm(HttpServletRequest request, HttpServletResponse response, String permType) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		return !empty(checkPerm(request, response, sGlobal, permType));
	}

	
	@SuppressWarnings("unchecked")
	public static Object checkPerm(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> sGlobal, String permType) {
		if ("admin".equals(permType)) {
			permType = "manageconfig";
		}
		String var = "checkperm_" + permType;
		Object perm = sGlobal.get(var);
		if (perm == null) {
			int supe_uid = (Integer) sGlobal.get("supe_uid");
			if (supe_uid == 0) {
				perm = 0;
			} else {
				Map<String, Object> member = (Map<String, Object>) sGlobal.get("member");
				if (member == null) {
					member = getMember(request);
				}

				int groupid = (Integer) member.get("groupid");
				int gid = getGroupid(request, response, (Integer) member.get("experience"), groupid);
				Map<String, Object> usergroup = getCacheDate(request, response, "/data/cache/usergroup_"
						+ gid + ".jsp", "usergroup" + gid);
				if (gid != groupid) {
					Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
					if (space != null) {
						space.put("groupid", gid);
					}
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
							+ " SET groupid = " + gid + " WHERE uid = " + supe_uid);
					Object magicawardOb = usergroup.get("magicaward");
					if (!Common.empty(magicawardOb)) {
						List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
								+ JavaCenterHome.getTableName("magicinlog") + " WHERE uid='" + supe_uid
								+ "' AND type='3' AND fromid='" + gid + "'");
						Map<String, Object> value = query.size() > 0 ? query.get(0) : null;
						if (empty(value)) {
							Map<String, Map<String, Object>> magicaward = (Map<String, Map<String, Object>>) magicawardOb;
							List<String> inserts_magicinlog = new ArrayList<String>();
							List<String> note_award = new ArrayList<String>();
							List<String> inserts_mymagic = new ArrayList<String>();
							List<String> ids = new ArrayList<String>();
							for (Entry<String, Map<String, Object>> entry : magicaward.entrySet()) {
								value = entry.getValue();
								ids.add((String) value.get("mid"));
							}

							query = dataBaseService.executeQuery("SELECT * FROM "
									+ JavaCenterHome.getTableName("usermagic") + " WHERE uid='" + supe_uid
									+ "' AND mid IN (" + sImplode(ids) + ")");
							Map<String, Map<String, Object>> mymagics = new HashMap<String, Map<String, Object>>();
							for (int i = 0; i < query.size(); i++) {
								value = query.get(i);
								mymagics.put((String) value.get("mid"), value);
							}
							Map<String, Object> globalMagic = getCacheDate(request, response,
									"/data/cache/cache_magic.jsp", "globalMagic");
							StringBuilder builder = new StringBuilder();
							String mid;
							int num;
							String supe_username = (String) sGlobal.get("supe_username");
							int timestamp = (Integer) sGlobal.get("timestamp");
							Map<String, Object> userMaigc;
							for (Entry<String, Map<String, Object>> entry : magicaward.entrySet()) {
								value = entry.getValue();
								mid = (String) value.get("mid");
								num = (Integer) value.get("num");
								builder.append("('");
								builder.append(supe_uid);
								builder.append("', '");
								builder.append(supe_username);
								builder.append("', '");
								builder.append(mid);
								builder.append("', '");
								builder.append(num);
								builder.append("', '3', '");
								builder.append(gid);
								builder.append("', '0', '");
								builder.append(timestamp);
								builder.append("')");
								inserts_magicinlog.add(builder.toString());

								builder.delete(0, builder.length());

								builder.append("<a href=\"cp.jsp?ac=magic&view=me&mid=");
								builder.append(value.get("mid"));
								builder.append("\" target=\"_blank\">");
								builder.append(globalMagic.get(mid));
								builder.append("</a>(");
								builder.append(num);
								builder.append(getMessage(request, "magicunit"));
								builder.append(")");
								note_award.add(builder.toString());

								builder.delete(0, builder.length());

								builder.append("('");
								builder.append(supe_uid);
								builder.append("', '");
								builder.append(supe_username);
								builder.append("', '");
								builder.append(mid);
								builder.append("', '");
								userMaigc = mymagics.get(mid);
								builder.append((num + (userMaigc == null ? 0 : (Integer) (userMaigc
										.get("count")))));
								builder.append("')");
								inserts_mymagic.add(builder.toString());

								builder.delete(0, builder.length());
							}
							dataBaseService.execute("REPLACE INTO "
									+ JavaCenterHome.getTableName("usermagic")
									+ "(uid, username, mid, count) VALUES " + implode(inserts_mymagic, ","));
							dataBaseService.execute("INSERT INTO "
									+ JavaCenterHome.getTableName("magicinlog")
									+ "(uid, username, mid, count, type, fromid, credit, dateline) VALUES "
									+ implode(inserts_magicinlog, ","));
							String noteAward = Common.implode(note_award, "; ");
							CpService cpService = (CpService) BeanFactory.getBean("cpService");
							sGlobal.put("supe_uid", 0);
							Map<String, Object> sConfig = (Map<String, Object>) request
									.getAttribute("sConfig");
							cpService.addNotification(request, sGlobal, sConfig, supe_uid, "", getMessage(
									request, "cp_upgrade_magic_award", (String) usergroup.get("grouptitle"),
									noteAward), false);
							sGlobal.put("supe_uid", supe_uid);
						}
					}
				}
				perm = usergroup.get(permType);
				if (permType.startsWith("manage") && empty(perm)) {
					perm = usergroup.get("manageconfig");
					if (empty(perm)) {
						perm = ckFounder(supe_uid) ? 1 : 0;
					}
				}
			}
			sGlobal.put(var, perm);
		}
		return perm;
	}

	
	@SuppressWarnings("unchecked")
	public static MessageVO ckSpaceLog(HttpServletRequest request) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		if (supe_uid > 0) {
			List<Map<String, Object>> spaceLogs = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("spacelog") + " WHERE uid=" + supe_uid);
			if (spaceLogs.size() > 0) {
				Map<String, Object> spaceLog = spaceLogs.get(0);
				int expiration = (Integer) spaceLog.get("expiration");
				if (expiration > 0 && expiration <= (Integer) sGlobal.get("timestamp")) {
					dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("spacelog")
							+ " WHERE uid=" + supe_uid);
				}
				MessageVO msgVO = new MessageVO();
				String expir = Common.sgmdate(request, "yyyy-MM-dd HH:mm", expiration);
				msgVO.setMsgKey("no_authority_expiration" + (expiration > 0 ? "_date" : ""));
				msgVO.setArgs(expir);
				return msgVO;
			}
		}
		return null;
	}

	
	public static int getGroupid(HttpServletRequest request, HttpServletResponse response, int experience,
			int gid) {
		boolean needFind = false;
		if (gid > 0) {
			Map<String, Object> usergroup = getCacheDate(request, response, "/data/cache/usergroup_" + gid
					+ ".jsp", "usergroup" + gid);
			if ((Integer) usergroup.get("system") == 0) {
				if ((Integer) usergroup.get("exphigher") < experience
						|| (Integer) usergroup.get("explower") > experience) {
					needFind = true;
				}
			}
		} else {
			needFind = true;
		}
		if (needFind) {
			List<Map<String, Object>> usergroups = dataBaseService.executeQuery("SELECT gid FROM "
					+ JavaCenterHome.getTableName("usergroup") + " WHERE explower<=" + experience
					+ " AND system=0 ORDER BY explower DESC LIMIT 1");
			if (usergroups.size() > 0) {
				gid = (Integer) usergroups.get(0).get("gid");
			}
		}
		return gid;
	}

	
	@SuppressWarnings("unchecked")
	public static Map getCacheDate(HttpServletRequest request, HttpServletResponse response, String path,
			String var) {
		Map cacheValue = (Map) request.getAttribute(var);
		if (cacheValue == null) {
			try {
				String jchRoot = JavaCenterHome.jchRoot;
				File configFile = new File(jchRoot + path);
				if (!configFile.exists()) {
					CacheService cacheService = (CacheService) BeanFactory.getBean("cacheService");
					cacheService.updateCache();
				}
				request.getRequestDispatcher(path).include(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
			cacheValue = (Map) request.getAttribute(var);
		}
		if (cacheValue == null) {
			cacheValue = new HashMap();
		}
		return cacheValue;
	}

	
	public static Map<String, Object> getMember(HttpServletRequest request) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		int uid = space.get("uid") != null ? (Integer) space.get("uid") : 0;
		if (supe_uid > 0 && empty(sGlobal.get("member"))) {
			if (supe_uid != uid) {
				space = getSpace(request, sGlobal, sConfig, supe_uid);
			}
			sGlobal.put("member", space);
		}
		return space;
	}
	public static Map<String, Object> getSpace(HttpServletRequest request, Map<String, Object> sGlobal,
			Map<String, Object> sConfig, Object key) {
		return getSpace(request, sGlobal, sConfig, key, "uid", false);
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getSpace(HttpServletRequest request, Map<String, Object> sGlobal,
			Map<String, Object> sConfig, Object key, String indexType, boolean autoOpen) {
		String var = "space_" + key + "_" + indexType;
		Map<String, Object> space = (Map<String, Object>) sGlobal.get(var);
		if (space == null) {
			List<Map<String, Object>> spaces = dataBaseService.executeQuery("SELECT sf.*, s.* FROM "
					+ JavaCenterHome.getTableName("space") + " s LEFT JOIN "
					+ JavaCenterHome.getTableName("spacefield") + " sf ON sf.uid=s.uid WHERE s." + indexType
					+ "='" + key + "'");
			if (spaces.size() > 0) {
				space = spaces.get(0);
				int uid = (Integer) space.get("uid");
				String name = (String) space.get("name");
				String username = (String) space.get("username");
				Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
				sNames.put(uid, (Integer) sConfig.get("realname") > 0 && name.length() > 0
						&& (Integer) space.get("namestatus") > 0 ? name : username);
				boolean self = uid == (Integer) sGlobal.get("supe_uid") ? true : false;
				space.put("self", self);

				String friend = (String) space.get("friend");
				if (empty(friend)) {
					if ((Integer) space.get("friendnum") > 0) {
						StringBuffer friendTemp = new StringBuffer();
						List<Map<String, Object>> values = dataBaseService.executeQuery("SELECT fuid FROM "
								+ JavaCenterHome.getTableName("friend") + " WHERE uid=" + uid
								+ " AND status=1");
						int size = values.size();
						String[] friends = new String[size];
						for (int i = 0; i < size; i++) {
							Map<String, Object> value = values.get(i);
							String fuid = value.get("fuid").toString();
							friends[i] = fuid;
							if (i > 0) {
								friendTemp.append(',');
							}
							friendTemp.append(fuid);
						}
						space.put("friends", friends);
						space.put("friend", friendTemp.toString());
					}
				} else {
					space.put("friends", friend.split(","));
				}

				space.put("username", Common.addSlashes(username));
				space.put("name", Common.addSlashes(name));
				String privacy = (String) space.get("privacy");
				space.put("privacy", empty(privacy) ? sConfig.get("privacy") : Serializer.unserialize(
						privacy, false));
				int allnotenum = 0;
				for (String note : notes) {
					allnotenum += (Integer) space.get(note);
				}
				space.put("allnotenum", allnotenum);

				if (self) {
					sGlobal.put("member", space);
				}
			}
			sGlobal.put(var, space);
		}
		return space;
	}

	
	public static void showMySQLMessage(HttpServletResponse response, String message, String sql,
			SQLException e) {
		String dbError = e.getMessage();
		int dbErrno = e.getErrorCode();
		try {
			PrintWriter out = response.getWriter();
			out
					.write("<div style=\"position:absolute;font-size:11px;font-family:verdana,arial;background:#BFBFBF;padding:0.5em;\">");
			out.write("<b>MySQL Error</b><br>");
			out.write("<b>Message</b>: <font color=\"red\">" + message + "</font><br>");
			if (sql != null) {
				out.write("<b>SQL</b>: " + sql + "<br>");
			}
			out.write("<b>Error</b>: <font color=\"red\">" + dbError + "<br></font>");
			out.write("<b>Errno.</b>: <font color=\"red\">" + dbErrno + "<br></font>");
			out.write("</div>");
			out.flush();
			out.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	
	@SuppressWarnings("unchecked")
	public static String template(Map<String, Object> sConfig, Map<String, Object> sGlobal, String pageName) {
		String tpl = null;
		if (empty(sGlobal.get("mobile"))) {
			if (pageName.startsWith("/")) {
				tpl = pageName;
			} else {
				tpl = "/template/" + sConfig.get("template") + "/" + pageName;
			}
			File file = new File(JavaCenterHome.jchRoot + tpl);
			if (!file.exists()) {
				tpl = "/template/default/" + pageName;
			}
		} else {
			tpl = "/api/mobile/tpl_" + pageName;
		}
		return tpl;
	}

	public static void main(String[] args) {
	}

	
	public static String getCount(String tableName, Map<String, Object> whereArr, String get) {
		StringBuffer whereSql = new StringBuffer();
		if (empty(get)) {
			get = "COUNT(*)";
		}
		if (empty(whereArr)) {
			whereSql.append("1");
		} else {
			String key = null, mod = "";
			for (Iterator<String> it = whereArr.keySet().iterator(); it.hasNext();) {
				key = it.next();
				whereSql.append(mod + "`" + key + "`='" + whereArr.get(key) + "'");
				mod = " AND ";
			}
		}
		return dataBaseService.findFirst("SELECT " + get + " FROM " + JavaCenterHome.getTableName(tableName)
				+ " WHERE " + whereSql + " LIMIT 1", 1);
	}

	@SuppressWarnings("unchecked")
	public static String myCheckUpdate(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		if (!Common.empty(sConfig.get("my_status")) && Common.empty(sConfig.get("my_closecheckupdate"))
				&& checkPerm(request, response, "admin")) {
			String sid = (String) sConfig.get("my_siteid");
			int ts = (Integer) sGlobal.get("timestamp");
			String key = md5(sid + ts + sConfig.get("my_sitekey"));
			return "<script type=\"text/javascript\" src=\"http://notice.jsprun.com/notice?sId=" + sid
					+ "&ts=" + ts + "&key=" + key + "\" charset=\"UTF-8\"></script>";
		}
		return null;
	}

	
	public static void setData(String var, Object dataValue, boolean clean) {
		if (clean) {
			dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("data")
					+ " WHERE var='" + var + "'");
		} else {
			if (dataValue instanceof Map || dataValue instanceof List) {
				dataValue = Serializer.serialize(sStripSlashes(dataValue));
			}
			dataBaseService.executeUpdate("REPLACE INTO " + JavaCenterHome.getTableName("data")
					+ " (var,datavalue,dateline) VALUES ('" + var + "','" + addSlashes((String) dataValue)
					+ "','" + time() + "')");
		}
	}

	
	public static String getData(String var) {
		return (String) getData(var, false);
	}

	
	public static Object getData(String var, boolean isArray) {
		List<Map<String, Object>> values = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("data") + " WHERE var='" + var + "' LIMIT 1");
		if (values.size() > 0) {
			Map<String, Object> value = values.get(0);
			return isArray ? value : value.get("datavalue");
		}
		return null;
	}

	
	public static boolean ipAccess(String ip, Object ipAccess) {
		return empty(ipAccess) ? true : ip.matches("^("
				+ pregQuote(String.valueOf(ipAccess), '/').replaceAll("\r\n", "|").replaceAll(" ", "")
				+ ").*");
	}

	
	public static boolean ipBanned(String ip, Object ipBanned) {
		return empty(ipBanned) ? false : ip.matches("^("
				+ pregQuote(String.valueOf(ipBanned), '/').replaceAll("\r\n", "|").replaceAll(" ", "")
				+ ").*");
	}

	
	public static Object[] reNum(Map<Integer, Integer> array) {
		Map<Integer, List<Integer>> newnums = new HashMap<Integer, List<Integer>>();
		List<Integer> nums = new ArrayList<Integer>();
		Set<Integer> keys = array.keySet();
		for (Integer key : keys) {
			int num = array.get(key);
			List<Integer> newnum = newnums.get(num);
			if (newnum == null) {
				newnum = new ArrayList<Integer>();
				newnums.put(num, newnum);
			}
			newnum.add(key);
			nums.add(num);
		}
		Object[] arrayObject = new Object[] {nums, newnums};
		return arrayObject;
	}

	
	public static String stripSearchKey(String string) {
		if (string == null || "".equals(string)) {
			return "";
		}
		string = string.trim();
		string = Common.addSlashes(string).replace("*", "%");
		string = string.replace("_", "\\_");
		return string;
	}

	
	@SuppressWarnings("unchecked")
	public static String ckStart(int start, int perPage, int maxPage) {
		int maxStart = perPage * maxPage;
		if (start < 0 || (maxStart > 0 && start >= maxStart)) {
			return "length_is_not_within_the_scope_of";
		}
		return null;
	}

	
	public static int strToTime(String string, String timeoffset) {
		return strToTime(string, timeoffset, "yyyy-MM-dd");
	}

	
	public static int strToTime(String string, String timeoffset, String format) {
		if (string == null || string.length() == 0) {
			return 0;
		}
		SimpleDateFormat sdf = getSimpleDateFormat(format, timeoffset);
		try {
			Date ndate = sdf.parse(string);
			if (sdf.format(ndate).equals(string)) {
				return (int) (ndate.getTime() / 1000);
			} else {
				return 0;
			}
		} catch (Exception e) {
			return 0;
		}
	}

	
	public static String pic_get(Map<String, Object> sConfig, String filePath, int thumb, int remote,
			boolean return_thumb) {
		String url = null;
		if (empty(filePath)) {
			url = "image/nopic.gif";
		} else {
			url = filePath;
			if (return_thumb && thumb > 0) {
				url += ".thumb.jpg";
			}
			if (remote > 0) {
				url = sConfig.get("ftpurl") + url;
			} else {
				Map<String, String> jchConfig = JavaCenterHome.jchConfig;
				url = jchConfig.get("attachUrl") + url;
			}
		}
		return url;
	}

	
	@SuppressWarnings("unchecked")
	public static String pic_cover_get(Map<String, Object> sConfig, String pic, int picFlag) {
		String url = null;
		if (empty(pic)) {
			url = "image/nopic.gif";
		} else {
			if (picFlag == 1) {
				Map<String, String> jchConfig = JavaCenterHome.jchConfig;
				url = jchConfig.get("attachUrl") + pic;
			} else if (picFlag == 2) {
				url = sConfig.get("ftpurl") + pic;
			} else {
				url = pic;
			}
		}
		return url;
	}

	
	public static Map<String, Integer> getReward(String action, boolean update, int uid, String needle,
			boolean setcookie, HttpServletRequest request, HttpServletResponse response) {
		Map<String, Integer> reward = new HashMap<String, Integer>();
		reward.put("credit", 0);
		reward.put("experience", 0);
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		int timestamp = (Integer) sGlobal.get("timestamp");
		Map<String, Map<String, Object>> creditRule = Common.getCacheDate(request, response,
				"/data/cache/cache_creditrule.jsp", "globalCreditrule");
		Map<String, Object> rule = creditRule.get(action);
		int credit = (Integer) rule.get("credit");
		int experience = (Integer) rule.get("experience");
		if (credit != 0 || experience != 0) {
			uid = uid > 0 ? uid : supe_uid;
			int norepeat = (Integer) rule.get("norepeat");
			if ((Integer) rule.get("rewardtype") > 0) {
				List<Map<String, Object>> creditlogList = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("creditlog") + " WHERE uid=" + uid + " AND rid="
						+ rule.get("rid"));
				int clid = 0;
				int cycletype = (Integer) rule.get("cycletype");
				if (creditlogList.size() == 0) {
					reward.put("credit", credit);
					reward.put("experience", experience);
					Map<String, Object> setarr = new HashMap<String, Object>();
					setarr.put("uid", uid);
					setarr.put("rid", rule.get("rid"));
					setarr.put("total", 1);
					setarr.put("cyclenum", 1);
					setarr.put("credit", credit);
					setarr.put("experience", experience);
					setarr.put("dateline", timestamp);
					String info = "", user = "", app = "";
					switch (norepeat) {
						case 1:
							info = needle;
							break;
						case 2:
							user = needle;
							break;
						case 3:
							app = needle;
							break;
					}
					setarr.put("info", info);
					setarr.put("user", user);
					setarr.put("app", app);

					if (in_array(new Integer[] {2, 3}, cycletype)) {
						setarr.put("starttime", timestamp);
					}
					Set<String> keys = setarr.keySet();
					StringBuffer insertkeysql = new StringBuffer();
					StringBuffer insertvaluesql = new StringBuffer();
					for (String key : keys) {
						insertkeysql.append(key + ",");
						insertvaluesql.append("'" + setarr.get(key) + "',");
					}
					String sql = "INSERT  INTO " + JavaCenterHome.getTableName("creditlog") + " ("
							+ insertkeysql.substring(0, insertkeysql.length() - 1) + ") VALUES ("
							+ insertvaluesql.substring(0, insertvaluesql.length() - 1) + ")";
					clid = dataBaseService.insert(sql);
				} else {
					boolean newcycle = false;
					Map<String, String> setarr = new HashMap<String, String>();
					Map<String, Object> creditlog = creditlogList.get(0);
					clid = (Integer) creditlog.get("clid");
					Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
					String timeoffset = Common.getTimeOffset(sGlobal, sConfig);
					int rewardnum = (Integer) rule.get("rewardnum");
					switch (cycletype) {
						case 0:
							break;
						case 1:
						case 4:
							String sql = "cyclenum+1";
							if (cycletype == 1) {
								int today = strToTime(gmdate("yyyy-MM-dd", timestamp, timeoffset),
										timeoffset, "yyyy-MM-dd");
								if ((Integer) creditlog.get("dateline") < today && rewardnum > 0) {
									creditlog.put("cyclenum", 0);
									sql = "1";
									newcycle = true;
								}
							}
							if (rewardnum == 0 || (Integer) creditlog.get("cyclenum") < rewardnum) {
								if (norepeat > 0) {
									if (!newcycle && checkCheating(creditlog, needle, norepeat)) {
										return reward;
									}
								}
								reward.put("credit", credit);
								reward.put("experience", experience);
								setarr.put("cyclenum", "cyclenum=" + sql);
								setarr.put("total", "total=total+1");
								setarr.put("dateline", "dateline=" + timestamp);
								setarr.put("credit", "credit=" + credit);
								setarr.put("experience", "experience=" + experience);
							}
							break;
						case 2:
						case 3:
							int nextcycle = 0;
							int starttime = (Integer) creditlog.get("starttime");
							if (starttime > 0) {
								int cycletime = (Integer) rule.get("cycletime");
								if (cycletype == 2) {
									int start = strToTime(
											gmdate("yyyy-MM-dd HH:00:00", starttime, timeoffset), timeoffset,
											"yyyy-MM-dd HH:00:00");
									nextcycle = start + cycletime * 3600;
								} else {
									nextcycle = starttime + cycletime * 60;
								}
							}
							if (timestamp <= nextcycle && (Integer) creditlog.get("cyclenum") < rewardnum) {
								if (norepeat > 0) {
									if (!newcycle && checkCheating(creditlog, needle, norepeat)) {
										return reward;
									}
									reward.put("credit", credit);
									reward.put("experience", experience);

									setarr.put("cyclenum", "cyclenum=+1");
									setarr.put("total", "total=total+1");
									setarr.put("dateline", "dateline=" + timestamp);
									setarr.put("credit", "credit=" + credit);
									setarr.put("experience", "experience=" + experience);
								}
							} else if (timestamp >= nextcycle) {
								newcycle = true;
								reward.put("credit", credit);
								reward.put("experience", experience);

								setarr.put("cyclenum", "cyclenum=1");
								setarr.put("total", "total=total+1");
								setarr.put("dateline", "dateline=" + timestamp);
								setarr.put("credit", "credit=" + credit);
								setarr.put("starttime", "starttime=" + timestamp);
								setarr.put("experience", "experience=" + experience);
							}
							break;
					}
					if (norepeat > 0 && !empty(needle)) {
						switch (norepeat) {
							case 1:
								String info = empty(creditlog.get("info")) || newcycle ? needle : creditlog
										.get("info")
										+ "," + needle;
								setarr.put("info", "info='" + info + "'");
								break;
							case 2:
								String user = empty(creditlog.get("user")) || newcycle ? needle : creditlog
										.get("user")
										+ "," + needle;
								setarr.put("user", "user='" + user + "'");
								break;
							case 3:
								String app = empty(creditlog.get("app")) || newcycle ? needle : creditlog
										.get("app")
										+ "," + needle;
								setarr.put("app", "app='" + app + "'");
								break;
						}
					}
					if (setarr.size() > 0) {
						dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("creditlog")
								+ " SET " + implode(setarr, ",") + " WHERE clid=" + creditlog.get("clid"));
					}
				}
				if (setcookie && uid == supe_uid) {
					if ((Integer) reward.get("credit") != 0 || (Integer) reward.get("experience") != 0) {
						String logstr = action + "," + clid;
						CookieHelper.setCookie(request, response, "reward_log", logstr);
						Map<String, String> sCookie = (Map<String, String>) request.getAttribute("sCookie");
						sCookie.put("reward_log", logstr);
					}
				}
			} else {
				reward.put("credit", -credit);
				reward.put("experience", -experience);
			}
			credit = reward.get("credit");
			experience = reward.get("experience");
			if (update && (credit != 0 || experience != 0)) {
				Map<String, String> setarr = new HashMap<String, String>();
				if (credit != 0) {
					setarr.put("credit", "credit=credit+" + credit);
				}
				if (experience != 0) {
					setarr.put("experience", "experience=experience+" + experience);
				}
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space") + " SET "
						+ implode(setarr, ",") + " WHERE uid=" + uid);
			}
		}
		Map<String, Integer> returnMap = new HashMap<String, Integer>();
		returnMap.put("credit", Math.abs(reward.get("credit")));
		returnMap.put("experience", Math.abs(reward.get("experience")));
		return returnMap;
	}

	
	public static boolean checkCheating(Map<String, Object> creditlog, String needle, int norepeat) {
		String var = null;
		switch (norepeat) {
			case 0:
				break;
			case 1:
				var = "info";
				break;
			case 2:
				var = "user";
				break;
			case 3:
				var = "app";
				break;
		}
		boolean repeat = false;
		if (var != null) {
			String[] values = ((String) creditlog.get(var)).split(",");
			if (in_array(values, needle)) {
				repeat = true;
			}
		}
		return repeat;
	}

	
	@SuppressWarnings("unchecked")
	public static String getColor(Map<String, Object> userGroup) {
		Object color = userGroup.get("color");
		if (empty(color)) {
			return null;
		} else {
			return " style=\"color:" + color + ";\"";
		}
	}

	
	@SuppressWarnings("unchecked")
	public static String getColor(HttpServletRequest request, HttpServletResponse response, int gid) {
		Map<Integer, Map> userGroups = Common.getCacheDate(request, response, "/data/cache/usergroup.jsp",
				"globalGroupTitle");
		Map userGroup = userGroups.get(gid);
		if (empty(userGroup)) {
			return null;
		} else {
			return getColor(userGroup);
		}
	}

	
	@SuppressWarnings("unchecked")
	public static String getIcon(Map<String, Object> userGroup) {
		Object icon = userGroup.get("icon");
		if (empty(icon)) {
			return null;
		} else {
			return " <img src=\"" + icon + "\" align=\"absmiddle\"> ";
		}
	}

	
	@SuppressWarnings("unchecked")
	public static String getIcon(HttpServletRequest request, HttpServletResponse response, int gid) {
		Map<Integer, Map> userGroups = Common.getCacheDate(request, response, "/data/cache/usergroup.jsp",
				"globalGroupTitle");
		Map userGroup = userGroups.get(gid);
		if (empty(userGroup)) {
			return null;
		} else {
			return getIcon(userGroup);
		}
	}

	
	
	public static File[] readDir(String dir, final String... extarr) {
		File supDir = new File(dir);
		if (supDir.isDirectory()) {
			if (extarr == null || extarr.length == 0) {
				return supDir.listFiles();
			} else {
				FilenameFilter filenameFilter = new FilenameFilter() {
					public boolean accept(File dir, String name) {
						int tempI = name.lastIndexOf(".");
						String postfix = null;
						if (tempI >= 0) {
							postfix = name.substring(tempI + 1);
						} else {
							postfix = name;
						}
						return in_array(extarr, postfix);
					}
				};
				return supDir.listFiles(filenameFilter);
			}
		}
		return null;
	}

	
	public static String multi(HttpServletRequest request, int num, int perPage, int currPage, int maxPage,
			String url, String ajaxDiv, String toDiv) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		StringBuffer multiPage = new StringBuffer();

		int inAjax = (Integer) sGlobal.get("inajax");
		if (empty(ajaxDiv) && inAjax > 0) {
			ajaxDiv = request.getParameter("ajaxdiv");
		}
		int page = 5;
		if (!empty(sGlobal.get("showpage"))) {
			page = (Integer) sGlobal.get("showpage");
		}
		int realPages = 1;
		if (num > perPage) {
			int offset = 2;
			realPages = (int) Math.ceil((float) num / (float) perPage);
			int pages = maxPage > 0 && maxPage < realPages ? maxPage : realPages;
			int from = 0, to = 0;
			if (page > pages) {
				from = 1;
				to = pages;
			} else {
				from = currPage - offset;
				to = from + page - 1;
				if (from < 1) {
					to = currPage + 1 - from;
					from = 1;
					if (to - from < page) {
						to = page;
					}
				} else if (to > pages) {
					from = pages - page + 1;
					to = pages;
				}
			}
			url += url.indexOf("?") == -1 ? "?" : "&";
			String urlPlus = !empty(toDiv) ? "#" + toDiv : "";
			if (currPage - offset > 1 && pages > page) {
				if (inAjax > 0) {
					multiPage.append("<a href=\"javascript:;\" onclick=\"ajaxget('");
					multiPage.append(url);
					multiPage.append("page=1&ajaxdiv=");
					multiPage.append(ajaxDiv);
					multiPage.append("','");
					multiPage.append(ajaxDiv);
					multiPage.append("')\" class=\"first\">1 ...</a>");
				} else {
					multiPage.append("<a href=\"");
					multiPage.append(url);
					multiPage.append("page=1");
					multiPage.append(urlPlus);
					multiPage.append("\" class=\"first\">1 ...</a>");
				}
			}
			if (currPage > 1) {
				if (inAjax > 0) {
					multiPage.append("<a href=\"javascript:;\" onclick=\"ajaxget('");
					multiPage.append(url);
					multiPage.append("page=");
					multiPage.append(currPage - 1);
					multiPage.append("&ajaxdiv=");
					multiPage.append(ajaxDiv);
					multiPage.append("','");
					multiPage.append(ajaxDiv);
					multiPage.append("')\" class=\"prev\">&lsaquo;&lsaquo;</a>");
				} else {
					multiPage.append("<a href=\"");
					multiPage.append(url);
					multiPage.append("page=");
					multiPage.append(currPage - 1);
					multiPage.append(urlPlus);
					multiPage.append("\" class=\"prev\">&lsaquo;&lsaquo;</a>");
				}
			}
			for (int i = from; i <= to; i++) {
				if (i == currPage) {
					multiPage.append("<strong>");
					multiPage.append(i);
					multiPage.append("</strong>");
				} else {
					if (inAjax > 0) {
						multiPage.append("<a href=\"javascript:;\" onclick=\"ajaxget('");
						multiPage.append(url);
						multiPage.append("page=");
						multiPage.append(i);
						multiPage.append("&ajaxdiv=");
						multiPage.append(ajaxDiv);
						multiPage.append("','");
						multiPage.append(ajaxDiv);
						multiPage.append("')\">");
					} else {
						multiPage.append("<a href=\"");
						multiPage.append(url);
						multiPage.append("page=");
						multiPage.append(i);
						multiPage.append(urlPlus);
						multiPage.append("\">");
					}
					multiPage.append(i);
					multiPage.append("</a>");
				}
			}
			if (currPage < pages) {
				if (inAjax > 0) {
					multiPage.append("<a href=\"javascript:;\" onclick=\"ajaxget('");
					multiPage.append(url);
					multiPage.append("page=");
					multiPage.append(currPage + 1);
					multiPage.append("&ajaxdiv=");
					multiPage.append(ajaxDiv);
					multiPage.append("','");
					multiPage.append(ajaxDiv);
					multiPage.append("')\" class=\"next\">&rsaquo;&rsaquo;</a>");
				} else {
					multiPage.append("<a href=\"");
					multiPage.append(url);
					multiPage.append("page=");
					multiPage.append(currPage + 1);
					multiPage.append(urlPlus);
					multiPage.append("\" class=\"next\">&rsaquo;&rsaquo;</a>");
				}
			}
			if (to < pages) {
				if (inAjax > 0) {
					multiPage.append("<a href=\"javascript:;\" onclick=\"ajaxget('");
					multiPage.append(url);
					multiPage.append("page=");
					multiPage.append(pages);
					multiPage.append("&ajaxdiv=");
					multiPage.append(ajaxDiv);
					multiPage.append("','");
					multiPage.append(ajaxDiv);
					multiPage.append("')\" class=\"last\">... ");
				} else {
					multiPage.append("<a href=\"");
					multiPage.append(url);
					multiPage.append("page=");
					multiPage.append(pages);
					multiPage.append(urlPlus);
					multiPage.append("\" class=\"last\">... ");
				}
				multiPage.append(realPages);
				multiPage.append("</a>");
			}
			if (multiPage.length() > 0) {
				multiPage.insert(0, "<em>&nbsp;" + num + "&nbsp;</em>");
			}
		}
		return multiPage.toString();
	}

	public static String fileext(String filePath) {
		int sl = filePath.length();
		if (sl < 2) {
			return "";
		}
		int lastPoint = filePath.lastIndexOf(".");
		if (lastPoint < 0) {
			return "";
		}
		return filePath.substring(lastPoint + 1, filePath.length());
	}

	
	public static String getStr(String string, int length, boolean in_slashes, boolean out_slashes,
			boolean censor, int bbcode, int html, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		string = Common.trim(string);

		if (in_slashes) {
			string = stripSlashes(string);
		}
		if (html < 0) {
			string = string.replaceAll("(?is)(\\<[^\\<]*\\>|\\r|\\n|\\s|\\[.+?\\])", " ");
			string = (String) sHtmlSpecialChars(string);
		} else if (html == 0) {
			string = (String) sHtmlSpecialChars(string);
		}
		if (censor) {
			Map<String, Object> globalCensor = getCacheDate(request, response,
					"/data/cache/cache_censor.jsp", "globalCensor");
			if (!empty(globalCensor.get("banned")) && matches(string, (String) globalCensor.get("banned"))) {
				throw new Exception("information_contains_the_shielding_text");
			} else if (!empty(globalCensor.get("filter"))) {
				Map<String, LinkedHashMap<Integer, String>> filter = (Map<String, LinkedHashMap<Integer, String>>) globalCensor
						.get("filter");
				LinkedHashMap<Integer, String> find = filter.get("find");
				LinkedHashMap<Integer, String> replace = filter.get("replace");
				int size = find.size();
				for (int i = 0; i < size; i++) {
					string = string.replaceAll(find.get(i), replace.get(i));
				}
			}
		}
		if (length > 0) {
			string = cutstr(string, length, "");
		}

		if (bbcode > 0) {
			string = BBCode.bbCode(string, bbcode);
		}
		if (out_slashes) {
			string = (String) sAddSlashes(string);
		}
		return string.trim();
	}

	
	public static void realname_set(Map<String, Object> sGlobal, Map<String, Object> sConfig,
			Map<Integer, String> sNames, int uid, String username, String name, int namestatus) {
		if (!empty(name)) {
			sNames.put(uid, (Integer) sConfig.get("realname") > 0 && namestatus > 0 ? name : username);
		} else if (empty(sNames.get(uid))) {
			sNames.put(uid, username);
			Map<Integer, Integer> select_realname = (Map<Integer, Integer>) sGlobal.get("select_realname");
			if (select_realname == null) {
				select_realname = new HashMap<Integer, Integer>();
			}
			select_realname.put(uid, uid);
			sGlobal.put("select_realname", select_realname);
		}
	}

	
	public static void realname_get(Map<String, Object> sGlobal, Map<String, Object> sConfig,
			Map<Integer, String> sNames, Map<String, Object> space) {
		if (empty(sGlobal.get("_realname_get")) && !empty(sConfig.get("realname"))
				&& !empty(sGlobal.get("select_realname"))) {
			sGlobal.put("_realname_get", 1);
			Map<Integer, Integer> select_realname = (Map<Integer, Integer>) sGlobal.get("select_realname");
			if (space != null && select_realname != null && select_realname.get(space.get("uid")) != null) {
				select_realname.remove(space.get("uid"));
			}
			Map<String, Object> member = (Map<String, Object>) sGlobal.get("member");
			if (member != null) {
				if (!empty(member.get("uid")) && select_realname != null
						&& select_realname.get(member.get("uid")) != null) {
					select_realname.remove(member.get("uid"));
				}
			}
			Set<Integer> uids = empty(select_realname) ? null : select_realname.keySet();
			if (!empty(uids)) {
				List<Map<String, Object>> spaceList = dataBaseService
						.executeQuery("SELECT uid, name, namestatus FROM "
								+ JavaCenterHome.getTableName("space") + " WHERE uid IN (" + sImplode(uids)
								+ ")");
				for (Map<String, Object> value : spaceList) {
					if (!empty(value.get("name")) && (Integer) value.get("namestatus") > 0) {
						sNames.put((Integer) value.get("uid"), (String) value.get("name"));
					}
				}
			}
		}
	}

	
	public static Map<String, Object> getMtag(HttpServletRequest request, HttpServletResponse response,
			int supe_uid, int id) throws Exception {
		List<Map<String, Object>> mtagList = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("mtag") + " WHERE tagid=" + id);
		if (mtagList.size() == 0) {
			throw new Exception("designated_election_it_does_not_exist");
		}
		Map<String, Object> mtag = mtagList.get(0);
		int joinPerm = (Integer) mtag.get("joinperm");
		int viewPerm = (Integer) mtag.get("viewperm");
		if ((Integer) mtag.get("membernum") < 1 && (joinPerm > 0 || viewPerm > 0)) {
			joinPerm = viewPerm = 0;
			mtag.put("joinperm", joinPerm);
			mtag.put("viewperm", viewPerm);
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("mtag")
					+ " SET joinperm='0', viewperm='0' WHERE tagid=" + id);
		}
		Map<Integer, Map<String, Object>> globalProfield = Common.getCacheDate(request, response,
				"/data/cache/cache_profield.jsp", "globalProfield");
		Map<String, Object> profield = globalProfield.get(mtag.get("fieldid"));
		mtag.put("field", profield);
		mtag.put("title", profield.get("title"));
		if (empty(mtag.get("pic"))) {
			mtag.put("pic", "image/nologo.jpg");
		}
		int grade = -9;
		int ismember = 0;
		List<String> grades = dataBaseService.executeQuery("SELECT grade FROM "
				+ JavaCenterHome.getTableName("tagspace") + " WHERE tagid=" + id + " AND uid=" + supe_uid
				+ " LIMIT 1", 1);
		if (grades.size() > 0) {
			grade = Integer.parseInt(grades.get(0));
			ismember = 1;
		}
		if (grade < 9 && checkPerm(request, response, "managemtag")) {
			grade = 9;
		}

		if (joinPerm > 0 && grade < 8) {
			mtag.put("allowinvite", 0);
		} else {
			mtag.put("allowinvite", grade >= 0 ? 1 : 0);
		}
		if ((Integer) mtag.get("close") > 0) {
			mtag.put("allowthread", 0);
			mtag.put("allowpost", 0);
		} else {
			mtag.put("allowthread", grade >= 0 ? 1 : mtag.get("threadperm"));
			mtag.put("allowpost", grade >= 0 ? 1 : mtag.get("postperm"));
		}
		mtag.put("allowview", (viewPerm > 0 && grade < -1) ? 0 : 1);
		mtag.put("grade", grade);
		mtag.put("ismember", ismember);
		return mtag;
	}

	
	public static void mkShare(Map<String, Object> share) {
		if (share != null && share.size() > 0) {
			Map<String, String> bodyData = Serializer.unserialize((String) share.get("body_data"), false);
			if (!empty(bodyData)) {
				Set<String> keys = bodyData.keySet();
				for (String key : keys) {
					share.put("body_template", ((String) share.get("body_template")).replace("{" + key + "}",
							bodyData.get(key)));
				}
			}
			share.put("body_data", bodyData);
		}
	}

	
	public static String getMessage(HttpServletRequest request, String key, Object... args) {
		if (key == null || key.length() == 0) {
			return key;
		}
		HttpSession session = request.getSession();
		if (mr == null) {
			mr = (MessageResources) session.getServletContext().getAttribute(Globals.MESSAGES_KEY);
		}

		Locale locale = (Locale) session.getAttribute(Globals.LOCALE_KEY);
		if (locale == null) {
			locale = request.getLocale();
		}
		String message = null;
		if (args == null || args.length == 0) {
			message = mr.getMessage(locale, key);
		} else {
			message = mr.getMessage(locale, key, args);
		}
		return message == null ? key : message;
	}

	
	public static String lang_replace(String text, Map vars) {
		if (text == null || text.length() == 0) {
			return text;
		}
		if (vars != null && vars.size() > 0) {
			int rk = 0;
			Set key = vars.keySet();
			for (Object k : key) {
				rk = Common.intval(k + "") + 1;
				text = text.replace("\\" + rk, vars.get(k) + "");
			}
		}
		return text;
	}

	
	public static String avatar(Integer uid, Map<String, Object> sGlobal, Map<String, Object> sConfig) {
		return avatar(uid, "small", false, sGlobal, sConfig);
	}

	
	public static String avatar(Integer uid, String sizeType, boolean returnUrl, Map<String, Object> sGlobal,
			Map<String, Object> sConfig) {
		String type = empty(sConfig.get("avatarreal")) ? "virtual" : "real";
		String avatarPath = "data/avatar/" + avatar_file(sGlobal, uid, sizeType, type);
		return returnUrl ? avatarPath : "<img src=\"" + avatarPath
				+ "\" onerror=\"this.onerror=null;this.src=\'data/avatar/noavatar_" + sizeType + ".gif\'\">";
	}

	public static String checkInput(HttpServletRequest request) {
		String input = request.getParameter("input");
		if (Common.empty(input)) {
			return "Invalid input";
		}
		input = authCode(input, "DECODE", null, 0);
		System.out.println(input);
		return null;
	}

	
	public static Object avatar(HttpServletRequest request, int uid, String type, boolean returnHtml) {
		String agent = Common.md5(request.getHeader("User-Agent"));
		String avatarFlash = "image/camera.swf?inajax=1&appid=" + JavaCenterHome.jchConfig.get("JC_APPID")
				+ "&uid=" + uid + "&hash=" + md5(uid + JavaCenterHome.jchConfig.get("JC_KEY")) + "&agent="
				+ agent + "&jcapi=" + Common.urlEncode(JavaCenterHome.jchConfig.get("siteUrl").replace("http://", ""))
				+ "&avatartype=" + type;
		if (returnHtml) {
			return "<object classid=\"clsid:d27cdb6e-ae6d-11cf-96b8-444553540000\" codebase=\"http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=9,0,0,0\" width=\"447\" height=\"477\" id=\"mycamera\" align=\"middle\">"
					+ "<param name=\"allowScriptAccess\" value=\"always\" />"
					+ "<param name=\"scale\" value=\"exactfit\" />"
					+ "<param name=\"wmode\" value=\"transparent\" />"
					+ "<param name=\"quality\" value=\"high\" />"
					+ "<param name=\"bgcolor\" value=\"#ffffff\" />"
					+ "<param name=\"movie\" value=\""
					+ avatarFlash
					+ "\" />"
					+ "<param name=\"menu\" value=\"false\" />"
					+ "<embed src=\""
					+ avatarFlash
					+ "\" quality=\"high\" bgcolor=\"#ffffff\" width=\"447\" height=\"477\" name=\"mycamera\" align=\"middle\" allowScriptAccess=\"always\" allowFullScreen=\"false\" scale=\"exactfit\"  wmode=\"transparent\" type=\"application/x-shockwave-flash\" pluginspage=\"http://www.macromedia.com/go/getflashplayer\" />"
					+ "</object>";
		} else {
			Map<String, String> map = new HashMap<String, String>();
			map.put("width", "447");
			map.put("height", "477");
			map.put("scale", "exactfit");
			map.put("src", avatarFlash);
			map.put("id", "mycamera");
			map.put("name", "mycamera");
			map.put("quality", "high");
			map.put("bgcolor", "#ffffff");
			map.put("wmode", "transparent");
			map.put("menu", "false");
			map.put("swLiveConnect", "true");
			map.put("allowScriptAccess", "always");
			return map;
		}
	}

	
	public static String avatar_file(Map<String, Object> sGlobal, int uid, String sizeType, String type) {
		String var = "avatarfile_" + uid + "_" + sizeType + "_" + type;
		String avatarPath = (String) sGlobal.get(var);
		if (empty(avatarPath)) {
			uid = Math.abs(uid);
			String struid = sprintf("000000000", uid);
			String dir1 = struid.substring(0, 3);
			String dir2 = struid.substring(3, 5);
			String dir3 = struid.substring(5, 7);
			StringBuffer avater = new StringBuffer();
			avater.append(dir1 + "/" + dir2 + "/" + dir3 + "/" + struid.substring(struid.length() - 2));
			avater.append("real".equals(type) ? "_real_avatar_" : "_avatar_");
			avater.append(sizeType + ".jpg");
			avatarPath = avater.toString();
			sGlobal.put(var, avatarPath);
		}
		return avatarPath;
	}

	
	public static Map<String, Object> mkFeed(Map<Integer, String> sNames, Map<String, Object> sConfig,
			HttpServletRequest request, Map<String, Object> feed, Object actors) {
		if (feed == null || feed.size() == 0) {
			return feed;
		}
		feed.put("title_data", Serializer.unserialize((String) feed.get("title_data"), false));
		feed.put("body_data", Serializer.unserialize((String) feed.get("body_data"), false));
		List<String> searchs = new ArrayList<String>();
		List<String> replaces = new ArrayList<String>();
		if (feed.get("title_data") != null && isArray(feed.get("title_data"))) {
			Map title_data = (Map) feed.get("title_data");
			Set keys = title_data.keySet();
			for (Object key : keys) {
				searchs.add("{" + key + "}");
				replaces.add(title_data.get(key) + "");
			}
		}
		searchs.add("{actor}");
		replaces.add(empty(actors) ? "<a href=\"space.jsp?uid=" + feed.get("uid") + "\">"
				+ sNames.get(feed.get("uid")) + "</a>" : implode(actors, "¡¢"));

		searchs.add("{app}");
		Map globalApp=(Map) request.getAttribute("globalApp");
		Map app = globalApp==null ? null : (Map)globalApp.get(feed.get("appid"));
		if (empty(app)) {
			replaces.add("");
		} else {
			replaces.add("<a href=\"" + app.get("url") + "\">" + app.get("name") + "</a>");
		}
		String title_template = (String) feed.get("title_template");
		title_template = title_template == null ? "" : title_template;
		for (int i = 0; i < replaces.size(); i++) {
			title_template = title_template.replace(searchs.get(i), replaces.get(i));
		}
		feed.put("title_template", mkTarget(title_template, sConfig));
		searchs.clear();
		replaces.clear();

		if (feed.get("body_data") != null && isArray(feed.get("body_data"))) {
			Map body_data = (Map) feed.get("body_data");
			Set keys = body_data.keySet();
			for (Object key : keys) {
				searchs.add("{" + key + "}");
				replaces.add(body_data.get(key) + "");
			}
		}
		feed.put("magic_class", "");
		if (feed.get("appid") != null && (Integer) feed.get("appid") > 0) {
			Map body_data = (Map) feed.get("body_data");
			if (body_data != null) {
				if (!empty(body_data.get("magic_color"))) {
					feed.put("magic_class", "magiccolor" + body_data.get("magic_color"));
				}
				if (!empty(body_data.get("magic_thunder"))) {
					feed.put("magic_class", "magicthunder");
				}
			}
		}

		searchs.add("{actor}");
		replaces.add("<a href=\"space.jsp?uid=" + feed.get("uid") + "\">" + feed.get("username") + "</a>");
		String body_template = (String) feed.get("body_template");
		body_template = body_template == null ? "" : body_template;
		for (int i = 0; i < replaces.size(); i++) {
			body_template = body_template.replace(searchs.get(i), replaces.get(i));
		}
		feed.put("body_template", mkTarget(body_template, sConfig));
		feed.put("body_general", mkTarget((String) feed.get("body_general"), sConfig));
		feed.put("icon_image", "image/icon/" + feed.get("icon") + ".gif");
		if (!Common.empty(sConfig.get("feedread")) && empty(feed.get("id"))) {
			Map<String, String> sCookie = (Map<String, String>) request.getAttribute("sCookie");
			String[] read_feed_ids = empty(sCookie.get("read_feed_ids")) ? null : sCookie
					.get("read_feed_ids").split(",");
			if (read_feed_ids != null && in_array(read_feed_ids, feed.get("feedid"))) {
				feed.put("style", " class=\"feedread\"");
			} else {
				feed.put("style", " onclick=\"readfeed(this, " + feed.get("feedid") + ");\"");
			}
		} else {
			feed.put("style", "");
		}
		if ((Integer) sConfig.get("feedtargetblank") > 0) {
			feed.put("target", " target=\"_blank\"");
		} else {
			feed.put("target", "");
		}
		if (in_array(new String[] {"blogid", "picid", "sid", "pid", "eventid"}, feed.get("idtype"))) {
			feed.put("showmanage", 1);
		}
		feed.put("thisapp", 0);
		if (feed.get("appid") != null
				&& (Integer) feed.get("appid") == intval(JavaCenterHome.jchConfig.get("JC_APPID"))) {
			feed.put("thisapp", 1);
		}
		return feed;
	}

	
	public static String mkTarget(String html, Map<String, Object> sConfig) {
		if (empty(html)) {
			return html;
		}
		if ((Integer) sConfig.get("feedtargetblank") > 0) {
			html = html.replaceAll("<a(.+?)href=([\\'\"]?)([^>\\s]+)\\2([^>]*)>",
					"<a target=\"_blank\" $1 href=\"$3\" $4>");
		}
		return html;
	}

	
	public static void setResponseHeader(HttpServletResponse response) {
		setResponseHeader(response, "text/html");
	}

	
	public static void setResponseHeader(HttpServletResponse response, String type) {
		response.setContentType(type + "; charset=" + JavaCenterHome.JCH_CHARSET);
		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Program", "no-cache");
		response.setDateHeader("Expirse", 0);
	}

	
	public static void deleteAvatar(Map<String, Object> sGlobal, Integer... uids) {
		if (uids != null) {
			File file = null;
			String avatarRoot = JavaCenterHome.jchRoot + "/data/avatar/";
			String[] sizeTypes = {"big", "middle", "small"};
			String[] types = {"", "real"};
			for (int uid : uids) {
				for (String sizeType : sizeTypes) {
					for (String type : types) {
						file = new File(avatarRoot + Common.avatar_file(sGlobal, uid, sizeType, type));
						if (file.exists()) {
							file.delete();
						}
					}
				}
			}
		}
	}

	public static Calendar getCalendar(String timeoffset) {
		return Calendar.getInstance(TimeZone.getTimeZone(timeZoneIDs.get(timeoffset)[0]));
	}

	
	public static String getTimeOffset(Map<String, Object> sGlobal, Map<String, Object> sConfig) {
		Map<String, Object> member = (Map<String, Object>) sGlobal.get("member");
		String timeoffset = null;
		if (member != null) {
			timeoffset = (String) member.get("timeoffset");
		}
		if (Common.empty(timeoffset)) {
			timeoffset = sConfig.get("timeoffset").toString();
		}
		return timeoffset;
	}

	
	public static String spaceKey(Map<String, Object> space, Map<String, Object> sConfig, int appId) {
		return md5(
				sConfig.get("sitekey") + "|" + (space == null ? "" : space.get("uid"))
						+ (appId == 0 ? "" : "|" + appId)).substring(8, 24);
	}

	
	public static boolean ckPrivacy(Map<String, Object> sGlobal, Map<String, Object> sConfig,
			Map<String, Object> space, String type, int feedMode) {
		String var = "ckprivacy_" + type + "_" + feedMode;
		if (sGlobal.get(var) != null) {
			return (Boolean) sGlobal.get(var);
		}
		boolean result = false;
		if (feedMode != 0) {
			if ("spaceopen".equals(type)) {
				Map feed = (Map) ((Map) sConfig.get("privacy")).get("feed");
				if (!Common.empty(feed.get(type))) {
					result = true;
				}
			} else {
				Map privacy = (Map) space.get("privacy");
				Map feed = privacy != null ? (Map) privacy.get("feed") : null;
				if (feed != null && !Common.empty(feed.get(type))) {
					result = true;
				}
			}
		} else if ((Boolean) space.get("self")) {
			result = true;
		} else {
			Map view = (Map) ((Map) space.get("privacy")).get("view");
			if (empty(view.get(type))) {
				result = true;
			}
			if (!result && (Integer) view.get(type) == 1) {
				if (space.get("isfriend") == null) {
					space.put("isfriend", space.get("self"));
					if (!empty(space.get("friends"))
							&& in_array((String[]) space.get("friends"), sGlobal.get("supe_uid"))) {
						space.put("isfriend", true);
					}
				}
				if ((Boolean) space.get("isfriend")) {
					result = true;
				}
			}
		}
		sGlobal.put(var, result);
		return result;
	}
	public static boolean ckAppPrivacy(Map<String, Object> sGlobal, Map<String, Object> space, int privacy) {
		String var = "app_ckprivacy_" + privacy;
		if (sGlobal.get(var) != null) {
			return (Boolean) sGlobal.get(var);
		}
		boolean result = false;
		switch (privacy) {
			case 0:
				result = true;
				break;
			case 1:
				if (space.get("isfriend") == null) {
					space.put("isfriend", space.get("self"));
					if (in_array((String[]) space.get("friends"), sGlobal.get("supe_uid"))) {
						space.put("isfriend", true);
					}
				}
				if ((Boolean) space.get("isfriend")) {
					result = true;
				}
				break;
			case 2:
				break;
			case 3:
				if ((Boolean) space.get("self")) {
					result = true;
				}
				break;
			case 4:
				break;
			case 5:
				break;
			default:
				result = true;
				break;
		}
		sGlobal.put(var, result);
		return result;
	}

	
	public static int mobPerpage(HttpServletRequest request, int perPage) {
		int newPerPage = intval(request.getParameter("perpage"));
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		if (!empty(sGlobal.get("mobile")) && newPerPage > 0 && newPerPage < 500) {
			perPage = newPerPage;
		}
		return perPage;
	}

	
	public static boolean ckFriend(Map<String, Object> sGlobal, Map<String, Object> space, int toUid,
			int friend, String target_ids) {
		Integer supe_uid = (Integer) sGlobal.get("supe_uid");
		if (empty(supe_uid)) {
			return friend > 0 ? false : true;
		}
		if (toUid == supe_uid) {
			return true;
		}

		String var = "ckfriend_" + md5(toUid + "_" + friend + "_" + target_ids);
		if (sGlobal.get(var) != null) {
			return (Boolean) sGlobal.get(var);
		}
		sGlobal.put(var, false);
		switch (friend) {
			case 0:
				sGlobal.put(var, true);
				break;
			case 1:
				if ((Integer) space.get("uid") == toUid) {
					if (!empty(space.get("friends")) && in_array((String[]) space.get("friends"), supe_uid)) {
						sGlobal.put(var, true);
					}
				} else {
					sGlobal.put(var, getFriendStatus(supe_uid, toUid) == 1 ? true : false);
				}
				break;
			case 2:
				if (!empty(target_ids)) {
					String[] targetIds = target_ids.split(",");
					if (in_array(targetIds, supe_uid)) {
						sGlobal.put(var, true);
					}
				}
				break;
			case 3:
				break;
			case 4:
				sGlobal.put(var, true);
				break;
			default:
				break;
		}

		return (Boolean) sGlobal.get(var);
	}

	
	public static int getFriendStatus(int uid, int fuid) {
		List<String> values = dataBaseService.executeQuery("SELECT status FROM "
				+ JavaCenterHome.getTableName("friend") + " WHERE uid=" + uid + " AND fuid=" + fuid
				+ " LIMIT 1", 1);
		if (values.size() > 0) {
			return Integer.parseInt(values.get(0));
		} else {
			return -1;
		}
	}

	
	public static String getStar(Map<String, Object> sConfig, int experience) {
		StringBuffer starImg = new StringBuffer();
		int starCredit = (Integer) sConfig.get("starcredit");
		if (starCredit > 1) {
			int starlevelnum = (Integer) sConfig.get("starlevelnum");
			int starNum = (experience / starCredit) + 1;
			if (starlevelnum < 2) {
				if (starNum > 10) {
					starNum = 10;
				}
				for (int i = 0; i < starNum; i++) {
					starImg.append("<img src=\"image/star_level10.gif\" align=\"absmiddle\" />");
				}
			} else {
				for (int i = 10; i > 0; i--) {
					int numLevel = (int) (starNum / Math.pow(starlevelnum, i - 1));
					if (numLevel > 10) {
						starNum = 10;
					}
					if (numLevel > 0) {
						for (int j = 0; j < numLevel; j++) {
							starImg.append("<img src=\"image/star_level" + i
									+ ".gif\" align=\"absmiddle\" />");
						}
						break;
					}
				}
			}
		}
		return starImg.length() == 0 ? "<img src=\"image/credit.gif\" align=\"absmiddle\" alt=\""
				+ experience + "\" title=\"" + experience + "\" />" : starImg.toString();
	}

	
	public static String spaceDomain(HttpServletRequest request, Map<String, Object> space,
			Map<String, Object> sConfig) {
		if (!empty(space.get("domain")) && !empty(sConfig.get("allowdomain"))
				&& !empty(sConfig.get("domainroot"))) {
			space.put("domainurl", "http://" + space.get("domain") + "." + sConfig.get("domainroot"));
		} else {
			space.put("domainurl", Common.getSiteUrl(request) + "?" + space.get("uid"));
		}
		return (String) space.get("domainurl");
	}

	
	public static Map<Integer, String> getFriendGroup(HttpServletRequest request) {
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		Map<String, Object> privacy = (Map<String, Object>) space.get("privacy");
		Map<Integer, String> spaceGroup = null;
		if (privacy != null) {
			spaceGroup = (Map<Integer, String>) privacy.get("groupname");
		}
		int groupNum = (Integer) sConfig.get("groupnum");
		Map<Integer, String> groups = new TreeMap<Integer, String>();
		for (int i = 0; i < groupNum; i++) {
			if (i == 0) {
				groups.put(0, Common.getMessage(request, "friend_group_default"));
			} else {
				if (spaceGroup != null && !Common.empty(spaceGroup.get(i))) {
					groups.put(i, spaceGroup.get(i));
				} else {
					if (i < 8) {
						groups.put(i, Common.getMessage(request, "friend_group_" + i));
					} else {
						groups.put(i, Common.getMessage(request, "friend_group") + i);
					}
				}
			}
		}
		return groups;
	}

	
	public static Map<String, Object> getTopic(HttpServletRequest request, int topicId) {
		Map<String, Object> topic = new HashMap<String, Object>();
		if (topicId > 0) {
			String[] types = new String[] {"blog", "pic", "thread", "poll", "event", "share"};
			List<Map<String, Object>> topicList = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("topic") + " WHERE topicid='" + topicId + "'");
			if (!topicList.isEmpty()) {
				Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
				Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
				topic = topicList.get(0);
				topic.put("pic", !empty(topic.get("pic")) ? pic_get(sConfig, (String) topic.get("pic"),
						(Integer) topic.get("thumb"), (Integer) topic.get("remote"), false) : "");
				topic.put("joingid", empty(topic.get("joingid")) ? null : topic.get("joingid").toString()
						.split(","));
				topic.put("jointype", empty(topic.get("jointype")) ? types : topic.get("jointype").toString()
						.split(","));
				topic.put("lastpost", sgmdate(request, "yyyy-MM-dd HH:mm", (Integer) topic.get("lastpost")));
				topic.put("dateline", sgmdate(request, "yyyy-MM-dd HH:mm", (Integer) topic.get("dateline")));
				topic
						.put("allowjoin",
								!empty(topic.get("endtime"))
										&& (Integer) sGlobal.get("timestamp") > (Integer) topic
												.get("endtime") ? false : true);
				topic.put("endtime", !empty(topic.get("endtime")) ? sgmdate(request, "yyyy-MM-dd HH:mm",
						(Integer) topic.get("endtime")) : "");
				topic.put("message", BBCode.bbCode((String) topic.get("message"), 1));

				topic.put("joinurl", "");
				for (String value : types) {
					if (in_array((String[]) topic.get("jointype"), value)) {
						if ("pic".equals(value)) {
							value = "upload";
						}
						topic.put("joinurl", "cp.jsp?ac=" + value + "&topicid=" + topicId);
						break;
					}
				}
			}
		}
		return topic;
	}

	
	public static int getUid(Map<String, Object> sConfig, String name) {
		name = name == null ? "" : name.trim();
		List<String> wheres = new ArrayList<String>();
		wheres.add("(username='" + name + "')");
		if (!empty(sConfig.get("realname"))) {
			wheres.add("(name='" + name + "' AND namestatus = 1)");
		}
		int uid = 0;
		List<Map<String, Object>> spaceList = dataBaseService
				.executeQuery("SELECT uid,username,name,namestatus FROM "
						+ JavaCenterHome.getTableName("space") + " WHERE " + implode(wheres, " OR ")
						+ " LIMIT 1");
		if (!spaceList.isEmpty()) {
			uid = (Integer) spaceList.get(0).get("uid");
		}
		return uid;
	}

	
	public static Map<String, Object> ckSearch(String theURL, HttpServletRequest request,
			HttpServletResponse response) {
		theURL = stripSlashes(theURL) + "&page=" + intval(request.getParameter("page"));
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		int searchInterVal = (Integer) checkPerm(request, response, sGlobal, "searchinterval");
		if (searchInterVal > 0) {
			int waitTime = searchInterVal
					- ((Integer) sGlobal.get("timestamp") - (Integer) space.get("lastsearch"));
			if (waitTime > 0) {
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("msgkey", "search_short_interval");
				result.put("url_forward", null);
				result.put("second", 1);
				result.put("args", new String[] {waitTime + "", theURL});
				return result;
			}
		}
		if (!checkPerm(request, response, "searchignore")) {
			Map<String, Integer> reward = getReward("search", false, 0, "", true, request, response);
			int credit = reward.get("credit");
			int experience = reward.get("experience");
			if (credit > 0 || experience > 0) {
				if (empty(request.getParameter("confirm"))) {
					theURL += "&confirm=yes";
					Map<String, Object> result = new HashMap<String, Object>();
					result.put("msgkey", "points_deducted_yes_or_no");
					result.put("url_forward", null);
					result.put("second", 1);
					result.put("args", new String[] {credit + "", experience + "", theURL});
					return result;
				} else {
					if (space.get("credit") == null || (Integer) space.get("credit") < credit
							|| space.get("experience") == null
							|| (Integer) space.get("experience") < experience) {
						Map<String, Object> result = new HashMap<String, Object>();
						result.put("msgkey", "points_search_error");
						result.put("url_forward", null);
						result.put("second", 1);
						result.put("args", null);
						return result;
					} else {
						dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
								+ " SET lastsearch='" + sGlobal.get("timestamp") + "', credit=credit-"
								+ credit + ", experience=experience-" + experience + " WHERE uid='"
								+ sGlobal.get("supe_uid") + "'");
					}
				}
			}
		}
		return null;
	}

	
	public static String question(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Map<Integer, Object>> globalSpam = getCacheDate(request, response,
				"/data/cache/cache_spam.jsp", "globalSpam");
		if (!empty(globalSpam.get("question"))) {
			Map<Integer, Object> question = globalSpam.get("question");
			int count = question.size();
			int key = count > 1 ? rand(0, count - 1) : 0;
			request.getSession().setAttribute("seccode", key);
			return question.get(key).toString();
		}
		return null;
	}

	
	public static Object[] uniqueArray(Object[] array) {
		List<Object> list = new LinkedList<Object>();
		for (Object obj : array) {
			if (!list.contains(obj)) {
				list.add(obj);
			}
		}
		return list.toArray(new Object[list.size()]);
	}

	
	public static List<String> pregMatch(String content, String regex) {
		List<String> strList = new ArrayList<String>();
		try {
			Perl5Matcher patternMatcher = new Perl5Matcher();
			if (patternMatcher.contains(content, new Perl5Compiler().compile(regex))) {
				MatchResult result = patternMatcher.getMatch();
				for (int i = 0; i < result.groups(); i++) {
					strList.add(result.group(i));
				}
				result = null;
			}
		} catch (MalformedPatternException e) {
			e.printStackTrace();
		}
		return strList;
	}

	
	@SuppressWarnings("unchecked")
	public static String formHash(Map<String, Object> sGlobal, Map<String, Object> sConfig, boolean in_admincp) {
		String formhash = (String) sGlobal.get("formhash");
		if (Common.empty(formhash)) {
			String timestamp = sGlobal.get("timestamp").toString();
			char split = '|';
			StringBuffer temp = new StringBuffer();
			temp.append(timestamp.substring(0, timestamp.length() - 7));
			temp.append(split);
			temp.append(sGlobal.get("supe_uid"));
			temp.append(split);
			temp.append(Common.md5((String) sConfig.get("sitekey")));
			temp.append(split);
			if (in_admincp) {
				temp.append("Only For JavaCenter Home AdminCP");
			}
			formhash = Common.md5(temp.toString()).substring(8, 16);
			sGlobal.put("formhash", formhash);
		}
		return formhash;
	}

	
	public static String debugInfo(Map<String, Object> sGlobal, Map<String, Object> sConfig) {
		int debuginfo = (Integer) sConfig.get("debuginfo");
		if (debuginfo > 0) {
			long startTime = (Long) sGlobal.get("starttime");
			long endTime = System.currentTimeMillis();
			String totalTime = sprintf("0.0000", (endTime - startTime) / 1000f);
			String gzipCompress = Common.intval(JavaCenterHome.jchConfig.get("gzipCompress")) > 0 ? ", Gzip enabled"
					: "";
			return "Processed in " + totalTime + " second(s) " + gzipCompress + ".";
		} else {
			return null;
		}
	}

	
	public static String getImageType(File imgFile) {
		String imageType = null;
		try {
			ImageInputStream iis = ImageIO.createImageInputStream(imgFile);
			Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
			if (iter.hasNext()) {
				ImageReader reader = iter.next();
				imageType = reader.getFormatName().toLowerCase();
			}
			iis.close();
		} catch (IOException e) {
		}
		return imageType;
	}

	
	public static int checkInterval(HttpServletRequest request, HttpServletResponse response, String type) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		String intervalName = type + "interval";
		String lastName = "last" + type;
		int waitTime = 0;
		int interval = (Integer) checkPerm(request, response, sGlobal, intervalName);
		if (interval != 0) {
			Map<String, Object> whereArr = new HashMap<String, Object>();
			whereArr.put("uid", sGlobal.get("supe_uid"));
			int lastTime = space.get("lastname") != null ? (Integer) space.get("lastname") : Common
					.intval(getCount("space", whereArr, lastName));
			waitTime = interval - ((Integer) sGlobal.get("timestamp") - lastTime);
		}
		return waitTime;
	}

	
	public static Object sarrayRand(Object arr, int num) {
		if (Common.empty(arr)) {
			return arr;
		}
		if (arr instanceof Object[]) {
			Object[] temp = (Object[]) arr;
			if (temp.length > num) {
				Object[] r_values = new Object[num];
				if (num > 1) {
					Integer[] r_keys = (Integer[]) arrayRand(temp, num);
					for (int i = 0; i < num; i++) {
						r_values[i] = temp[r_keys[i]];
					}
				} else {
					Integer[] r_keys = (Integer[]) arrayRand(temp, 1);
					r_values[0] = temp[r_keys[0]];
				}
				return r_values;
			}
		} else if (arr instanceof List) {
			List temp = (List) arr;
			if (temp.size() > num) {
				try {
					List r_values = new ArrayList();
					if (num > 1) {
						Integer[] r_keys = (Integer[]) arrayRand(temp, num);
						for (Integer key : r_keys) {
							r_values.add(temp.get(key));
						}
					} else {
						Integer[] r_keys = (Integer[]) arrayRand(temp, 1);
						r_values.add(temp.get(r_keys[0]));
					}
					return r_values;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else if (arr instanceof Map) {
			Map temp = (Map) arr;
			if (temp.size() > num) {
				Map r_values = new LinkedHashMap();
				if (num > 1) {
					Object[] r_keys = (Object[]) arrayRand(temp, num);
					for (Object key : r_keys) {
						r_values.put(key, temp.get(key));
					}
				} else {
					Object[] r_keys = (Object[]) arrayRand(temp, 1);
					r_values.put(r_keys[0], temp.get(r_keys[0]));
				}
				return r_values;
			}
		}
		return arr;
	}

	
	public static Object[] arrayRand(Object input, int num) {
		if (input == null) {
			return new Object[] {};
		}
		num = num <= 0 ? 1 : num;
		int max = 0;
		Object[] objs = null;
		if (input instanceof Object[]) {
			objs = (Object[]) input;
			max = objs.length;
			if (num == 1) {
				return new Integer[] {Common.rand(0, max - 1)};
			}
			if (max <= num) {
				Integer[] returnKeys = new Integer[max];
				for (int i = 0; i < max; i++) {
					returnKeys[i] = i;
				}
				return returnKeys;
			}
		} else if (input instanceof Collection) {
			Collection<Object> temp = (Collection<Object>) input;
			max = temp.size();
			if (num == 1) {
				return new Integer[] {Common.rand(0, max - 1)};
			}
			if (max <= num) {
				Integer[] returnKeys = new Integer[max];
				for (int i = 0; i < max; i++) {
					returnKeys[i] = i;
				}
				return returnKeys;
			}
		} else if (input instanceof Map) {
			Map<Object, Object> temp = (Map<Object, Object>) input;
			Set<Object> keys = temp.keySet();
			max = keys.size();
			objs = keys.toArray();
			if (num == 1) {
				return new Object[] {objs[Common.rand(0, max - 1)]};
			}
			if (max <= num) {
				return objs;
			}
		} else {
			return new Object[] {};
		}
		int i = 0;
		Set<Integer> set = new HashSet<Integer>();
		while (i < num) {
			set.add(Common.rand(0, max - 1));
			i = set.size();
		}
		if (input instanceof Object[] || input instanceof Collection) {
			return set.toArray(new Integer[num]);
		} else {
			Object[] returnKeys = new Object[num];
			int j = 0;
			for (Integer randKey : set) {
				returnKeys[j] = objs[randKey];
				j++;
			}
			return returnKeys;
		}
	}

	
	@SuppressWarnings("unchecked")
	public static List getRandList(List list, int num) {
		int size = list == null ? 0 : list.size();
		if (num > 0 && num <= size) {
			List temp = new ArrayList(num);
			for (int i = 0; i < num; i++) {
				int rand = rand(size - 1);
				if (temp.indexOf(rand) == -1) {
					temp.add(list.get(rand));
				} else {
					i--;
				}
			}
			return temp;
		}
		return null;
	}

	
	public static int mkTime(int hour, int min, int sec, int mon, int day, int year) {
		if (0 >= (mon -= 2)) {
			mon += 12;
			year -= 1;
		}
		int y = (year - 1) * 365 + year / 4 - year / 100 + year / 400;
		int m = 367 * mon / 12 - 30 + 59;
		int d = day - 1;
		int x = y + m + d - 719162;
		int t = ((x * 24 + hour) * 60 + min) * 60 + sec;
		return t;
	}
	public static String smulti(Map<String, Object> sGlobal, int start, int perPage, int count, String url,
			String ajaxDiv) throws Exception {
		Map<String, Integer> multi = new HashMap<String, Integer>();
		multi.put("last", -1);
		multi.put("next", -1);
		multi.put("begin", -1);
		multi.put("end", -1);
		String html = null;
		if (start > 0) {
			if (count == 0) {
				throw new Exception("no_data_pages");
			} else {
				multi.put("last", start - perPage);
			}
		}
		ajaxDiv = ajaxDiv == null ? "" : ajaxDiv;
		boolean showHtml = false;
		if (count == perPage) {
			multi.put("next", start + perPage);
		}
		multi.put("begin", start + 1);
		multi.put("end", start + count);
		if (multi.get("begin") >= 0) {
			if (multi.get("last") >= 0) {
				showHtml = true;
				if (!empty(sGlobal.get("inajax"))) {
					html = "<a href=\"javascript:;\" onclick=\"ajaxget('" + url + "&ajaxdiv=" + ajaxDiv
							+ "', '" + ajaxDiv
							+ "')\">|&lt;</a> <a href=\"javascript:;\" onclick=\"ajaxget('" + url + "&start="
							+ multi.get("last") + "&ajaxdiv=" + ajaxDiv + "', '" + ajaxDiv + "')\">&lt;</a> ";
				} else {
					html = "<a href=\"" + url + "\">|&lt;</a> <a href=\"" + url + "&start="
							+ multi.get("last") + "\">&lt;</a> ";
				}
			} else {
				html = "&lt;";
			}
			html += " " + multi.get("begin") + "~" + multi.get("end") + " ";
			if (multi.get("next") >= 0) {
				showHtml = true;
				if (!empty(sGlobal.get("inajax"))) {
					html += " <a href=\"javascript:;\" onclick=\"ajaxget('" + url + "&start="
							+ multi.get("next") + "&ajaxdiv=" + ajaxDiv + "', '" + ajaxDiv + "')\">&gt;</a> ";
				} else {
					html += " <a href=\"" + url + "&start=" + multi.get("next") + "\">&gt;</a>";
				}
			} else {
				html += " &gt;";
			}
		}
		return showHtml ? html : "";
	}

	
	public static Object siconv(String str, String outCharset, String inCharset, String jchConfigCharset) {
		return str;
	}

	
	public static boolean isHoldDomain(Map<String, Object> sConfig, String domain) {
		domain = domain.toLowerCase();
		if (Common.matches(domain, "(?i)^[^a-z]")) {
			return true;
		}
		String[] holdMainArr = null;
		if (Common.empty(sConfig.get("holddomain"))) {
			holdMainArr = new String[] {"www"};
		} else {
			holdMainArr = ((String) sConfig.get("holddomain")).split("|");
		}
		boolean isHold = false;
		for (String value : holdMainArr) {
			if (value.indexOf("*") == -1) {
				if (value.toLowerCase().equals(domain)) {
					isHold = true;
					break;
				}
			} else {
				value = value.replace("*", "");
				if (domain.matches("(?i)" + value)) {
					isHold = true;
					break;
				}
			}
		}
		return isHold;
	}

	
	public static long getByteSizeByBKMG(String unitSize) {
		long maxSize = 0;
		String maxFileSize = unitSize;
		Matcher matcher = Pattern.compile("\\d+([bkmg]?)").matcher(maxFileSize.toLowerCase());
		if (matcher.matches()) {
			String ch = matcher.replaceAll("$1");
			if (ch.equals("k")) {
				maxSize = Common.intval(maxFileSize) * 1024;
			} else if (ch.equals("m")) {
				maxSize = Common.intval(maxFileSize) * 1024 * 1024;
			} else if (ch.equals("g")) {
				maxSize = Common.intval(maxFileSize) * 1024 * 1024;
			} else {
				maxSize = Common.intval(maxFileSize);
			}
		}
		return maxSize;
	}

	public static String sub_url(String url, int length) throws UnsupportedEncodingException {
		if (url == null) {
			return "";
		}
		if (url.length() > length) {
			url = URLEncoder.encode(url, JavaCenterHome.JCH_CHARSET);
			url = url.replace("%3A", ":").replace("%2F", "/");
			url = url.substring(0, length / 2) + " ... "
					+ url.substring(url.length() - (int) (length * 0.3F));
		}
		return url;
	}

	
	public static String showAdById(Object id) {
		String filePath = JavaCenterHome.jchRoot + "data/adtpl/" + id + ".htm";
		return FileHelper.readFile(filePath);
	}

	
	public static String showAd(Map ids) {
		if (Common.empty(ids)) {
			return null;
		}
		int key = Common.rand(0, ids.size() - 1);
		Object id = ids.get(key);
		return showAdById(id);
	}

	
	public static void showData(HttpServletRequest request, String param) {
		BlockService bs = new BlockService();
		bs.block_batch(request, param);
	}

	
	public static void set_home(int uid, String dir) {
		String struid = String.format("%09d", uid);
		String dir1 = struid.substring(0, 3);
		String dir2 = struid.substring(3, 5);
		String dir3 = struid.substring(5, 7);
		File file = new File(dir + "/" + dir1);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(dir + "/" + dir1 + "/" + dir2);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(dir + "/" + dir1 + "/" + dir2 + "/" + dir3);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	
	public static String get_home(int uid) {
		String struid = String.format("%09d", uid);
		String dir1 = struid.substring(0, 3);
		String dir2 = struid.substring(3, 5);
		String dir3 = struid.substring(5, 7);
		return dir1 + "/" + dir2 + "/" + dir3;
	}
	
	public static Map<String,Object> getPassPort(String userName,String passWord) {
		Map<String,Object> passPort=new HashMap<String, Object>();
		List<Map<String, Object>> members = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("member") + " WHERE username = '" + userName + "'");
		if (!members.isEmpty()) {
			Map<String, Object> member = members.get(0);
			passWord = Common.md5(Common.md5(passWord) + member.get("salt"));
			if (passWord.equals(member.get("password"))) {
				passPort.put("uid", member.get("uid"));
				passPort.put("username", member.get("username"));
			}
		}
		return passPort;
	}
}