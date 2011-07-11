<%@ page language="java" pageEncoding="UTF-8" isErrorPage="true"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<base href="${jchConfig.siteUrl}"/>
		<title>500 - Server Error</title>
		<meta http-equiv="content-type" content="text/html; charset=${jchConfig.charset}">
		<link rel="shortcut icon" href="favicon.ico">
	</head>
	<body bgcolor="#DFEBFA">
		<center>
			<table>
				<tr><td><img src="image/500.gif"/></td></tr>
				<tr><td>讯息： <%=request.getAttribute("javax.servlet.error.message")%></td></tr>
				<tr><td>例外： <%=request.getAttribute("javax.servlet.error.exception_type")%></td></tr>
			</table>
		</center>
	</body>
</html>
