package org.agnitas.beans.factory.impl;

import org.agnitas.beans.UserForm;
import org.agnitas.beans.factory.UserFormFactory;
import org.agnitas.beans.impl.UserFormImpl;


public class UserFormFactoryImpl implements UserFormFactory {
    @Override
    public UserForm newUserForm() {
        return new UserFormImpl();
    }
}
