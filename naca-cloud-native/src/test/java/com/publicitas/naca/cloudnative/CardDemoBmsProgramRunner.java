package com.publicitas.naca.cloudnative;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.publicitas.naca.cloudnative.carddemo.api.BmsFieldInput;
import com.publicitas.naca.cloudnative.carddemo.api.BmsTerminalFlags;
import com.publicitas.naca.cloudnative.carddemo.api.BmsTerminalRequest;
import com.publicitas.naca.cloudnative.carddemo.api.BmsTerminalResponse;
import com.publicitas.naca.cloudnative.carddemo.bms.BmsJsonMapGateway;
import idea.onlinePrgEnv.OnlineEnvironment;
import idea.onlinePrgEnv.OnlineProgramLoader;
import idea.onlinePrgEnv.OnlineSession;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import jlib.classLoader.CodeManager;
import jlib.misc.BasePic9Comp3BufferSupport;
import nacaLib.calledPrgSupport.BaseCalledPrgPublicArgPositioned;
import nacaLib.tempCache.TempCacheLocator;
import org.w3c.dom.Document;

/** Isolated runtime launcher for the generated BMS JSON proof program. */
public final class CardDemoBmsProgramRunner
{
    private static final String RESULT_PREFIX = "RESULT_JSON=";

    /** Runs JSON-to-Form-to-generated-program-to-JSON in an isolated JVM. */
    @SuppressWarnings("PMD.SystemPrintln")
    public static void main(String[] args) throws Exception
    {
        String programName = args[0];
        CodeManager.setPath(args[1]);
        CodeManager.initLoadPossibilities(true, false);
        BasePic9Comp3BufferSupport.init();
        TempCacheLocator.setTempCache();

        BmsTerminalRequest request = new BmsTerminalRequest(
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            UUID.fromString("00000000-0000-0000-0000-000000000002"),
            "TST0", "BMSJSM1", "BMSJS0F", "ENTER", "REQUEST",
            Map.of(
                "REQUEST", new BmsFieldInput("PING", true, false),
                "RESULT", new BmsFieldInput("", false, false)));
        BmsJsonMapGateway gateway = new BmsJsonMapGateway();
        Document receive = gateway.toReceiveDocument(request);

        OnlineProgramLoader loader = new OnlineProgramLoader(null, null);
        OnlineSession session = new OnlineSession(false);
        OnlineEnvironment environment =
            (OnlineEnvironment) loader.GetEnvironment(session, null, null);
        environment.setXMLData(receive);
        environment.setNextProgramToLoad(programName);
        loader.runTopProgram(environment, new ArrayList<BaseCalledPrgPublicArgPositioned>());

        BmsTerminalResponse response = gateway.fromSendDocument(request, programName,
            session.getXMLData(), new BmsTerminalFlags(true, false, true, "REQUEST"));
        System.out.println(RESULT_PREFIX + new ObjectMapper().writeValueAsString(response));
    }

    private CardDemoBmsProgramRunner()
    {
    }
}
