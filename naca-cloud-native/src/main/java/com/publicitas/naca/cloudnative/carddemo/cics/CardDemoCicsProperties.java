package com.publicitas.naca.cloudnative.carddemo.cics;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** Cloud deployment identifiers exposed through EXEC CICS ASSIGN. */
@Component
@ConfigurationProperties(prefix = "carddemo.cics")
public class CardDemoCicsProperties
{
    private String applicationId = "CARDDEMO";
    private String systemId = "NACA";

    /** Returns the CICS application identifier. */
    public String getApplicationId()
    {
        return applicationId;
    }

    /** Sets the CICS application identifier. */
    public void setApplicationId(String applicationId)
    {
        this.applicationId = applicationId;
    }

    /** Returns the CICS system identifier. */
    public String getSystemId()
    {
        return systemId;
    }

    /** Sets the CICS system identifier. */
    public void setSystemId(String systemId)
    {
        this.systemId = systemId;
    }
}
