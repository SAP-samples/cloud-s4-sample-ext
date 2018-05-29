package com.sap.csc.timebackend.security;

import com.sap.cloud.sdk.cloudplatform.security.user.UserAccessor;
import org.springframework.stereotype.Component;

@Component
public class NeoAuthFacade implements AuthenticationFacade {

    @Override
    public SAPUser getLoggedUser() {
        return SAPUser.create(UserAccessor.getCurrentUser().getName().toUpperCase());
    }
}
