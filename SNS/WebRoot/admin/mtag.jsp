<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />
<div class="mainarea">
 <div class="maininner">
  <form method="post" action="admincp.jsp">
   <div class="block style4">
    <table cellspacing="3" cellpadding="3">
     <tr>
      <th>群组名*</th><td><input type="text" name="tagname" value="${param.tagname}"></td>
      <th>指定群组ID</th><td><input type="text" name="tagid" value="${param.tagid}"/></td>
     </tr>
     <tr>
      <th>是否锁定</th>
      <td>
       <select name="close">
        <option value="">不限制</option>
        <option value="1"${param.close==1 ? " selected" : ""}>锁定</option>
       </select>
      </td>
      <th>是否推荐</th>
      <td>
       <select name="recommend">
        <option value="">不限制</option>
        <option value="1"${param.recommend==1 ? " selected" : ""}>推荐</option>
       </select>
      </td>
     </tr>
     <tr>
      <th>加入权限</th>
      <td>
       <select name="joinperm">
        <option value="">全部</option>
        <option value="0"${joinperm_0}>公开</option>
        <option value="1"${joinperm_1}>审核</option>
        <option value="2"${joinperm_2}>私密</option>
       </select>
      </td>
      <th>浏览权限</th>
      <td>
       <select name="viewperm">
        <option value="">全部</option>
        <option value="0"${viewperm_0}>公开</option>
        <option value="1"${viewperm_1}>封闭</option>
       </select>
      </td>
     </tr>
     <tr>
      <th>发帖权限</th>
      <td>
       <select name="threadperm">
        <option value="">全部</option>
        <option value="0"${threadperm_0}>仅成员可发话题</option>
        <option value="1"${threadperm_1}>所有人可以发话题</option>
       </select>
      </td>
      <th>回帖权限</th>
      <td>
       <select name="postperm">
        <option value="">全部</option>
        <option value="0"${postperm_0}>仅成员可回帖</option>
        <option value="1"${postperm_1}>所有人可以回帖</option>
       </select>
      </td>
     </tr>
     <tr>
      <th>归属栏目</th>
      <td colspan="3">
       <select name="fieldid">
        <option value="">全部</option>
        <c:forEach items="${globalProfield}" var="profield">
         <option value="${profield.key}"${param.fieldid==profield.key ? " selected" :""}>${profield.value.title }</option>
        </c:forEach>
       </select>
      </td>
     </tr>
     <tr>
      <th>用户数</th>
      <td colspan="3">
       <input type="text" name="membernum1" value="${param.membernum1}" size="10"> ~
       <input type="text" name="membernum2" value="${param.membernum2}" size="10">
      </td>
     </tr>
     <tr>
      <th>话题数</th>
      <td colspan="3">
       <input type="text" name="threadnum1" value="${param.threadnum1}" size="10"> ~
       <input type="text" name="threadnum2" value="${param.threadnum2}" size="10">
      </td>
     </tr>
     <tr>
      <th>回帖数</th>
      <td colspan="3">
       <input type="text" name="postnum1" value="${param.postnum1}" size="10"> ~
       <input type="text" name="postnum2" value="${param.postnum2}" size="10">
      </td>
     </tr>
     <tr>
      <th>结果排序</th>
      <td colspan="3">
       <select name="orderby">
        <option value="">默认排序</option>
        <option value="membernum"${orderby_membernum}>成员数</option>
        <option value="threadnum"${orderby_threadnum}>话题数</option>
        <option value="postnum"${orderby_postnum}>回帖数</option>
       </select>
       <select name="ordersc">
        <option value="desc"${ordersc_desc}>递减</option>
        <option value="asc"${ordersc_asc}>递增</option>
       </select>
       <select name="perpage">
        <option value="20"${perpage_20}>每页显示20个</option>
        <option value="50"${perpage_50}>每页显示50个</option>
        <option value="100"${perpage_100}>每页显示100个</option>
       </select>
       <input type="hidden" name="ac" value="mtag">
       <input type="submit" name="searchsubmit" value="搜索" class="submit">
      </td>
     </tr>
    </table>
   </div>
  </form>
  <c:choose><c:when test="${not empty list}">
   <form method="post" action="admincp.jsp?ac=mtag">
    <input type="hidden" name="formhash" value="${FORMHASH}"/>
    <div class="bdrcontent">
     <table cellspacing="0" cellpadding="0" class="formtable">
      <tr><th>群组名</th><th>分类</th><th width="80">用户数</th><th width="80">话题数</th><th width="80">回帖数</th><th width="100">群主</th></tr>
      <c:forEach items="${list}" var="value">
       <tr>
        <td>
         <input type="${managebatch ? 'checkbox' : 'radio'}" name="ids" value="${value.tagid}">
         <a href="space.jsp?do=mtag&tagid=${value.tagid }" target="_blank">${value.tagname}</a>
         <c:if test="${value.close>0}">(<a href="admincp.jsp?ac=mtag&close=${value.close}">锁定</a>)</c:if>
         <c:if test="${value.recommend>0}">(<a href="admincp.jsp?ac=mtag&recommend=${value.recommend}">推荐</a>)</c:if>
        </td>
        <td><a href="admincp.jsp?ac=mtag&fieldid=${value.fieldid}">${globalProfield[value.fieldid].title}</a></td>
        <td>${value.membernum}</td>
        <td>${value.threadnum}</td>
        <td>${value.postnum}</td>
        <td>[<a href="cp.jsp?ac=mtag&op=manage&tagid=${value.tagid}&subop=base" target="_blank">群组管理</a>]</td>
       </tr>
      </c:forEach>
     </table>
    </div>
    <div class="footactions" style="text-align: left; line-height: 2.5em;">
     <input type="hidden" name="mpurl" value="${mpurl}">
     <c:if test="${managebatch}"><input type="checkbox" id="chkall" name="chkall" onclick="checkAll(this.form, 'ids')">全选 &nbsp; 请选择操作类型：<br></c:if>
     <input type="radio" name="optype" value="delete"> 删除(群组里面的帖子也会删除)<br>
     <input type="radio" name="optype" value="close"> 锁定
     <input type="radio" name="optype" value="open"> 取消锁定<br>
     <input type="radio" name="optype" value="recommend"> 推荐
     <input type="radio" name="optype" value="unrecommend"> 取消推荐<br>
     <input type="radio" name="optype" value="move"> 转移到新分类:
     <select name="move_newfieldid">
      <c:forEach items="${globalProfield}" var="profield">
       <option value="${profield.key}">${profield.value.title}</option>
      </c:forEach>
     </select><br>
     <input type="radio" name="optype" value="merge"> 合并到其他群组:
     <select name="merge_newfieldid">
      <c:forEach items="${globalProfield}" var="profield">
       <option value="${profield.key}">${profield.value.title}</option>
      </c:forEach>
     </select>
     群组名: <input type="text" name="newtagname" value="" size="5"><br>
     <input type="submit" name="opsubmit" value="执行操作" onclick="return confirm('本操作不可恢复，确认继续？');" class="submit">
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