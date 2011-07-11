<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}" />
<h2 class="title"><a href="do.jsp?ac=stat">站点趋势统计</a></h2>
<table cellspacing="0" cellpadding="0" class="formtable">
 <caption>
  <h2>统计分类</h2>
  <p>站点趋势统计系统，会记录每日站点的发展概况。通过每日的趋势变化，为站长运营站点提供科学的数据基础。</p>
 </caption>
 <tr>
  <td>
   <ul class="line_list">
    <li>
     <strong>基础数据</strong>：
     <a href="do.jsp?ac=stat"${actives['all']}>综合概况</a> &nbsp;
     <c:forEach items="${cols.login}" var="value">
      <a href="do.jsp?ac=stat&type=${value}"${actives[value]}> <bean:message key="cp_do_stat_${value}"/></a> &nbsp;
     </c:forEach>
    </li>
    <li>
     <strong>信息发布</strong>：
     <c:forEach items="${cols.add}" var="value">
      <a href="do.jsp?ac=stat&type=${value}"${actives[value]}> <bean:message key="cp_do_stat_${value}"/></a> &nbsp;
     </c:forEach>
    </li>
    <li>
     <strong>信息互动</strong>：
     <c:forEach items="${cols.comment}" var="value">
      <a href="do.jsp?ac=stat&type=${value}"${actives[value]}> <bean:message key="cp_do_stat_${value}"/></a> &nbsp;
     </c:forEach>
    </li>
    <li>
     <strong>用户互动</strong>：
     <c:forEach items="${cols.space}" var="value">
      <a href="do.jsp?ac=stat&type=${value}"${actives[value]}> <bean:message key="cp_do_stat_${value}"/></a> &nbsp;
     </c:forEach>
    </li>
   </ul>
  </td>
 </tr>
</table>
<br>
<table cellspacing="0" cellpadding="0" class="formtable">
 <c:choose>
  <c:when test="${type == 'all'}">
   <caption>
    <h2>综合概况</h2>
    <p>这里看到的是站点的综合概况发展统计。(需要至少统计2天后才有效)</p>
   </caption>
   <tr>
    <td>
     <ul class="line_list">
      <li><strong>来访用户</strong>：指的是每天访问本站的唯一用户数。一个用户访问多次，也只算一次；</li>
      <li><strong>信息发布</strong>：指的是每天发布记录、日志、图片、话题、投票、活动和分享的总数量；</li>
      <li><strong>信息互动</strong>：指的是每天进行以上信息互相评论和表态、参加投票、参与活动的互动总数量；</li>
      <li><strong>用户互动</strong>：指的是每天用户之间互相留言、打招呼的互动总数量；</li>
     </ul>
    </td>
   </tr>
  </c:when>
  <c:otherwise>
   <caption>
    <h2><bean:message key="cp_do_stat_${type}"/></h2>
   </caption>
  </c:otherwise>
 </c:choose>
 <tr>
  <td>
   <div class="borderbox">
    <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=9,0,45,0" width="750" height="300">
     <param name="movie" value="image/stat.swf?${statuspara}" />
     <param name="quality" value="high" />
     <param name="WMode" value="transparent" />
     <embed src="image/stat.swf?${statuspara}" quality="high" pluginspage="http://www.adobe.com/shockwave/download/download.cgi?P1_Prod_Version=ShockwaveFlash" type="application/x-shockwave-flash" width="750" height="300"></embed>
    </object>
   </div>
  </td>
 </tr>
</table>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}" />