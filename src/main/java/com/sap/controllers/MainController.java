package com.sap.controllers;

import com.sap.model.SapUser;
import com.sap.security.AuthFacade;
import com.sap.utils.DestinationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class MainController {

    private final DestinationUtils destinationUtils;
    private final AuthFacade authFacade;

    @Value("${destination.name}")
    private String destinationName;

    @Value("${destination.basePath}")
    private String destinationPath;

    @Autowired
    public MainController(DestinationUtils destinationUtils, AuthFacade authFacade) {
        this.destinationUtils = destinationUtils;
        this.authFacade = authFacade;
    }

    @GetMapping("health")
    public String helloNurse() {
        return "Up and running!";
    }


    @GetMapping
    public String index() {
        return "Welcome!";
    }

    @GetMapping("destination")
    public String getP23DestinationMetadata() {
        final String serviceUrl = destinationUtils.getDestinationServiceUrlForPath(destinationName, destinationPath);
        final MultiValueMap<String, String> newHeaders = destinationUtils.getNewHeaders(destinationName, serviceUrl);
        final ResponseEntity<String> result = destinationUtils.getDataFromServiceAsString(destinationName, serviceUrl, HttpMethod.GET,
                newHeaders);

        return result.getBody();
    }

    @GetMapping("user")
    public SapUser getLoggedUser() {
        return authFacade.getLoggedUser();
    }
}
