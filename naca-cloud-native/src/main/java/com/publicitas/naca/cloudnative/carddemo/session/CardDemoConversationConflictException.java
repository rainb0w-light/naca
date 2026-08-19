package com.publicitas.naca.cloudnative.carddemo.session;

/** Raised when a client attempts to update a stale conversation version. */
public class CardDemoConversationConflictException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    /** Describes the expected and actual versions without exposing session data. */
    public CardDemoConversationConflictException(long expectedVersion, long actualVersion)
    {
        super("Conversation version conflict: expected=" + expectedVersion + ", actual=" + actualVersion);
    }
}
