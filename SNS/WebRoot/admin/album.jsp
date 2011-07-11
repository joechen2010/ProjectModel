<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />
<div class="mainarea">
 <div class="maininner">
  <form method="post" action="admincp.jsp">
   <div class="block style4">
    <table cellspacing="3" cellpadding="3">
     <tr>
      <th>相册名*</th>
      <td><input type="text" name="albumname" value="${param.albumname }" /></td>
      <th>查看权限</th>
      <td>
       <select name="friend">
        <option value="">不限</option>
        <option value="0"${param.friend == '0' ? " selected" : ""}>全站用户可见</option>
        <option value="1"${param.friend == '1' ? " selected" : ""}>全好友可见</option>
        <option value="2"${param.friend == '2' ? " selected" : ""}>仅指定的好友可见</option>
        <option value="3"${param.friend == '3' ? " selected" : ""}>仅自己可见</option>
        <option value="4"${param.friend == '4' ? " selected" : ""}>凭密码查看</option>
       </select>
      </td>
     </tr>
     <tr>
      <th>相册ID</th>
      <td colspan="3"><input type="text" name="albumid" value="${param.albumid }" /></td>
     </tr>
     <c:if test="${allowmanage}">
      <tr>
       <th>作者UID</th>
       <td><input type="text" name="uid" value="${param.uid}" /></td>
       <th>作者名</th>
       <td><input type="text" name="username" value="${param.username}" /></td>
      </tr>
     </c:if>
     <tr>
      <th>创建时间</th>
      <td colspan="3">
       <input type="text" name="dateline1" value="${param.dateline1}" size="10" /> ~
       <input type="text" name="dateline2" value="${param.dateline2}" size="10" /> (YYYY-MM-DD)
      </td>
     </tr>
     <tr>
      <th>结果排序</th>
      <td colspan="3">
       <select name="orderby">
        <option value="">默认排序</option>
        <option value="dateline"${orderby_dateline}>创建时间</option>
        <option value="updatetime"${orderby_updatetime}>更新时间</option>
        <option value="picnum"${orderby_picnum}>图片数</option>
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
       <input type="hidden" name="ac" value="album" />
       <input type="submit" name="searchsubmit" value="搜索" class="submit" />
      </td>
     </tr>
    </table>
   </div>
  </form>
  <c:choose><c:when test="${not empty list}">
   <form method="post" action="admincp.jsp?ac=album">
    <input type="hidden" name="formhash" value="${FORMHASH}" />
    <div class="bdrcontent">
     <c:choose><c:when test="${perpage>100}">
      <p>总共有满足条件的数据 <strong>${count}</strong> 个</p>
      <c:forEach items="${list}" var="value">
       <input type="hidden" name="ids" value="${value.albumid}">
      </c:forEach>
     </c:when><c:otherwise>
      <table cellspacing="0" cellpadding="0" class="formtable">
       <tr>
        <c:forEach items="${list}" var="value" varStatus="key">
         <td width="105">
          <a href="space.jsp?uid=${value.uid }&do=album&id=${value.albumid==0 ? -1 : value.albumid}" target="_blank"><img src="${value.pic}" alt="${value.albumname}" width="100" height="90"></a>
          <input type="${allowbatch ? 'checkbox' : 'radio'}" name="ids" value="${value.albumid}"> 选择
         </td>
         <td>
          <a href="admincp.jsp?ac=pic&albumid=${value.albumid }">${value.albumname}</a>
          <c:if test="${allowmanage}"><br />作者: <a href="admincp.jsp?ac=album&uid=${value.uid}">${value.username}</a></c:if>
          <br />时间: ${value.dateline}
          <c:if test="${value.friend>0}"><br />权限: [<a href="admincp.jsp?ac=album&friend=${value.friend}">${value.friendTitle}</a>]</c:if>
          <br /><a href="admincp.jsp?ac=pic&uid=${value.uid}&albumid=${value.albumid}">管理图片(${value.picnum})</a>
         </td>
         ${key.index %2 == 1 ? "</tr><tr>" : ""}
        </c:forEach>
       </tr>
      </table>
     </c:otherwise></c:choose>
    </div>
    <div class="footactions">
     <c:if test="${allowbatch && perpage<=100}"><input type="checkbox" id="chkall" name="chkall" onclick="checkAll(this.form, 'ids')">全选</c:if>
     <input type="hidden" name="mpurl" value="${mpurl}">
     <input type="submit" name="deletesubmit" value="批量删除" onclick="return confirm('本操作不可恢复，确认删除？');" class="submit">
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