package nacaLib.program;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CobolIntrinsicFunctionsTest
{
    @Test
    void ordUsesCobolOneBasedCollatingPosition()
    {
        assertEquals(1, CobolIntrinsicFunctions.ord("\0"));
        assertEquals(33, CobolIntrinsicFunctions.ord(" "));
        assertEquals(66, CobolIntrinsicFunctions.ord("A"));
        assertEquals(256, CobolIntrinsicFunctions.ord("\u00ff"));
    }

    @Test
    void ordRejectsAnEmptyArgument()
    {
        assertThrows(IllegalArgumentException.class, () -> CobolIntrinsicFunctions.ord(""));
    }
}
