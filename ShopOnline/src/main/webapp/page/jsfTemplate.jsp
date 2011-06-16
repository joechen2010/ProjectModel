<%@ page language="java" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<base href="<%=basePath%>"/>

	<title>Alineo&#8482; by MEDecision</title>
	
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<link style="" href="utils/style.css" type="text/css" rel="stylesheet">
<%@ include file="genericHeaderInclude.jsp" %>
</head>
<f:view>
<f:loadBundle basename="com.med.relationshipmanagement.jsf.bundle.messages" var="bundle"/>
<body text="#000000" bgcolor="#FFFFFF" link="#666699" vlink="#666699" alink="#9999CC" topmargin="0" leftmargin="0" marginheight="0" marginwidth="0"><br>
<h:form id="mainForm">
<jsp:include flush="true" page="titleHeader.jsp"></jsp:include>
<jsp:include flush="true" page="PageHeader.jsp"></jsp:include>
<jsp:include flush="true" page="Messages.jsp"></jsp:include>
         <table cellspacing="0" cellpadding="0" border="0" width="780" height="20">
            <tr>
               <td width="19" height="1">
               <br></td>
               <td>
                  <table cellspacing="0" cellpadding="6" border="0" width="742">
                     <tr>
                        <td align="right"><font class="ftext">&nbsp;</font><br></td>
                     </tr>
                  </table>
                  <table cellspacing="0" cellpadding="0" border="0" width="755" height="20" bgcolor="#9FA5C8">
                     <tr>
                        <td colspan="7"><img src="images/px_blue.gif" height="1"><br></td>
                     </tr>
                     <tr>
                        <td bgcolor="#9FA5C8" width="29" height="20" align="right"><img src="images/d_nbtabl.gif" width="19" height="20"><br></td>
                        <td bgcolor="#FFFFFF" align="center" width="254"><h:outputText id="tab1Text" styleClass="navheadselect" value="Tab1"></h:outputText><br></td>
                        <td bgcolor="#9FA5C8" align="center" width="250"><h:commandLink id="tab2CommandLink"styleClass="nblink" value="Tab 2"></h:commandLink><br></td>
                        <td bgcolor="#FFFFFF" width="1" height="20">
                        <br></td>
                        <td bgcolor="#9FA5C8" align="center" width="250"><h:commandLink id="tab3CommandLink"styleClass="nblink" value="Tab 3"></h:commandLink><br></td>
                     </tr>
                  </table>
               <br></td>
            </tr>
         </table><br><table cellspacing="0" cellpadding="4" border="0" width="780">
            <tr>
               <td width="19">
               <br></td>
               <td colspan="8"><h:outputText id="headText"styleClass="goldsubhead" value="Header Text"></h:outputText><br></td>
            </tr>
         </table>
         <table cellspacing="0" cellpadding="4" border="0" width="780" style="height: 30px;" height="30">
            <tr>
               <td width="9">
               <br></td>
               <td><h:selectBooleanCheckbox id="checkboxpractitioner_assignSystemId" style="mono" ></h:selectBooleanCheckbox>
				<h:outputLabel id="labelpractitioner_assignSystemId" styleClass="flabel" for="checkboxpractitioner_assignSystemId" value="Assign system ID" />
				<br><br></td><TD style="" vAlign="top" align="right" colSpan="1" nowrap><FONT id="reqpractitioner_practitionerId" name="reqpractitioner_practitionerId" class="required" style="required">*</FONT><h:outputLabel id="labelpractitioner_practitionerId" styleClass="flabel" value="ID:" for="practitioner_practitionerID"/><br></TD>
               <TD style="" vAlign="top" colSpan="1"><h:inputText value="#{PractitionerBean.practitionerID}" id="practitioner_practitionerID"/>
				<br></TD><TD style="" vAlign="top" align="right" colSpan="1" nowrap><br></TD>
               <TD style="" vAlign="top" colSpan="1"><br><br></TD>
            </tr>
         </table>
         <table cellspacing="0" cellpadding="4" border="0" width="780">
            <tr>
               <td width="19">
               <br></td>
               <td colspan="8"><img src="images/px_lblue.gif" width="100%" height="1"><br></td>
            </tr>
         </table>
         <table cellspacing="0" cellpadding="4" border="0" width="780">
            <tr>
               <td width="19">
               <br></td>
               <td colspan="7"><h:outputText id="practitionerText" styleClass="goldsubhead" value="Practitioner"/><br></td>
            </tr>
         </table>
         <table cellspacing="0" cellpadding="4" border="0" width="780">
            <tr>
               <td width="57" nowrap="true">
               <br></td><TD style="" vAlign="top" align="right" colSpan="1" nowrap><FONT id="reqpractitioner_prefix" name="reqpractitioner_prefix" class="required" style="required">*</FONT><FONT id="labelpractitioner_prefix" name="labelpractitioner_prefix" class="flabel" style="flabel">Prefix:</FONT><br></TD>
               <TD style="" vAlign="top" colSpan="1">
               <h:selectOneMenu id="practitioner_prefix" binding="#{PractitionerBean.prefixList}" value="#{PractitionerBean.selectedPrefix}"/>
			<br></TD><TD style="" vAlign="top" align="right" colSpan="1" nowrap><FONT id="reqpractitioner_firstName" name="reqpractitioner_firstName" class="required" style="required">*</FONT><FONT id="labelpractitioner_firstName" name="labelpractitioner_firstName" class="flabel" style="flabel">First name:</FONT><br></TD>
               <TD style="" vAlign="top" colSpan="1"><h:inputText id="practitioner_firstName" value="#{PractitionerBean.firstName}" required="" style="mono" size="20" maxlength="25" valueChangeListener="#{PractitionerBean.testChangeListener}" onblur="submit();"/>
               <br></TD><TD style="" vAlign="top" align="right" colSpan="1" nowrap><FONT id="reqpractitioner_middleName" name="reqpractitioner_middleName" class="required" style="required">*</FONT><FONT id="labelpractitioner_middleName" name="labelpractitioner_middleName" class="flabel" style="flabel">Middle name:</FONT><br></TD>
        <TD style="" vAlign="top" colSpan="1"><h:inputText id="practitioner_middleName" value="#{PractitionerBean.middleName}" style="mono" size="20" maxlength="35" />
		<br></TD>
            </tr> 
         </table>
         <table cellspacing="0" cellpadding="4" border="0">
            <tr>
               <td width="30" nowrap="true">
               <br></td><TD style="" vAlign="top" align="right" colSpan="1" nowrap><FONT id="reqpractitioner_lastName" name="reqpractitioner_lastName" class="required" style="required">*</FONT><h:outputLabel id="labelpractitioner_lastName" styleClass="flabel" value="Last name:" for="practitioner_lastname"/><br></TD>
               <TD style="" vAlign="top" colSpan="1">
               <h:inputText id="practitioner_lastName" value="#{PractitionerBean.lastName}" required="true" style="mono" size="40" maxlength="35" />
				<br></TD>
            </tr>
         </table>
         <table cellspacing="0" cellpadding="4" border="0" width="513" height="33" style="width: 554px;">
            <tr>
               <td width="63" nowrap="true">
               <br></td><TD style="" vAlign="top" align="right" colSpan="1" nowrap><FONT id="reqpractitioner_suffix" name="reqpractitioner_suffix" class="required" style="required">*</FONT><h:outputLabel id="labelpractitioner_suffix" styleClass="flabel" value="Suffix:" for="practitioner_suffix"/><br></TD>
               <TD style="" vAlign="top" colSpan="1"><h:selectOneMenu id="practitioner_suffix" binding="#{PractitionerBean.suffixList}" value="#{PractitionerBean.selectedSuffix}" />
               <br></TD>
               <td width="50" nowrap="true">
               <br></td><TD style="" vAlign="top" align="right" colSpan="1" nowrap><FONT id="reqpractitioner_gender" name="reqpractitioner_gender" class="required" style="required">*</FONT><h:outputLabel id="labelpractitioner_gender" styleClass="flabel" value="Gender:" for="practitioner_gender"/><br></TD>
               <TD style="" vAlign="top" colSpan="1"><h:selectOneMenu id="practitioner_gender" binding="#{PractitionerBean.genderList}" value="#{PractitionerBean.selectedGender}" required="" />
               <br></TD>
               <td width="50" nowrap="true">
               <br></td><TD style="" vAlign="top" align="right" colSpan="1" nowrap><FONT id="reqpractitioner_gender" name="reqpractitioner_birthdate" class="required" style="required">*</FONT><h:outputLabel id="labelpractitioner_gender" styleClass="flabel" value="Birthdate:" for="practitioner_birthdate"/><br></TD>
               <TD style="" vAlign="top" colSpan="1">
               <h:inputText id="practitioner_birthdate" value="#{PractitionerBean.birthDate}" required="" >
               		<f:convertDateTime pattern="MM/dd/yyyy" />
               	</h:inputText>
               <br></TD>
            </tr>
         </table>
         <table cellspacing="0" cellpadding="4" border="0" width="780">
            <tr>
               <td width="19">
               <br></td>
               <td colspan="7"><img src="images/px_lblue.gif" width="100%" height="1"><br></td>
            </tr>
         </table>
         <table cellspacing="0" cellpadding="4" border="0" width="780">
            <tr>
               <td width="19">
               </td>
               <td colspan="4"><img src="images/px_lblue.gif" width="100%" height="1"></td>
            </tr>
            <tr>
               <td width="19">
               </td>
               <td colspan="3"><h:commandButton  type="submit" id="sizeCommandButton" value="Save" action="#{PractitionerBean.createPractitioner}" />&nbsp;
                  				<h:commandButton type="submit" id="cancelButton" value="Cancel" />&nbsp;&nbsp;
               </td>
            </tr>
         </table>
      </h:form>
</body>
</f:view>
<script language="JavaScript" type="text/javascript" src="js/setFocus.js"></script>
</html>
