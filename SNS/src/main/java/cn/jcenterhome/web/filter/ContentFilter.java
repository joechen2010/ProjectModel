package cn.jcenterhome.web.filter;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.FileHelper;
import cn.jcenterhome.util.JavaCenterHome;
import cn.jcenterhome.web.servlet.WapperedResponse;


public class ContentFilter implements Filter {
	public void init(FilterConfig fc) throws ServletException {
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		String ac = request.getParameter("ac");
		if ("seccode".equals(ac)) {
			doSeccode(request, response);
		} else {
			WapperedResponse wapper = new WapperedResponse(response);
			if("swfupload".equals(ac) ||("stat".equals(ac) && !Common.empty(request.getParameter("xml")))){
				request.setCharacterEncoding("UTF-8");
				chain.doFilter(request, wapper);
				swf_Out(request, response, wapper);
			}else{
				chain.doFilter(request, wapper);
				String contentType = response.getContentType();
				if (contentType != null && contentType.startsWith("application/octet-stream")) {
					byte[] content = wapper.getByteData();
					ServletOutputStream out = response.getOutputStream();
					out.write(content);
					out.flush();
				} else {
					ob_Out(request, response, wapper);
				}
			}
		}
	}

	
	private void doSeccode(HttpServletRequest request, HttpServletResponse response) {
		String seccode = mkSeccode();
		request.getSession().setAttribute("seccode", seccode);
		int width = 100, height = 40;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		g.setColor(getRandColor(200, 235));
		g.fillRect(0, 0, width, height);
		g.setFont(new Font("Times New Roman", Font.BOLD, 22));
		for (int i = 0; i < 4; i++) {
			g.setColor(new Color(20 + Common.rand(110), 20 + Common.rand(110), 20 + Common.rand(110)));
			int x = Common.rand(width);
			int y = Common.rand(height);
			int xl = Common.rand(width);
			int yl = Common.rand(height);
			g.drawLine(x, y, x + xl, y + yl);
		}
		for (int i = 0; i < 4; i++) {
			char rand = seccode.charAt(i);
			g.setColor(getRandColor(10, 150));
			g.drawString(String.valueOf(rand), 24 * i + Common.rand(4), 22 + Common.rand(6));
		}
		g.dispose();
		try {
			response.setContentType("image/JPEG");
			ImageIO.write(image, "JPEG", response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	private String mkSeccode() {
		int seccode = Integer.parseInt(Common.getRandStr(6, true));
		String s = Integer.toString(seccode, 24);
		while (s.length() < 4) {
			s = "0" + s;
		}
		String seccodeUnits = "BCEFGHJKMPQRTVWXY2346789";
		StringBuffer secCodeHiddenBuf = new StringBuffer(4);
		for (int i = 0; i < 4; i++) {
			int unit = s.charAt(i);
			if (unit >= 0x30 && unit <= 0x39) {
				secCodeHiddenBuf.append(seccodeUnits.charAt(unit - 0x30));
			} else {
				secCodeHiddenBuf.append(seccodeUnits.charAt(unit - 0x57));
			}
		}
		return secCodeHiddenBuf.toString();
	}

	
	private Color getRandColor(int fc, int bc) {
		if (fc > 255) {
			fc = 255;
		}
		if (bc > 255) {
			bc = 255;
		}
		int r = fc + Common.rand(bc - fc);
		int g = fc + Common.rand(bc - fc);
		int b = fc + Common.rand(bc - fc);
		return new Color(r, g, b);
	}
	private void swf_Out(HttpServletRequest request, HttpServletResponse response, WapperedResponse wapper)
			throws IOException {
		String content = wapper.getResponseData();
		response.reset();
		response.resetBuffer();
		response.setContentType("application/xml; charset=UTF-8");
		response.setHeader("Expires", "-1");
		response.addHeader("Cache-Control", "no-store, private, post-check=0, pre-check=0, max-age=0");
		response.setHeader("Pragma", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.write(Common.trim(content));
		out.flush();
	}
	private void ob_Out(HttpServletRequest request, HttpServletResponse response, WapperedResponse wapper)
			throws IOException {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		int allowRewrite = 0;
		int linkGuide = 0;
		int headerCharset = 0;
		if (sConfig != null) {
			allowRewrite = (Integer) sConfig.get("allowrewrite");
			linkGuide = (Integer) sConfig.get("linkguide");
			headerCharset = (Integer) sConfig.get("headercharset");
		}
		String content = wapper.getResponseData().trim();
		if (allowRewrite > 0) {
			content = rewriteURL(content,
					"(?is)\\<a href\\=\"space\\.jsp\\?(uid|do)+\\=([a-z0-9\\=\\&]+?)\"", "space-", 2);
			content = content.replaceAll("(?i)\\<a href\\=\"space.jsp\"", "<a href=\"space.html\"");
			content = rewriteURL(content, "(?is)\\<a href\\=\"network\\.jsp\\?ac\\=([a-z0-9\\=\\&]+?)\"",
					"network-", 1);
			content = content.replaceAll("(?i)\\<a href\\=\"network.jsp\"", "<a href=\"network.html\"");
		}
		if (linkGuide > 0) {
			content = iframeURL(content, "(?is)\\<a href\\=\"http\\:\\/\\/(.+?)\"");
		}
		PrintWriter out = response.getWriter();
		Integer inajax = (Integer) sGlobal.get("inajax");
		if (inajax != null && inajax > 0) {
			content = content.replaceAll("([\\x01-\\x09\\x0b-\\x0c\\x0e-\\x1f])+", " ");
			content = content.replace("]]>", "]]&gt;").trim();

			response.setContentType("application/xml; charset=" + JavaCenterHome.JCH_CHARSET);
			response.setHeader("Cache-Control", "no-store, private, post-check=0, pre-check=0, max-age=0");
			response.setHeader("Program", "no-cache");
			response.setDateHeader("Expirse", -1);
			out.write("<?xml version=\"1.0\" encoding=\"" + JavaCenterHome.JCH_CHARSET + "\"?>\n");
			out.write("<root><![CDATA[" + Common.trim(content) + "]]></root>");
		} else {
			if (headerCharset == 1) {
				response.setContentType("text/html; charset=" + JavaCenterHome.JCH_CHARSET);
			}
			String uri = request.getRequestURI();
			boolean isWriteJsFile = request.getAttribute("isWriteJsFile")==null ? false : (Boolean)request.getAttribute("isWriteJsFile");
			if(uri!=null && uri.endsWith("js.jsp")&&isWriteJsFile){
				String s = "(?is)\\<div\\s+class=\"pages\"\\>.+?\\</div\\>";
				String obcontent = "";
				if(!"".equals(content)){
					content = content.replaceAll(s, "");
					content = content.replaceAll("(\r|\n)", "\n");
					Matcher m = Pattern.compile("(?i)\\s+(href|src)=\"(.+?)\"").matcher(content);
					if(m.find()){
						String tag = m.group(1);
						String url = m.group(2);
						if(tag!=null&&url!=null){
							if(!Common.matches(url,"(?i)^(http\\:\\/\\/|ftp\\:\\/\\/|https\\:\\/\\/|\\/)")) {
								url = Common.getSiteUrl(request)+url;
							}
							StringBuffer buffer = new StringBuffer();
							m.appendReplacement(buffer, " "+tag+"=\""+url+"\"");
							m.appendTail(buffer);
							content = buffer.toString();
						}
					}
					String[] lines = content.split("\n");
					for(String line:lines){
						line = Common.addCSlashes(line.trim(), new char[]{'\'','\\'});
						obcontent += "document.writeln('"+line+"');\n";
					}
				}else{
					obcontent += "document.writeln('NO DATA')";
				}
				int updatetime = (Integer)request.getAttribute("updatetime");
				if(updatetime>0) {
					int id = (Integer)request.getAttribute("id");
					String path = JavaCenterHome.jchRoot+"./data/block_cache/block_"+id+".js";
					FileHelper.writeFile(path, obcontent);
				}
				content = obcontent;
			}
			out.write(content);
		}
		out.flush();
	}

	
	private String rewriteURL(String content, String regex, String pre, int group) {
		Pattern pCode = Pattern.compile(regex);
		Matcher m = pCode.matcher(content);
		StringBuffer b = new StringBuffer();
		while (m.find()) {
			String para = m.group(group);
			para = para.replace("&", "-");
			para = para.replace("=", "-");
			m.appendReplacement(b, "<a href=\"" + pre + para + ".html\"");
		}
		m.appendTail(b);
		return b.toString();
	}

	
	private String iframeURL(String content, String regex) {
		Pattern pCode = Pattern.compile(regex);
		Matcher m = pCode.matcher(content);
		StringBuffer b = new StringBuffer();
		while (m.find()) {
			String url = Common.urlEncode(m.group(1));
			m.appendReplacement(b, "<a href=\"link.jsp?url=http://" + url + "\"");
		}
		m.appendTail(b);
		return b.toString();
	}

	public void destroy() {
	}
}