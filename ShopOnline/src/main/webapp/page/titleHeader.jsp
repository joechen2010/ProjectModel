<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<f:subview id="titleHeader">
<f:loadBundle basename="com.med.careplannerweb.jsf.bundle.messages" var="titleBundle"/>
<f:verbatim>
<table border="0" cellpadding="0" cellspacing="0" width="780">
<!--- White space at top -->
<tr><td colspan="5" width="780" height="6"></td></tr>
<tr><td width="6" height="1"></td><td width="768" bgcolor="#696D86" colspan="3"><spacer type="block" width="768" height="1"></td><td width="6"><spacer type="block" width="6" height="1"></td></tr>
<tr>
	<td width="6"></td>
	<td width="1" bgcolor="#696D86"></td>
	<td width="766" background="images/bg_navtable.gif">
	<table cellpadding="0" cellspacing="0" border="0">
		<tr><td colspan="11" width="766" height="4"></td></tr>
		<tr>
			<td width="151" valign="bottom">
</f:verbatim>
			<h:graphicImage id="oldLogoImage" value="#{titleBundle.oldlogo}" alt="Alineo | MEDecision logo" width="#{titleBundle.oldlogowidth}" height="#{titleBundle.oldloldlogoheight}"/>
<f:verbatim>
			</td>
			<td width="600" height="48" align="right" valign="bottom">
					</f:verbatim>
					<h:commandLink id="helpTextCommandLink" styleClass="admin" value="#{titleBundle.helptext}"/><h:outputText id="value1" styleClass="admin" value=" | "/> 
        			<h:commandLink id="howTextCommandLink" styleClass="admin" value="#{titleBundle.howtext}"/><h:outputText id="value2" styleClass="admin" value=" | "/> 
        			<h:commandLink id="logOffCommandLink" value="#{titleBundle.logouttext}" action="#{loginBean.logoff}" immediate="true" styleClass="admin"></h:commandLink>
        			<f:verbatim>
       		</td>
		</tr>
		<tr>
			<td colspan=2 width="21" height="5" align="center" valign="top"><spacer type="block" width="21" height="5"></td>
		</tr>
		<!-- spacer row -->
	</table>
	</td>
	<td width="1" bgcolor="#696D86"><spacer type="block" width="1" height="1"></td>
	<td width="6"><spacer type="block" width="6" height="1"></td>
</tr>
<tr><td width="6" height="1"><spacer type="block" width="6" height="1"></td><td width="768" bgcolor="#696D86" colspan="3"><spacer type="block" width="768" height="1"></td><td width="6"><spacer type="block" width="6" height="1"></td></tr>
</table>
</f:verbatim>
</f:subview>