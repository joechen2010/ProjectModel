package com.joe.utilities.core.lookup;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.joe.utilities.core.configuration.admin.domain.IApplicationConfiguration;
import com.joe.utilities.core.configuration.admin.facade.IApplicationConfigurationFacade;
import com.joe.utilities.core.hibernate.repository.ApplicationConfigurationRepository;
import com.joe.utilities.core.serviceLocator.ServiceLocator;
import com.joe.utilities.core.util.EvaluationException;
import com.joe.utilities.core.util.ILookupProfile;

@Transactional(readOnly = true)
public class ApplicationConfigurationFacadeImpl implements	IApplicationConfigurationFacade, ApplicationContextAware {

	private Log log = LogFactory.getLog(ApplicationConfigurationFacadeImpl.class);
	private ApplicationConfigurationRepository applicationConfigurationRepo;
	private ApplicationContext appContext;
	private LookupManager lookupManager;

	/*
	 * Default constructor
	 */
	public ApplicationConfigurationFacadeImpl() {
		super();
	}

	public List<IApplicationConfiguration> getListApplicationConfigurations(String filter, Long startIndex, Long maxResults) {
        log.trace("Entering getListApplicationConfigurations facade method");
		ApplicationConfigurationRepository acr = getApplicationConfigRepository();

		List<IApplicationConfiguration> l;
        if (filter == null) {
			l= acr.listApplicationConfigurations();
		} else {
			l = acr.listApplicationConfigurationsByCategoryCode(filter);
		}
		return l;
	}
	
	public IApplicationConfiguration getApplicationConfiguration(String key) {
        log.trace("Entering getApplicationConfiguration facade method");
		return lookupManager.getApplicationConfiguration(key);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = { EvaluationException.class })
	public void saveApplicationConfiguration(IApplicationConfiguration appConfig) {
        log.trace("Entering saveApplicationConfiguration facade method");
		ApplicationConfigurationRepository acr = getApplicationConfigRepository();
		IApplicationConfiguration ac = acr.saveApplicationConfiguration(appConfig);
		if (ac == null) 
    		throw new UnsupportedOperationException("Unable to save ApplicationConfiguration to database.");
	}

	/**
	 * @return
	 */
	private ApplicationConfigurationRepository getApplicationConfigRepository() {
		/*
		 * hack: if this bean wasn't wired by spring, use ServiceLocator
		 */
		if(this.appContext == null)
		{
			return (ApplicationConfigurationRepository) ServiceLocator.getInstance().getBean("applicationConfigRepository");
		}
		return this.applicationConfigurationRepo;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = { EvaluationException.class })
	public void deleteApplicationConfiguration(IApplicationConfiguration appConfig) {
		ApplicationConfigurationRepository acr = getApplicationConfigRepository();
		acr.removeApplicationConfigProperty(appConfig);
	}
	

	@Required
	public void setApplicationConfigurationRepo(ApplicationConfigurationRepository applicationConfigurationRepo) {
		this.applicationConfigurationRepo = applicationConfigurationRepo;
	}

	@Required
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.appContext = applicationContext;
	}

	@Required
	public void setLookupManager(LookupManager lookupManager) {
		this.lookupManager = lookupManager;
	}

	@Override
	public Long retrieveDBTimeInMilliseconds() {
		return applicationConfigurationRepo.retrieveDBTimeInMilliseconds();
	}

	@Override
	public Long retrieveDBTimeOffsetInMilliseconds() {
		return applicationConfigurationRepo.retrieveDBTimeOffsetInMilliseconds();
	}
}
