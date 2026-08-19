package com.publicitas.naca.cloudnative.carddemo.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Exposes the machine-readable acceptance state of every CardDemo transaction. */
@RestController
@RequestMapping("/api/carddemo/capabilities")
@ConditionalOnProperty(prefix = "carddemo", name = "enabled", havingValue = "true")
public class CardDemoCapabilityController
{
    private final CardDemoCapabilityService capabilityService;

    /** Creates the capability endpoint. */
    public CardDemoCapabilityController(CardDemoCapabilityService capabilityService)
    {
        this.capabilityService = capabilityService;
    }

    /** Returns a defensive copy of the fixed-source capability report. */
    @GetMapping
    public ObjectNode capabilities()
    {
        return capabilityService.report();
    }
}
