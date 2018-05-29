package com.sap.csc.timebackend.controller;

import com.sap.csc.timebackend.security.AuthenticationFacade;
import com.sap.csc.timebackend.security.SAPUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private final AuthenticationFacade authenticationFacade;

    @Autowired
    public UserController(AuthenticationFacade authenticationFacade) {
        this.authenticationFacade = authenticationFacade;
    }

    @GetMapping
    public SAPUser getAuthUser() {
        return authenticationFacade.getLoggedUser();
    }


}
