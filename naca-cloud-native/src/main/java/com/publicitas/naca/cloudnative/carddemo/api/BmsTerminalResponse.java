package com.publicitas.naca.cloudnative.carddemo.api;

import java.util.Map;
import java.util.UUID;

/** JSON response corresponding to the final SEND MAP snapshot of one request. */
public record BmsTerminalResponse(
    UUID requestId,
    UUID conversationId,
    String transactionId,
    String program,
    String mapSet,
    String map,
    Map<String, BmsFieldOutput> fields,
    BmsTerminalFlags terminal)
{
}
