package com.sap.security;

import com.sap.model.SapUser;
import com.sap.security.um.service.UserManagementAccessor;
import com.sap.security.um.user.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AuthFacadeImpl implements AuthFacade {

    private static final Logger log = LoggerFactory.getLogger(AuthFacadeImpl.class);

    @Override
    public SapUser getLoggedUser() {
        try {
            return SapUser.create(UserManagementAccessor.getUserProvider().getCurrentUser().getName(),
                    UserManagementAccessor.getUserProvider().getCurrentUser().getName());
        } catch (PersistenceException e) {
            log.error("User retrieval from context failed...");
            e.printStackTrace();
        }

        return SapUser.create("0", "Guest");
    }
}
