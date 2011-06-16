package com.joe.core.facade;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.joe.client.domain.IUserInfo;
import com.joe.client.facade.IUserInfoFacade;
import com.joe.core.repository.IUserInfoRepository;
import com.joe.utilities.core.util.EvaluationException;

/**
 * @see com.med.activities.facade.ActivityFacade
 * @author fmacartn
 *
 * Creation date: May 31, 2007 10:43:51 AM
 * Copyright (c) 2007 MEDecision, Inc.  All rights reserved.
 *
 */
public class UserInfoFacadeImpl implements IUserInfoFacade {

	private Log log = LogFactory.getLog(UserInfoFacadeImpl.class);
	private IUserInfoRepository userInfoRepository;

	/**
	 * Constructor for the Activity Facade passing in the activity service and repository objects.
	 * @param activityService the activity service object (must be non-null).
	 * @param activityRepository the activity repository object (must be non-null).
	 */
	public UserInfoFacadeImpl(IUserInfoRepository userInfoRepository)
	{
		super();
		if (userInfoRepository == null)
			throw new IllegalArgumentException("repository is null");
		this.userInfoRepository = userInfoRepository;
	}
	
	
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = { EvaluationException.class })
	public List<IUserInfo> findPage(String sortColumnName, boolean sortAscending, int startRow, int maxResults)
	{
        		
		return (List<IUserInfo>)userInfoRepository.findPage(sortColumnName, sortAscending, startRow, maxResults);
	}
	
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = { EvaluationException.class })
	 public int getDataCount() {
		 return userInfoRepository.getDataCount();
	 }
	
	
}