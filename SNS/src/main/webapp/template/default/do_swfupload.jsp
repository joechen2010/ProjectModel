<%@ page language="java"  pageEncoding="UTF-8" contentType="application/xml; charset=utf-8"%><?xml version="1.0" encoding="UTF-8"?>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:choose>
<c:when test="${!empty uploadResponse && uploadResponse}">
<uploadResponse>
	<message>${status == 'success' ? '完成' : uploadfiles}</message>
	<status>${status }</status>
	<proid>${proid }</proid>
	<albumid>${albumid }</albumid>
	<picid>${picid }</picid>
	<c:if test="${!empty fileurl}">
	<filepath>${fileurl }</filepath>
	</c:if>
</uploadResponse>
</c:when>
<c:otherwise>
<parameter>
	<c:choose>
	<c:when test="${iscamera}">
		<images>
			<c:forEach items="${dirarr}" var="key_value">
			<categories name="${key_value.value[0]}" directory="${key_value.value[1]}">
				<c:forEach items="${key_value.value[2]}" var="subValue">
					<img name="${subValue }"/>
				</c:forEach>
			</categories>
			</c:forEach>
		</images></c:when>
	<c:when test="${isdoodle}">
		<background>
			<c:forEach items="${filearr}" var="filename">
			<bg url="./image/doodle/big/${filename}" thumb="./image/doodle/thumb/${filename}"/>
			</c:forEach>
		</background>
	</c:when>
	<c:when test="${isupload}">
		<allowsExtend>
			<extend depict="All Image File(*.jpg,*.jpeg,*.gif,*.png)">*.jpg,*.gif,*.png,*.jpeg</extend>
		</allowsExtend>
	</c:when>
	</c:choose>
	<language>
			<create>创建</create>
			<notCreate>取消</notCreate>
			<albumName>相册名</albumName>
			<createTitle>创建新相册</createTitle>
		<c:choose>
		<c:when test="${!isdoodle}">
			<okbtn>继续</okbtn>
			<cancelbtn>查看</cancelbtn>
			<c:choose>
			<c:when test="${isupload}">
				<fileName>文件名</fileName>
				<depict>描述(单击修改)</depict>
				<size>文件大小</size>
				<stat>上传进度</stat>
				<aimAlbum>上传到:</aimAlbum>
				<browser>浏览</browser>
				<delete>删除</delete>
				<upload>上传</upload>
				<okTitle>上传完成</okTitle>
				<okMsg>所有文件上传完成!</okMsg>
				<uploadTitle>正在上传</uploadTitle>
				<uploadMsg1>总共有</uploadMsg1>
				<uploadMsg2>个文件等待上传,正在上传第</uploadMsg2>
				<uploadMsg3>个文件</uploadMsg3>
				<bigFile>文件过大</bigFile>
				<uploaderror>上传失败</uploaderror>
			</c:when>
			<c:when test="${iscamera}">
				<desultory>抓拍</desultory>
				<series>连拍</series>
				<save>保存</save>
				<pageup>上一页</pageup>
				<pagedown>下一页</pagedown>
				<clearbg>清除相框</clearbg>
				<reload>重载</reload>
				<cancel>取消</cancel>
				<siteerror>参数错误,系统载入失败</siteerror>
				<ver1>程序需FlashPlayer 9.0.45以上版本您的播放器版本为</ver1>
				<ver2>请升级</ver2>
				<refuse>在您的机器上检测到摄象头但您拒绝了摄象头的使用</refuse>
				<countdown>倒数</countdown>
				<second>秒</second>
				<nocam>在您的机器上没有检测到摄象头或者您的摄象头设备正在使用中</nocam>
				<autoShooting>秒自拍</autoShooting>
				<explain>参数设置：</explain>
				<okTitle>上传完成</okTitle>
				<okMsg>大头贴上传完成</okMsg>
				<saveTitle>正在上传</saveTitle>
				<saveToNote>保存到</saveToNote>
				<saveMsg1>总共有</saveMsg1>
				<saveMsg2>张大头贴,正在保存第</saveMsg2>
				<saveMsg3>张大头贴。</saveMsg3>
			</c:when>
			</c:choose>
		</c:when>
		<c:otherwise>
			<reload>重画</reload>
			<save>保存</save>
			<notDraw>没有任何涂鸦动作，无法保存</notDraw>
		</c:otherwise>
		</c:choose>
	</language>
	<config>
		<userid>${sGlobal.supe_uid }</userid>
		<hash>${hash }</hash>
		<maxupload>${max }</maxupload>
		<c:if test="${iscamera}">
			<countdown>3</countdown>
			<countBy>2000</countBy>
		</c:if>
	</config>
	<c:if test="${isdoodle}">
	<filters>
		<filter id="0">禁用</filter>
		<filter id="1">阴影</filter>
		<filter id="2">模糊</filter>
		<filter id="3">发光</filter>
		<filter id="4">水彩</filter>
		<filter id="5">喷溅</filter>
		<filter id="6">布纹</filter>
	</filters>
	</c:if>
	<albums>
		<album id="-1">请选择相册</album>
		<c:forEach items="${albums}" var="aValue">
			<album id="${aValue.albumid }">${aValue.albumname }</album>
		</c:forEach>
		<album id="add">+新建相册</album>
	</albums>
</parameter>
</c:otherwise>
</c:choose>