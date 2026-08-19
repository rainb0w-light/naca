package com.publicitas.naca.cloudnative;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.publicitas.naca.cloudnative.carddemo.api.BmsFieldInput;
import com.publicitas.naca.cloudnative.carddemo.api.BmsTerminalFlags;
import com.publicitas.naca.cloudnative.carddemo.api.BmsTerminalRequest;
import com.publicitas.naca.cloudnative.carddemo.api.BmsTerminalResponse;
import com.publicitas.naca.cloudnative.carddemo.bms.BmsJsonMapGateway;
import com.publicitas.naca.cloudnative.carddemo.cics.PostgresCicsRecordStore;
import idea.onlinePrgEnv.OnlineEnvironment;
import idea.onlinePrgEnv.OnlineProgramLoader;
import idea.onlinePrgEnv.OnlineSession;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import jlib.classLoader.CodeManager;
import jlib.misc.BasePic9Comp3BufferSupport;
import nacaLib.basePrgEnv.BaseProgramLoader;
import nacaLib.calledPrgSupport.BaseCalledPrgPublicArgPositioned;
import nacaLib.misc.KeyPressed;
import nacaLib.misc.CCommarea;
import nacaLib.tempCache.TempCacheLocator;
import nacaLib.varEx.InternalCharBuffer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/** Isolated launcher for the translated CardDemo sign-on transaction. */
public final class CardDemoSignonProgramRunner
{
    private static final String PROGRAM = "Cosgn00c";

    /** Executes an unknown-user sign-on against the supplied PostgreSQL database. */
    @SuppressWarnings("PMD.SystemPrintln")
    public static void main(String[] args) throws Exception
    {
        CodeManager.setPath(args[0]);
        CodeManager.initLoadPossibilities(true, false);
        BasePic9Comp3BufferSupport.init();
        TempCacheLocator.setTempCache();

        DriverManagerDataSource dataSource = new DriverManagerDataSource(args[1], args[2], args[3]);
        PostgresCicsRecordStore records = new PostgresCicsRecordStore(new JdbcTemplate(dataSource));
        OnlineProgramLoader loader = new OnlineProgramLoader(null, null);
        BaseProgramLoader.registerTransaction("CC00", PROGRAM);
        OnlineSession session = new OnlineSession(false);
        OnlineEnvironment environment =
            (OnlineEnvironment) loader.GetEnvironment(session, null, null);
        environment.setRuntimeConfigOption("APPLID", "CARDDEMO");
        environment.setRuntimeConfigOption("SYSID", "NACA");
        environment.setCicsRecordStore(records);

        environment.setNextProgramToLoad(PROGRAM);
        loader.runTopProgram(environment, new ArrayList<BaseCalledPrgPublicArgPositioned>());

        // COSGN00C only tests EIBCALEN before authentication. Its generated linkage
        // declaration currently retains the COBOL minimum OCCURS size, so the JSON
        // controller uses a one-byte continuation marker until dynamic linkage arrays
        // are supported without changing the translation pipeline.
        CCommarea continuation = new CCommarea();
        continuation.setVarPassedByValue(new InternalCharBuffer(1));
        environment.setCommarea(continuation);

        BmsTerminalRequest request = new BmsTerminalRequest(
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            UUID.fromString("00000000-0000-0000-0000-000000000002"),
            "CC00", "COSGN00", "COSGN0A", "ENTER", "USERID",
            Map.of(
                "USERID", new BmsFieldInput("MISSING1", true, false),
                "PASSWD", new BmsFieldInput("PASSWORD", true, false)));
        BmsJsonMapGateway gateway = new BmsJsonMapGateway();
        environment.setXMLData(gateway.toReceiveDocument(request));
        environment.setKeyPressed(KeyPressed.ENTER);
        loader.runTopProgram(environment, new ArrayList<BaseCalledPrgPublicArgPositioned>());

        BmsTerminalResponse response = gateway.fromSendDocument(request, PROGRAM,
            session.getXMLData(), new BmsTerminalFlags(true, false, true, "USERID"));
        System.out.println("RESULT_JSON=" + new ObjectMapper().writeValueAsString(response));
    }

    private CardDemoSignonProgramRunner()
    {
    }
}
