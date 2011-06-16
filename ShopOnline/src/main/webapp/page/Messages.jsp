<%@ page language="java" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>


<f:subview id="messages">
<script language="javascript" src="js/lib/jquery-compressed.js"></script>
<script language="javascript" src="js/lib/jquery.bgiframe.js"></script>
<link href="utils/messages.css" media="screen" rel="Stylesheet" type="text/css" />
<%@ page import="java.util.Iterator"%>
<%@ page import="javax.faces.context.FacesContext"%>
<%@ page import="javax.faces.application.FacesMessage"%>
<%@ page import="com.med.utilities.core.Utils"%>
<script>
var timeinterval = 50;
var errorCount = 0;
var warnCount = 0;
var infoCount = 0;
var adCount = 0;
var fdCount = 10;
var adDiv;
var documentHeight = $("html").height();	
function initPopup(){
	adDiv=$("div[@id=msgWindlocation]", $("body")).eq(0);	
	$("div[@id=msgWindowClose]", adDiv).bind(
			'click',
			hidePopup
	);
	showPopup();
	 $(function(){
	    	$('#msgWindlocation').bgiframe();
	 }); 
}

function showPopup(){
	adDiv.css("left",0 + 'px');
	if(adCount==0){
	    adDiv.css("top",documentHeight -5  + 'px');
	    adDiv.show();
	}
	if(adCount<15){
		adCount++;
		adDiv.css("top",documentHeight -adCount*20+5 + 'px');
		iTime = setTimeout("showPopup()",timeinterval);
	}
}

function hidePopup(){
	if(adCount>=3){
		adCount--;
		adDiv.css("top",documentHeight-adCount*20 + 'px');		
		iTime = setTimeout("hidePopup()",timeinterval);
	} else{
		adDiv.css("top",documentHeight-28 + 'px');
		adDiv.css("height", 28 + 'px');
	  $("div[@id=msgWindowTop]", adDiv).css("cursor","pointer");
		flodPopup();
	}
}

function flodPopup(){
	if(fdCount>0){
		fdCount--;
		adDiv.css("width", 110 + fdCount*25 + 'px');
		$("div[@id=msgWindowClose]", adDiv).css("left", 90 + fdCount*25 + 'px');
		iTime = setTimeout("flodPopup()",10);
	} else {
	  $("div[@id=msgWindowTop]", adDiv).bind(
			'click',
			initUnFlodPopup
	  );
	  $("div[@id=msgWindowClose]", adDiv).addClass("msgOpen");
	  //adDiv.fadeTo(1000,0.5,function(){});
	}
}

function unFlodPopup(){
	if(fdCount<10){
		fdCount++;
		adDiv.css("width", 110 + fdCount*25 + 'px');	
		$("div[@id=msgWindowClose]", adDiv).css("left", 90 + fdCount*25 + 'px');	
		iTime = setTimeout("unFlodPopup()",10);
	} else {
		$("div[@id=msgWindowTop]", adDiv).css("cursor","");
		adCount=1;
		$("div[@id=msgWindowClose]", adDiv).removeClass("msgOpen");
		initPopup();
	}
}

function initUnFlodPopup(){  
	//adDiv.fadeTo(300,1,function(){});
	$("div[@id=msgWindowTop]", adDiv).unbind(
		'click',
		initUnFlodPopup
  );
  adDiv.css("height", 300 + 'px');
  iTime = setTimeout("unFlodPopup()",100);
}

function addError(text) {
	errorCount++;
	errorBody = $("dl[@id=msgErrors]", adDiv).eq(0);
	errorBody.show();
	errorBody.append('<dd><b>'+errorCount+'.</b> '+text+'</dd>\n');
}

function addWarning(text) {
	warnCount++;
	warnBody = $("dl[@id=msgWarnings]", adDiv).eq(0);
	warnBody.show();
	warnBody.append('<dd><b>'+warnCount+'.</b> '+text+'</dd>\n');
}

function addInfo(text) {
	infoCount++;
	infoBody = $("dl[@id=msgInformations]", adDiv).eq(0);
	infoBody.show();
	infoBody.append('<dd><b>'+infoCount+'.</b> '+text+'</dd>\n');
}
</script>
<div  style="display: none;" class="msgWindow" id="msgWindlocation">

	<div id="msgWindowTop">
		<div id="msgWindowTopContent">Messages</div>
        <div id="msgWindowClose" class="msgClose">close</div>
	</div>
	<div id="msgWindowBottom"><div id="msgWindowBottomContent">&nbsp;</div></div>
	<div id="msgWindowContent">
		<dl id="msgErrors" style="display: none">
			<dt class="errors" >Errors</dt>
		</dl>
		<dl id="msgWarnings" style="display: none">
			<dt class="warnings" >Warnings</dt>
		</dl>
		<dl id="msgInformations" style="display: none">
			<dt class="ttext" >Information</dt>
		</dl>
	</div>

</div>
<script>
$(document).ready(
	function(){
    <%
	    FacesContext context = FacesContext.getCurrentInstance();
		Iterator iterator = context.getMessages();
		boolean isPopup = true;
		while (iterator.hasNext()){ 
			FacesMessage message = (FacesMessage)iterator.next();
			if(message.getSeverity().compareTo(FacesMessage.SEVERITY_ERROR) 
					!= 0 &&message.getSeverity().compareTo(FacesMessage.SEVERITY_WARN) 
					!= 0 && message.getSeverity().compareTo(FacesMessage.SEVERITY_INFO) != 0) continue;
			if(isPopup){
		    %>
		        initPopup();
			<%
			    isPopup = false;
			}		
			if(message.getSeverity().compareTo(FacesMessage.SEVERITY_ERROR) == 0)
			{
			%>
			    addError('<%= Utils.replace(message.getDetail(),"'","\"") %>');
	        <%
			} else if(message.getSeverity().compareTo(FacesMessage.SEVERITY_WARN) == 0)
			{
			%>
			    addWarning('<%= Utils.replace(message.getDetail(),"'","\"") %>');
	        <%
			} else 
			{
			%>
			    addInfo('<%= Utils.replace(message.getDetail(),"'","\"") %>');
	        <%
			}
		}
	    %>
    }
);
</script>

</f:subview>