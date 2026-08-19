package com.publicitas.naca.cloudnative.carddemo.bms;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Provides a stable entry URL for the CardDemo browser demonstration. */
@Controller
@ConditionalOnProperty(prefix = "carddemo", name = "enabled", havingValue = "true")
public class CardDemoPortalController
{
    /** Redirects the directory URL to its explicit static resource. */
    @GetMapping({"/carddemo", "/carddemo/"})
    public String portal()
    {
        return "redirect:/carddemo/index.html";
    }
}
