<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://medecision.com" prefix="med" %>
<%@ page import="com.med.clientletter.jsf.view.ClientLetterView" %>
<%@ page import="com.med.jsf.helper.SessionController" %>
<script src="js/PageHeader.js"></script> 
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%
    Boolean isCLBrowserInitialized = (Boolean)SessionController.getSessionAttribute("ISCLBROWSERINITIALIZED");
    if (isCLBrowserInitialized == null){
        isCLBrowserInitialized = Boolean.FALSE;
    }
%>
<f:subview id="pageHeader">
<f:verbatim>
<script language="javascript">

function openIMModule()
{
	var memberDetailsPage = document.getElementById('mainForm:MemberNavTab:tabType');
	var cmvPage = document.getElementById('mainForm:tabType');
	var tabType;
	if(memberDetailsPage != null)
	{
		tabType = memberDetailsPage.value;
	}
	else if(cmvPage != null)
	{
		tabType = cmvPage.value;
	}
	else
	{
		tabType = 'null';
	}
	var url = '<%=basePath%>' + "interactionmanagement/interactionManagement.iface?fetchImData=true&tabType="+ tabType;
	document.getElementById('nonRecordButton').style.visibility="hidden";
	document.getElementById('recordButton').style.visibility="visible";
	var width = screen.width - 5;
	var height = screen.height;
	startLoc = height - 580;
	var param = 'dialogHeight:'+ (height-188) +'px;dialogWidth:'+ width +'px;dialogTop: '+ startLoc +'px;status: No;resizable:no;help:no;';
	retVal=window.showModalDialog(url,"Interaction Management",param);
	if(retVal=="true"){
		document.getElementById('recordButton').style.visibility="visible";
		document.getElementById('nonRecordButton').style.visibility="hidden";
	}else{
		document.getElementById('recordButton').style.visibility="hidden";
		document.getElementById('nonRecordButton').style.visibility="visible";
	}
}

function openPopup()
{
	var url = '<%=basePath%>' + "contacts/Contacts.iface?contactParam=fetchMemberData";
	var width = document.body.clientWidth - 80;
	var height = document.body.clientHeight ;
	startLoc = screen.height - height - 25 ;
	var param = 'dialogHeight:'+ (height-50) +'px;dialogWidth:'+ width +'px;dialogTop: '+ startLoc +'px;status: No;resizable:no;help:no;';
	try {
		window.showModalDialog(url,"Contact List",param);
	}catch(err){}
}	

function callRAV(memberId) {
	var ravUrl = '<%=basePath + "servlet/RavServlet"%>';
//alert(memberId);
	ravUrl = ravUrl + "?memberId=" + memberId;
//alert("ravUrl="+ravUrl);
	ravWin = window.open(ravUrl, 'RAV', 'toolbar=no,scrollbars=yes,height=600,width=800,status=no,resizable=yes');
	try{
		ravWin.focus();
	}catch(err){}
}

function substr(str,len){
    if(str.length>len)
    {
      str=str.substring(0,len)+"...";
    }  
    document.write(str);
}

function closeCLBrowser() {
    var isCLBrowserInitialized = <%=isCLBrowserInitialized.booleanValue()%>;
    if (isCLBrowserInitialized){
        var clUrl = "<%=ClientLetterView.getClientLetterURL()%>";
        // overlay the CL Browser window using default sessionid as Target
        clWindow=window.open(clUrl+"/clwebservices/html/frSplash.aspx","WSESSION1"); 
        // close the CL Browser Window
        clWindow.close();
    }
}    
</script>

  </f:verbatim>
<f:loadBundle basename="com.med.careplannerweb.jsf.bundle.messages" var="pageHeaderBundle"/>
		<f:verbatim>
		<div class="hdr">
			<div class="hdr_alerts" >
				<div class="hdr_alerts_text">
				</f:verbatim>
				<h:outputText id="alertsTitleText" value="#{HeaderBean.alertsTitle}" />
				<f:verbatim>
				</div>
			</div>
			<div class="hdr_queue_utility">
				</f:verbatim>
				<h:commandLink id="linkToHomePageTasks" styleClass="careplanner_home_link careplanner_home_icon" action="#{HomepageNavigationBean.openHomepageTasks}" immediate="true"/>
				<h:panelGroup id="renderMainTitleGroup" rendered="#{HeaderBean.renderMainTitle}">
				<f:verbatim>
				<dl class="patient_info">
					<dt></dt> 
					<dd id="mainTitleText" class="mainTitleText"></f:verbatim><h:outputText value="#{HeaderBean.mainTitle}" /><f:verbatim></dd>
					<dd>
						</f:verbatim>
						<h:commandLink id="hideAlertCommandLink" styleClass="#{HeaderBean.alertStyleClass}"   
						   onmouseout="hideAlert()" onmouseover="openAlert()" />
						<f:verbatim>
					</dd>
				</dl> 
				</f:verbatim>
				</h:panelGroup>
				<f:verbatim>
				<div class="utility">
				</f:verbatim>
					<h:outputText id="userName" value="Welcome #{loginBean.currUser.fullName}" styleClass="welcome"/><f:verbatim>
					<ul>
						<li>
							</f:verbatim>
							<h:commandLink value="Help" id="help_link" onclick="window.open('#{facesContext.externalContext.request.contextPath}/WebHelp/index.htm','');" title="Help" immediate="true" styleClass="utility_help_btn">
							</h:commandLink>
							<f:verbatim>  
						</li>
						<li></f:verbatim><h:commandLink id="logOffCommandLink" styleClass="utility_logout_btn" action="#{loginBean.logoff}" title="Log Out" value="Log out" immediate="true" onclick="closeCLBrowser();"/><f:verbatim></li>
					</ul>
				</div>
			</div>
			<div class="hdr_status_data" id="party_status">
				<ul class="status">
					<li>
						<table id="headerInfo_1" cellpadding="0" cellspacing="0">
							<tr><th nowrap="nowrap" id="cmp1DisplayLabelText"></f:verbatim><h:outputText value="#{HeaderBean.headerDisplayComponents.component1.displayLabel}"/><f:verbatim></th>
								<td nowrap="nowrap"><span style='font-weight:700' id="cmp1DisplayValueText" title="</f:verbatim><h:outputText value='#{HeaderBean.headerDisplayComponents.component1.displayValue}'/><f:verbatim>"><script></f:verbatim><h:outputText value="substr(\"#{HeaderBean.headerDisplayComponents.component1.displayValue}\",12);" escape="false"/><f:verbatim></script></span></td>
							</tr>
							<tr><th nowrap="nowrap" id="cmp2DisplayLabelText"></f:verbatim><h:outputText value="#{HeaderBean.headerDisplayComponents.component2.displayLabel}"/><f:verbatim></th>
								<td nowrap="nowrap" id="cmp2DisplayValueText"></f:verbatim><h:outputText value="#{HeaderBean.headerDisplayComponents.component2.displayValue}"/><f:verbatim></td>
							</tr>
						</table>
					</li>
					<li>
						<table id="headerInfo_2" cellpadding="0" cellspacing="0">
							<tr><th nowrap="nowrap" id="cmp3DisplayLabel"></f:verbatim><h:outputText value="#{HeaderBean.headerDisplayComponents.component3.displayLabel}"/><f:verbatim></th>
								<td nowrap="nowrap" id="cmp3DisplayValue"></f:verbatim><h:outputText value="#{HeaderBean.headerDisplayComponents.component3.displayValue}"/><f:verbatim></td>
							</tr>
							<tr><th nowrap="nowrap" id="cmp4DisplayLabel"></f:verbatim><h:outputText value="#{HeaderBean.headerDisplayComponents.component4.displayLabel}"/><f:verbatim></th>
								<td nowrap="nowrap" id="cmp4DisplayValue"></f:verbatim><h:outputText value="#{HeaderBean.headerDisplayComponents.component4.displayValue}"/><f:verbatim></td>
							</tr>
						</table>
					</li>
					<li>
						<table id="headerInfo_3" cellpadding="0" cellspacing="0">
							<tr><th nowrap id="cmp5DisplayLabel"></f:verbatim><h:outputText value="#{HeaderBean.headerDisplayComponents.component5.displayLabel}"/><f:verbatim></th>
								<td nowrap="nowrap"><span id="cmp5DisplayValue" style='font-weight:700' title="</f:verbatim><h:outputText value='#{HeaderBean.headerDisplayComponents.component5.displayValue}'/><f:verbatim>"><script></f:verbatim><h:outputText  value="substr(\"#{HeaderBean.headerDisplayComponents.component5.displayValue}\",10);" escape="false"/><f:verbatim></script></span></td>
								</td>
							</tr>
							<tr><th nowrap="nowrap" id="cmp6DisplayLabel"></f:verbatim><h:outputText value="#{HeaderBean.headerDisplayComponents.component6.displayLabel}"/><f:verbatim></th>
								<td nowrap="nowrap"><span id="cmp6DisplayValue" style='font-weight:700' title="</f:verbatim><h:outputText value='#{HeaderBean.headerDisplayComponents.component6.displayValue}'/><f:verbatim>"><script></f:verbatim><h:outputText  value="substr(\"#{HeaderBean.headerDisplayComponents.component6.displayValue}\",10);" escape="false"/><f:verbatim></script></span></td>
							</tr>
						</table>
					</li>
					<li>
						<table id="headerInfo_4" cellpadding="0" cellspacing="0">
							<tr><th nowrap="nowrap" id="cmp7DisplayLabel"></f:verbatim><h:outputText value="#{HeaderBean.headerDisplayComponents.component7.displayLabel}"/><f:verbatim></th>
								<td nowrap="nowrap" id="cmp7DisplayValue"></f:verbatim><h:outputText value="#{HeaderBean.headerDisplayComponents.component7.displayValue}"/><f:verbatim></td>
							</tr>
							<tr><th nowrap="nowrap" id="cmp8DisplayLabel"></f:verbatim><h:outputText value="#{HeaderBean.headerDisplayComponents.component8.displayLabel}"/><f:verbatim></th>
								<td nowrap="nowrap" id="cmp8DisplayValue"></f:verbatim><h:outputText value="#{HeaderBean.headerDisplayComponents.component8.displayValue}"/><f:verbatim></td>
							</tr>
						</table>
					</li>
				</ul>
				<ul class="status_links clearfix">
					<li style="width: 80px;"></f:verbatim><h:commandLink id="headBeanCommandLink" action="#{HeaderBean.doAction}" styleClass="status_details_link" title="#{HeaderBean.actionTitle}" rendered="#{HeaderBean.actionAvailable}">
						<f:param name="#{HeaderBean.actionParameterName1}" value="#{HeaderBean.actionParameterValue1}" />
						<f:param name="#{HeaderBean.actionParameterName2}" value="#{HeaderBean.actionParameterValue2}" />
						</h:commandLink>&nbsp;<f:verbatim></li>
					<li id="onClickActionLink" style="width: 80px;"></f:verbatim><h:outputLink styleClass="#{HeaderBean.action2StyleClass}" onclick="#{HeaderBean.action2OnClickEvent}" value="#"></h:outputLink>
					&nbsp;
						<f:verbatim></li><li>
						</f:verbatim>
							<h:commandLink rendered="#{ContactInfoBean.memberDisplayContactsPopup}" id="contactButton" value="" styleClass="contactButtonStyle" onclick="openPopup();" immediate="true"/>
							<h:graphicImage rendered="#{ContactInfoBean.memberDisableContactsPopup}" styleClass="contactImageStyle" url="/images/contacts_80x30Pixel_disabled.png"/>
						<f:verbatim>
						</li>
				</ul>
				<ul>
					<li>
					
					<div class="IMDivStyle" style="bottom:13px;">
								<div id="nonRecordButton">
									<h:commandLink  styleClass="IMButtonStyle"  value="" onclick="openIMModule();" rendered="#{InteractionUtilityBean.imButtonPresent}" />
								</div>
								<div id="recordButton" style="visibility: hidden;">
									<h:commandLink id="recordingState"  styleClass="IMRecordButtonStyle" onclick="openIMModule();" value="" rendered="#{InteractionUtilityBean.imButtonPresent}"  />
								</div>
								<div>
									<h:commandLink id="continueRecordingState"  styleClass="IMRecordButtonStyle" onclick="openIMModule();" rendered="#{InteractionUtilityBean.recordStatus and InteractionUtilityBean.imButtonPresent }" />
								</div>	
					</div>
					<!--<div class="contactDivStyle">
						</f:verbatim>
						
							<h:commandLink rendered="#{ContactInfoBean.memberDisplayContactsPopup}" id="contadfctButton" value="" styleClass="contactButtonStyle" onclick="openPopup();" immediate="true"/>
							<h:graphicImage rendered="#{ContactInfoBean.memberDisableContactsPopup}" styleClass="contactImageStyle" url="/images/contacts_80x30Pixel_disabled.png"/>
							<f:verbatim>
						
					</div>
					--><span class="hdr_home_link clearfix">Medecision</span>
					</li>				
				</ul>
				
			</div>
		</div>
</f:verbatim>
<med:breadCrumb breadCrumbList="#{BreadCrumbBean.breadCrumbHistory}" styleClass="bcr" id="bcr"></med:breadCrumb>
</f:subview>