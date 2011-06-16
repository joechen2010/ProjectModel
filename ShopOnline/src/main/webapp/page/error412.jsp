<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<%@page import="com.med.utilities.core.ReturnStatus"%>
<%@page import="com.med.utilities.core.ReturnStatusItem"%>
<%@page import="com.med.configuration.Globals"%><html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  	<head>
		
  <base href="<%=basePath%>"/>
    <title>Alineo&#8482; by MEDecision</title>
    <meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="this is my page">
    <meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
	<link href="utils/screen_logon.css" media="screen" rel="stylesheet" type="text/css" >
	<link href="utils/header_logon.css" media="screen" rel="stylesheet" type="text/css" >
	<link href="utils/style.css" rel="stylesheet" type="text/css">
	<script language="javascript" src="js/lib/jquery-compressed.js"></script>
	<script language="javascript" src="js/login.js"></script>
<%@ include file="genericHeaderInclude.jsp" %>
</head>
  
  <body>
<f:view>
<h:form id="mainForm">
<div class="hdr">
	<div class="hdr_queue_utility">
	<span class="hdr_home_link">Medecision</span>	</div>
</div>
<div class="cont">
	<div class="cont_area">
	<ul class="logon">
		<li class="logon_left" style="padding-left:250px;">
			<dl>
				<dt class="logon_head">
					<div style="text-align:center;padding-top:6px"><font class="subhead"><B>Cannot Access Alineo</B></font></div>
				</dt>
<%
    ReturnStatus status = (ReturnStatus) request.getAttribute("412");
    String loginPage = Globals.getString("com.med.security.authenticate.LOGOFF_REDIRECT_PAGE");
    if (loginPage == null || loginPage.length() < 1) {
        loginPage = path + "/login.faces";
    }
	if (status != null && status.getErrorResultStatusItems() != null && status.getErrorResultStatusItems().length > 0) {
	    ReturnStatusItem[] errorList = status.getErrorResultStatusItems();
	    for (int i = 0; i < errorList.length; i++) {
	        if (errorList[i] != null) {
%>
				<dd style="text-align:center"><FONT CLASS="ttext"><%=errorList[i].getDefaultMessage()%></FONT></dd>
<%	    	}
	    }
	} else { %>
				<dd style="text-align:center"><FONT CLASS="ttext">User not found or is not active.</FONT></dd>
				<dd style="text-align:center"><FONT CLASS="ttext">Please contact your System Administrator.</FONT></dd>
	    
<%	}    %>
				<dd style="text-align:center"><a href="<%=loginPage%>" class="admin">Return to Login</a></dd>
				<dt class="logon_foot"></dt>
			</dl>
		</li>
	</ul>
	</div>
</div>
</h:form>
</f:view>
  </body>
</html>
