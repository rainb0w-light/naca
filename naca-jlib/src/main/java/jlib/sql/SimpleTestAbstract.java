package jlib.sql;

public abstract class SimpleTestAbstract {
    private String testField = "test";
    
    public String getTestField() {
        return testField;
    }
    
    public abstract void doSomething();
}