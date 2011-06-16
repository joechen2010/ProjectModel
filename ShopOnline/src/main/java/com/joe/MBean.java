package com.joe;

import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.model.DataModel;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.joe.client.domain.IUserInfo;
import com.joe.client.facade.IUserInfoFacade;
import com.joe.core.domain.UserInfo;
import com.joe.utilities.core.data.DataSource;
import com.joe.utilities.core.data.LocalDataModel;
import com.joe.utilities.core.exception.DBException;
import com.joe.utilities.core.facade.IFacade;
import com.joe.utilities.core.manager.facade.ICommonFacade;
import com.joe.utilities.core.serviceLocator.ServiceLocator;



@ManagedBean(name="mBean")
@RequestScoped
public class MBean extends DataSource{
	
	@ManagedProperty(value="#{userInfoFacade}")
	private IUserInfoFacade userInfoFacade ;
	
	@Size(min = 1, message = "Please enter the Email")
	@Pattern(regexp = "[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+", message = "Email format is invalid.")
	private String email;
	
	
	public MBean() {   
		super("id");
    }   

	public String addUser(){
		IUserInfo user = new UserInfo();
		user.setCreateDate(new Date());
		user.setName("niu");
		
		ICommonFacade commonFacade = (ICommonFacade)ServiceLocator.getInstance().getBean("commonFacade");
		try {
			commonFacade.save(user);
		} catch (DBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public DataModel getMyPagedDataModel() {
		if (onePageDataModel == null) {
            onePageDataModel = new LocalDataModel((IFacade)getUserInfoFacade(), pageSize, sortColumnName, true);
        }
        return onePageDataModel;
	}      
	

	protected boolean isDefaultAscending(String sortColumn) {
	        return true;
	 }

	@SuppressWarnings("deprecation")
	public IUserInfoFacade getUserInfoFacade() {
		if(userInfoFacade == null){
			userInfoFacade = (IUserInfoFacade)ServiceLocator.getInstance().getBean("userInfoFacade");
		}
		return userInfoFacade;
	}

	public void setUserInfoFacade(IUserInfoFacade userInfoFacade) {
		this.userInfoFacade = userInfoFacade;
	}
	
}
