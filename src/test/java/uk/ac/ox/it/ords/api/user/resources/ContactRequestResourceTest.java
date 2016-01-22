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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.ox.it.ords.api.user.model.ContactRequest;
import uk.ac.ox.it.ords.api.user.model.User;
import uk.ac.ox.it.ords.api.user.services.UserService;

public class ContactRequestResourceTest extends AbstractResourceTest {

	@Test
	public void sendRequest() throws Exception{
		
		User user = new User();
		user.setEmail("scott.bradley.wilson@gmail.com");
		user.setName("Scott");
		user.setVerificationUuid("9999");
		UserService.Factory.getInstance().createUser(user);
		user = UserService.Factory.getInstance().getUserByVerificationId("9999");
		
		ContactRequest contactRequest = new ContactRequest();
		contactRequest.setEmailAddress("penguin@mailinator.com");
		contactRequest.setMessage("Pls talk 2 me");
		contactRequest.setName("Penguin");
		contactRequest.setProject("A Project");
		contactRequest.setUserId(user.getUserId());
		
		loginUsingSSO("anonymous","");
		
		assertEquals(200, getClient().path("/contact").post(contactRequest).getStatus());
		
	}
}
