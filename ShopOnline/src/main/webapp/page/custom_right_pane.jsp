<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://medecision.com" prefix="med" %>

<f:loadBundle basename="com.med.careplannerweb.jsf.bundle.resource" var="bundle" />
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">  
		<title>Alineo&#8482; by MEDecision</title>
		<link href="utils/screen.css" media="screen" rel="Stylesheet" type="text/css" />
		<link href="utils/header.css" media="screen" rel="Stylesheet" type="text/css" />
		<link href="utils/bcr.css" media="screen" rel="Stylesheet" type="text/css" />
		<link href="utils/left_pane.css" media="screen" rel="Stylesheet" type="text/css" />
		<link href="utils/right_pane.css" media="screen" rel="Stylesheet" type="text/css" />		
		<script language="javascript" src="js/lib/jquery-compressed.js"></script>
		<script language="javascript" src="js/bcr.js"></script>
		<script language="javascript" src="js/left_pane.js"></script>
		<script language="javascript" src="js/right_pane.js"></script>

<%@ include file="genericHeaderInclude.jsp" %>
</head>
	<body>
	<f:view>
		<h:form id="mainForm">
		<med:collapsePanel id="activitiesPanel" 
			panelSectionDescription="Tasks" 
			panelSectionCount="#{ComprehensiveMemberViewBean.activityListSize}"
			panelFilterButtonText="Show My Tasks Only"
			panelIconStyleClass="icon_large activities"
			styleClass="right_panel activities_panel open"
			togglePaneID=".activities_panel">
			<h:dataTable id="activilityListTable" var="nextData" value="#{ComprehensiveMemberViewBean.activityList}" rowClasses=",alt" styleClass="right_panel_data" columnClasses=",date" cellpadding="0" cellspacing="0">
				<h:column>
					<h:outputText value="#{nextData.description}"></h:outputText>
				</h:column>
				<h:column>
					<h:outputText value="#{nextData.dateDue}">
						<f:convertDateTime pattern="MM/dd/yyyy" />
					</h:outputText>
				</h:column>
			</h:dataTable>
		</med:collapsePanel>	
		<med:collapsePanel id="notesPanel" 
			panelSectionDescription="Notes" 
			panelSectionCount="2"
			panelIconStyleClass="icon_large notes"
			styleClass="right_panel notes_panel closed"
			togglePaneID=".notes_panel">
		</med:collapsePanel>	
		<med:collapsePanel id="lettersPanel" 
			panelSectionDescription="Letters" 
			panelSectionCount="2"
			panelIconStyleClass="icon_large letters"
			styleClass="right_panel letters_panel closed"
			togglePaneID=".letters_panel">
		</med:collapsePanel>	
		</h:form>	
	</f:view>
	</body>
</html>  
