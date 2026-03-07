/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.testSupport;

import java.util.ArrayList;
import java.util.List;

public final class TestAssertionCollector {
    
    private static final ThreadLocal<TestAssertionCollector> INSTANCE = new ThreadLocal<>();
    
    private final List<AssertionResult> results = new ArrayList<>();
    private boolean collecting = false;
    
    public static final class AssertionResult {
        public final boolean passed;
        public final String message;
        public final String expected;
        public final String actual;
        
        AssertionResult(boolean passed, String message, String expected, String actual) {
            this.passed = passed;
            this.message = message;
            this.expected = expected;
            this.actual = actual;
        }
    }
    
    private TestAssertionCollector() {}
    
    public static TestAssertionCollector getInstance() {
        TestAssertionCollector collector = INSTANCE.get();
        if (collector == null) {
            collector = new TestAssertionCollector();
            INSTANCE.set(collector);
        }
        return collector;
    }
    
    public static void startCollecting() {
        TestAssertionCollector collector = getInstance();
        collector.results.clear();
        collector.collecting = true;
    }
    
    public static void stopCollecting() {
        TestAssertionCollector collector = getInstance();
        collector.collecting = false;
    }
    
    public static void clear() {
        getInstance().results.clear();
    }
    
    public static boolean isCollecting() {
        TestAssertionCollector collector = INSTANCE.get();
        return collector != null && collector.collecting;
    }
    
    public static void addResult(boolean passed, String message, String expected, String actual) {
        if (isCollecting()) {
            getInstance().results.add(new AssertionResult(passed, message, expected, actual));
        }
    }
    
    public static void assertTrue(boolean condition, String message) {
        if (isCollecting()) {
            addResult(condition, message, "true", String.valueOf(condition));
        } else if (!condition) {
            throw new AssertionError(message);
        }
    }
    
    public static void assertEquals(String expected, String actual, String message) {
        boolean passed = expected != null ? expected.equals(actual) : actual == null;
        if (isCollecting()) {
            addResult(passed, message, expected, actual);
        } else if (!passed) {
            throw new AssertionError(message + " - Expected: " + expected + ", Actual: " + actual);
        }
    }
    
    public static void assertEquals(int expected, int actual, String message) {
        boolean passed = expected == actual;
        if (isCollecting()) {
            addResult(passed, message, String.valueOf(expected), String.valueOf(actual));
        } else if (!passed) {
            throw new AssertionError(message + " - Expected: " + expected + ", Actual: " + actual);
        }
    }
    
    public static void assertEquals(double expected, double actual, String message) {
        boolean passed = Double.compare(expected, actual) == 0;
        if (isCollecting()) {
            addResult(passed, message, String.valueOf(expected), String.valueOf(actual));
        } else if (!passed) {
            throw new AssertionError(message + " - Expected: " + expected + ", Actual: " + actual);
        }
    }
    
    public static void assertNotNull(Object obj, String message) {
        boolean passed = obj != null;
        if (isCollecting()) {
            addResult(passed, message, "not null", obj == null ? "null" : obj.toString());
        } else if (!passed) {
            throw new AssertionError(message + " - Expected: not null");
        }
    }
    
    public static void fail(String message) {
        if (isCollecting()) {
            addResult(false, message, null, null);
        } else {
            throw new AssertionError(message);
        }
    }
    
    public List<AssertionResult> getResults() {
        return new ArrayList<>(results);
    }
    
    public boolean hasFailures() {
        for (AssertionResult result : results) {
            if (!result.passed) {
                return true;
            }
        }
        return false;
    }
    
    public int getFailureCount() {
        int count = 0;
        for (AssertionResult result : results) {
            if (!result.passed) {
                count++;
            }
        }
        return count;
    }
    
    public String getFailureSummary() {
        StringBuilder sb = new StringBuilder();
        for (AssertionResult result : results) {
            if (!result.passed) {
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                sb.append(result.message);
                if (result.expected != null || result.actual != null) {
                    sb.append(" - Expected: ").append(result.expected);
                    sb.append(", Actual: ").append(result.actual);
                }
            }
        }
        return sb.toString();
    }
}