package jlib.sql;

/** Provides simple test abstract behavior. */
public abstract class SimpleTestAbstract {
    private String testField = "test";

    public String getTestField() {
        return testField;
    }

    /** Executes the do something operation. */
    public abstract void doSomething();
}
