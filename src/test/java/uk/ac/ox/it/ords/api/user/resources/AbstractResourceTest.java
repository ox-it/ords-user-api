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
package uk.ac.ox.it.ords.api.user.resources;

import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.lifecycle.ResourceProvider;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.transport.local.LocalConduit;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.util.Factory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import uk.ac.ox.it.ords.api.user.resources.UserResource;
import uk.ac.ox.it.ords.api.user.services.UserService;
import uk.ac.ox.it.ords.api.user.services.impl.hibernate.HibernateUtils;
import uk.ac.ox.it.ords.security.AbstractShiroTest;
import uk.ac.ox.it.ords.security.model.UserRole;

public class AbstractResourceTest extends AbstractShiroTest {

	protected final static String ENDPOINT_ADDRESS = "local://user-api";
	protected static Server server;
	private static boolean dbCreated = false;

	protected static void startServer() throws Exception {

	}
	
	public WebClient getClient(){
		List<Object> providers = new ArrayList<Object>();
		providers.add(new JacksonJsonProvider());
		WebClient client = WebClient.create(ENDPOINT_ADDRESS, providers);
		client.type("application/json");
		client.accept("application/json");
		WebClient.getConfig(client).getRequestContext().put(LocalConduit.DIRECT_DISPATCH, Boolean.TRUE);
		return client;
	}
	
	public static void createTestUsersAndRoles() throws Exception{
		
		UserService.Factory.getInstance().init();
		
		//
		// Set up the database
		//
		
		//
		// Set up the test users and their permissions
		//
		Session session = HibernateUtils.getSessionFactory().getCurrentSession();
		Transaction transaction = session.beginTransaction();
		
		//
		// Add test users to roles
		//
		UserRole admin = new UserRole();
		admin.setPrincipalName("admin");
		admin.setRole("administrator");
		session.save(admin);
		
		UserRole anonymous = new UserRole();
		anonymous.setPrincipalName("anonymous");
		anonymous.setRole("anonymous");
		session.save(anonymous);
		
		//
		// Commit our changes
		//
		transaction.commit();
		HibernateUtils.closeSession();
		
		dbCreated = true;
	}

	/**
	 * Configure Shiro and start the server
	 * @throws Exception
	 */
	@BeforeClass
	public static void initialize() throws Exception {
	
		//
		// Set up roles
		//
		if (!dbCreated) createTestUsersAndRoles();
		
		//
		// This is for unit testing only and uses the test.shiro.ini configuration
		//
		Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:test.shiro.ini");
		SecurityManager securityManager = factory.getInstance();
		SecurityUtils.setSecurityManager(securityManager);
		
		//
		// Create an embedded server with JSON processing
		//
		JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
		
		ArrayList<Object> providers = new ArrayList<Object>();
		providers.add(new JacksonJsonProvider());
		//providers.add(new UnrecognizedPropertyExceptionMapper());
		//providers.add(new ValidationExceptionMapper());
		sf.setProviders(providers);
		
		//
		// Add our REST resources to the server
		//
		ArrayList<ResourceProvider> resources = new ArrayList<ResourceProvider>();
		resources.add(new SingletonResourceProvider(new UserResource(), true));
		resources.add(new SingletonResourceProvider(new Verification(), true));
		resources.add(new SingletonResourceProvider(new ContactRequestResource(), true));
		sf.setResourceProviders(resources);
		
		//
		// Start the server at the endpoint
		//
		sf.setAddress(ENDPOINT_ADDRESS);
		server = sf.create(); 
		startServer();
	}

	@AfterClass
	public static void destroy() throws Exception {
		server.stop();
		server.destroy();
	}

	@After
	public void logout(){
		SecurityUtils.getSubject().logout();
	}



}
