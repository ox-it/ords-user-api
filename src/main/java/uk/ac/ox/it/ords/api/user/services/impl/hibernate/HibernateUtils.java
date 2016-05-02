/*
 * Copyright 2015 University of Oxford
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ox.it.ords.api.user.services.impl.hibernate;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import uk.ac.ox.it.ords.api.user.model.User;
import uk.ac.ox.it.ords.security.configuration.MetaConfiguration;
import uk.ac.ox.it.ords.security.model.Audit;
import uk.ac.ox.it.ords.security.model.DatabaseServer;
import uk.ac.ox.it.ords.security.model.Permission;
import uk.ac.ox.it.ords.security.model.UserRole;
import uk.ac.ox.it.ords.security.services.ServerConfigurationService;


public class HibernateUtils
{
	public static Logger log = LoggerFactory.getLogger(HibernateUtils.class);
	
	private static SessionFactory sessionFactory;
	private static ServiceRegistry serviceRegistry;
	
	protected static String HIBERNATE_CONFIGURATION_PROPERTY = "ords.hibernate.configuration";
	
	protected static Configuration configuration;
	
	/**
	 * Add the class mappings for this module
	 */
	protected static void addMappings(){
		configuration.addAnnotatedClass(User.class);
		configuration.addAnnotatedClass(Permission.class);
		configuration.addAnnotatedClass(Audit.class);
		configuration.addAnnotatedClass(UserRole.class);
	}
	
	protected static void init()
	{
		try
		{
			String hibernateConfigLocation = MetaConfiguration.getConfiguration().getString(HIBERNATE_CONFIGURATION_PROPERTY);			
			if (hibernateConfigLocation == null){
				log.warn("No hibernate configuration found; using default hibernate.cfg.xml");
				configuration = new Configuration().configure();
			} else {
				log.info("Hibernate configuration found; using configuration from "+hibernateConfigLocation);
				configuration = new Configuration().configure(new File(hibernateConfigLocation));
			}
			
			
			//
			// Add server connection details
			//
			DatabaseServer databaseServer = ServerConfigurationService.Factory.getInstance().getOrdsDatabaseServer();
			configuration.setProperty("hibernate.connection.url", databaseServer.getUrl());
			configuration.setProperty("hibernate.connection.username", databaseServer.getUsername());
			configuration.setProperty("hibernate.connection.password", databaseServer.getPassword());
			
			//
			// Add class mappings. Note we do this programmatically as this is
			// completely independent of the database configuration.
			//
			addMappings();
			
			serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
			sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		}
		catch (Exception he)
		{
			throw new ExceptionInInitializerError(he);
		}
	}

	public static SessionFactory getSessionFactory()
	{
		if (sessionFactory == null) init();
		return sessionFactory;
	}

	public static void closeSession() {
		sessionFactory.getCurrentSession().close();
	} 
}
