<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}" />
<jsp:include page="${jch:template(sConfig, sGlobal, 'cp_header.jsp')}" />
<c:choose><c:when test="${param.op=='diy'}">
 <div class="h_status"><a href="cp.jsp?ac=theme">返回主页风格列表</a></div>
 <div class="d_content">
  <form method="post" action="cp.jsp?ac=theme" class="c_form">
   <c:if test="${!empty param.view}"><div class="notice">最后保存时间：${lastSaveTime}，<a href="space.jsp" target="_blank">新窗口预览个人主页</a></div></c:if>
   <table cellspacing="6" cellpadding="0" width="100%">
    <tr>
     <td>
      <c:choose>
       <c:when test="${allowCss}">自己设计个人主页风格，这需要你了解一定的CSS知识<br>CSS内容:<br><textarea name="css" style="width: 98%;" rows="20">${space.css}</textarea></c:when>
       <c:otherwise>您当前不能自定义CSS风格，您可以设置屏蔽他人风格，或者<a href="cp.jsp?ac=theme">返回主页风格列表</a>选择系统的风格。</c:otherwise>
      </c:choose>
     </td>
    </tr>
    <tr>
     <td>
      <input type="checkbox" name="nocss" value="1"${space.nocss==1 ? " checked" : ""}> <strong>屏蔽其他人风格</strong><br>
      别人美化不当可能会造成查看其个人主页时出现问题。你可以勾选此选项，则查看所有人的个人主页将显示默认效果。
     </td>
    </tr>
    <tr><td><input type="submit" name="csssubmit" id="csssubmit" value="保存修改" class="submit" /></td></tr>
   </table>
   <input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
  </form>
 </div>
</c:when><c:otherwise>
 <div class="c_form">
  <table cellspacing="0" cellpadding="0" class="formtable">
   <caption>
    <h2>主页风格</h2>
    <p>选择自己喜欢的个人主页风格。<c:if test="${jch:checkPerm(pageContext.request, pageContext.response,'admin')}"><a href="http://j.jsprun.net/theme">作为站长，您可以来这里下载更多的风格提供给站内用户选择使用。</a></c:if></p>
   </caption>
   <tr><c:forEach items="${themes}" var="theme" varStatus="index"><td align="center">
    <div style="border: 1px solid #CCC; padding: 2px; width: 110px;"><c:choose><c:when test="${theme.dir=='jchomediy'}"><a href="cp.jsp?ac=theme&op=diy"><img src="${theme.pic}" alt="${theme.name}" /></a></c:when><c:otherwise><a href="space.jsp?theme=${theme.dir}" target="_blank"><c:choose><c:when test="${empty theme.pic}"><img src="theme/${theme.dir}/preview.jpg" alt="${theme.name}" style="width: 110px; height: 120px" /></c:when><c:otherwise><img src="${theme.pic}" alt="${theme.name}" style="width: 110px; height: 120px" /></c:otherwise></c:choose></a></c:otherwise></c:choose></div>
    <div style="padding: 10px;"><c:choose><c:when test="${theme.dir=='jchomediy'}"><a href="cp.jsp?ac=theme&op=diy"><strong>${theme.name}</strong></a><br><a href="cp.jsp?ac=theme&op=diy">进入设置界面</a></c:when><c:otherwise><a href="space.jsp?theme=${theme.dir}" target="_blank"><strong>${theme.name}</strong></a><br><a href="space.jsp?theme=${theme.dir}" target="_blank">预览</a> | <a href="cp.jsp?ac=theme&op=use&dir=${theme.dir}">启用</a></c:otherwise></c:choose></div>
   </td>${index.count%4==0 ? "</tr><tr>" : ""}</c:forEach></tr>
  </table>
  <form method="post" action="cp.jsp?ac=theme">
   <table cellspacing="0" cellpadding="0" class="formtable">
    <caption>
     <h2>我的时区</h2>
     <p>如果发现当前显示的时间与你本地时间相差几个小时，那么你需要更改自己的时区设置。</p>
    </caption>
    <tr>
     <th width="120">当前时间</th>
     <td>${currentTime}</td>
    </tr>
    <tr>
     <th>我的时区</th>
     <td>
      <select name="timeoffset">
       <option value="">使用系统默认</option><c:forEach items="${timeZoneIDs}" var="timeZoneID">
       <option value="${timeZoneID.key}"${space.timeoffset == timeZoneID.key ? " selected" : ""}>${timeZoneID.value[1]}</option></c:forEach>
      </select>
     </td>
    </tr>
    <tr>
     <th>&nbsp;</th>
     <td><input type="submit" name="timeoffsetsubmit" value="提交" class="submit" /></td>
    </tr>
   </table>
   <input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
  </form>
 </div>
</c:otherwise></c:choose>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}" />