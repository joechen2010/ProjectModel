<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" pageEncoding="ISO-8859-1" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>

    <base href="<%=basePath%>"/>
    <title>Alineo&#8482; by MEDecision</title>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="this is my page">
    <meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
    <link href="utils/screen_logon.css" media="screen" rel="stylesheet" type="text/css">
    <link href="utils/header_logon.css" media="screen" rel="stylesheet" type="text/css">
    <style type="text/css">
        .footer{
            float:right;position:absolute;right:0%;min-width:900px;text-align:right;margin-right:8px;bottom:0%;margin-bottom:8px;                        
        }        

    </style>
    <script language="javascript" src="js/lib/jquery-compressed.js"></script>
    <script language="javascript" src="js/login.js"></script>
<%@ include file="genericHeaderInclude.jsp" %>
</head>

<body >
<f:view>
    <f:loadBundle basename="com.med.careplannerweb.jsf.bundle.messages"
                  var="bundle"/>
    <h:form id="mainForm" onkeypress="return clickSubmit(event);" onsubmit="return clickSubmit(event);">
        <jsp:include flush="true" page="Messages.jsp"/>
        <div class="hdr">
            <div class="hdr_queue_utility">
                <span class="hdr_home_link">Medecision</span></div>
        </div>
        <div class="cont">
            <div class="cont_area">
                <ul class="logon">
                    <li class="logon_left">
                        <dl>
                            <dt class="logon_head"><span
                                    class="logon_title"></span><a
                                    onclick="javascript:window.open('<%=basePath%>WebHelp/index.htm','Help_Window')"
                                    class="logon_help" title="Help"
                                    style="cursor:pointer">Help</a></dt>
                            <dd class="logon_button">
                                <h:commandLink id="submitbutton"
                                               action="#{loginBean.authenticate}"
                                               styleClass="login_btn" 
                                               value="login_btn"></h:commandLink>
                            </dd>
                            <dt class="logon_foot"></dt>
                        </dl>
                    </li>
                </ul>
                <div class="footer" id="copyrightText">
                    <h:outputText value="#{bundle.copyright}"/>
                </div>
            </div>
        </div>
    </h:form>
</f:view>
<script language="JavaScript">
	document.getElementById('mainForm:submitbutton').click();
</script>
</body>
</html>
