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

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class HibernateUtils
{
	public static Logger log = LoggerFactory.getLogger(HibernateUtils.class);
	
	private static SessionFactory sessionFactory;
	private static ServiceRegistry serviceRegistry;
	
	private static final String HIBERNATE_CONFIGURATION_LOCATION = "/etc/ordsConfig/ords-user-api.hibernate.cfg.xml";
	
	private static void init()
	{
		try
		{
			Configuration configuration;
			
			if (new File(HIBERNATE_CONFIGURATION_LOCATION).exists()){
				configuration = new Configuration().configure(HIBERNATE_CONFIGURATION_LOCATION);	
			} else {
				configuration = new Configuration().configure();
			}

			serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
			sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		}
		catch (HibernateException he)
		{
			log.error("Error creating Session: " + he);
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
