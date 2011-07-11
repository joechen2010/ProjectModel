<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:directive.include file="header.jsp" />
<div class="mainarea">
 <div class="maininner">
  <div class="tabs_header">
   <ul class="tabs">
    <li${active_all}><a href="admincp.jsp?ac=${ac}"><span>浏览全部列表</span></a></li>
    <li${active_grade0}><a href="admincp.jsp?ac=${ac}&perpage=20&grade=0&searchsubmit=1"><span>待审核活动</span></a></li>
    <li${active_grade_1}><a href="admincp.jsp?ac=${ac}&perpage=20&grade=-1&searchsubmit=1"><span>未通过审核活动</span></a></li>
    <li${active_grade1}><a href="admincp.jsp?ac=${ac}&perpage=20&grade=1&searchsubmit=1"><span>已通过审核活动</span></a></li>
    <li${active_grade_2}><a href="admincp.jsp?ac=${ac}&perpage=20&grade=-2&searchsubmit=1"><span>已关闭活动</span></a></li>
    <li${active_grade2}><a href="admincp.jsp?ac=${ac}&perpage=20&grade=2&searchsubmit=1"><span>推荐活动</span></a></li>
   </ul>
  </div>
  <form method="post" action="admincp.jsp">
   <div class="block style4">
    <table cellspacing="3" cellpadding="3">
     <tr>
      <th>活动ID</th><td><input type="text" name="eventid" value="${param.eventid}"></td>
      <th>创建者UID</th><td><input type="text" name="uid" value="${param.uid}"></td>
     </tr>
     <tr>
      <th>标题*</th><td><input type="text" name="title" value="${param.title}"></td>
      <th>活动类型</th>
      <td>
       <select name="classid">
        <option value="">请选择活动分类</option>
        <c:forEach items="${globalEventClass}" var="eventClass">
         <option value="${eventClass.key}"${param.classid==eventClass.key ? " selected" : ""}>${eventClass.value.classname}</option>
        </c:forEach>
       </select>
      </td>
     </tr>
     <tr>
      <th>活动城市</th>
      <td id="citybox">
       <script type="text/javascript" src="source/script_city.js" charset="${jchConfig.charset}"></script>
       <script type="text/javascript" charset="${jchConfig.charset}">
        showprovince('province', 'city', '${param.province}', 'citybox');
                       showcity('city', '${param.city}', 'province', 'citybox');
                   </script>
      </td>
      <th>公开性质</th>
      <td>
       <select name="public">
        <option value="">不限</option>
        <option value="0"${param.public=='0' ? " selected" : ""}>私密</option>
        <option value="1"${param.public=='1' ? " selected" : ""}>半公开</option>
        <option value="2"${param.public=='2' ? " selected" : ""}>完全公开</option>
       </select>
      </td>
     </tr>
     <tr>
      <th>活动时间</th>
      <td>
       <script type="text/javascript" src="source/script_calendar.js"></script>
       <input type="text" name="starttime" value="${param.starttime}" onclick="showcalendar(event,this,1)" /> ~
       <input type="text" name="endtime" value="${param.endtime}" onclick="showcalendar(event,this,1)" />
      </td>
      <th>是否结束</th>
      <td>
       <select name="over">
        <option value="">不限</option>
        <option value="0"${param.over=='0' ? ' selected' : ''}>未结束</option>
        <option value="1"${param.over=='1' ? ' selected' : ''}>已结束</option>
       </select>
      </td>
     </tr>
     <tr>
      <th>活动状态</th>
      <td colspan="3">
       <select name="grade">
        <option value="">不限</option>
        <option value="-2"${param.grade=='-2' ? ' selected' : ''}>已关闭</option>
        <option value="-1"${param.grade=='-1' ? ' selected' : ''}>未通过审核</option>
        <option value="0"${param.grade=='0' ? ' selected' : ''}>待审核</option>
        <option value="1"${param.grade=='1' ? ' selected' : ''}>通过审核</option>
        <option value="2"${param.grade=='2' ? ' selected' : ''}>推荐</option>
       </select>
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
      <th>结果排序</th>
      <td colspan="3">
       <select name="orderby">
        <option value="">默认排序</option>
        <option value="dateline"${orderby_dateline}>发布时间</option>
        <option value="starttime"${orderby_starttime}>开始时间</option>
        <option value="membernum"${orderby_membernum}>参加人数</option>
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
       <input type="hidden" name="ac" value="event">
       <input type="submit" name="searchsubmit" value="搜索" class="submit">
      </td>
     </tr>
    </table>
   </div>
  </form>
  <c:choose><c:when test="${not empty list}">
   <form method="post" action="admincp.jsp?ac=event">
    <input type="hidden" name="formhash" value="${FORMHASH}" />
    <div class="bdrcontent">
     <c:choose><c:when test="${perpage>100}">
      <p>总共有满足条件的数据 <strong>${count}</strong> 个</p>
      <c:forEach items="${list}" var="value">
       <input type="hidden" name="ids" value="${value.eventid}">
      </c:forEach>
     </c:when><c:otherwise>
      <table cellspacing="0" cellpadding="0" class="formtable">
       <tr><td width="25">&nbsp;</td><th>活动名称</th><th width="80">活动城市</th><th width="120">活动时间</th><th width="80">参加/关注</th><th width="80">发起者</th><th width="40">操作</th></tr>
       <c:forEach items="${list}" var="value">
        <tr>
         <td><input type="${allowbatch ? 'checkbox' : 'radio'}" name="ids" value="${value.eventid}"></td>
         <td>
          <a href="space.jsp?do=event&id=${value.eventid}" target="_blank">${value.title}</a>
          <a class="gray" href="admincp.jsp?ac=event&grade=${value.grade}"><c:choose><c:when test="${value.grade==0}">待审核</c:when><c:when test="${value.grade==-1}">未通过审核</c:when><c:when test="${value.grade==2}">推荐</c:when><c:when test="${value.grade==-2}">已关闭</c:when></c:choose></a>
          <c:if test="${value.hot>0}"><span style="color: red;">热度(${value.hot})</span></c:if>
         </td>
         <td>
          <a href="admincp.jsp?ac=event&province=${jch:urlEncoder(value.province)}">${value.province}</a> -
          <a href="admincp.jsp?ac=event&province=${jch:urlEncoder(value.province)}&city=${jch:urlEncoder(value.city)}">${value.city}</a>
         </td>
         <td>${value.starttime} ~ ${value.endtime}</td>
         <td>${value.membernum} / ${value.follownum}</td>
         <td><a href="space.jsp?uid=${value.uid}">${value.username}</a></td>
         <td><a href="cp.jsp?ac=event&op=edit&id=${value.eventid}" target="_blank">编辑</a></td>
        </tr>
       </c:forEach>
      </table>
     </c:otherwise></c:choose>
    </div>
    <div class="footactions">
     <input type="hidden" name="mpurl" value="${mpurl}">
     <c:if test="${allowbatch && perpage<=100}"><input type="checkbox" id="chkall" name="chkall" onclick="checkAll(this.form, 'ids')">全选</c:if>
     <input id="ckdelete" type="radio" name="optype" value="delete"><label for="ckdelete">删除</label>
     <input id="ckverify" type="radio" name="optype" value="verify"><label for="ckverify">通过审核</label>
     <input id="ckdelayverify" type="radio" name="optype" value="delayverify"><label for="ckdelayverify">不通过审核</label>
     <input id="ckrecommend" type="radio" name="optype" value="recommend"><label for="ckrecommend">推荐</label>
     <input id="ckunrecommend" type="radio" name="optype" value="unrecommend"><label for="ckunrecommend">取消推荐</label>
     <input type="submit" name="opsubmit" value="执行操作" onclick="if($('ckdelete').checked){return confirm('本操作不可恢复，确认继续？')};" class="submit">
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