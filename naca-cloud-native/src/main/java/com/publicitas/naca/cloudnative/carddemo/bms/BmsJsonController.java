package com.publicitas.naca.cloudnative.carddemo.bms;

import com.publicitas.naca.cloudnative.carddemo.api.BmsTerminalRequest;
import com.publicitas.naca.cloudnative.carddemo.api.BmsTerminalResponse;
import jakarta.validation.Valid;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST proof of the JSON-to-NacaRT BMS adapter contract. */
@RestController
@RequestMapping("/api/carddemo/bms")
@ConditionalOnProperty(prefix = "carddemo", name = "enabled", havingValue = "true")
public class BmsJsonController
{
    private final BmsJsonMapGateway gateway;
    private final CardDemoSignonService signon;

    /** Creates the diagnostic BMS adapter endpoint. */
    public BmsJsonController(BmsJsonMapGateway gateway,
        ObjectProvider<CardDemoSignonService> signon)
    {
        this.gateway = gateway;
        this.signon = signon.getIfAvailable();
    }

    /** Normalizes a terminal event through the same DOM contract used by RECEIVE/SEND MAP. */
    @PostMapping("/adapter")
    public BmsTerminalResponse adapt(@Valid @RequestBody BmsTerminalRequest request)
    {
        if (signon != null && "CC00".equals(request.transactionId()))
        {
            return signon.execute(request);
        }
        return gateway.roundTrip(request);
    }
}
