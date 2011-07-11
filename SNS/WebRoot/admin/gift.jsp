<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />
<style type="text/css">
.giftview {
	max-width: 70px;
	max-height: 70px;
}
.tabstyle{
	background:url(image/ht.gif) no-repeat;
	background-position: 24px 5px;
	text-indent: 72px;
}
.tabstyle_last {
	background:url(image/hl.gif) no-repeat;
	background-position: 24px 5px;
	text-indent: 72px;
}
.tabstyle2{
	background:url(image/ht.gif) no-repeat;
	background-position: 4px 5px;
	text-indent: 50px;
}
.tabstyle2_last{
	background:url(image/hl.gif) no-repeat;
	background-position: 4px 5px;
	text-indent: 50px;
}
</style>
<script type="text/javascript">
	function linkMenu() {
		if($('v_category').value == 'feeGift') {
			$('v_subcategory').style.display = '';
			$('v_price').style.display = '';
		} else {
			$('v_subcategory').style.display = 'none';
			$('v_price').style.display = 'none';
		}
	}
	function add_tbody() {
		//var len = $("cateTable").rows.length;
		//var aaa = $("cateTable").rows[len-3].children[1];
		//aaa.className="tabstyle2";
		var rowNum = $("cateTable").rows.length;
		if($("newtbody").rows.length > 0) {
			$("cateTable").rows[rowNum-2].children[1].className="tabstyle2";
		} else {
			$("cateTable").rows[rowNum-3].children[1].className="tabstyle2";
		}
		newnode = $("oldtbody").rows[0].cloneNode(true);
		$("newtbody").appendChild(newnode);
	}
	function del_tbody(element) {
		var index = element.parentNode.parentNode.rowIndex;
		var rowNum = $("cateTable").rows.length;
		if(index == (rowNum - 2)) {
			if($("newtbody").rows.length == 1) {
				$("cateTable").rows[rowNum-4].children[1].className="tabstyle2_last";
			} else {
				$("cateTable").rows[rowNum-3].children[1].className="tabstyle2_last";
			}
		}
		$('cateTable').deleteRow(index);
	}
</script>
<div class="mainarea">
	<div class="maininner">
		<div class="tabs_header">
			<ul class="tabs">
				<li${actives_gift}><a href="admincp.jsp?ac=gift&view=gift"><span>浏览全部礼物</span></a></li>
				<li${actives_category}><a href="admincp.jsp?ac=gift&view=category"><span>浏览全部分类</span></a></li>
				<li class="null"><a href="admincp.jsp?ac=gift&op=add">添加新礼物</a></li>
			</ul>
		</div>
		<form method="post" action="admincp.jsp?ac=gift" <c:if test="${param.op == 'add' || param.op == 'edit'}">enctype="multipart/form-data"</c:if>>
		<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,true)}" />
		<c:choose>
		<c:when test="${param.op == 'add' || param.op == 'edit'}">
			<div class="bdrcontent">
				<table cellspacing="0" cellpadding="0" class="formtable">
					<tr>
						<th style="width: 15em;">礼物名称</th>
						<td><input type="text" name="giftname" value="${gift.giftname}"></td>
					</tr>
					<tr>
						<th style="width: 15em;">礼物提示</th>
						<td>
							<input type="text" name="tips" value="${gift.tips}"> (留空和礼物名称相同)<br/>
							当用户鼠标移动到礼物图像时，显示的提示信息
						</td>
					</tr>
					<tr>
						<th>礼物图像</th>
						<td>
							<img id="giftview" class="giftview" src="${!empty gift ? gift.icon : 'image/event/default.jpg'}" /><br/>
							<input id="icon" type="file" name="icon"><br/>
							展示礼物外观的图像，此项必填
						</td>
					</tr>
					<tr>
						<th style="width: 15em;">所属分类</th>
						<td>
							<select id="v_category" name="category" onchange="linkMenu()">
								<c:forEach items="${categorymap}" var="cm" begin="0" end="2">
									<option value="${cm.key}" ${param.op == 'edit' && (gift.typeid == cm.key || (cm.key == 'feeGift' && gift.typeid != 'defGift' && gift.typeid != 'advGift')) ? 'selected' : ''}>${cm.value.typename}</option>
								</c:forEach>
							</select>
							<select id="v_subcategory" name="feecate" style="display:none;">
								<c:forEach items="${categorymap}" var="cm" begin="3">
									<option value="${cm.key}" ${gift.typeid == cm.key ? 'selected' : ''}>${cm.value.typename}</option>
								</c:forEach>
							</select> (必填，设置礼物所属分类)
						</td>
					</tr>
					<tr id="v_price" style="display:none;">
						<th style="width: 15em;">礼物价格</th>
						<td>
							<input type="text" name="price" value="${gift.price}"/> (必填，值必须大于0)
						</td>
					</tr>
					<script type="text/javascript">
						if($('v_category').value == 'feeGift') {
							$('v_subcategory').style.display = '';
							$('v_price').style.display = '';
						}
					</script>
				</table>
			</div>
			<div class="footactions">
				<input type="hidden" name="giftid" value="${gift.giftid}" />
				<input type="submit" name="giftsubmit" value=" 提交 " class="submit">
				<input type="button" value=" 返回 " class="submit" onclick="javascript:history.go(-1);" />
			</div>
		</c:when>
		<c:when test="${param.op == 'delete' && !empty param.giftid}">
			<div class="bdrcontent">
				<table cellspacing="0" cellpadding="0" class="formtable">
					<tr>
						<th style="width: 15em;">礼物名称</th>
						<td>${gift.giftname}</td>
					</tr>
					<tr>
						<th style="width: 15em;">礼物提示</th>
						<td>${gift.tips}</td>
					</tr>
					<tr>
						<th>礼物图像</th>
						<td><img id="giftview" class="giftview" src="${gift.icon}" /></td>
					</tr>
					<tr>
						<th style="width: 15em;">所属分类</th>
						<td>
							<c:choose>
								<c:when test="${gift.typeid == 'defGift'}">普通</c:when>
								<c:when test="${gift.typeid == 'advGift'}">高级</c:when>
								<c:otherwise>收费 - ${categorymap[gift.typeid].typename}</c:otherwise>
							</c:choose>
						</td>
					</tr>
					<c:if test="${gift.typeid != 'defGift' && gift.typeid != 'advGift'}">
					<tr id="v_price">
						<th style="width: 15em;">礼物价格</th>
						<td>${gift.price}</td>
					</tr>
					</c:if>
					<c:if test="${used}">
					<tr id="v_price" style="color:red;">
						<th style="width: 15em;">重要提示<input type="hidden" name="used" value="${used}"/></th>
						<td>该礼物已经被用户发送使用过，删除将影响到发送或接受该礼物的用户</td>
					</tr>
					</c:if>
				</table>
			</div>
			<div class="footactions">
				<input type="hidden" name="giftid" value="${gift.giftid}" />
				<input type="hidden" name="typeid" value="${gift.typeid}" />
				<input type="hidden" name="icon" value="${gift.icon}" />
				<input type="submit" name="giftdeletesubmit" value=" 确认删除 " class="submit">
				<input type="button" value=" 取消 " class="submit" onclick="javascript:history.go(-1);" />
			</div>
		</c:when>
		<c:when test="${param.op == 'delete' && !empty param.typeid}">
			<div class="bdrcontent">
				<table cellspacing="0" cellpadding="0" class="formtable">
					<tr>
						<th style="width: 15em;">分类名称</th>
						<td>${type.typename}</td>
					</tr>
					<tr>
						<th style="width: 15em;">分类排序</th>
						<td>${type.order}</td>
					</tr>
					<tr>
						<th style="width: 15em;">分类归属</th>
						<td>
							收费礼物
						</td>
					</tr>
					<tr style="color:red">
						<th>重要提示<input type="hidden" name="used" value="${used}"/></th>
						<td>该分类下有礼物数据！如果删除该分类，将会删除该分类下的所有礼物数据，将影响到发送或接受这些礼物的用户</td>
					</tr>
				</table>
			</div>
			<div class="footactions">
				<input type="hidden" name="typeid" value="${type.typeid}" />
				<input type="submit" name="categorydeletesubmit" value=" 确认删除 " class="submit">
				<input type="button" value=" 取消 " class="submit" onclick="javascript:history.go(-1);" />
			</div>
		</c:when>
		<c:when test="${param.view == 'category'}">
			<div class="bdrcontent">
				<table id="cateTable" cellspacing="0" cellpadding="0" class="formtable">
					<tr>
						<th>名称</th>
						<th>显示顺序</th>
						<th width="80">操作</th>
					</tr>
					<c:forEach items="${categorymap}" var="m" begin="0" end="2">
					<tr>
						<th>
							${m.value.typename}
						</th>
						<td>${m.value.order}</td>
						<td width="80">系统分类</td>
					</tr>
					<c:if test="${m.key == 'feeGift'}">
						<c:forEach items="${categorymap}" var="sm" begin="3" varStatus="vs">
						<tr>
							<td class="tabstyle">
								<input type="hidden" name="typeid[]" value="${sm.key}"/>
								<input type="text" name="typename[]" value="${sm.value.typename}" size="8"/>
							</td>
							<td ${vs.index+1 == categorysize ? "class='tabstyle2_last'" : "class='tabstyle2'"}><input type="text" name="order[]" value="${sm.value.order}" size="4"></td>
							<td width="80"><a href="admincp.jsp?ac=gift&op=delete&typeid=${sm.key}">删除</a></td>
						</tr>
						</c:forEach>
						<tbody id="oldtbody" style="display:none;">
							<tr>
								<td class="tabstyle"><input type="text" name="typename[]" size="8"/></td>
								<td class="tabstyle2_last"><input type="text" name="order[]" value="0" size="4"></td>
								<td width="80"><a onclick="del_tbody(this)" style="cursor:pointer;">删除</a></td>
							</tr>
						</tbody>
						<tbody id="newtbody"></tbody>
						<tr>
							<td class="tabstyle_last"><img src="image/add_subcate.gif"/>&nbsp;<a href="javascript:add_tbody();">新增分类</a></td>
							<td></td>
							<td></td>
						</tr>
					</c:if>
					</c:forEach>
				</table>
			</div>
			<div class="footactions"><input type="submit" name="categorysubmit" value=" 提交 " class="submit"></div>
		</c:when>
		<c:otherwise>
			<div class="bdrcontent">
				<table cellspacing="0" cellpadding="0" class="formtable">
					<tr>
						<th>名称</th>
						<th>所属分类</th>
						<th>购买次数</th>
						<th>添加日期</th>
						<th width="80">操作</th>
					</tr>
					<c:forEach items="${gifts}" var="g">
					<tr>
						<th>${g.giftname}</th>
						<td>
							<c:choose>
								<c:when test="${g.typeid == 'defGift'}">普通</c:when>
								<c:when test="${g.typeid == 'advGift'}">高级</c:when>
								<c:otherwise>收费 - ${categorymap[g.typeid].typename}</c:otherwise>
							</c:choose>
						</td>
						<td>${g.buycount}</td>
						<td><jch:date dateformat="yyyy-M-d" timestamp="${g.addtime}" format="1"/></td>
						<td width="80">
							<a href="admincp.jsp?ac=gift&op=edit&giftid=${g.giftid}">编辑</a> | 
							<a href="admincp.jsp?ac=gift&op=delete&giftid=${g.giftid}">删除</a>
						</td>
					</tr>
					</c:forEach>
				</table>
			</div>
			<div class="footactions"><div class="pages">${multi}</div></div>
		</c:otherwise>
		</c:choose>
		</form>
	</div>
</div>
<div class="side">
	<jsp:directive.include file="side.jsp" />
</div>
<jsp:directive.include file="footer.jsp" />