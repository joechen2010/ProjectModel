<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ include file="install_method.jsp"%>
<jsp:include page="install_header.jsp"></jsp:include>
<%
	boolean checkOK = true;
	Map<String, String> perms = new HashMap<String, String>();
	String jchRoot = JavaCenterHome.jchRoot;
	if (!checkFilePermissions(jchRoot + "config.properties", true)) {
		perms.put("config", "失败");
		checkOK = false;
	} else {
		perms.put("config", "OK");
	}
	if (!checkFilePermissions(jchRoot + "attachment/", false)) {
		perms.put("attachment", "失败");
		checkOK = false;
	} else {
		perms.put("attachment", "OK");
	}
	if (!checkFilePermissions(jchRoot + "data/", false)) {
		perms.put("data", "失败");
		checkOK = false;
	} else {
		perms.put("data", "OK");
	}
	request.setAttribute("perms", perms);
	request.setAttribute("checkok", checkOK);
%>
<script type="text/javascript">
	function readme() {
		var tbl_readme = document.getElementById('tbl_readme');
		if(tbl_readme.style.display == '') {
			tbl_readme.style.display = 'none';
		} else {
			tbl_readme.style.display = '';
		}
	}
	</script>
<table class="showtable">
	<tr>
		<td>
			<strong>欢迎您使用JavaCenter Home</strong><br>
			通过 JavaCenter Home，作为建站者的您，可以轻松构建一个以好友关系为核心的交流网络，让站点用户可以用一句话记录生活中的点点滴滴；方便快捷地发布日志、上传图片；更可以十分方便的与其好友们一起分享信息、讨论感兴趣的话题；轻松快捷的了解好友最新动态。<br>
			<a href="javascript:;" onclick="readme()"><strong>请先认真阅读我们的软件使用授权协议</strong></a>
		</td>
	</tr>
</table>
<table>
	<tr>
		<td>
			<strong>文件/目录权限设置</strong><br>
			在您执行安装文件进行安装之前，先要设置相关的目录属性，以便数据文件可以被程序正确读/写/删/创建子目录。<br>
			推荐您这样做：<br>
			使用 FTP 软件登录您的服务器，将服务器上以下目录、以及该目录下面的所有文件的属性设置为777，win主机请设置internet来宾帐户可读写属性<br>
			<table class="datatable">
				<tr style="font-weight: bold;">
					<td>名称</td>
					<td>所需权限属性</td>
					<td>说明</td>
					<td>检测结果</td>
				</tr>
				<tr>
					<td><strong>./config.properties</strong></td>
					<td>读/写</td>
					<td>系统配置文件</td>
					<td>${perms.config}</td>
				</tr>
				<tr>
					<td><strong>./attachment/</strong> (包括本目录、子目录和文件)</td>
					<td>读/写/删</td>
					<td>附件目录</td>
					<td>${perms.attachment}</td>
				</tr>
				<tr>
					<td><strong>./data/</strong> (包括本目录、子目录和文件)</td>
					<td>读/写/删</td>
					<td>站点数据目录</td>
					<td>${perms.data}</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
<c:choose>
	<c:when test="${!checkok}">
		<table>
			<tr>
				<td>
					<b>出现问题</b>:<br>
					系统检测到以上目录或文件权限没有正确设置<br>
					强烈建议正常设置权限后再刷新本页面以便继续安装<br>
					否则系统可能会出现无法预料的问题 [<a href="${theurl}?step=1">强制继续</a>]
				</td>
			</tr>
		</table>
	</c:when>
	<c:otherwise>
		<form id="theform" method="post" action="${theurl}?step=1">
			<table class=button>
				<tr><td><input type="submit" id="startsubmit" name="startsubmit" value="接受授权协议，开始安装JavaCenter Home"></td></tr>
			</table>
			<input type="hidden" name="formhash" value="${formHash}">
		</form>
	</c:otherwise>
</c:choose>
<table id="tbl_readme" style="display: none;" class="showtable">
	<tr><td><strong>请您务必仔细阅读下面的许可协议:</strong></td></tr>
	<tr>
		<td>
			<div>
				中文版授权协议 适用于中文用户
				<p>版权所有 (c) 2007-2010，北京飞速创想科技有限公司保留所有权利。</p>
				<p>感谢您选择 JavaCenter Home 系列产品。希望我们的努力能为您提供一个高效快速和强大的社会化网络（SNS）解决方案。</p>
				<p>北京飞速创想科技有限公司为 JavaCenter Home 所有系列产品的开发商，依法独立拥有 JavaCenter Home 所有系列产品著作权。北京飞速创想科技有限公司网址为 http://www.jsprun.com，JavaCenter Home 官方网站网址为 http://j.jsprun.net，JavaCenter Home 官方讨论区网址为 http://www.jsprun.net。</p>
				<p>JavaCenter Home 的著作权已在中华人民共和国国家版权局登记，著作权受到法律和国际公约保护。使用者：无论个人或组织、盈利与否、用途如何（包括以学习和研究为目的），均需仔细阅读本协议，在理解、同意、并遵守本协议的全部条款后，方可开始使用 JavaCenter Home 所有系列软件。</p>
				<p>本授权协议适用且仅适用于 JavaCenter Home 系列产品所有版本，北京飞速创想科技有限公司拥有对本授权协议的最终解释权。</p>
				<ul type="I">
					<li>
						<b>协议许可的权利</b>
						<ul type="1">
							<li>您可以在完全遵守本最终用户授权协议的基础上，将本软件应用于非商业用途，而不必支付软件版权授权费用。</li>
							<li>您可以在协议规定的约束和限制范围内修改 JavaCenter Home 源代码(如果被提供的话)或界面风格以适应您的网站要求。</li>
							<li>您拥有使用本软件构建的社区中全部会员资料、文章及相关信息的所有权，并独立承担与文章内容的相关法律责任。</li>
							<li>获得商业授权之后，您可以将本软件应用于商业用途，同时依据所购买的授权类型中确定的技术支持期限、技术支持方式和技术支持内容，自购买时刻起，在技术支持期限内拥有通过指定的方式获得指定范围内的技术支持服务。商业授权用户享有反映和提出意见的权力，相关意见将被作为首要考虑，但没有一定被采纳的承诺或保证。</li>
						</ul>
					</li>
					<p></p>
					<li>
						<b>协议规定的约束和限制</b>
						<ul type="1">
							<li>未获商业授权之前，不得将本软件用于商业用途（包括但不限于企业网站、经营性网站、以营利为目或实现盈利的网站）。购买商业授权请登陆 http://www.jsprun.com 参考相关说明，也可以致电 010-51900451 了解详情。</li>
							<li>如未获商业授权，将本软件用于商业用途（包括但不限于企业网站、经营性网站、以营利为目或实现盈利的网站），且在自将本软件用于商业用途起一个月内未告知北京飞速创想科技有限公司并购买商业授权，继续将本软件用于商业用途，那么北京飞速创想科技有限公司将追究其违约责任。</li>
							<li>不得对本软件或与之关联的商业授权进行出租、出售、抵押或发放子许可证。</li>
							<li>无论如何，即无论用途如何、是否经过修改或美化、修改程度如何，只要使用 JavaCenter Home 的整体或任何部分，未经书面许可，论坛页面页脚处的 JavaCenter Home 名称、软件相关版权信息和北京飞速创想科技有限公司下属网站（http://www.jsprun.com 或 http://j.jsprun.net） 的链接都必须保留，而不能清除或修改。</li>
							<li>禁止在 JavaCenter Home 的整体或任何部分基础上以发展任何派生版本、修改版本、翻译版本或第三方版本用于重新分发。</li>
							<li>如果您未能遵守本协议的条款，您的授权将被终止，所被许可的权利将被收回，并承担相应法律责任。</li>
						</ul>
					</li>
					<p></p>
					<li>
						<b>有限担保和免责声明</b>
						<ul type="1">
							<li>本软件及所附带的文件是作为不提供任何明确的或隐含的赔偿或担保的形式提供的。</li>
							<li>用户出于自愿而使用本软件，您必须了解使用本软件的风险，在尚未购买产品技术服务之前，我们不承诺提供任何形式的技术支持、使用担保，也不承担任何因使用本软件而产生问题的相关责任。</li>
							<li>北京飞速创想科技有限公司不对使用本软件构建的社区网站中的文章或信息承担责任。</li>
						</ul>
					</li>
				</ul>
				<p>有关 JavaCenter Home 最终用户授权协议、商业授权与技术服务的详细内容，均由 JspRun! 官方网站独家提供。北京飞速创想科技有限公司拥有在不事先通知的情况下，修改授权协议和服务价目表的权力，修改后的协议或价目表对自改变之日起的新授权用户生效。</p>
				<p>电子文本形式的授权协议如同双方书面签署的协议一样，具有完全的和等同的法律效力。您一旦开始安装 JavaCenter Home，即被视为完全理解并接受本协议的各项条款，在享有上述条款授予的权力的同时，受到相关的约束和限制。协议许可范围以外的行为，将直接违反本授权协议并构成侵权，我们有权随时终止授权，责令停止损害，并保留追究相关责任的权力。</p>
			</div>
		</td>
	</tr>
</table>
<jsp:include page="install_footer.jsp"></jsp:include>