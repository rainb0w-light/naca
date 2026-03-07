package com.publicitas.naca.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "naca.rt")
@Validated
public class NacaRtProperties {

    private String helpXslFilePath = "";
    private String resourcePath = "";
    private String xmlFrameFilePath = "";
    private String logSettingsPathFile = "";

    private boolean preLoadAllProgramFromDir = false;
    private boolean preLoadAllProgramFromList = false;
    private boolean keepPreloadedProgramList = false;
    private String preLoadProgramList = "";
    private boolean asynchronousPreloadPrograms = false;
    private boolean gcAfterPreloadPrograms = false;
    private boolean loadCopyByPrimordialLoader = false;

    private boolean useProgramPool = true;
    private boolean useSqlStatementCache = true;
    private boolean useSqlObjectCache = true;
    private boolean useVarFillCache = true;
    private boolean manageVarDefCatalog = true;
    private boolean cacheResourceFiles = false;

    private long maxSessionExecTimeMs = 36000000;
    private int httpSessionMaxInactiveIntervalS = 10800;

    private String applicationClassPath = "";
    private String jarFile = "";
    private boolean canLoadJar = false;
    private boolean canLoadClass = true;

    private int maxSizeMemPoolCodeCacheMb = 0;
    private int maxSizeMemPoolPermGenMb = 0;

    private String serverName = "NACT";

    private String codeJavaToDb = "255-159,10-133";
    private String codeDbToJava = "159-255";
    private String comparisonMode = "EBCDIC";

    private String standardCalendar = "";
    private String customCalendar = "";

    private GcThreadConfig gcThread = new GcThreadConfig();
    private DebugConfig debug = new DebugConfig();
    private SequencerConfig sequencer = new SequencerConfig();

    public String getHelpXslFilePath() { return helpXslFilePath; }
    public void setHelpXslFilePath(String helpXslFilePath) { this.helpXslFilePath = helpXslFilePath; }

    public String getResourcePath() { return resourcePath; }
    public void setResourcePath(String resourcePath) { this.resourcePath = resourcePath; }

    public String getXmlFrameFilePath() { return xmlFrameFilePath; }
    public void setXmlFrameFilePath(String xmlFrameFilePath) { this.xmlFrameFilePath = xmlFrameFilePath; }

    public String getLogSettingsPathFile() { return logSettingsPathFile; }
    public void setLogSettingsPathFile(String logSettingsPathFile) { this.logSettingsPathFile = logSettingsPathFile; }

    public boolean isPreLoadAllProgramFromDir() { return preLoadAllProgramFromDir; }
    public void setPreLoadAllProgramFromDir(boolean preLoadAllProgramFromDir) { this.preLoadAllProgramFromDir = preLoadAllProgramFromDir; }

    public boolean isPreLoadAllProgramFromList() { return preLoadAllProgramFromList; }
    public void setPreLoadAllProgramFromList(boolean preLoadAllProgramFromList) { this.preLoadAllProgramFromList = preLoadAllProgramFromList; }

    public boolean isKeepPreloadedProgramList() { return keepPreloadedProgramList; }
    public void setKeepPreloadedProgramList(boolean keepPreloadedProgramList) { this.keepPreloadedProgramList = keepPreloadedProgramList; }

    public String getPreLoadProgramList() { return preLoadProgramList; }
    public void setPreLoadProgramList(String preLoadProgramList) { this.preLoadProgramList = preLoadProgramList; }

    public boolean isAsynchronousPreloadPrograms() { return asynchronousPreloadPrograms; }
    public void setAsynchronousPreloadPrograms(boolean asynchronousPreloadPrograms) { this.asynchronousPreloadPrograms = asynchronousPreloadPrograms; }

    public boolean isGcAfterPreloadPrograms() { return gcAfterPreloadPrograms; }
    public void setGcAfterPreloadPrograms(boolean gcAfterPreloadPrograms) { this.gcAfterPreloadPrograms = gcAfterPreloadPrograms; }

    public boolean isLoadCopyByPrimordialLoader() { return loadCopyByPrimordialLoader; }
    public void setLoadCopyByPrimordialLoader(boolean loadCopyByPrimordialLoader) { this.loadCopyByPrimordialLoader = loadCopyByPrimordialLoader; }

    public boolean isUseProgramPool() { return useProgramPool; }
    public void setUseProgramPool(boolean useProgramPool) { this.useProgramPool = useProgramPool; }

    public boolean isUseSqlStatementCache() { return useSqlStatementCache; }
    public void setUseSqlStatementCache(boolean useSqlStatementCache) { this.useSqlStatementCache = useSqlStatementCache; }

    public boolean isUseSqlObjectCache() { return useSqlObjectCache; }
    public void setUseSqlObjectCache(boolean useSqlObjectCache) { this.useSqlObjectCache = useSqlObjectCache; }

    public boolean isUseVarFillCache() { return useVarFillCache; }
    public void setUseVarFillCache(boolean useVarFillCache) { this.useVarFillCache = useVarFillCache; }

    public boolean isManageVarDefCatalog() { return manageVarDefCatalog; }
    public void setManageVarDefCatalog(boolean manageVarDefCatalog) { this.manageVarDefCatalog = manageVarDefCatalog; }

    public boolean isCacheResourceFiles() { return cacheResourceFiles; }
    public void setCacheResourceFiles(boolean cacheResourceFiles) { this.cacheResourceFiles = cacheResourceFiles; }

    public long getMaxSessionExecTimeMs() { return maxSessionExecTimeMs; }
    public void setMaxSessionExecTimeMs(long maxSessionExecTimeMs) { this.maxSessionExecTimeMs = maxSessionExecTimeMs; }

    public int getHttpSessionMaxInactiveIntervalS() { return httpSessionMaxInactiveIntervalS; }
    public void setHttpSessionMaxInactiveIntervalS(int httpSessionMaxInactiveIntervalS) { this.httpSessionMaxInactiveIntervalS = httpSessionMaxInactiveIntervalS; }

    public String getApplicationClassPath() { return applicationClassPath; }
    public void setApplicationClassPath(String applicationClassPath) { this.applicationClassPath = applicationClassPath; }

    public String getJarFile() { return jarFile; }
    public void setJarFile(String jarFile) { this.jarFile = jarFile; }

    public boolean isCanLoadJar() { return canLoadJar; }
    public void setCanLoadJar(boolean canLoadJar) { this.canLoadJar = canLoadJar; }

    public boolean isCanLoadClass() { return canLoadClass; }
    public void setCanLoadClass(boolean canLoadClass) { this.canLoadClass = canLoadClass; }

    public int getMaxSizeMemPoolCodeCacheMb() { return maxSizeMemPoolCodeCacheMb; }
    public void setMaxSizeMemPoolCodeCacheMb(int maxSizeMemPoolCodeCacheMb) { this.maxSizeMemPoolCodeCacheMb = maxSizeMemPoolCodeCacheMb; }

    public int getMaxSizeMemPoolPermGenMb() { return maxSizeMemPoolPermGenMb; }
    public void setMaxSizeMemPoolPermGenMb(int maxSizeMemPoolPermGenMb) { this.maxSizeMemPoolPermGenMb = maxSizeMemPoolPermGenMb; }

    public String getServerName() { return serverName; }
    public void setServerName(String serverName) { this.serverName = serverName; }

    public String getCodeJavaToDb() { return codeJavaToDb; }
    public void setCodeJavaToDb(String codeJavaToDb) { this.codeJavaToDb = codeJavaToDb; }

    public String getCodeDbToJava() { return codeDbToJava; }
    public void setCodeDbToJava(String codeDbToJava) { this.codeDbToJava = codeDbToJava; }

    public String getComparisonMode() { return comparisonMode; }
    public void setComparisonMode(String comparisonMode) { this.comparisonMode = comparisonMode; }

    public String getStandardCalendar() { return standardCalendar; }
    public void setStandardCalendar(String standardCalendar) { this.standardCalendar = standardCalendar; }

    public String getCustomCalendar() { return customCalendar; }
    public void setCustomCalendar(String customCalendar) { this.customCalendar = customCalendar; }

    public GcThreadConfig getGcThread() { return gcThread; }
    public void setGcThread(GcThreadConfig gcThread) { this.gcThread = gcThread; }

    public DebugConfig getDebug() { return debug; }
    public void setDebug(DebugConfig debug) { this.debug = debug; }

    public SequencerConfig getSequencer() { return sequencer; }
    public void setSequencer(SequencerConfig sequencer) { this.sequencer = sequencer; }

    public static class GcThreadConfig {
        private boolean activateThreadGarbageCollectorStatement = false;
        private long garbageCollectorStatementMs = 300000;
        private int maxPermanentHeapMo = 800;
        private int nbStatementForcedRemoved = 100;
        private int nbSystemGcCall = 3;
        private int nbStatementsToRemoveBeforeGc = 100;

        public boolean isActivateThreadGarbageCollectorStatement() { return activateThreadGarbageCollectorStatement; }
        public void setActivateThreadGarbageCollectorStatement(boolean activateThreadGarbageCollectorStatement) { this.activateThreadGarbageCollectorStatement = activateThreadGarbageCollectorStatement; }
        public long getGarbageCollectorStatementMs() { return garbageCollectorStatementMs; }
        public void setGarbageCollectorStatementMs(long garbageCollectorStatementMs) { this.garbageCollectorStatementMs = garbageCollectorStatementMs; }
        public int getMaxPermanentHeapMo() { return maxPermanentHeapMo; }
        public void setMaxPermanentHeapMo(int maxPermanentHeapMo) { this.maxPermanentHeapMo = maxPermanentHeapMo; }
        public int getNbStatementForcedRemoved() { return nbStatementForcedRemoved; }
        public void setNbStatementForcedRemoved(int nbStatementForcedRemoved) { this.nbStatementForcedRemoved = nbStatementForcedRemoved; }
        public int getNbSystemGcCall() { return nbSystemGcCall; }
        public void setNbSystemGcCall(int nbSystemGcCall) { this.nbSystemGcCall = nbSystemGcCall; }
        public int getNbStatementsToRemoveBeforeGc() { return nbStatementsToRemoveBeforeGc; }
        public void setNbStatementsToRemoveBeforeGc(int nbStatementsToRemoveBeforeGc) { this.nbStatementsToRemoveBeforeGc = nbStatementsToRemoveBeforeGc; }
    }

    public static class DebugConfig {
        private boolean logAllSQLException = true;
        public boolean isLogAllSQLException() { return logAllSQLException; }
        public void setLogAllSQLException(boolean logAllSQLException) { this.logAllSQLException = logAllSQLException; }
    }
}