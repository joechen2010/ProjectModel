<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />
<div class="mainarea">
 <div class="maininner">
  <form method="post" action="admincp.jsp">
   <div class="block style4">
    <table cellspacing="3" cellpadding="3">
     <tr>
      <th>所在相册ID</th><td><input type="text" name="albumid" value="${param.albumid}"></td>
      <c:choose>
       <c:when test="${allowmanage}"><th>作者名</th><td><input type="text" name="username" value="${param.username}"/></td></c:when>
       <c:otherwise><td>&nbsp;</td><td>&nbsp;</td></c:otherwise>
      </c:choose>
     </tr>
     <tr>
      <th>指定图片ID</th><td><input type="text" name="picid" value="${param.picid}"></td>
      <th>发布IP</th><td><input type="text" name="postip" value="${param.postip}"></td>
     </tr>
     <tr>
      <th>文件名*</th><td><input type="text" name="filename" value="${param.filename}"></td>
      <th>图片说明*</th><td><input type="text" name="title" value="${param.title}"></td>
     </tr>
     <tr>
      <th>热度</th>
      <td colspan="3">
       <input type="text" name="hot1" value="${param.hot1}" size="10"> ~
       <input type="text" name="hot2" value="${param.hot2}" size="10">
      </td>
     </tr>
     <tr>
      <th>上传时间</th>
      <td colspan="3">
       <input type="text" name="dateline1" value="${param.dateline1}" size="10"> ~
       <input type="text" name="dateline2" value="${param.dateline2}" size="10"> (格式为: YYYY-MM-DD)
      </td>
     </tr>
     <tr>
      <th>结果排序</th>
      <td colspan="3">
       <select name="orderby">
        <option value="">默认排序</option>
        <option value="dateline"${orderby_dateline}>上传时间</option>
        <option value="size"${orderby_size}>图片大小</option>
        <option value="hot"${orderby_hot}>热度</option>
       </select>
       <select name="ordersc">
        <option value="desc"${ordersc_desc}>递减</option>
        <option value="asc"${ordersc_asc}>递增</option>
       </select>
       <select name="perpage">
        <option value="20"${perpage_20}>每页显示20个</option>
        <option value="50"${perpage_50}>每页显示50个</option>
        <option value="100"${perpage_100}>每页显示100个</option>
        <option value="1000"${perpage_1000}>一次处理1000个</option>
       </select>
       <input type="hidden" name="ac" value="pic">
       <input type="submit" name="searchsubmit" value="搜索" class="submit">
      </td>
     </tr>
    </table>
   </div>
  </form>
  <c:choose><c:when test="${not empty list}">
   <form method="post" action="admincp.jsp?ac=pic">
    <input type="hidden" name="formhash" value="${FORMHASH}" />
    <div class="bdrcontent">
     <c:choose><c:when test="${perpage>100}">
      <p>总共有满足条件的数据 <strong>${count}</strong> 个</p>
      <c:forEach items="${list}" var="value">
       <input type="hidden" name="ids" value="${value.picid}">
      </c:forEach>
     </c:when><c:otherwise>
      <table cellspacing="0" cellpadding="0" class="formtable">
       <tr>
        <c:forEach items="${list}" var="value" varStatus="key">
         <td width="105">
          <a href="${value.bigpic}" target="_blank"><img src="${value.pic}" width="90" alt="${value.filename}"></a>
          <input type="${allowbatch ? 'checkbox' : 'radio'}" name="ids" value="${value.picid}"> 选择
         </td>
         <td>
          <c:if test="${not empty value.title }">${value.title}<br /></c:if>
          大小: ${value.size}
          <br />相册: <a href="admincp.jsp?ac=pic&uid=${value.uid}&albumid=${value.albumid}">${value.albumid>0 ? albums[value.albumid] : "默认相册"}</a>
          <c:if test="${allowmanage}"><br />作者: <a href="admincp.jsp?ac=pic&uid=${value.uid}">${users[value.uid]}</a></c:if>
          <br />时间: ${value.dateline}
          <c:if test="${value.hot>0}"><br /><span style="color: red;">热度: ${value.hot}</span></c:if>
          <br><a href="admincp.jsp?ac=comment&id=${value.picid}&idtype=picid">评论管理</a>
         </td>
         ${key.index %2==1 ? "</tr><tr>" : ""}
        </c:forEach>
       </tr>
      </table>
     </c:otherwise></c:choose>
    </div>
    <div class="footactions">
     <c:if test="${allowbatch && perpage<=100}"><input type="checkbox" id="chkall" name="chkall" onclick="checkAll(this.form, 'ids')">全选</c:if>
     <input type="hidden" name="mpurl" value="${mpurl}">
     <input type="submit" name="batchsubmit" value="批量删除" onclick="return confirm('本操作不可恢复，确认删除？');" class="submit">
     <div class="pages">${multi}</div>
    </div>
   </form>
  </c:when><c:otherwise>
   <div class="bdrcontent"><p>指定条件下还没有数据</p></div>
  </c:otherwise></c:choose>
 </div>
</div>
<div class="side">
 <jsp:directive.include file="side.jsp" />
</div>
<jsp:directive.include file="footer.jsp" />