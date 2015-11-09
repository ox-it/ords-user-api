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
package uk.ac.ox.it.ords.api.user.services.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.ac.ox.it.ords.api.user.services.impl.hibernate.UserRoleServiceImpl;

public class UserRoleServiceTest extends UserRoleServiceImpl {
	
	@Test
	public void checkLocalUser(){
		
		//
		// We're using the test configuration which has "ox.ac.uk" as the suffix
		//
		assertTrue(isLocalUser("test@ox.ac.uk"));
		assertTrue(isLocalUser("test@sbs.ox.ac.uk"));		
		assertFalse(isLocalUser("test@test.com"));
		assertFalse(isLocalUser("test@nox.ac.uk"));
		assertFalse(isLocalUser("test@box.ac.uk"));
		assertFalse(isLocalUser("test@fox.ac.uk"));
	}

}
