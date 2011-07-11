<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>

<h3 class="feed_header">
	<a href="space.jsp?uid=${space.uid}" class="r_option">返回个人主页</a>
	个人资料
</h3>
<br>
<table cellspacing="0" cellpadding="0" class="infotable">

	<tr>
		<th width="120">经验:</th>
		<td>${space.experience} ${space.star}</td>
	</tr>
	<tr>
		<th>用户组:</th>
		<td>${globalGroupTitle[space.groupid].grouptitle} ${gIcon}</td>
	</tr>
	<tr>
		<th>积分:</th>
		<td>${space.credit}</td>
	</tr>
	<tr>
		<th>访问人次:</th>
		<td>${space.viewnum}</td>
	</tr>
	<tr>
		<th>创建时间:</th>
		<td><jch:date dateformat="yyyy-MM-dd" timestamp="${space.dateline}" format="1"/></td>
	</tr>
	<tr>
		<th>上次登录:</th>
		<td><c:if test="${not empty space.lastlogin}"><jch:date dateformat="yyyy-MM-dd" timestamp="${space.lastlogin}" format="1"/></c:if></td>
	</tr>
	<c:if test="${not empty isonline}">
	<tr style="color:red;">
		<th>最后活跃:</th>
		<td>${isonline} (当前在线)</td>
	</tr>
	</c:if>

<c:if test="${space.profile_base}">
	<tr>
		<td class="td_title" colspan="2">基本资料</td>
	</tr>
	<tr>
		<td colspan="2">&nbsp;</td>
	</tr>
</c:if>
<c:if test="${not empty space.sex}">
	<tr><th>性别:</th><td>${space.sex}</td></tr>
</c:if>
<c:if test="${not empty space.birth}">
	<tr><th>生日:</th><td>${space.birth}</td></tr>
</c:if>
<c:if test="${not empty space.blood}">
	<tr><th>血型:</th><td>${space.blood}</td></tr>
</c:if>
<c:if test="${not empty space.marry}">
	<tr><th>婚恋:</th><td>${space.marry}</td></tr>
</c:if>
<c:if test="${not empty space.residecity}">
	<tr><th>居住:</th><td>${space.residecity}</td></tr>
</c:if>
<c:if test="${not empty space.birthcity}">
	<tr><th>家乡:</th><td>${space.birthcity}</td></tr>
</c:if>

<c:forEach items="${fields}" var="field">
	<c:if test="${not empty field.value.fieldvalue}">
	<tr><th>${field.value.title}:</th><td>
	<c:choose>
	<c:when test="${field.value.allowsearch>0}">
	<a href="cp.jsp?ac=friend&op=search&field_${field.key}=${field.value.urlvalue}&searchmode=1">${field.value.fieldvalue}</a>
	</c:when>
	<c:otherwise>
	${field.value.fieldvalue}
	</c:otherwise>
	</c:choose></td></tr>
	</c:if>
</c:forEach>

<c:if test="${space.profile_contact}">
	<tr>
		<td class="td_title" colspan="2">联系方式</td>
	</tr>
	<tr>
		<td colspan="2">&nbsp;</td>
	</tr>
</c:if>
<c:if test="${not empty space.mobile}">
	<tr><th>手机:</th><td>${space.mobile}</td></tr>
</c:if>
<c:if test="${not empty space.qq}">
	<tr><th>QQ:</th><td>${space.qq}</td></tr>
</c:if>
<c:if test="${not empty space.msn}">
	<tr><th>MSN:</th><td>${space.msn}</td></tr>
</c:if>

<c:if test="${not empty space.edu}">
	<tr>
		<td class="td_title" colspan="2">教育情况</td>
	</tr>
	<tr>
		<td colspan="2">&nbsp;</td>
	</tr>

	<tr><th>学校:</th><td>
		<c:forEach items="${space.edu}" var="value">
		<p><a href="space.jsp?do=mtag&tagname=${jch:urlEncoder(value.title)}">${value.title}</a> <a href="space.jsp?do=mtag&tagname=${jch:urlEncoder(value.subtitle)}">${value.subtitle}</a> <span class="gray">(${value.startyear} 年)</span></p>
		</c:forEach>
	</td></tr>
</c:if>

<c:if test="${not empty space.work}">
	<tr>
		<td class="td_title" colspan="2">工作情况</td>
	</tr>
	<tr>
		<td colspan="2">&nbsp;</td>
	</tr>

	<tr><th>公司或机构:</th><td>
	<c:forEach items="${space.work}" var="value">
		<p><a href="space.jsp?do=mtag&tagname=${jch:urlEncoder(value.title)}">${value.title}</a> <a href="space.jsp?do=mtag&tagname=${jch:urlEncoder(value.subtitle)}">${value.subtitle}</a> <span class="gray">(${value.startyear}年${value.startmonth}月 ~ 
			<c:if test="${value.endyear>0}">${value.endyear}年</c:if>
			<c:if test="${value.endmonth>0}">${value.endmonth}月</c:if>
			<c:if test="${value.endyear==0 && value.endmonth==0}">现在</c:if>)</span></p>
	</c:forEach>
			</td></tr>
</c:if>

<c:if test="${not empty space.info}">
	<tr>
		<td class="td_title" colspan="2">个人介绍</td>
	</tr>
	<tr>
		<td colspan="2">&nbsp;</td>
	</tr>
<c:forEach items="${space.info}" var="value">
<c:if test="${!jch:jchEmpty(value.title)}">
	<tr><th>${infos[value.subtype]}:</th><td>${value.title}
	</td></tr>
</c:if>
</c:forEach>
</c:if>

</table><br>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>