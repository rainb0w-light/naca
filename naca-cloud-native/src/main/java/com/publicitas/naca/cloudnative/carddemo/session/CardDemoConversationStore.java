package com.publicitas.naca.cloudnative.carddemo.session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/** PostgreSQL conversation repository with row locking and optimistic versions. */
@Repository
@ConditionalOnProperty(prefix = "carddemo", name = "enabled", havingValue = "true")
public class CardDemoConversationStore
{
    private final JdbcTemplate jdbc;
    private final ObjectMapper objectMapper;

    /** Creates the persistent conversation adapter. */
    public CardDemoConversationStore(JdbcTemplate jdbc, ObjectMapper objectMapper)
    {
        this.jdbc = jdbc;
        this.objectMapper = objectMapper;
    }

    /** Returns the current snapshot without acquiring a long-lived lock. */
    @Transactional(readOnly = true)
    public Optional<CardDemoConversationState> find(UUID conversationId)
    {
        try
        {
            return Optional.ofNullable(jdbc.queryForObject("""
                select conversation_id, transaction_id, program_name, commarea,
                       terminal_state::text, version, expires_at
                  from carddemo_runtime.conversation
                 where conversation_id = ?
                """, this::mapState, conversationId));
        }
        catch (EmptyResultDataAccessException missing)
        {
            return Optional.empty();
        }
    }

    /** Inserts or updates one snapshot, rejecting stale clients before any state changes. */
    @Transactional
    public CardDemoConversationState save(UUID conversationId, String transactionId,
        String programName, byte[] commarea, JsonNode terminalState, long expectedVersion,
        Duration timeToLive)
    {
        Optional<CardDemoConversationState> locked = findForUpdate(conversationId);
        long actualVersion = locked.map(CardDemoConversationState::version).orElse(0L);
        if (expectedVersion != actualVersion)
        {
            throw new CardDemoConversationConflictException(expectedVersion, actualVersion);
        }

        long nextVersion = actualVersion + 1;
        Instant expiresAt = Instant.now().plus(timeToLive);
        if (locked.isEmpty())
        {
            insert(conversationId, transactionId, programName, commarea, terminalState,
                nextVersion, expiresAt);
        }
        else
        {
            update(conversationId, transactionId, programName, commarea, terminalState,
                nextVersion, expiresAt);
        }
        return new CardDemoConversationState(conversationId, transactionId, programName,
            commarea.clone(), terminalState.deepCopy(), nextVersion, expiresAt);
    }

    private Optional<CardDemoConversationState> findForUpdate(UUID conversationId)
    {
        try
        {
            return Optional.ofNullable(jdbc.queryForObject("""
                select conversation_id, transaction_id, program_name, commarea,
                       terminal_state::text, version, expires_at
                  from carddemo_runtime.conversation
                 where conversation_id = ?
                   for update
                """, this::mapState, conversationId));
        }
        catch (EmptyResultDataAccessException missing)
        {
            return Optional.empty();
        }
    }

    private void insert(UUID conversationId, String transactionId, String programName,
        byte[] commarea, JsonNode terminalState, long version, Instant expiresAt)
    {
        jdbc.update("""
            insert into carddemo_runtime.conversation
                (conversation_id, transaction_id, program_name, commarea,
                 terminal_state, version, expires_at)
            values (?, ?, ?, ?, cast(? as jsonb), ?, ?)
            """, conversationId, transactionId, programName, commarea,
            json(terminalState), version, Timestamp.from(expiresAt));
    }

    private void update(UUID conversationId, String transactionId, String programName,
        byte[] commarea, JsonNode terminalState, long version, Instant expiresAt)
    {
        jdbc.update("""
            update carddemo_runtime.conversation
               set transaction_id = ?, program_name = ?, commarea = ?,
                   terminal_state = cast(? as jsonb), version = ?, expires_at = ?,
                   updated_at = current_timestamp
             where conversation_id = ?
            """, transactionId, programName, commarea, json(terminalState), version,
            Timestamp.from(expiresAt), conversationId);
    }

    @SuppressWarnings("PMD.UnusedFormalParameter")
    private CardDemoConversationState mapState(ResultSet result, int rowNumber) throws SQLException
    {
        try
        {
            return new CardDemoConversationState(
                result.getObject("conversation_id", UUID.class),
                result.getString("transaction_id"),
                result.getString("program_name"),
                result.getBytes("commarea"),
                objectMapper.readTree(result.getString("terminal_state")),
                result.getLong("version"),
                result.getTimestamp("expires_at").toInstant());
        }
        catch (JsonProcessingException failure)
        {
            throw new SQLException("Invalid terminal_state JSON in the conversation store", failure);
        }
    }

    private String json(JsonNode value)
    {
        try
        {
            return objectMapper.writeValueAsString(value);
        }
        catch (JsonProcessingException failure)
        {
            throw new IllegalArgumentException("Cannot serialize terminal state", failure);
        }
    }
}
