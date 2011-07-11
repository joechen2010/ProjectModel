<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />
<div class="mainarea">
 <div class="maininner">
  <c:choose><c:when test="${jch:jchEmpty(op)}">
   <div class="bdrcontent"><c:choose><c:when test="${!jch:jchEmpty(tpls)}"><c:forEach items="${tpls}" var="key_value">
    <div class="title"><h3>${key_value.key}</h3></div>
    <table cellspacing="0" cellpadding="0" class="formtable">
     <tr>
      <th>模板文件名</th>
      <th width="80">操作</th>
     </tr>
     <c:forEach items="${key_value.value}" var="subvalue"><tr>
      <td>${subvalue[0]}</td>
      <td><a href="admincp.jsp?ac=template&op=edit&filename=${subvalue[0]}">编辑</a><c:if test="${subvalue[1] == 1}"> <a href="admincp.jsp?ac=template&op=repair&filename=${subvalue[0]}">恢复</a></c:if></td>
     </tr></c:forEach>
    </table><br /></c:forEach>
   </c:when><c:otherwise>
    <div class="c_form">当前模板目录下，没有可编辑的模板文件</div>
   </c:otherwise></c:choose></div>
  </c:when><c:when test="${op == 'edit'}">
   <form method="post" action="admincp.jsp?ac=template">
    <input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,true)}" />
    <div class="bdrcontent">
     <div class="title"><h3>编辑模板: ${filename}</h3></div>
     <table cellspacing="0" cellpadding="0" width="100%">
      <tr><td><textarea name="content" style="width: 98%; font-size: 11px;" rows="30">${content}</textarea></td></tr>
     </table>
    </div>
    <div class="footactions">
     <input type="hidden" name="filename" value="${filename}">
     <input type="submit" name="editsubmit" value="提交" class="submit">&nbsp;
     <button onclick="javascript:history.go(-1);" class="submit">返回</button>
    </div>
   </form>
  </c:when></c:choose>
 </div>
</div>
<div class="side">
 <jsp:directive.include file="side.jsp" />
</div>
<jsp:directive.include file="footer.jsp" />