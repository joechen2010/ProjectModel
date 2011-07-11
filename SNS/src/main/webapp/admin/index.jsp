<%@ page language="java"  pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />
<div class="mainarea">
 <div class="maininner">
<c:choose>
 <c:when test="${menus.menu0.config == 1}">
  <div class="bdrcontent">
   <div class="title"><h3>欢迎光临管理平台</h3></div>
   <p>通过登录管理平台，您可以对站点的参数进行设置，并可以及时获取官方的更新动态和重要补丁通告。</p>
  </div><br />
  <div class="bdrcontent">
   <div class="title">
    <h3>官方最新动态</h3>
    <p>官方新版本的发布与重要补丁的升级等动态，都会在这里显示。</p>
   </div>
   <div id="customerinfor" style="line-height:1.5em;"></div><br />
   <div class="title">
    <h3>技术支持服务</h3>
    <p>如果你在使用中遇到问题，可以访问以下链接需求帮助</p>
   </div>
   <ul class="listcol list2col">
    <li><a href=http://www.jsprun.net/index.jsp?gid=91 target="_blank">官方交流论坛</a></li>
    <li><a href=http://www.jsprun.com/purchase.jsp?action=jchome target="_blank">JspRun!商业支持服务</a></li>
   </ul>
  </div><br />
  <div class="bdrcontent">
   <div class="title">
    <h3>站点数据统计</h3>
    <p>通过站点统计，您可以整体把握站点的发展状况。</p>
    <p>您还可以查看<a href="do.jsp?ac=stat" target="_blank">趋势统计</a>，把握站点每日变化。</p>
   </div>
   <ul class="listcol list2col">
    <li>开通空间数: ${statistics.spacenum} (<a href="do.jsp?ac=stat&type=login" target="_blank">趋势</a>)</li>
    <li>全部动态数: ${statistics.feednum}</li>
    <li>全部日志数: ${statistics.blognum} (<a href="do.jsp?ac=stat&type=blog" target="_blank">趋势</a>)</li>
    <li>全部相册数: ${statistics.albumnum} (<a href="do.jsp?ac=stat&type=pic" target="_blank">趋势</a>)</li>
    <li>全部分享数: ${statistics.sharenum} (<a href="do.jsp?ac=stat&type=share" target="_blank">趋势</a>)</li>
    <li>全部话题数: ${statistics.threadnum} (<a href="do.jsp?ac=stat&type=thread" target="_blank">趋势</a>)</li>
    <li>全部评论数: ${statistics.commentnum}</li>
    <li>开启应用数: ${statistics.myappnum}</li>
   </ul>
  </div><br />
  <div class="bdrcontent">
   <div class="title"><h3>程序数据库/版本</h3></div>
   <ul>
    <li>操作系统: ${os}</li>
    <li>服务器软件: ${serverInfo}</li>
    <li>数据库版本: ${statistics.mysql}</li>
    <li>上传许可: ${fileupload}</li>
    <li>数据库尺寸: ${dbsize}</li>
    <li>附件尺寸: ${attachsize}</li>
    <li>当前程序版本: JavaCenter Home ${statistics.version} ( ${statistics.release} )</li>
   </ul>
  </div><br />
  <div class="bdrcontent">
   <div class="title"><h3>开发团队</h3></div>
   <table>
    <tr><td width="80">版权所有</td><td><a  href="http://www.jsprun.com/" target="_blank">北京飞速创想科技有限公司 (JspRun! Inc.)</a></td></tr>
    <tr><td>公司网站</td><td><a href=http://www.jsprun.com target="_blank">http://www.jsprun.com</a></td></tr>
    <tr><td>产品网站</td><td><a href=http://j.jsprun.net target="_blank">http://j.jsprun.net</a></td></tr>
   </table>
  </div>
 </c:when>
 <c:otherwise>
  <div class="bdrcontent">
   <div class="title"><h3>欢迎光临管理平台</h3></div>
   <p>通过管理平台操作，你可以对发布的信息进行批量管理。</p><br />
   <div class="title"><h3>快捷管理菜单</h3></div>
   <ul class="listcol list2col">
    <c:if test="${menus.menu1.space == 1}"><li><a href="admincp.jsp?ac=space" style="font-weight:bold;">用户</a><p>编辑用户的积分、用户组、管理空间信息、删除用户</p></li></c:if>
    <li><a href="admincp.jsp?ac=feed" style="font-weight:bold;">事件</a><p>对&quot;我在做什么&quot;进行批量删除</p></li>
    <li><a href="admincp.jsp?ac=blog" style="font-weight:bold;">日志</a><p>对日志进行批量删除、编辑</p></li>
    <li><a href="admincp.jsp?ac=album" style="font-weight:bold;">相册</a><p>对发布的相册进行批量删除</p></li>
    <li><a href="admincp.jsp?ac=pic" style="font-weight:bold;">图片</a><p>对发布的图片进行批量删除</p></li>
    <li><a href="admincp.jsp?ac=comment" style="font-weight:bold;">评论</a><p>对发布的评论进行批量删除</p></li>
    <li><a href="admincp.jsp?ac=thread" style="font-weight:bold;">话题</a><p>对群组上面发布的话题进行批量删除、精华、置顶</p></li>
    <li><a href="admincp.jsp?ac=post" style="font-weight:bold;">回帖</a><p>对群组上面发布的回帖进行批量删除</p></li>
    <li><a href="admincp.jsp?ac=poll" style="font-weight:bold;">投票</a><p>对投票进行批量删除</p></li>
    <c:if test="${menus.menu1.tag == 1}"><li><a href="admincp.jsp?ac=tag" style="font-weight:bold;">标签</a><p>对站点的标签进行删除、关闭、合并</p></li></c:if>
    <c:if test="${menus.menu1.mtag == 1}"><li><a href="admincp.jsp?ac=mtag" style="font-weight:bold;">群组</a><p>对站点的群组进行删除、关闭、合并</p></li></c:if>
    <c:if test="${menus.menu1.event == 1}"><li><a href="admincp.jsp?ac=event" style="font-weight:bold;">活动</a><p>对站点的活动进行删除、审核、推荐</p></li></c:if>
    <c:if test="${menus.menu1.css == 1}"><li><a href="admincp.jsp?ac=css" style="font-weight:bold;">模版样式</a><p>对站点共享的模版样式进行批量审核、删除</p></li></c:if>
   </ul>
  </div><br />
 </c:otherwise>
</c:choose>
 </div><br>
</div>
<div class="side">
 <jsp:include flush="true" page="/data.do?action=setSiteData" />
 <jsp:directive.include file="side.jsp" />
</div>
<c:if test="${not empty statistics.update}">
<script language="javascript" src="http://www.jsprun.com/update/jchome.jsp?get=${statistics.update}"></script>
</c:if>
${my_checkupdate}
<jsp:directive.include file="footer.jsp" />