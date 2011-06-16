<%@ page language="java" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<f:subview id="error">
<head>
	<base href="<%=basePath%>"/>
	<title>Alineo&#8482; by MEDecision</title>
	
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<link style="" href="utils/style.css" type="text/css" rel="stylesheet">

<%@ include file="genericHeaderInclude.jsp" %>
</head>
  
 <body>
<h:form id="mainForm">
<jsp:include flush="true" page="titleHeader.jsp"></jsp:include>
<!--  END HEADER -->
 <p><h:outputText id="unexceptederrorText" value="An unexpected Error has occured in the application during startup."/></p>
 <p><h:outputText id="stacktraceerrorText" value="The stack trace of the error is as follows:" /></p>
 <h:inputTextarea value="#{ExceptionBean.startupException}"  id="startupExceptionTextArea" rows="25" cols="100" readonly="true"/>
 </h:form>
 </body>
 </f:subview>
 </html>
