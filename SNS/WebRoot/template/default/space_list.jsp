<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<div class="space_list"><c:choose>
 <c:when test="${empty list}"><div class="c_form">没有相关成员。</div></c:when>
 <c:otherwise>
  <c:forEach items="${list}" var="value" varStatus="key">
   <table cellspacing="0" cellpadding="0" width="100%">
    <tr>
     <td width="65"><div class="avatar48"><a href="space.jsp?uid=${value.uid}">${jch:avatar1(value.uid,sGlobal, sConfig)}</a></div></td>
     <td>
      <h2>
       <c:if test="${!jch:jchEmpty(ols[value.uid])}"><img src="image/online_icon.gif" align="absmiddle"></c:if>
       <a href="space.jsp?uid=${value.uid}" title="${sNames[value.uid]}"${value.gColor}>${sNames[value.uid]}</a>
      <c:if test="${not empty value.name && sNames[value.uid]!=value.name}"><span class="gray">(${value.name})</span></c:if>
       ${value.gIcon}
       <c:if test="${value.videostatus>0}"><img src="image/videophoto.gif" align="absmiddle"></c:if>
      </h2>
      <c:choose><c:when test="${value.sex==2}"><p>美女</p></c:when><c:when test="${value.sex==1}"><p>帅哥</p></c:when></c:choose>
      <p><c:if test="${param.view=='show'}">竞价</c:if>积分：${value.credit} / <c:if test="${value.experience>0}">经验：${value.experience} / </c:if>人气：${value.viewnum} / 好友：${value.friendnum}</p>
      <c:if test="${!jch:jchEmpty(value.note)}">${value.note}</c:if>
     </td>
     <td width="100">
      <ul class="line_list">
       <li><img src="image/icon/pm.gif"><a href="cp.jsp?ac=pm&uid=${value.uid}" id="a_pm_${key.index}" onclick="ajaxmenu(event, this.id, 1)" title="发消息">发送消息</a></li>
       <li><img src="image/icon/poke.gif"><a href="cp.jsp?ac=poke&op=send&uid=${value.uid}" id="a_poke_${key.index}" onclick="ajaxmenu(event, this.id, 1)" title="打招呼">打个招呼</a></li>
       <c:if test="${!value.isfriend}"><li><img src="image/icon/friend.gif"><a href="cp.jsp?ac=friend&op=add&uid=${value.uid}" id="a_friend_${key.index}" onclick="ajaxmenu(event, this.id, 1)" title="加好友">加为好友</a></li></c:if>
      </ul>
     </td>
    </tr>
   </table>
  </c:forEach>
  <div class="page">${multi}</div>
 </c:otherwise>
</c:choose></div>