package com.sap.csc.timebackend.security;

import org.springframework.stereotype.Service;

@Service
public interface AuthenticationFacade {

    SAPUser getLoggedUser();
}
