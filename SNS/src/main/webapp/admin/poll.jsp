<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />
<div class="mainarea">
 <div class="maininner">
  <form method="post" action="admincp.jsp">
   <div class="block style4">
    <table cellspacing="3" cellpadding="3">
     <c:if test="${allowmanage}">
      <tr>
       <th>作者UID</th><td><input type="text" name="uid" value="${param.uid}"></td>
       <th>作者名</th><td><input type="text" name="username" value="${param.username}"></td>
      </tr>
     </c:if>
     <tr>
      <th>标题*</th>
      <td><input type="text" name="subject" value="${param.subject}"></td>
      <th>指定投票ID</th>
      <td><input type="text" name="pid" value="${param.pid}"> *表示支持模糊查询</td>
     </tr>
     <tr>
      <th>评论限制</th>
      <td>
       <select name="noreply">
        <option value="">不限</option>
        <option value="0"${param.noreply=='0' ? ' selected' : ''}>全站用户可见</option>
        <option value="1"${param.noreply=='1' ? ' selected' : ''}>仅好友可评论</option>
       </select>
      </td>
      <th>投票限制</th>
      <td>
       <select name="sex">
        <option value="">不限</option>
        <option value="1"${param.sex=='1' ? ' selected' : ''}>男</option>
        <option value="2"${param.sex=='2' ? ' selected' : ''}>女</option>
       </select>
      </td>
     </tr>
     <tr>
      <th>过期投票</th>
      <td colspan="3">
       <select name="expiration">
        <option value="">不限</option>
        <option value="1"${param.expiration=='1' ? ' selected' : ''}>未过期</option>
        <option value="2"${param.expiration=='2' ? ' selected' : ''}>已过期</option>
       </select>
      </td>
     </tr>
     <tr>
      <th>悬赏积分</th>
      <td colspan="3">
       <input type="text" name="percredit1" value="${param.percredit1}" size="10"> ~
       <input type="text" name="percredit2" value="${param.percredit2}" size="10">
      </td>
     </tr>
     <tr>
      <th>参与人数</th>
      <td colspan="3">
       <input type="text" name="voternum1" value="${param.voternum1}" size="10"> ~
       <input type="text" name="voternum2" value="${param.voternum2}" size="10">
      </td>
     </tr>
     <tr>
      <th>评论数</th>
      <td colspan="3">
       <input type="text" name="replynum1" value="${param.replynum1}" size="10"> ~
       <input type="text" name="replynum2" value="${param.replynum2}" size="10">
      </td>
     </tr>
     <tr>
      <th>热度</th>
      <td colspan="3">
       <input type="text" name="hot1" value="${param.hot1}" size="10"> ~
       <input type="text" name="hot2" value="${param.hot2}" size="10">
      </td>
     </tr>
     <tr>
      <th>发布时间</th>
      <td colspan="3">
       <input type="text" name="dateline1" value="${param.dateline1}" size="10"> ~
       <input type="text" name="dateline2" value="${param.dateline2}" size="10"> (YYYY-MM-DD)
      </td>
     </tr>
     <tr>
      <th>结果排序</th>
      <td colspan="3">
       <select name="orderby">
        <option value="">默认排序</option>
        <option value="dateline"${orderby_dateline}>发布时间</option>
        <option value="voternum"${orderby_voternum}>参与人数</option>
        <option value="replynum"${orderby_replynum}>评论数</option>
        <option value="percredit"${orderby_percredit}>悬赏积分</option>
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
       <input type="hidden" name="ac" value="poll">
       <input type="submit" name="searchsubmit" value="搜索" class="submit">
      </td>
     </tr>
    </table>
   </div>
  </form>
  <c:choose><c:when test="${not empty list}">
   <form method="post" action="admincp.jsp?ac=poll">
    <input type="hidden" name="formhash" value="${FORMHASH }" />
    <div class="bdrcontent">
     <c:choose><c:when test="${perpage>100}">
      <p>总共有满足条件的数据 <strong>${count}</strong> 个</p>
      <c:forEach items="${list}" var="value">
       <input type="hidden" name="ids" value="${value.pid }">
      </c:forEach>
     </c:when><c:otherwise>
      <table cellspacing="0" cellpadding="0" class="formtable">
       <tr><td width="25">&nbsp;</td><th>标题</th><c:if test="${allowmanage}"><th width="80">作者</th></c:if><th width="80">参与数/评论</th><th width="80">时间</th><th width="80">操作</th></tr>
       <c:forEach items="${list}" var="value">
        <tr>
         <td><input type="${allowbatch ? 'checkbox' : 'radio'}" name="ids" value="${value.pid}"></td>
         <td>
          <a href="space.jsp?uid=${value.uid}&do=poll&pid=${value.pid}" target="_blank"><strong><c:if test="${value.credit>0}">[悬赏:${value.credit}]</c:if><c:if test="${value.isexpired}">[已过期]</c:if></strong>${value.subject}</a>
          <c:if test="${value.hot>0}"><span style="color: red;">热度(${value.hot})</span></c:if>
          <c:if test="${value.friend>0}">[<a href="admincp.jsp?ac=blog&friend=${value.friend}">${value.friend}</a>]</c:if>
         </td>
         <c:if test="${allowmanage}"><td><a href="admincp.jsp?ac=poll&uid=${value.uid}">${value.username}</a></td></c:if>
         <td align="center">${value.voternum}/${value.replynum}</td>
         <td>${value.dateline}</td>
         <td>
          <a href="admincp.jsp?ac=poll&op=delete&pid=${value.pid}" onclick="return confirm('本操作不可恢复，确认删除？');">删除</a>&nbsp;
          <a href="admincp.jsp?ac=comment&id=${value.pid}&idtype=pid">评论</a>
         </td>
        </tr>
       </c:forEach>
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