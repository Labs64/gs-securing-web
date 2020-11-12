package com.example.securingweb;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.labs64.netlicensing.domain.vo.Composition;
import com.labs64.netlicensing.domain.vo.Context;
import com.labs64.netlicensing.domain.vo.SecurityMode;
import com.labs64.netlicensing.domain.vo.ValidationParameters;
import com.labs64.netlicensing.domain.vo.ValidationResult;
import com.labs64.netlicensing.exception.NetLicensingException;
import com.labs64.netlicensing.service.LicenseeService;

@Controller
public class NetLicensingController {

    @GetMapping("/hello")
    public String greeting(Model model) throws NetLicensingException {

        // NetLicensing configuration
        // API Key - min role needed: Licensee
        final String NLIC_APIKEY = "ea624604-028e-458c-b5e5-85bc6620b890";
        // Product number
        final String NLC_PRODUCT = "P1S85TDRU-DEMO";
        // Product module number
        final String NLC_MODULE = "MTGTOW8EC-DEMO";

        // Initiate NetLicensing context
        final Context context = new Context();
        context.setBaseUrl("https://go.netlicensing.io/core/v2/rest");
        context.setSecurityMode(SecurityMode.APIKEY_IDENTIFICATION);
        context.setApiKey(NLIC_APIKEY);

        // Prepare validation request
        final ValidationParameters validationParameters = new ValidationParameters();
        validationParameters.setProductNumber(NLC_PRODUCT);
        // Send validation request; where current login username will be used as unique customer identifier
        ValidationResult validationResult = LicenseeService.validate(context, getUsername(), validationParameters);

        // Prepare model to be rendered on the hello page
        Composition moduleValidation = validationResult.getProductModuleValidation(NLC_MODULE);
        model.addAttribute("name", getUsername());
        model.addAttribute("moduleValidation", moduleValidation);

        return "hello";
    }

    /**
     * Extract logged-in username.
     */
    private String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            return currentUserName;
        }
        return "unknown";
    }

}
