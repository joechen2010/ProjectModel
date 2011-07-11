<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="java.sql.*"%>
<%@ page import="java.io.*"%>
<%@ page import="cn.jcenterhome.util.PropertiesHelper"%>
<%@ page import="cn.jcenterhome.util.JavaCenterHome"%>
<%@ page import="cn.jcenterhome.util.Serializer"%>
<%@ page import="cn.jcenterhome.util.Common"%>
<%@ page import="cn.jcenterhome.util.BeanFactory;"%>
<%!
	private Connection connection = null;
	private String errorMessage = null;
	public void showMessage(String message, int next, int jump,	HttpServletRequest request, HttpServletResponse response) {
		String theURL=(String)request.getAttribute("theurl");
		theURL=theURL==null ? "" : theURL;
		String nextStr = "";
		String backStr = "";
		if (Common.empty(next)) {
			backStr += "<a href=\"javascript:history.go(-1);\">返回上一步</a>";
		} else if (next == 999) {
		} else {
			String url_forward = theURL + "?step=" + next;
			if (jump != 0) {
				nextStr += "<a href=\"" + url_forward + "\">请稍等...</a><script>setTimeout(\"window.location.href ='" + url_forward + "';\", 1000);</script>";
			} else {
				nextStr += "<a href=\"" + url_forward + "\">继续下一步</a>";
				backStr += "<a href=\"javascript:history.go(-1);\">返回上一步</a>";
			}
		}
		try {
			request.getRequestDispatcher("/install/install_header.jsp").include(request, response);
			PrintWriter out = response.getWriter();
			out.print("<table><tr><td>" + message + "</td></tr><tr><td>&nbsp;</td></tr><tr><td>" + backStr + " " + nextStr + "</td></tr></table>");
			out.flush();
			request.getRequestDispatcher("/install/install_footer.jsp").include(request, response);
			return;
		} catch (Exception e) {

		}
	}
	public boolean checkFilePermissions(String path, boolean isFile) {
		File file = null;
		try {
			if (isFile) {
				file = new File(path);
			} else {
				file = new File(path + "/install_tmptest.data");
				if (!file.createNewFile()) {
					return false;
				}
			}
			if (!file.canWrite()) {
				return false;
			}
			if (!isFile) {
				FileOutputStream fins = new FileOutputStream(file);
				fins.write(new byte[]{0});
				fins.flush();
				fins.close();
				fins = null;
				if (!file.delete()) {
					return false;
				}
				File dirFile = new File(path + "/install_tmpdir");
				if (dirFile.isDirectory()) {
					if (!dirFile.delete()) {
						return false;
					}
				}
				if (!dirFile.mkdir()) {
					return false;
				}
				if (!dirFile.delete()) {
					return false;
				}
			}
			return true;
		} catch (IOException ioe) {
			return false;
		}
	}
	public boolean mysql_connect(String dbHost, String dbPort, String dbName,
			String dbUser, String dbPw, String dbCharset) {
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://" + dbHost + ":"
					+ dbPort + (Common.empty(dbName) ? "" : "/" + dbName)
					+ "?useUnicode=true&characterEncoding=" + dbCharset,
					dbUser, dbPw);
			if (errorMessage != null) {
				errorMessage = null;
			}
		} catch (Exception e) {
			errorMessage = e.getMessage();
			return false;
		}
		if (conn != null) {
			connection = conn;
			return true;
		}
		return false;
	}
	public ResultSet mysql_query(String sql) {
		PreparedStatement pstmt = null;
		try {
			pstmt = connection.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			if (errorMessage != null) {
				errorMessage = null;
			}
			return rs;
		} catch (SQLException e) {
			errorMessage = e.getMessage();
		}
		return null;
	}
	public boolean mysql_update(String sql) {
		PreparedStatement pstmt =null;
		try {
			pstmt = connection.prepareStatement(sql);
			pstmt.execute();
			if (errorMessage != null) {
				errorMessage = null;
			}
			return true;
		} catch (SQLException e) {
			errorMessage = e.getMessage();
		}finally {
			try {
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
			} catch (SQLException e) {
				return false;
			}
		}
		return false;
	}
	public String mysql_get_server_info() {
		ResultSet rs = mysql_query("select version()");
		String version = "";
		try {
			while (rs.next()) {
				version = rs.getString(1);
			}
			if (errorMessage != null) {
				errorMessage = null;
			}
		} catch (SQLException e) {
			errorMessage = e.getMessage();
		}finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException e) {
			}
		}
		return version;
	}
	public boolean mysql_select_db(String dbname) {
		return mysql_update("use " + dbname);
	}
	public void mysql_close() {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
				connection = null;
			}
			if (errorMessage != null) {
				errorMessage = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public String sreadFile(File file) {
		StringBuffer strBuf = new StringBuffer();
		try {
			if (file != null && file.canRead()) {
				InputStream in = new FileInputStream(file);
				InputStreamReader isr = new InputStreamReader(in);
				BufferedReader br = new BufferedReader(isr);
				String sqlContent = br.readLine();
				while (sqlContent != null) {
					if ((!sqlContent.startsWith("--"))&& (!sqlContent.startsWith("#"))) {
						strBuf.append(sqlContent + "\n");
					}
					sqlContent = br.readLine();
				}
				br.close();
				br=null;
				isr.close();
				isr=null;
				in.close();
				in=null;
			}
			if (errorMessage != null) {
				errorMessage = null;
			}
		} catch (IOException ioe) {
			errorMessage = ioe.getMessage();
			ioe.printStackTrace();
		}
		return strBuf.toString();
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	public String createTable(String sqlContent,String dbCharset) throws Exception{
		sqlContent=sqlContent.replaceAll("(\r\n|\r|\n)", "\n");
		String[] queries=sqlContent.trim().split(";\n");
		if(queries!=null && queries.length>0)
		{
			for(String query:queries)
			{
				query=query.trim();
				if(!"".equals(query)) {
					if(query.startsWith("CREATE TABLE")) {
						String tableName = query.substring(13, query.indexOf("(")-1);
						if(!mysql_update("DROP TABLE IF EXISTS `"+tableName+"`")){
							return tableName;
						}
						List<String> types=Common.pregMatch(query,"(?i)MYISAM|HEAP");
						String type=types!=null&&types.size()>0?types.get(0):"MYISAM";
						boolean containsSession=tableName.indexOf("session")>=0;
						String charSet=Common.empty(dbCharset) ? "" : " DEFAULT CHARSET="+dbCharset.replace("-","");
						String finalStr=(mysql_get_server_info().compareTo("4.1")> 0 ? " ENGINE="+(containsSession ? "MEMORY" : type)+charSet:" TYPE="+(containsSession ? "HEAP" : type));
						query=query.substring(0,query.lastIndexOf(")")+1)+finalStr;
						if(!mysql_update(query)){
							return tableName;
						}
					}
				}
			}
		}
		return null;
	}
	
	public void data_set(String var, Object dataValue, boolean clean) {
		if (clean) {
			mysql_update("DELETE FROM " + JavaCenterHome.getTableName("data")
					+ " WHERE var='" + var + "'");
		} else {
			if (dataValue instanceof Map || dataValue instanceof List) {
				dataValue = Serializer.serialize(Common.sStripSlashes(dataValue));
			}
			mysql_update("REPLACE INTO " + JavaCenterHome.getTableName("data")
					+ " (var,datavalue,dateline) VALUES ('" + var + "','" + Common.addSlashes((String) dataValue)
					+ "','" + Common.time() + "')");
		}
	}
	
	public boolean submitcheck(HttpServletRequest request,HttpServletResponse response, String var){
		if ("POST".equals(request.getMethod()) && !Common.empty(request.getParameter(var))) {
			String referer = request.getHeader("Referer");
			Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
			if (Common.empty(referer)
					|| referer.replaceAll("https?://([^:/]+).*", "$1").equals(
							request.getHeader("Host").replaceAll("([^:]+).*", "$1"))
					&& Common.formHash(sGlobal, new HashMap(),false).equals(request.getParameter("formhash"))) {
				return true;
			} else {
				showMessage(Common.getMessage(request,"submit_invalid"),
						0, 0, request, response);
						return false;
			}
		}
		return false;
	}
	public int checkName(String userName,HttpServletRequest request, HttpServletResponse response) {
		userName = Common.addSlashes(Common.stripSlashes(Common.trim(userName)));
		String guestexp = "\\xA1\\xA1|\\xAC\\xA3|^Guest|^\\xD3\\xCE\\xBF\\xCD|\\xB9\\x43\\xAB\\xC8";
		int len = Common.strlen(userName);
		if (len > 15 || len < 3
				|| Common.matches(userName, "(?is)\\s+|^c:\\con\\con|[%,\\*\"\\s\\<\\>\\&]|" + guestexp)) {
			return -1;
		}
		return 1;
	}
	
	private void initConfig(HttpServletRequest request) throws IOException {
		PropertiesHelper propHelper = new PropertiesHelper(JavaCenterHome.jchRoot + "config.properties");
		Properties config = propHelper.getProperties();
		Set<Object> keys = config.keySet();
		Map<String, String> jchConfig=new HashMap<String,String>();
		for (Object key : keys) {
			String k = (String) key;
			String v = (String) config.get(key);
			jchConfig.put(k, v);
		}
		String siteUrl = jchConfig.get("siteUrl");
		if (Common.empty(siteUrl)) {
			jchConfig.put("siteUrl", Common.getSiteUrl(request));
		}
		JavaCenterHome.jchConfig = jchConfig;
		ServletContext servletContext=request.getSession().getServletContext();
		servletContext.setAttribute("jchConfig", JavaCenterHome.jchConfig);
		config = null;
		propHelper = null;
	}
%>