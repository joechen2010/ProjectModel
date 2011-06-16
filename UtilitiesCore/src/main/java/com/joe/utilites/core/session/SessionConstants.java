package com.joe.utilites.core.session;


/**
 * This class contains static constants for any object/bean that is referenced 
 * 	as a key in the session.
 * 
 * @author       John J. Jones III
 * @version      1.0
 * 
 * Creation date: Aug 3, 2007
 * Copyright (c) 2007 MEDecision, Inc.  All rights reserved.
 */

public class SessionConstants {
	
	/*
	 * Managed Bean Constants
	 */
	public final static String MANAGED_BEAN_MEMBER = "MemberBean";
	public final static String CONTACT_INFO_BEAN = "ContactInfoBean";
	public final static String IM_UTILITY_BEAN = "InteractionUtilityBean";
	public final static String MANAGED_BEAN_BREAD_CRUMB = "BreadCrumbBean";
	public final static String MANAGED_BEAN_HEADER = "HeaderBean";
	public final static String MANAGED_BEAN_CMV = "ComprehensiveMemberViewBean";
	public final static String MANAGED_BEAN_PGM_ENROLL = "ProgramEnrollmentBean";	
	public final static String MANAGED_BEAN_PROVIDER = "ProviderBean";
	public final static String MANAGED_BEAN_PROVIDER_SEARCH = "ProviderSearchBean";
    public final static String MANAGED_BEAN_MEMBER_SEARCH = "MemberSearchBean";
    public final static String ATTRIBUTES_IN_SESSION = "ATTRIBUTES_IN_SESSION";
    public final static String MANAGED_BEAN_INPATIENT_REQUEST = "InpatientStayRequestBean";
    public final static String MANAGED_BEAN_INPATIENT_EXTENSION = "InpatientStayExtensionBean";
    public final static String MANAGED_BEAN_OUTPATIENT_REQUEST = "OutpatientRequestBean";
    public final static String MANAGED_BEAN_REFERRAL_REQUEST = "ReferralServiceRequestBean";
    public final static String MANAGED_BEAN_RESTORE_VIEW = "RestoreViewBean";
    public final static String MANAGED_BEAN_MESSAGES_SUPPORT = "MessagesSupportBean";
    public final static String MANAGED_BEAN_APPEAL = "AppealBean";
    
    public final static String SESSION_BEAN_HOMEPAGE_TASKS_SETTINGS = "HomepageTasksSettingsBean";
    public final static String SESSION_BEAN_HOMEPAGE_REQUESTS_SETTINGS = "HomepageProgramsSettingsBean";
    public final static String SESSION_BEAN_HOMEPAGE_PROGRAMS_SETTINGS = "HomepageRequestsSettingsBean";
    public final static String SESSION_BEAN_HOMEPAGE_TOPICS_SETTINGS = "HomepageTopicsSettingsBean";
    public final static String SESSION_BEAN_FONT_SETTINGS = "SettingBean";

    /** The managed bean used to display/access ACC information. */
    public final static String MANAGED_BEAN_OCC_REVIEW_VIEW ="OccReviewViewBean";
    
    /*
	 * Business object and literal constants
	 */
	public final static String BUSINESS_OBJECT_MEMBER_PID = "memberPID";
	public final static String BUSINESS_OBJECT_PROGRAM_ID = "programID";
	public final static String BUSINESS_OBJECT_REQUEST_ID = "requestID";

	/*
	 * CERMe proprietary notice display constant
	 */
	public final static String CERMe_PROPRIETARY_NOTICE_DISPLAYED = "isCERMeProprietaryNoticeDisplayed";
	
}
