<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />
<div class="mainarea">
 <div class="maininner">
  <c:if test="${allowmanage}">
   <div class="tabs_header">
    <ul class="tabs">
     <li${active_all}><a href="admincp.jsp?ac=feed"><span>浏览全部动态</span></a></li>
     <li${active_site}><a href="admincp.jsp?ac=feed&uid=0"><span>浏览全局动态</span></a></li>
     <li class="null"><a href="admincp.jsp?ac=feed&op=add">发布全局动态</a></li>
    </ul>
   </div>
  </c:if>
  <c:choose><c:when test="${op=='add'}">
   <div class="bdrcontent">
    全局动态，就是会在站内任何一个成员的好友动态里面都会出现的动态，每个成员都能第一时间看到。站长可以灵活使用全局动态来发布一些公开的信息。
   </div><br>
  </c:when><c:otherwise>
   <form method="post" action="admincp.jsp">
    <div class="block style4">
     <table cellspacing="3" cellpadding="3">
      <c:if test="${allowmanage}">
       <tr>
        <th>作者UID</th>
        <td><input type="text" name="uid" value="${param.uid}"></td>
        <th>作者名</th>
        <td><input type="text" name="username" value="${param.username}"></td>
       </tr>
      </c:if>
      <tr>
       <th>动态(feedid)</th>
       <td><input type="text" name="feedid" value="${param.feedid}"></td>
       <th>动态类型(icon)</th>
       <td><input type="text" name="icon" value="${param.icon}"></td>
      </tr>
      <tr>
       <th>发布时间</th>
       <td colspan="3">
        <input type="text" name="dateline1" value="${param.dateline1}" size="10"> ~
        <input type="text" name="dateline2" value="${param.dateline2}" size="10"> (YYYY-MM-DD)
       </td>
      </tr>
      <tr>
       <th>热度范围</th>
       <td colspan="3">
        <input type="text" name="hot1" value="${param.hot1}" size="10"> ~
        <input type="text" name="hot2" value="${param.hot2}" size="10">
       </td>
      </tr>
      <tr>
       <th>结果排序</th>
       <td colspan="3">
        <select name="orderby">
         <option value="">默认排序</option>
         <option value="dateline"${orderby_dateline}>发布时间</option>
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
        <input type="hidden" name="ac" value="feed">
        <input type="submit" name="searchsubmit" value="搜索" class="submit">
       </td>
      </tr>
     </table>
    </div>
   </form>
  </c:otherwise></c:choose>
  <c:choose><c:when test="${op=='add' || op=='edit'}">
   <form method="post" action="admincp.jsp?ac=feed">
    <script language="javascript" src="image/editor/editor_function.js"></script>
    <div class="bdrcontent">
     <table cellspacing="3" cellpadding="3" width="100%">
      <c:choose><c:when test="${jch:jchEmpty(feed.uid)}">
       <tr>
        <th width="150">动态标题</th>
        <td>
         <input type="text" name="title_template" value="${feed.title_template}" size="100">
         <br>支持html，例如：加粗 &lt;b&gt;&lt;/b&gt;，变色 &lt;font color=red&gt;&lt;/font&gt;
        </td>
       </tr>
       <tr>
        <th>动态内容</th>
        <td>
         <textarea class="userData" name="body_template" id="jchome-ttHtmlEditor" style="height: 100%; width: 100%; display: none; border: 0px">${feed.body_template }</textarea>
         <iframe src="editor.jsp?allowhtml=1" name="jchome-ifrHtmlEditor" id="jchome-ifrHtmlEditor" scrolling="no" border="0" frameborder="0" style="width: 100%; border: 1px solid #C5C5C5;" height="400"></iframe>
        </td>
       </tr>
       <tr><th>动态备注</th><td><input type="text" name="body_general" value="${feed.body_general }" size="60"> (支持html)</td></tr>
      </c:when><c:otherwise>
       <tr><th width="150">动态标题</th><td>${feed.title_template}</td></tr>
       <tr><th>动态内容</th><td>${feed.body_template}</td></tr>
       <tr><th>动态备注</th><td>${feed.body_general}</td></tr>
      </c:otherwise></c:choose>
      <tbody id="image">
       <tr><th>第1张图片地址</th><td><input type="text" name="image_1" value="${feed.image_1}" size="60"></td></tr>
       <tr><th>第1张图片链接</th><td><input type="text" name="image_1_link" value="${feed.image_1_link}" size="60"></td></tr>
       <tr><th>第2张图片地址</th><td><input type="text" name="image_2" value="${feed.image_2}" size="60"></td></tr>
       <tr><th>第2张图片链接</th><td><input type="text" name="image_2_link" value="${feed.image_2_link}" size="60"></td></tr>
       <tr><th>第3张图片地址</th><td><input type="text" name="image_3" value="${feed.image_3}" size="60"></td></tr>
       <tr><th>第3张图片链接</th><td><input type="text" name="image_3_link" value="${feed.image_3_link}" size="60"></td></tr>
       <tr><th>第4张图片地址</th><td><input type="text" name="image_4" value="${feed.image_4}" size="60"></td></tr>
       <tr><th>第4张图片链接</th><td><input type="text" name="image_4_link" value="${feed.image_4_link}" size="60"></td></tr>
      </tbody>
      <tr>
       <th>发布时间</th>
       <td>
        <input type="text" name="dateline" value="${feed.dateline}" size="30"> (当前时刻：${feed.timestamp})
        <br>您可以填写一个将来的日期和时间，那么这条动态会在这个将来的日期到来之前，一直显示在第一位。
       </td>
      </tr>
      <c:if test="${feed.id>0}">
       <tr><th>动态热度</th><td><input type="text" name="hot" value="${feed.hot }" size="10"></td></tr>
      </c:if>
      <tr>
       <td>&nbsp;</td>
       <td>
        <input type="hidden" name="feedid" value="${feed.feedid}">
        <input type="hidden" name="feeduid" value="${feed.uid}">
        <input type="hidden" name="id" value="${feed.id}">
        <input type="hidden" name="idtype" value="${feed.idtype}">
        <input type="hidden" name="formhash" value="${FORMHASH}" />
        <input type="submit" name="feedsubmit" value="提交保存" class="submit"${jch:jchEmpty(feed.uid) ? " onclick='edit_save();'" : ""}>
        <c:if test="${feed.feedid>0}"> &nbsp; <a href="admincp.jsp?ac=feed&op=delete&feedid=${feed.feedid }" onclick="return confirm('确定要删除吗？');">删除此动态</a></c:if>
       </td>
      </tr>
     </table>
    </div>
   </form><br>
  </c:when><c:otherwise>
   <c:choose><c:when test="${not empty list}">
    <form method="post" action="admincp.jsp?ac=feed">
     <input type="hidden" name="formhash" value="${FORMHASH}"/>
     <div class="bdrcontent">
      <c:choose><c:when test="${perpage>100}">
       <p>总共有满足条件的数据 <strong>${count}</strong> 个</p>
       <c:forEach items="${list}" var="value">
        <input type="hidden" name="ids" value="${value.feedid}">
       </c:forEach>
      </c:when><c:otherwise>
       <div class="feed">
        <div id="feed_div" class="feed_content">
         <ul>
          <c:forEach items="${list}" var="value">
           <li id="feed_${value.feedid}_li">
            <div style="width: 100%; overflow: hidden;">
             <div style="padding: 10px 0 0 0;" class="r_option gray">
              <input type="<c:if test="${allowbatch}">checkbox</c:if><c:if test="${!allowbatch}">radio</c:if>" name="ids" value="${value.feedid}"> 选择
              <c:if test="${allowmanage}"> &nbsp; <a href="admincp.jsp?ac=feed&op=edit&feedid=${value.feedid}">编辑</a></c:if>
             </div>
             <a class="type" href="admincp.jsp?ac=feed&icon=${value.icon}" title="只看此类动态"><img src="${value.icon_image}" /></a> ${value.title_template}
             <span class="time">${value.dateline}</span>
             <c:if test="${value.hot>0}"><span style="color: red;">(热度：${value.hot})</span></c:if>
             <div class="feedContent">
              <c:if test="${not empty value.image_1}"><a href="${value.image_1_link}"><img src="${value.image_1}" class="summaryimg"/></a></c:if>
              <c:if test="${not empty value.image_2}"><a href="${value.image_2_link}"><img src="${value.image_2}" class="summaryimg"/></a></c:if>
              <c:if test="${not empty value.image_3}"><a href="${value.image_3_link}"><img src="${value.image_3}" class="summaryimg"/></a></c:if>
              <c:if test="${not empty value.image_4}"><a href="${value.image_4_link}"><img src="${value.image_4}" class="summaryimg"/></a></c:if>
              <c:if test="${not empty value.body_template}"><div class="detail"${not empty value.image_3 ? "style='clear: both;'" : ""}>${value.body_template}</div></c:if>
              <c:if test="${not empty value.body_general}"><div class="quote"><span class="q">${value.body_general}</span></div></c:if>
             </div>
            </div>
           </li>
          </c:forEach>
         </ul>
        </div>
       </div>
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
  </c:otherwise></c:choose>
 </div>
</div>
<div class="side">
 <jsp:directive.include file="side.jsp" />
</div>
<jsp:directive.include file="footer.jsp" />