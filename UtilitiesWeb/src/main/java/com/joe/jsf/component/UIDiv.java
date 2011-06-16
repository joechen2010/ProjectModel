package com.joe.jsf.component;

import javax.faces.component.UIPanel;

public class UIDiv extends UIPanel {
    public static final String COMPONENT_TYPE = "com.med.careplannerweb.jsf.component.DivPanel";
    public static final String RENDERER_TYPE = "com.med.careplannerweb.jsf.component.Div";

    public UIDiv() {
        setRendererType(RENDERER_TYPE);
    }

}