package com.sap.security;

import com.sap.model.SapUser;
import org.springframework.stereotype.Service;

@Service
public interface AuthFacade {

    SapUser getLoggedUser();
}
