<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
            String path = request.getContextPath();
            String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<base href="<%=basePath%>"/>

		<title>Alineo&#8482; by MEDecision</title>
		<link type="text/css" href="utils/style.css" rel="stylesheet">
		<Style>
.actiondeact {font-family:verdana; font-size:12px; color:#669; font-weight:bold; text-decoration:none;};
.header		{background-color:#cccc99;}; 
.odd		{background-color:#FFFFFF;};
.even		{background-color:#F9F9E9;};
</Style>

<%@ include file="genericHeaderInclude.jsp" %>
</head>

	<body>
		<f:view>
			<h:form id="mainForm">

				<table cellpadding="2" border="0" width="766" cellspacing="0">
					<tr>
						<td style="PADDING-LEFT: 15px;">
							<h:outputText id="buttonLabel" value="Select the button below to make current database settings for tables and options take effect." />
						</td>
					</tr>
					
					<tr>
						<td style="PADDING-LEFT: 15px;">
							<h:commandButton id="flushCache" action="#{CacheManagementBean.flushCache}" value="Take effect" />
						</td>
					</tr>
					
					<c:if test="${CacheManagementBean.message != ''}">
					
						<tr>
							<td style="PADDING-LEFT: 15px;"/>
						</tr>
					
						<tr>
							<td style="PADDING-LEFT: 15px;">
								<h:outputText id="messageText" value="#{CacheManagementBean.message}" />
							</td>
						</tr>
					</c:if>
				</table>
				
			</h:form>
		</f:view>
	</body>
</html>
