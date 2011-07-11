<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />
<div class="mainarea">
	<div class="maininner">
		<form method="post" action="admincp.jsp">
			<div class="bdrcontent">如果发现站点某些统计出现问题（比如某些人的日志数有误）时候，才使用本功能进行重新统计。重新统计对服务器负载占用严重，建议选择站点访问人数较少的时候进行。</div>
			<br />
			<div class="bdrcontent">
				<table cellspacing="0" cellpadding="0" class="formtable">
					<tr>
						<th style="width: 12em;">选择更新统计数类型</th>
						<td>
							<select name="counttype">
								<option value="blogReplyNum">日志回复数</option>
								<option value="spaceFriendNum">空间好友数</option>
								<option value="spaceFriend">空间好友缓存</option>
								<option value="mtagMemberNum">群组成员数</option>
								<option value="mtagThreadNum">群组话题数</option>
								<option value="mtagPostNum">群组回帖数</option>
								<option value="threadReplyNum">话题回复数</option>
								<option value="albumPicNum">相册图片数</option>
								<option value="tagBlogNum">标签日志数</option>
							</select>
						</td>
					</tr>
					<tr>
						<th>每次处理个数</th>
						<td><input type="text" name="perpage" value="10" size="10"></td>
					</tr>
				</table>
			</div>
			<div class="footactions">
				<input type="hidden" name="ac" value="stat">
				<input type="submit" name="countsubmit" value="统计更新" class="submit">
			</div>
		</form>
	</div>
</div>
<div class="side">
	<jsp:directive.include file="side.jsp" />
</div>
<jsp:directive.include file="footer.jsp" />