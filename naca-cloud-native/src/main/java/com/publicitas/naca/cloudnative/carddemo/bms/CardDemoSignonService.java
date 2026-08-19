package com.publicitas.naca.cloudnative.carddemo.bms;

import com.publicitas.naca.cloudnative.carddemo.api.BmsTerminalFlags;
import com.publicitas.naca.cloudnative.carddemo.api.BmsTerminalRequest;
import com.publicitas.naca.cloudnative.carddemo.api.BmsTerminalResponse;
import com.publicitas.naca.cloudnative.carddemo.cics.CardDemoCicsProperties;
import com.publicitas.naca.cloudnative.carddemo.cics.PostgresCicsRecordStore;
import idea.onlinePrgEnv.OnlineEnvironment;
import idea.onlinePrgEnv.OnlineProgramLoader;
import idea.onlinePrgEnv.OnlineSession;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import jlib.classLoader.CodeManager;
import jlib.misc.BasePic9Comp3BufferSupport;
import nacaLib.basePrgEnv.BaseProgramLoader;
import nacaLib.calledPrgSupport.BaseCalledPrgPublicArgPositioned;
import nacaLib.misc.CCommarea;
import nacaLib.misc.KeyPressed;
import nacaLib.tempCache.TempCacheLocator;
import nacaLib.varEx.InternalCharBuffer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Executes the translated CC00 sign-on program behind the JSON terminal API. */
@Service
@ConditionalOnProperty(prefix = "carddemo.execution", name = "enabled", havingValue = "true")
public class CardDemoSignonService
{
    private static final String TRANSACTION = "CC00";
    private static final String PROGRAM = "Cosgn00c";

    private final BmsJsonMapGateway gateway;
    private final PostgresCicsRecordStore records;
    private final CardDemoCicsProperties cics;
    private final Path generatedClasses;

    /** Creates the controller-managed CardDemo execution adapter. */
    public CardDemoSignonService(BmsJsonMapGateway gateway, PostgresCicsRecordStore records,
        CardDemoCicsProperties cics,
        @Value("${carddemo.execution.generated-classes-dir:build/generated-carddemo/signon/compile-classes}")
        String generatedClasses)
    {
        this.gateway = gateway;
        this.records = records;
        this.cics = cics;
        this.generatedClasses = Path.of(generatedClasses).toAbsolutePath().normalize();
    }

    /** Runs one JSON request through the translated program and PostgreSQL CICS store. */
    @Transactional
    public synchronized BmsTerminalResponse execute(BmsTerminalRequest request)
    {
        if (!TRANSACTION.equals(request.transactionId()))
        {
            throw new IllegalArgumentException("Unsupported CardDemo transaction: "
                + request.transactionId());
        }
        if (!Files.isRegularFile(generatedClasses.resolve(PROGRAM + ".class")))
        {
            throw new IllegalStateException("Generated CardDemo classes are missing at "
                + generatedClasses + "; run :naca-cloud-native:cardDemoOnlineBaseline first");
        }

        CodeManager.setPath(generatedClasses.toString());
        CodeManager.initLoadPossibilities(true, false);
        BasePic9Comp3BufferSupport.init();
        TempCacheLocator.setTempCache();

        OnlineProgramLoader loader = new OnlineProgramLoader(null, null);
        BaseProgramLoader.registerTransaction(TRANSACTION, PROGRAM);
        OnlineSession session = new OnlineSession(false);
        OnlineEnvironment environment =
            (OnlineEnvironment) loader.GetEnvironment(session, null, null);
        environment.setRuntimeConfigOption("APPLID", cics.getApplicationId());
        environment.setRuntimeConfigOption("SYSID", cics.getSystemId());
        environment.setCicsRecordStore(records);

        environment.setNextProgramToLoad(PROGRAM);
        run(loader, environment);
        CCommarea continuation = new CCommarea();
        continuation.setVarPassedByValue(new InternalCharBuffer(1));
        environment.setCommarea(continuation);
        environment.setXMLData(gateway.toReceiveDocument(request));
        environment.setKeyPressed(KeyPressed.getKey(request.aid()));
        run(loader, environment);

        return gateway.fromSendDocument(request, PROGRAM, session.getXMLData(),
            new BmsTerminalFlags(true, false, true, request.cursorField()));
    }

    private static void run(OnlineProgramLoader loader, OnlineEnvironment environment)
    {
        try
        {
            loader.runTopProgram(environment,
                new ArrayList<BaseCalledPrgPublicArgPositioned>());
        }
        catch (RuntimeException failure)
        {
            throw failure;
        }
        catch (Exception failure)
        {
            throw new IllegalStateException("Translated CardDemo execution failed", failure);
        }
    }
}
