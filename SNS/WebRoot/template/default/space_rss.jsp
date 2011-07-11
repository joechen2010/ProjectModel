<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<?xml version="1.0" encoding="${jchConfig.charset}"?>
<rss version="2.0">
 <channel>
 <c:choose><c:when test="${space.uid>0}">
  <title>${sNames[space.uid]}</title>
  <link>${space.space_url}</link>
  <description>${sNames[space.uid]}</description>
 </c:when><c:otherwise>
  <title>${sConfig.sitename}</title>
  <link>${siteurl}</link>
  <description>${sConfig.sitename}</description>
 </c:otherwise></c:choose>
 <copyright>${sConfig.sitename}</copyright>
 <generator>JavaCenter Home ${JCH_VERSION}</generator>
 <lastBuildDate>${space.lastupdate}</lastBuildDate>
 <c:forEach items="${list}" var="value"><item>
  <title>${value.subject}</title>
  <link>${siteurl}space.jsp?uid=${value.uid}&amp;do=blog&amp;id=${value.blogid}</link>
  <description><![CDATA[${value.message}]]></description>
  <author>${sNames[value.uid]}</author>
  <pubDate>${value.dateline}</pubDate>
 </item></c:forEach>
 </channel>
</rss>