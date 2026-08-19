package com.publicitas.naca.cloudnative.carddemo.api;

import jakarta.validation.constraints.Size;

/** One browser field submitted to CICS RECEIVE MAP semantics. */
public record BmsFieldInput(@Size(max = 4096) String value, boolean modified, boolean cleared)
{
    /** Returns the value consumed by the NacaRT form loader. */
    public String normalizedValue()
    {
        return cleared || value == null ? "" : value;
    }

    /** Cleared fields are necessarily considered modified. */
    public boolean effectiveModified()
    {
        return modified || cleared;
    }
}
