package nacaLib.basePrgEnv;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BaseProgramAlphabeticCaseTest
{
    @Test
    void alphabeticLowerAllowsOnlyLowercaseLettersAndSpaces()
    {
        assertTrue(BaseProgram.isAlphabeticLower("lower case"));
        assertFalse(BaseProgram.isAlphabeticLower("Lower"));
        assertFalse(BaseProgram.isAlphabeticLower("lower1"));
    }

    @Test
    void alphabeticUpperAllowsOnlyUppercaseLettersAndSpaces()
    {
        assertTrue(BaseProgram.isAlphabeticUpper("UPPER CASE"));
        assertFalse(BaseProgram.isAlphabeticUpper("Upper"));
        assertFalse(BaseProgram.isAlphabeticUpper("UPPER1"));
    }
}
