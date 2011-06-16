<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://medecision.com" prefix="med" %>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<html>
  <head>
    <base href="<%=basePath%>"/>
    <title>Alineo&#8482; by MEDecision</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<link href="utils/screen_logon.css" media="screen" rel="stylesheet" type="text/css">
    <link href="utils/header_logon.css" media="screen" rel="stylesheet" type="text/css">
    <style type="text/css">
        .footer{
            float:right;position:absolute;right:0%;min-width:900px;text-align:right;margin-right:8px;bottom:0%;margin-bottom:8px;                        
        }        

    </style>
    <script language="javascript" src="js/lib/jquery-compressed.js"></script>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
<%@ include file="genericHeaderInclude.jsp" %>
</head>
<f:view>
<f:loadBundle basename="com.med.careplannerweb.jsf.bundle.messages" var="bundle"/>
<h:form>
  
  <body>
  <div class="hdr">
      <div class="hdr_queue_utility">
         <span class="hdr_home_link">Medecision</span></div>
  </div>
  <div class="cont">
            <div class="cont_area">
                <ul class="logon">
                    <li>
                        <dl>
                            <dt class="logon_head">
                            	<span>
                            		Http Status 404: You have reached this page in error.  The following link will take you back to the login page.
                            		<h:commandLink styleClass="utility_logout_btn" action="#{loginBean.logoff}" title="Log Out" value="Log out" immediate="true" rendered="#{ExceptionBean.uncheckedExceptionHandledByActionListener}" id="clickLogoutLink" />
                            		<med:div rendered="#{(!ExceptionBean.uncheckedExceptionHandledByActionListener)}">
                            			<f:verbatim>
                            			<a href="<%=basePath%>login.faces" id="clickLogoutLink" > Log Out </a>
                            			</f:verbatim> 
                            		</med:div>
                            	</span>
                            </dt>
                            <dd>
                            	<BR><BR>
 								<h:inputHidden id="hiddenErrorMessage" value="Status 404 page"/>
                            </dd>
                            <dt class="logon_foot"></dt>
                        </dl>
                    </li>
                </ul>
                <div class="footer">
                    <h:outputText value="#{bundle.copyright}"/>
                </div>
            </div>
        </div>
   <br>
  </body>
 
</h:form>
</f:view>
</html>




