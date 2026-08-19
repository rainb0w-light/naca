package com.publicitas.naca.cloudnative;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import com.publicitas.naca.cloudnative.carddemo.bms.BmsJsonController;
import com.publicitas.naca.cloudnative.carddemo.bms.BmsJsonMapGateway;
import com.publicitas.naca.cloudnative.carddemo.bms.CardDemoPortalController;
import com.publicitas.naca.cloudnative.carddemo.api.CardDemoCapabilityController;
import com.publicitas.naca.cloudnative.carddemo.api.CardDemoCapabilityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** REST contract proof for the BMS JSON adapter. */
@WebMvcTest(controllers = {BmsJsonController.class, CardDemoCapabilityController.class,
    CardDemoPortalController.class},
    properties = "carddemo.enabled=true")
@Import({BmsJsonMapGateway.class, CardDemoCapabilityService.class})
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class BmsJsonControllerTest
{
    private static final String PORTAL_URL = "/carddemo/";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void exposesStableCardDemoPortalUrl() throws Exception
    {
        mockMvc.perform(get(PORTAL_URL))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(PORTAL_URL + "index.html"));
    }

    @Test
    void exposesAcceptedSignonSliceAndHonestRemainingBlocker() throws Exception
    {
        mockMvc.perform(get("/api/carddemo/capabilities"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.source.commit")
                .value("59cc6c2fd7ebd7ef7925cad552a01a4b8b6e4d5e"))
            .andExpect(jsonPath("$.transactions.length()").value(25))
            .andExpect(jsonPath("$.transactions[?(@.transactionId == 'CC00')].status")
                .value("TRANSLATED"))
            .andExpect(jsonPath("$.transactions[?(@.transactionId == 'CC00')].accepted")
                .value(true))
            .andExpect(jsonPath("$.transactions[?(@.transactionId == 'CC00')].remainingBlocker.code")
                .value("CARDDEMO_VALID_LOGIN_XCTL_TARGETS_NOT_EXECUTABLE"));
    }

    @Test
    void mapsModifiedClearedAidCursorAndTerminalFlagsWithoutExposingXml() throws Exception
    {
        mockMvc.perform(post("/api/carddemo/bms/adapter")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "requestId": "00000000-0000-0000-0000-000000000001",
                      "conversationId": "00000000-0000-0000-0000-000000000002",
                      "transactionId": "CC00",
                      "mapSet": "COSGN00",
                      "map": "COSGN0A",
                      "aid": "ENTER",
                      "cursorField": "USERID",
                      "fields": {
                        "USERID": {"value": "DEMO0001", "modified": true, "cleared": false},
                        "PASSWD": {"value": "secret", "modified": true, "cleared": false},
                        "TITLE01": {"value": "CardDemo", "modified": false, "cleared": false}
                      }
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.program").value("BMSJSON"))
            .andExpect(jsonPath("$.fields.USERID.value").value("DEMO0001"))
            .andExpect(jsonPath("$.fields.USERID.modified").value(true))
            .andExpect(jsonPath("$.fields.USERID.cursor").value(true))
            .andExpect(jsonPath("$.fields.PASSWD.value").value(""))
            .andExpect(jsonPath("$.fields.PASSWD.modified").value(true))
            .andExpect(jsonPath("$.terminal.erase").value(true))
            .andExpect(jsonPath("$.terminal.freeKeyboard").value(true))
            .andExpect(jsonPath("$.xml").doesNotExist());
    }

    @Test
    void rejectsUnknownAidBeforeRuntimeExecution() throws Exception
    {
        mockMvc.perform(post("/api/carddemo/bms/adapter")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "requestId": "00000000-0000-0000-0000-000000000001",
                      "conversationId": "00000000-0000-0000-0000-000000000002",
                      "transactionId": "CC00",
                      "mapSet": "COSGN00",
                      "map": "COSGN0A",
                      "aid": "EXECUTE",
                      "fields": {}
                    }
                    """))
            .andExpect(status().isBadRequest());
    }
}
