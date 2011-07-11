<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}" />
<c:if test="${op == 'edit' || op == 'editpic'}">
	<h2 class="title"><img src="image/app/album.gif" />相册<c:if test="${!empty album.albumname}"> - ${album.albumname}</c:if></h2>
	<div class="tabs_header">
		<ul class="tabs">
			<li${op == 'edit' ? " class='active'" : ""}><a href="cp.jsp?ac=album&op=edit&albumid=${albumid }"><span>编辑相册信息</span></a></li>
			<li${op == 'editpic' ? " class='active'" : ""}><a href="cp.jsp?ac=album&op=editpic&albumid=${albumid }"><span>编辑图片</span></a></li>
			<li><a href="space.jsp?uid=${space.uid}&do=album&view=me"><span>返回我的相册</span></a></li>
		</ul>
	</div>
</c:if>
<c:choose>
	<c:when test="${op == 'edit'}">
		<form method="post" id="theform" name="theform" action="cp.jsp?ac=album&op=edit&albumid=${albumid}" class="c_form">
			<table cellspacing="0" cellpadding="0" class="formtable">
				<tr>
					<th width="80"><label for="albumname">相册名</label></th>
					<td><input type="text" id="albumname" name="albumname" value="${album.albumname}" size="20" class="t_input" /></td>
				</tr>
				<tr>
					<th>隐私设置</th>
					<td>
						<select name="friend" onchange="passwordShow(this.value);">
							<option value="0"${friend_0}>全站用户可见</option>
							<option value="1"${friend_1}>全好友可见</option>
							<option value="2"${friend_2}>仅指定的好友可见</option>
							<option value="3"${friend_3}>仅自己可见</option>
							<option value="4"${friend_4}>凭密码查看</option>
						</select>
						<span id="span_password" style="${passwordstyle}">密码:<input type="text" name="password" value="${album.password}" size="10"></span>
					</td>
				</tr>
				<tbody id="tb_selectgroup" style="${selectgroupstyle}">
					<tr>
						<th>指定好友</th>
						<td>
							<select name="selectgroup" onchange="getgroup(this.value);">
								<option value="">从好友组选择好友</option>
								<c:forEach items="${groups}" var="group">
									<option value="${group.key}">${group.value}</option>
								</c:forEach>
							</select> 多次选择会累加到下面的好友名单
						</td>
					</tr>
					<tr>
						<th>&nbsp;</th>
						<td><textarea name="target_names" id="target_names" style="width: 85%;" rows="3">${album.target_names}</textarea><br>(可以填写多个好友名，请用空格进行分割)</td>
					</tr>
				</tbody>
				<tr>
					<th>&nbsp;</th>
					<td>
						<input type="hidden" name="refer" value="${sGlobal.refer}" />
						<input type="hidden" name="editsubmit" value="true" />
						<button name="submit" type="submit" class="submit" value="true">确定</button>
					</td>
				</tr>
				<tr>
					<th>&nbsp;</th>
					<td><a href="cp.jsp?ac=album&op=delete&albumid=${album.albumid }" id="album_delete_${album.albumid}" onclick="ajaxmenu(event, this.id)">删除相册</a></td>
				</tr>
			</table>
			<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
		</form>
	</c:when>
	<c:when test="${op == 'editpic'}">
		<div class="notice">提示：您可以指定一张图片作为当前相册的封面图片，但是，在下次上传新的图片后，系统会自动选择一张新图片来更新本相册的封面图片。</div>
		<c:choose>
			<c:when test="${!empty list}">
				<div class="album_list">
					<form method="post" id="theform" name="theform" action="cp.jsp?ac=album&op=editpic&albumid=${albumid}">
						<table cellspacing="6" cellpadding="0">
							<c:forEach items="${list}" var="value">
								<tr>
									<td class="album">
										<table cellspacing="0" cellpadding="0">
											<tr>
												<td width="20"><input type="checkbox" name="ids[${value.picid}]" value="${value.picid}"></td>
												<td class="image"><a href="${value.bigpic}" target="_blank"><img src="${value.pic}"/></a> <c:if test="${!empty album.albumname}"><p style="text-align: center;"><a href="cp.jsp?ac=album&op=setpic&picid=${value.picid}" id="a_picid_${value.picid}" onclick="ajaxmenu(event, this.id, 0, 2000)">设为封面</a></p></c:if></td>
												<td><textarea name="title[${value.picid}]" rows="4" cols="70">${value.title}</textarea></td>
											</tr>
										</table>
									</td>
								</tr>
							</c:forEach>
							<tr>
								<td style="padding: 10px;">
									<input type="checkbox" id="chkall" name="chkall" onclick="checkAll(this.form, 'ids')"><label for="chkall">全选</label>
									<input type="submit" name="editpicsubmit" value="更新说明" class="submit" onclick="this.form.action+='&subop=update';">
									<input type="submit" name="editpicsubmit" value="删除" class="submit" onclick="this.form.action+='&subop=delete';return ischeck('theform', 'ids')">
									<input type="submit" name="editpicsubmit" value="转移到:" class="submit" onclick="this.form.action+='&subop=move';return ischeck('theform', 'ids')">
									<select name="newalbumid">
										<c:forEach items="${albumlist}" var="value">
											<c:if test="${albumid != value.albumid}"><option value="${value.albumid}">${value.albumname}</option></c:if>
										</c:forEach>
										<c:if test="${albumid>0}"><option value="0">默认相册</option></c:if>
									</select>
									<br>删除图片提示：如果你要删除的图片出现在你的日志、话题中，删除后，会导致内容里面的图片同时无法显示。
								</td>
							</tr>
						</table>
						<input type="hidden" name="page" value="${page}" />
						<input type="hidden" name="editpicsubmit" value="true" />
						<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
					</form>
				</div>
				<div class="page">${multi}</div>
			</c:when>
			<c:otherwise><div class="c_form">该相册下还没有图片。</div></c:otherwise>
		</c:choose>
	</c:when>
	<c:when test="${op == 'delete'}">
		<h1>删除相册</h1>
		<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
		<div class="popupmenu_inner">
			<form method="post" id="theform" name="theform" action="cp.jsp?ac=album&op=delete&albumid=${albumid}">
				<p>确定删除相册吗？</p>
				<p>
					相册中的图片:
					<select name="moveto">
						<option value="-1">彻底删除</option>
						<option value="0">转移到 默认相册</option>
						<c:forEach items="${albums}" var="key_value">
							<option value="${key_value.value.albumid}">转移到 ${key_value.value.albumname}</option>
						</c:forEach>
					</select>
				</p>
				<p class="btn_line">
					<input type="hidden" name="refer" value="${sGlobal.refer}" />
					<input type="hidden" name="deletesubmit" value="true" />
					<button name="submit" type="submit" class="submit" value="true">确定</button>
				</p>
				<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
			</form>
		</div>
	</c:when>
	<c:when test="${op == 'edittitle'}">
		<h1>编辑图片说明</h1>
		<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
		<div class="popupmenu_inner">
			<form id="titleform" name="titleform" action="cp.jsp?ac=album&op=editpic&subop=update&albumid=${pic.albumid}" method="post">
				<textarea name="title[${pic.picid}]" cols="50" rows="7">${pic.title}</textarea>
				<p class="btn_line">
					<input type="submit" name="editpicsubmit" class="submit" value="更新">
					<input type="hidden" name="refer" value="${sGlobal.refer}">
				</p>
				<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
			</form>
		</div>
	</c:when>
	<c:when test="${op == 'edithot'}">
		<h1>调整热度</h1>
		<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
		<div class="popupmenu_inner">
			<form method="post" action="cp.jsp?ac=album&op=edithot&picid=${picid}">
				<p class="btn_line">
					新的热度：<input type="text" name="hot" value="${pic.hot}" size="5">
					<input type="hidden" name="refer" value="${sGlobal.refer}" />
					<input type="hidden" name="hotsubmit" value="true" />
					<input type="submit" name="btnsubmit" value="确定" class="submit" />
				</p>
				<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
			</form>
		</div>
	</c:when>
</c:choose>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}" />