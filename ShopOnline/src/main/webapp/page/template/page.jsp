<%@ page contentType="text/html; charset=GBK"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="sql" uri="http://java.sun.com/jstl/sql_rt"%>
<%@taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/elfunction.tld" prefix="el" %>
<%@ taglib uri="/WEB-INF/oscache.tld" prefix="os" %>
<%@ page import="com.joe.utilities.common.util.MyConnection"%>
<html>
	<head>
		<title>JSTL的SQL标签</title>
	</head>

	<body bgcolor="white">
	
	<os:cache time="30">



		<%MyConnection myconn= new MyConnection();
		  String[] dbp = myconn.getDBbaseParam(); 
		  String[] tableHeader = new String[]{"企业代码","公司名称","登录帐号"};
		  request.setAttribute("dbp",dbp);
		  request.setAttribute("tableHeader",tableHeader);
		  System.out.println(">>>>>>>>>>>>oscach>>>>>>>>>");
		 %>
		 
		 
		<c:set var="startUrl" value="page.jsp?page=" />
		<c:set var="endUrl" value="" />
		<c:set var="noOfRows" value="10" />
		<c:set var="max_page_value" value="10" />
		
		
		<sql:setDataSource driver="${dbp[0]}" url="${dbp[1]}" user="${dbp[2]}" password="${dbp[3]}" var="conn" />
		<c:if test="${custList == null}">
			<sql:query var="custList" scope="session" sql="SELECT  ecode,gprspianhao,loginname FROM account ORDER BY id asc" dataSource="${conn}" />
		</c:if>
		 
		<c:set var="rowsCount" value="${custList.rowCount}" />
		<c:set var="currentPage" value="${param.page == null ? 1 : param.page}" />
		<fmt:formatNumber var="pageCount" type="number"	value="${rowsCount / noOfRows}"	maxFractionDigits="0" />
		<fmt:formatNumber var="harfShift" type="number"	value="${max_page_value/2}"	maxFractionDigits="0" />
		<c:set var="total_page" value="${(rowsCount % noOfRows) == 0 ? pageCount : (pageCount+1)}" />
		<c:set var="start" value="${(currentPage-1)*noOfRows}" />
		<c:set var="place_shift" value="${currentPage < (total_page-harfShift) ? ((currentPage -harfShift)>0?(currentPage -harfShift):1) : (total_page-harfShift)}" />
		
		
		<c:choose>
			<c:when test="${rowsCount == 0}">此处不再有其他客户...</c:when>
			<c:otherwise>
				
				<table border="1" align="center">
					<c:forEach var= "h" items="${tableHeader}"><th>${h}</th></c:forEach>					
					<c:forEach items="${custList.rows}" var="row" begin="${start}" end="${start +noOfRows - 1}">
						<tr> 
							<td>
								<c:out value="${row.ecode}" />
							</td>
							<td>
								<c:out value="${row.gprspianhao}" />
							</td>
							<td>
								<c:out value="${row.loginname}" />
							</td>
						</tr>
					</c:forEach>
				</table>
			</c:otherwise>
		</c:choose>
			分页显示：
			<c:if test="${currentPage>1}"><a href="${startUrl}1${endUrl}">首页</a>	</c:if>
			<c:if test="${currentPage>1}">
				<a href="${startUrl}${currentPage-1}${endUrl}" />上一页</a>	
			</c:if>
			
			<c:if test="${currentPage>harfShift && total_page>10}"><a href="${startUrl}${(currentPage -harfShift)}${endUrl}" /> << </a>	</c:if>
			<c:forEach   var="i" begin="${1+ place_shift}" step="1" end="${max_page_value+place_shift}">
				<c:choose>
					<c:when test="${i==currentPage}">
						&nbsp;<b>&nbsp;${i}&nbsp;</b>&nbsp;
					</c:when>
					<c:otherwise>
						<a href="${startUrl}${i}${endUrl}">${i}</a>
					</c:otherwise>
				</c:choose>
			</c:forEach>
    
        	<c:choose>
				<c:when test="${((currentPage+1)*noOfRows)>rowsCount}">
					<c:if test="${currentPage>harfShift && total_page>10 && currentPage<(total_page-harfShift)}"><a href="${startUrl}${currentPage+harfShift}${endUrl}">  >> </a>	</c:if>
				</c:when>
				<c:otherwise>
					<c:if test="${(currentPage<(total_page-harfShift)) && (total_page>10)}"><a href="${startUrl}${currentPage +max_page_value}${endUrl}">  >> </a>	</c:if>
					<a href="${startUrl}${currentPage + 1}${endUrl}">下一页</a>
				</c:otherwise>
			</c:choose>
        	<c:if test="${(total_page - 1) * noOfRows > currentPage}"><a href="${startUrl}${total_page}${endUrl}">尾页</a>	</c:if>
              	每页显示${noOfRows}项&nbsp; 共${total_page}页 &nbsp; 共${rowsCount}条记录 
			
			</br>
			${el:createPager(max_page_value, rowsCount,noOfRows,currentPage,startUrl,endUrl)}
	</os:cache>
			
	</body>
</html>
