<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />
<div class="mainarea">
	<div class="maininner">
		<div class="tabs_header">
			<ul class="tabs">
				<li${actives_view}><a href="admincp.jsp?ac=ad"><span>浏览广告</span></a></li>
				<li class="null"><a href="admincp.jsp?ac=ad&op=add">添加新广告</a></li>
			</ul>
		</div>
		<c:choose>
			<c:when test="${empty param.op}">
				<form method="post" action="admincp.jsp?ac=ad">
					<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,true)}" />
					<div class="bdrcontent">
						<div class="title">
							<h3>系统内置广告</h3>
							<p>广告位置已经内置，根据需要自己填写要显示的广告内容就可以了</p>
						</div>
						<table cellspacing="0" cellpadding="0" class="formtable">
							<tr>
								<th>标题</th>
								<th width="24%">页面位置</th>
								<th width="8%">有效</th>
								<th width="8%">编辑</th>
							</tr>
							<c:forEach items="${listvalue['1']}" var="value">
								<tr>
									<td><input type="checkbox" name="adids[]" value="${value.adid}"> ${value.title}</td>
									<td><a href="admincp.jsp?ac=ad&pagetype=${value.pagetype}">${pageTypes[value.pagetype]}</a></td>
									<td>${value.available}</td>
									<td><a href="admincp.jsp?ac=ad&op=edit&adid=${value.adid}">编辑</a></td>
								</tr>
							</c:forEach>
						</table>
						<br />
						<div class="title">
							<h3>自定义广告列表</h3>
							<p>可以自由决定广告的显示位置，只需要获取广告代码(模板内嵌代码，或者Javascript代码均可)，然后将广告代码插入到模板任意位置即可显示</p>
						</div>
						<table cellspacing="0" cellpadding="0" class="formtable">
							<tr>
								<th>标题</th>
								<th width="32%">调用广告</th>
								<th width="8%">操作</th>
							</tr>
							<c:forEach items="${listvalue['0']}" var="value">
								<tr>
									<td><input type="checkbox" name="adids[]" value="${value.adid}"> ${value.title}</td>
									<td>
										<a href="admincp.jsp?ac=ad&op=tpl&adid=${value.adid}">模板内嵌代码</a> |
										<a href="admincp.jsp?ac=ad&op=js&adid=${value.adid}">Javascript代码</a>
									</td>
									<td><a href="admincp.jsp?ac=ad&op=edit&adid=${value.adid}">编辑</a></td>
								</tr>
							</c:forEach>
						</table>
					</div>
					<div class="footactions">
						<input type="checkbox" name="chkall" onclick="checkAll(this.form, 'adid')">全选
						<input type="submit" name="delsubmit" value="批量删除" class="submit">
					</div>
				</form>
			</c:when>
			<c:when test="${param.op == 'add' || param.op == 'edit'}">
				<script type="text/JavaScript">
				function style_show(theobj) {
					var styles,key;
					styles = new Array('html','flash','image','text');
					for(key in styles){
						var obj=$('style_'+styles[key]);
						obj.style.display = styles[key]==theobj.options[theobj.selectedIndex].value?'':'none';
					}
				}
				</script>
				<form method="post" action="admincp.jsp?ac=ad">
					<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,true)}" />
					<div class="bdrcontent">
						<table cellspacing="0" cellpadding="0" class="formtable">
							<tr>
								<th style="width: 12em;">广告类型</th>
								<td>
									<input type="radio" name="system" value="1" ${system_1} onclick="$('style_system').style.display='';">系统内置广告
									<input type="radio" name="system" value="0" ${system_0} onclick="$('style_system').style.display='none';">用户自定义广告
								</td>
							</tr>
							<tr>
								<th>广告标题</th>
								<td><input name="title" value="${advalue.title}" class="t_input"></td>
							</tr>
							<tbody id="style_system" style="display: ${advalue.system == 0 ? 'none' : ''}">
								<tr>
									<th>页面位置</th>
									<td>
										<select name="pagetype">
											<option value="header"${pagetype_header}>页面顶部 (宽970px)</option>
											<option value="footer"${pagetype_footer}>页脚 (宽970px)</option>
											<option value="contenttop"${pagetype_contenttop}>页面主区域上方 (宽800px)</option>
											<option value="contentbottom"${pagetype_contentbottom}>页面主区域下方 (宽800px)</option>
											<option value="rightside"${pagetype_rightside}>日志(话题)内容区域 (宽300px)</option>
											<option value="couplet"${pagetype_couplet}>对联广告 (宽90px)</option>
											<option value="feedbox"${pagetype_feedbox}>动态顶部 (宽500px)</option>
										</select>
									</td>
								</tr>
								<tr>
									<th>有效性</th>
									<td>
										<input type="radio" name="available" value="1"${available_1}>有效
										<input type="radio" name="available" value="0"${available_0}>无效
									</td>
								</tr>
							</tbody>
							<tr>
								<th>广告方式</th>
								<td>
									<select name="adcode[type]" onchange="style_show(this)">
										<option value="html"${adcode_html}>代码</option>
										<option value="flash"${adcode_flash}>Flash</option>
										<option value="image"${adcode_image}>Image</option>
										<option value="text"${adcode_text}>文本</option>
									</select>
								</td>
							</tr>
							<tbody id="style_html" style="display: ${advalue.adcode.type != 'html' ? 'none' : ''}">
								<tr>
									<th>广告代码(必填)</th>
									<td><textarea rows="10" style="width: 98%;" name="adcode[html]">${advalue.adcode.html}</textarea></td>
								</tr>
							</tbody>
							<tbody id="style_flash" style="display: ${advalue.adcode.type != 'flash' ? 'none' : ''}">
								<tr>
									<th>Flash宽度(必填)</th>
									<td><input name="adcode[flashwidth]" value="${advalue.adcode.flashwidth}" size="5"></td>
								</tr>
								<tr>
									<th>Flash高度(必填)</th>
									<td><input name="adcode[flashheight]" value="${advalue.adcode.flashheight}" size="5"></td>
								</tr>
								<tr>
									<th>Flash URL(必填)</th>
									<td><input name="adcode[flashurl]" value="${advalue.adcode.flashurl}" size="50"></td>
								</tr>
							</tbody>
							<tbody id="style_image" style="display: ${advalue.adcode.type != 'image' ? 'none' : ''}">
								<tr>
									<th>图片地址(必填)</th>
									<td><input name="adcode[imagesrc]" value="${advalue.adcode.imagesrc}" size="50"></td>
								</tr>
								<tr>
									<th>图片链接(必填)</th>
									<td><input name="adcode[imageurl]" value="${advalue.adcode.imageurl}" size="50"></td>
								</tr>
								<tr>
									<th>图片宽度(选填)</th>
									<td><input name="adcode[imagewidth]" value="${advalue.adcode.imagewidth}" size="5"></td>
								</tr>
								<tr>
									<th>图片高度(选填)</th>
									<td><input name="adcode[imageheight]" value="${advalue.adcode.imageheight}" size="5"></td>
								</tr>
								<tr>
									<th>图片替换文字(选填)</th>
									<td><input name="adcode[imagealt]" value="${advalue.adcode.imagealt}"></td>
								</tr>
							</tbody>
							<tbody id="style_text" style="display: ${advalue.adcode.type != 'text' ? 'none' : ''}">
								<tr>
									<th>文字内容(必填)</th>
									<td><input name="adcode[textcontent]" value="${advalue.adcode.textcontent}" size="50"></td>
								</tr>
								<tr>
									<th>文字链接(必填)</th>
									<td><input name="adcode[texturl]" value="${advalue.adcode.texturl}" size="50"></td>
								</tr>
								<tr>
									<th>文字大小(选填)</th>
									<td><input name="adcode[textsize]" value="${advalue.adcode.textsize}" size="5"> px</td>
								</tr>
							</tbody>
						</table>
					</div>
					<div class="footactions">
						<input type="hidden" name="adid" value="${advalue.adid}">
						<input type="submit" name="adsubmit" value="提交" class="submit">
					</div>
				</form>
			</c:when>
			<c:when test="${param.op == 'tpl'}">
				<div class="bdrcontent">
					<div class="title"><h3>模版调用代码</h3></div>
					<table class="formtable">
						<tr><td>请将以下代码复制，放到站点模板的任意页面的指定位置即可。</td></tr>
						<tr><td><input type="text" name="blockadcode" value="${adcode}" size="80"></td></tr>
					</table>
				</div>
			</c:when>
			<c:when test="${param.op == 'js'}">
				<div class="bdrcontent">
					<div class="title"><h3>Javascript调用代码</h3></div>
					<table class="formtable">
						<tr><td><textarea name="blockadcode" style="width: 98%;" rows="5">${adcode}</textarea></td></tr>
					</table>
				</div>
			</c:when>
		</c:choose>
	</div>
</div>
<div class="side">
	<jsp:directive.include file="side.jsp" />
</div>
<jsp:directive.include file="footer.jsp" />