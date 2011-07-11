<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<c:if test="${sGlobal.inajax==0}">
 <c:if test="${empty tpl_noSideBar}">
  <c:if test="${not empty globalAd.contentbottom}"><br style="line-height:0;clear:both;"/><div id="ad_contentbottom">${jch:showAd(globalAd.contentbottom)}</div></c:if>
     </div>
    <!--/mainarea-->
    <div id="bottom"></div>
   </div>
   <!--/main-->
 </c:if>
 
  <div id="footer">
   <c:if test="${not empty TPL.templates}">${TPL.defaultTemplate.value}
    <div class="chostlp" title="切换风格"><img id="chostlp" src="${TPL.defaultTemplate.icon}" onmouseover="showMenu(this.id)" alt="${TPL.defaultTemplate.name}"></div>
    <ul id="chostlp_menu" class="chostlp_drop" style="display: none">
     <c:forEach items="${TPL.templates}" var="template">
      <li><a href="cp.jsp?ac=common&op=changetpl&name=${template.key}" title="${template.key}"><img src="${template.value}" alt="${template.key}"></a></li>
     </c:forEach>
    </ul>
   </c:if>
   <p class="r_option"><a href="javascript:;" onclick="window.scrollTo(0,0);" id="a_top" title="TOP"><img src="image/top.gif" alt="" style="padding: 5px 6px 6px;"></a></p>
   <c:if test="${not empty globalAd.footer}"><p style="padding:5px 0 10px 0;">${jch:showAd(globalAd.footer)}</p></c:if>
   <c:if test="${sConfig.close>0}"><p style="color:blue;font-weight:bold;">提醒：当前站点处于关闭状态</p></c:if>
   <p>${sConfig.sitename} -  <a href="mailto:${sConfig.adminemail}">联系我们</a><c:if test="${not empty sConfig.miibeian}"> - <a  href="http://www.miibeian.gov.cn" target="_blank">${sConfig.miibeian}</a></c:if></p>
   <p>Powered by <a  href="http://j.jsprun.net" target="_blank"><strong>JavaCenter Home</strong></a> <span title="${JCH_RELEASE}">${JCH_VERSION}</span><c:if test="${sConfig.licensed>0}"> <a  href="http://www.jsprun.com/license.jsp?host=${pageContext.request.serverName}" target="_blank">Licensed</a></c:if> &copy; 2007-2010 <a  href="http://www.jsprun.com" target="_blank">JspRun! Inc.</a></p>
   <c:if test="${sConfig.debuginfo>0}"><p>${jch:debugInfo(sGlobal,sConfig)}</p></c:if>
  </div>
 </div>
 <!--/wrap-->

<c:if test="${not empty sGlobal.appmenu}">
 <ul id="jcappmenu_menu" class="dropmenu_drop" style="display:none;">
  <li><a href="${sGlobal.appmenu.url}" title="${sGlobal.appmenu.name}" target="_blank">${sGlobal.appmenu.name}</a></li>
  <c:forEach items="${sGlobal.appmenus}" var="appmenu">
   <li><a href="${appmenu.value.url}" title="${appmenu.value.name}" target="_blank">${appmenu.value.name}</a></li>
  </c:forEach>
 </ul>
</c:if>

<c:if test="${sGlobal.supe_uid>0}">
 <ul id="membernotemenu_menu" class="dropmenu_drop" style="display:none;">
  <c:if test="${sGlobal.member.notenum>0}"><li><img src="image/icon/notice.gif" width="16" alt=""> <a href="space.jsp?do=notice"><strong>${sGlobal.member.notenum}</strong> 个新通知</a></li></c:if>
  <c:if test="${sGlobal.member.pokenum>0}"><li><img src="image/icon/poke.gif" alt=""> <a href="cp.jsp?ac=poke"><strong>${sGlobal.member.pokenum}</strong> 个新招呼</a></li></c:if>
  <c:if test="${sGlobal.member.addfriendnum>0}"><li><img src="image/icon/friend.gif" alt=""> <a href="cp.jsp?ac=friend&op=request"><strong>${sGlobal.member.addfriendnum}</strong> 个好友请求</a></li></c:if>
  <c:if test="${sGlobal.member.mtaginvitenum>0}"><li><img src="image/icon/mtag.gif" alt=""> <a href="cp.jsp?ac=mtag&op=mtaginvite"><strong>${sGlobal.member.mtaginvitenum}</strong> 个群组邀请</a></li></c:if>
  <c:if test="${sGlobal.member.eventinvitenum>0}"><li><img src="image/icon/event.gif" alt=""> <a href="cp.jsp?ac=event&op=eventinvite"><strong>${sGlobal.member.eventinvitenum}</strong> 个活动邀请</a></li></c:if>
  <c:if test="${sGlobal.member.myinvitenum>0}"><li><img src="image/icon/userapp.gif" alt=""> <a href="space.jsp?do=notice&view=userapp"><strong>${sGlobal.member.myinvitenum}</strong> 个应用消息</a></li></c:if>
 </ul>
</c:if>
<c:if test="${sGlobal.supe_uid>0}"><c:if test="${empty sCookie.checkpm}"><script language="javascript"  type="text/javascript" src="cp.jsp?ac=pm&op=checknewpm&rand=${sGlobal.timestamp}"></script></c:if></c:if>
<c:if test="${empty sCookie.sendmail}"><script language="javascript"  type="text/javascript" src="do.jsp?ac=sendmail&rand=${sGlobal.timestamp}"></script></c:if>
<c:if test="${jchConfig.updatetime =='true'}"><jsp:include flush="true" page="/other.do?action=update" /></c:if>
<c:if test="${not empty globalAd.couplet}">
 <script language="javascript" type="text/javascript" src="source/script_couplet.js"></script>
 <div id="jch_couplet" style="z-index: 10; position: absolute; display:none">
   <div id="couplet_left" style="position: absolute; left: 2px; top: 60px; overflow: hidden;">
    <div style="position: relative; top: 25px; margin:0.5em;" onMouseOver="this.style.cursor='hand'" onClick="closeBanner('jch_couplet');"><img src="image/advclose.gif"></div>
    ${jch:showAd(globalAd.couplet)}
   </div>
   <div id="couplet_rigth" style="position: absolute; right: 2px; top: 60px; overflow: hidden;">
   <div style="position: relative; top: 25px; margin:0.5em;" onMouseOver="this.style.cursor='hand'" onClick="closeBanner('jch_couplet');"><img src="image/advclose.gif"></div>
   ${jch:showAd(globalAd.couplet)}
  </div>
  <script type="text/javascript">lsfloatdiv('jch_couplet', 0, 0, '', 0).floatIt();</script>
 </div>
</c:if>
 <c:if test="${!empty sCookie.reward_log}"><script type="text/javascript">showreward();</script></c:if>
 <c:if test="${sGlobal.supe_uid != 0 && sConfig.openim == '1'}">
     <script type="text/javascript">
		  var comet = {
		  connection   : false,
		  iframediv    : false,
		
		  initialize: function() {
			if (navigator.appVersion.indexOf("MSIE") != -1) {
		      comet.connection = new ActiveXObject("htmlfile");
		      comet.connection.open();
		      comet.connection.write("<html>");
		      comet.connection.write("<script>document.domain = '"+document.domain+"'");
		      comet.connection.write("</html>");
		      comet.connection.close();
		      comet.iframediv = comet.connection.createElement("div");
		      comet.connection.appendChild(comet.iframediv);
		      comet.connection.parentWindow.comet = comet;
		      comet.iframediv.innerHTML = "<iframe id='comet_iframe' src='./servlet/comet?supe_uid=${sGlobal.supe_uid}'></iframe>";
		
		    } else if (navigator.appVersion.indexOf("KHTML") != -1) {
		      comet.connection = document.createElement('iframe');
		      comet.connection.setAttribute('id',     'comet_iframe');
		      comet.connection.setAttribute('src',    './servlet/comet?supe_uid=${sGlobal.supe_uid}');
		      with (comet.connection.style) {
		        position   = "absolute";
		        left       = top   = "-100px";
		        height     = width = "1px";
		        visibility = "hidden";
		      }
		      document.body.appendChild(comet.connection);
		
		    } else {
		      comet.connection = document.createElement('iframe');
		      comet.connection.setAttribute('id',     'comet_iframe');
		      with (comet.connection.style) {
		        left       = top   = "-100px";
		        height     = width = "1px";
		        visibility = "hidden";
		        display    = 'none';
		      }
		      comet.iframediv = document.createElement('iframe');
		      comet.iframediv.setAttribute('src', './servlet/comet?supe_uid=${sGlobal.supe_uid}');
		      comet.connection.appendChild(comet.iframediv);
		      document.body.appendChild(comet.connection);
		      
		    }
		  },
		  showServerData: function (data) {
			  if(data != '') {
			  	$("msgtips").innerHTML = "消息 (新)";
			  	titleFlicker();
			  }
		  },
		
		  onUnload: function() {
		    if (comet.connection) {
		      comet.connection = false;
		    }
		  }
		}
			
		var step = 0;
		var title = document.title;
		function titleFlicker(){
		  	if (step % 2 == 0) {
		  		document.title = "【新消息】 - "+title;
		  	} else {
		  		document.title = "【　　　】 - "+title;
		  	}
		  	step++;
		  	setTimeout("titleFlicker()", 1000);
		}
		
		if (window.addEventListener) {
			window.addEventListener("load", comet.initialize, false);
			window.addEventListener("unload", comet.onUnload, false);
	 	} else if (window.attachEvent) {
	 		window.attachEvent("onload", comet.initialize);
	 		window.attachEvent("onunload", comet.onUnload);
		}
  	</script>
  </c:if>
</body>
</html>
</c:if>