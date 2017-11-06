package com.acme.intreportapp.controller;

import java.net.URI;
import java.security.Principal;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;

@Controller
public class RootController {

	@Value("${s4cld.destination_name}")
	private String s4cldDestinationName;

	private static final String HTTPS = "https://";
	private static final String NOTAPPICAPABLE = "n/a";
	private static final String APIHYPEN = "-api";
	private static final String EMPTYSTRING = "";
	private static final String MODEL_ATTR_S4HOMEREF = "s4HomeHref";
	private static final String MODEL_ATTR_USER = "user";
	private static final String MODEL_ATTR_GMAPSKEY = "gMapsKey";
	private static final String VIEW_INDEX = "index";

	public RootController() {
	}

	/**
	 * 
	 * @param model
	 * @param principal
	 * @param gMapsKey
	 * @return index as view name
	 */
	@RequestMapping("/")
	public String index(Model model, Principal principal, @Value("${gmaps.apikey}") String gMapsKey) {

		String s4Url = null;
		try {
			URI s4uri = DestinationAccessor.getDestination(s4cldDestinationName).getUri();

			s4Url = HTTPS + StringUtils.replace(s4uri.getHost(), APIHYPEN, EMPTYSTRING) + "/ui";

			model.addAttribute(MODEL_ATTR_S4HOMEREF, s4Url);
			model.addAttribute(MODEL_ATTR_USER, principal != null ? principal.getName() : NOTAPPICAPABLE);
			model.addAttribute(MODEL_ATTR_GMAPSKEY, gMapsKey);

		} catch (Exception e) {
			throw new RuntimeException(
					"Unable to access destination. Ensure to create a destination as detailed in 1RW.", e);
		}

		return VIEW_INDEX;
	}

}