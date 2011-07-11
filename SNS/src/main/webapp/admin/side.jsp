<%@ page language="java"  pageEncoding="UTF-8"%>
<script>
var collapsed = Cookie.get('${jchConfig.cookiePre}collapse');
function collapse_change(menucount) {
	if($('menu_' + menucount).style.display == 'none') {
		$('menu_' + menucount).style.display = '';
		collapsed = collapsed.replace('_' + menucount + '_' , '');
		$('menuimg_' + menucount).src = 'image/minus.gif';
	} else {
		$('menu_' + menucount).style.display = 'none';
		collapsed = (collapsed ? collapsed : '') + '_' +  menucount + '_';
		$('menuimg_' + menucount).src = 'image/plus.gif';
	}
	Cookie.set('${jchConfig.cookiePre}collapse', collapsed, 24);
}
</script>
<c:if test="${!empty menus.menu0}">
 <div class="block style1">
  <h2><a href="###" onclick="collapse_change(1)"><img id="menuimg_1" src="${empty menu_img_1 ? 'image/minus.gif' : menu_img_1}" border="0"/></a>&nbsp;<a href="###" onclick="collapse_change(1)">基本设置</a></h2>
  <ul id="menu_1" class="folder"${menu_style_1}><c:forEach items="${acs[0]}" var="value">
   <c:if test="${menus.menu0[value]>0}"><li${ac==value ? " class='active'" : ""}><a href="admincp.jsp?ac=${value}">${menuNames[value]}</a></li></c:if></c:forEach>
  </ul>
 </div>
</c:if>
 <div class="block style1">
  <h2><a href="###" onclick="collapse_change(2)"><img id="menuimg_2" src="${empty menu_img_2 ? 'image/minus.gif' : menu_img_2}" border="0"/></a>&nbsp;<a href="###" onclick="collapse_change(2)">批量管理</a></h2>
  <ul id="menu_2" class="folder"${menu_style_2}><c:forEach items="${acs[3]}" var="value">
   <li${ac==value ? " class='active'" : ""}><a href="admincp.jsp?ac=${value}">${menuNames[value]}</a></li></c:forEach><c:forEach items="${acs[1]}" var="value">
   <c:if test="${menus.menu1[value]>0}"><li${ac==value ? " class='active'" : ""}><a href="admincp.jsp?ac=${value}">${menuNames[value]}</a></li></c:if></c:forEach>
  </ul>
 </div>
<c:if test="${!empty menus.menu2}">
 <div class="block style1">
  <h2><a href="###" onclick="collapse_change(3)"><img id="menuimg_3" src="${empty menu_img_3 ? 'image/minus.gif' : menu_img_3}" border="0"/></a>&nbsp;<a href="###" onclick="collapse_change(3)">高级设置</a></h2>
  <ul id="menu_3" class="folder"${menu_style_3}><c:forEach items="${acs[2]}" var="value">
   <c:if test="${menus.menu2[value]>0}"><li${ac==value ? " class='active'" : ""}><a href="admincp.jsp?ac=${value}">${menuNames[value]}</a></li></c:if></c:forEach>
   <!-- <c:if test="${menus.menu0['config']>0}"><li><a href="${jchConfig.JC_API}" target="_blank">JavaCenter</a></li></c:if>屏蔽应用 -->
  </ul>
 </div>
</c:if>