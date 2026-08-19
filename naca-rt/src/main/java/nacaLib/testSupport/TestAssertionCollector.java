/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.testSupport;

import java.util.ArrayList;
import java.util.List;

/** Provides test assertion collector behavior. */
public final class TestAssertionCollector {

    private static final ThreadLocal<TestAssertionCollector> INSTANCE = new ThreadLocal<>();

    private final List<AssertionResult> results = new ArrayList<>();
    private boolean collecting = false;

    /** Provides assertion result behavior. */
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

    /** Returns the instance. */
    public static TestAssertionCollector getInstance() {
        TestAssertionCollector collector = INSTANCE.get();
        if (collector == null) {
            collector = new TestAssertionCollector();
            INSTANCE.set(collector);
        }
        return collector;
    }

    /** Executes the start collecting operation. */
    public static void startCollecting() {
        TestAssertionCollector collector = getInstance();
        collector.results.clear();
        collector.collecting = true;
    }

    /** Executes the stop collecting operation. */
    public static void stopCollecting() {
        TestAssertionCollector collector = getInstance();
        collector.collecting = false;
    }

    /** Executes the clear operation. */
    public static void clear() {
        getInstance().results.clear();
    }

    /** Returns whether collecting. */
    public static boolean isCollecting() {
        TestAssertionCollector collector = INSTANCE.get();
        return collector != null && collector.collecting;
    }

    /** Adds the result. */
    public static void addResult(boolean passed, String message, String expected, String actual) {
        if (isCollecting()) {
            getInstance().results.add(new AssertionResult(passed, message, expected, actual));
        }
    }

    /** Executes the assert true operation. */
    public static void assertTrue(boolean condition, String message) {
        if (isCollecting()) {
            addResult(condition, message, "true", String.valueOf(condition));
        } else if (!condition) {
            throw new AssertionError(message);
        }
    }

    /** Executes the assert equals operation. */
    public static void assertEquals(String expected, String actual, String message) {
        boolean passed = expected != null ? expected.equals(actual) : actual == null;
        if (isCollecting()) {
            addResult(passed, message, expected, actual);
        } else if (!passed) {
            throw new AssertionError(message + " - Expected: " + expected + ", Actual: " + actual);
        }
    }

    /** Executes the assert not equals operation. */
    public static void assertNotEquals(String actual, String prohibited, String message) {
        boolean passed = actual != null ? !actual.equals(prohibited) : prohibited != null;
        if (isCollecting()) {
            addResult(passed, message, "different from " + prohibited, actual);
        } else if (!passed) {
            throw new AssertionError(message + " - Expected a value different from: " + prohibited);
        }
    }

    /** Executes the assert equals operation. */
    public static void assertEquals(int expected, int actual, String message) {
        boolean passed = expected == actual;
        if (isCollecting()) {
            addResult(passed, message, String.valueOf(expected), String.valueOf(actual));
        } else if (!passed) {
            throw new AssertionError(message + " - Expected: " + expected + ", Actual: " + actual);
        }
    }

    /** Executes the assert equals operation. */
    public static void assertEquals(double expected, double actual, String message) {
        boolean passed = Double.compare(expected, actual) == 0;
        if (isCollecting()) {
            addResult(passed, message, String.valueOf(expected), String.valueOf(actual));
        } else if (!passed) {
            throw new AssertionError(message + " - Expected: " + expected + ", Actual: " + actual);
        }
    }

    /** Executes the assert not null operation. */
    public static void assertNotNull(Object obj, String message) {
        boolean passed = obj != null;
        if (isCollecting()) {
            addResult(passed, message, "not null", obj == null ? "null" : obj.toString());
        } else if (!passed) {
            throw new AssertionError(message + " - Expected: not null");
        }
    }

    /** Executes the fail operation. */
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

    /** Returns whether s failures. */
    public boolean hasFailures() {
        for (AssertionResult result : results) {
            if (!result.passed) {
                return true;
            }
        }
        return false;
    }

    /** Returns the failure count. */
    public int getFailureCount() {
        int count = 0;
        for (AssertionResult result : results) {
            if (!result.passed) {
                count++;
            }
        }
        return count;
    }

    /** Returns the failure summary. */
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
