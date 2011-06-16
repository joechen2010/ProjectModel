/*
 * Created on Feb 1, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.joe.jsf.viewHandlers.classViewHandler;

import javax.faces.application.Application;
import javax.faces.component.UICommand;
import javax.faces.component.UIForm;
import javax.faces.component.UIInput;
import javax.faces.component.UIOutput;
import javax.faces.component.UIPanel;
import javax.faces.component.UISelectItems;
import javax.faces.component.UISelectMany;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joe.jsf.viewHandlers.classViewHandler.View;

/**
 */
public class SubscribeView implements View
{
    public UIViewRoot createView (FacesContext context)
    {
        Application application = context.getApplication();
        UIViewRoot viewRoot = new UIViewRoot();

        UIForm form = new UIForm();
        viewRoot.getChildren().add(form);

        UIPanel grid = new UIPanel();
        grid.setRendererType("javax.faces.Grid");
        grid.getAttributes().put("columns", "2");

        UIOutput emailLabel = new UIOutput();
        emailLabel.setValue("Email Address:");
        grid.getChildren().add(emailLabel);
        UIInput email = new UIInput();
        ValueBinding emailAddr = application
                .createValueBinding("#{subscr.emailAddr}");
        email.setValueBinding("value", emailAddr);
        grid.getChildren().add(email);

        UIOutput subsLabel = new UIOutput();
        subsLabel.setValue("Newsletters:");
        grid.getChildren().add(subsLabel);
        UISelectMany subs = new UISelectMany();
        subs.setRendererType("javax.faces.Checkbox");
        ValueBinding subIds = application
                .createValueBinding("#{subscr.subscriptionIds}");
        subs.setValueBinding("value", subIds);
        UISelectItems sis = new UISelectItems();
        List choices = new ArrayList();
        choices.add(new SelectItem("1", "JSF News"));
        choices.add(new SelectItem("2", "IT Industry News"));
        choices.add(new SelectItem("3", "Company News"));
        sis.setValue(choices);
        subs.getChildren().add(sis);
        grid.getChildren().add(subs);
        form.getChildren().add(grid);

        UICommand command = new UICommand();
        command.setValue("Save");
        MethodBinding action = application.createMethodBinding(
            "#{subscrHandler.saveSubscriber}", null);
        command.setAction(action);
        form.getChildren().add(command);
        viewRoot.getChildren().add(form);

        return viewRoot;
    }
}
