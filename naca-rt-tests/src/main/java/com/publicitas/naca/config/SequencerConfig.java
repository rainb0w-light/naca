package com.publicitas.naca.config;

import java.util.ArrayList;
import java.util.List;

public class SequencerConfig {

    private String transIdMappingFilePath = "";
    private String translationFilePath = "";
    private MailConfig mail = new MailConfig();
    private SqlConfig sql = new SqlConfig();
    private List<StartProgram> startPrograms = new ArrayList<>();

    public String getTransIdMappingFilePath() { return transIdMappingFilePath; }
    public void setTransIdMappingFilePath(String transIdMappingFilePath) { this.transIdMappingFilePath = transIdMappingFilePath; }

    public String getTranslationFilePath() { return translationFilePath; }
    public void setTranslationFilePath(String translationFilePath) { this.translationFilePath = translationFilePath; }

    public MailConfig getMail() { return mail; }
    public void setMail(MailConfig mail) { this.mail = mail; }

    public SqlConfig getSql() { return sql; }
    public void setSql(SqlConfig sql) { this.sql = sql; }

    public List<StartProgram> getStartPrograms() { return startPrograms; }
    public void setStartPrograms(List<StartProgram> startPrograms) { this.startPrograms = startPrograms; }

    public static class MailConfig {
        private String addressFrom = "alert-naca@domain.com";
        private String smtpServer = "smtp.domain.com";
        private String title = "NACA ALERT";
        private List<String> addressesTo = new ArrayList<>();

        public String getAddressFrom() { return addressFrom; }
        public void setAddressFrom(String addressFrom) { this.addressFrom = addressFrom; }
        public String getSmtpServer() { return smtpServer; }
        public void setSmtpServer(String smtpServer) { this.smtpServer = smtpServer; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public List<String> getAddressesTo() { return addressesTo; }
        public void setAddressesTo(List<String> addressesTo) { this.addressesTo = addressesTo; }
    }

    public static class SqlConfig {
        private String dbUrl = "";
        private String dbUser = "";
        private String dbPassword = "";
        private String dbConnectionUrlOptionalParams = "";
        private String dbEnvironment = "";
        private String driverClass = "com.ibm.db2.jcc.DB2Driver";
        private boolean closeCursorOnCommit = true;
        private boolean autoCommit = false;
        private String validationQuery = "";
        private List<PoolConfig> pools = new ArrayList<>();

        public String getDbUrl() { return dbUrl; }
        public void setDbUrl(String dbUrl) { this.dbUrl = dbUrl; }
        public String getDbUser() { return dbUser; }
        public void setDbUser(String dbUser) { this.dbUser = dbUser; }
        public String getDbPassword() { return dbPassword; }
        public void setDbPassword(String dbPassword) { this.dbPassword = dbPassword; }
        public String getDbConnectionUrlOptionalParams() { return dbConnectionUrlOptionalParams; }
        public void setDbConnectionUrlOptionalParams(String dbConnectionUrlOptionalParams) { this.dbConnectionUrlOptionalParams = dbConnectionUrlOptionalParams; }
        public String getDbEnvironment() { return dbEnvironment; }
        public void setDbEnvironment(String dbEnvironment) { this.dbEnvironment = dbEnvironment; }
        public String getDriverClass() { return driverClass; }
        public void setDriverClass(String driverClass) { this.driverClass = driverClass; }
        public boolean isCloseCursorOnCommit() { return closeCursorOnCommit; }
        public void setCloseCursorOnCommit(boolean closeCursorOnCommit) { this.closeCursorOnCommit = closeCursorOnCommit; }
        public boolean isAutoCommit() { return autoCommit; }
        public void setAutoCommit(boolean autoCommit) { this.autoCommit = autoCommit; }
        public String getValidationQuery() { return validationQuery; }
        public void setValidationQuery(String validationQuery) { this.validationQuery = validationQuery; }
        public List<PoolConfig> getPools() { return pools; }
        public void setPools(List<PoolConfig> pools) { this.pools = pools; }
    }

    public static class PoolConfig {
        private String name = "Generic";
        private String programId = "";
        private int maxConnection = 50;
        private long timeBeforeRemoveConnectionMs = 1200000;
        private long maxStatementLiveTimeMs = 1200001;
        private boolean useExplain = false;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getProgramId() { return programId; }
        public void setProgramId(String programId) { this.programId = programId; }
        public int getMaxConnection() { return maxConnection; }
        public void setMaxConnection(int maxConnection) { this.maxConnection = maxConnection; }
        public long getTimeBeforeRemoveConnectionMs() { return timeBeforeRemoveConnectionMs; }
        public void setTimeBeforeRemoveConnectionMs(long timeBeforeRemoveConnectionMs) { this.timeBeforeRemoveConnectionMs = timeBeforeRemoveConnectionMs; }
        public long getMaxStatementLiveTimeMs() { return maxStatementLiveTimeMs; }
        public void setMaxStatementLiveTimeMs(long maxStatementLiveTimeMs) { this.maxStatementLiveTimeMs = maxStatementLiveTimeMs; }
        public boolean isUseExplain() { return useExplain; }
        public void setUseExplain(boolean useExplain) { this.useExplain = useExplain; }
    }

    public static class StartProgram {
        private String name;
        private int delaySeconds;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getDelaySeconds() { return delaySeconds; }
        public void setDelaySeconds(int delaySeconds) { this.delaySeconds = delaySeconds; }
    }
}