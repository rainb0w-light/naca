package com.publicitas.naca.cloudnative.carddemo.api;

import java.util.Map;

/** One field emitted from CICS SEND MAP semantics. */
public record BmsFieldOutput(String value, boolean modified, boolean cursor,
                             Map<String, String> attributes)
{
}
