package com.publicitas.naca.cloudnative.carddemo.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

/** Builds an honest runtime report from the pinned corpus and translation baseline. */
@Service
public class CardDemoCapabilityService
{
    private static final String INVENTORY_RESOURCE =
        "carddemo/capabilities/CAPABILITY_INVENTORY.json";
    private static final String TRANSLATION_RESOURCE =
        "carddemo/translation/ONLINE_TRANSLATION_BASELINE.json";

    private final ObjectNode capabilityReport;

    /** Loads both machine-generated ledgers and rejects an invalid application package. */
    public CardDemoCapabilityService(ObjectMapper objectMapper)
    {
        try
        {
            JsonNode inventory = read(objectMapper, INVENTORY_RESOURCE);
            JsonNode translation = read(objectMapper, TRANSLATION_RESOURCE);
            capabilityReport = buildReport(objectMapper, inventory, translation);
        }
        catch (IOException failure)
        {
            throw new IllegalStateException("Cannot load packaged CardDemo capability ledgers", failure);
        }
    }

    /** Returns a defensive copy so request serialization cannot mutate the application ledger. */
    public ObjectNode report()
    {
        return capabilityReport.deepCopy();
    }

    private static JsonNode read(ObjectMapper objectMapper, String resource) throws IOException
    {
        try (InputStream input = new ClassPathResource(resource).getInputStream())
        {
            return objectMapper.readTree(input);
        }
    }

    private static ObjectNode buildReport(ObjectMapper mapper, JsonNode inventory, JsonNode translation)
    {
        Map<String, JsonNode> translationByProgram = new ConcurrentHashMap<>();
        translation.path("programs").forEach(program ->
            translationByProgram.put(program.path("program").asText(), program));

        ObjectNode report = mapper.createObjectNode();
        report.set("source", inventory.path("source"));
        report.set("summary", inventory.path("summary"));
        report.put("phase", "BMS_JSON_MINIMAL_SLICE");
        ArrayNode transactions = report.putArray("transactions");
        inventory.path("transactions").forEach(transaction ->
            transactions.add(transactionCapability(mapper, transaction, translationByProgram)));
        return report;
    }

    private static ObjectNode transactionCapability(ObjectMapper mapper, JsonNode transaction,
        Map<String, JsonNode> translationByProgram)
    {
        String programName = transaction.path("program").asText();
        JsonNode probe = translationByProgram.get(programName);
        ObjectNode capability = mapper.createObjectNode();
        capability.put("transactionId", transaction.path("transactionId").asText());
        capability.put("program", programName);
        capability.put("translated", probe != null
            && "TRANSLATED".equals(probe.path("status").asText()));
        capability.put("compiled", probe != null && probe.path("compiled").asBoolean(false));
        capability.put("runtimeDependenciesReady", probe != null
            && probe.path("runtimeDependenciesReady").asBoolean(false));
        capability.put("accepted", probe != null && probe.path("accepted").asBoolean(false));
        capability.put("status", probe == null ? "NOT_PROBED" : probe.path("status").asText());
        if (probe != null && probe.has("acceptedScenarios"))
        {
            capability.set("acceptedScenarios", probe.path("acceptedScenarios"));
        }
        if (probe != null && probe.has("firstBlocker"))
        {
            capability.set("firstBlocker", probe.path("firstBlocker"));
        }
        if (probe != null && probe.has("remainingBlocker"))
        {
            capability.set("remainingBlocker", probe.path("remainingBlocker"));
        }
        return capability;
    }
}
