package uk.ac.ox.it.ords.api.user.services.impl;

import uk.ac.ox.it.ords.api.user.model.User;
import uk.ac.ox.it.ords.api.user.services.UserService;

public abstract class AbstractUserService implements UserService {

	public boolean validate(User user) {
		return true;
	}
	
	

}
