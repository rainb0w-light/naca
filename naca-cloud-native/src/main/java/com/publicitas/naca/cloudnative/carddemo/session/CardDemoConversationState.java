package com.publicitas.naca.cloudnative.carddemo.session;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.util.UUID;

/** Persistent state required to resume one CICS-style browser conversation. */
public record CardDemoConversationState(
    UUID conversationId,
    String transactionId,
    String programName,
    byte[] commarea,
    JsonNode terminalState,
    long version,
    Instant expiresAt)
{
}
