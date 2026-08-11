package parser.Cobol.elements;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CWorkingPicTypeTest {
    @Test
    void exposesTheDeclaredCobolPictureLabels() {
        assertAll(
                () -> assertEquals("STRING", CWorkingEntry.CWorkingPicType.STRING.text),
                () -> assertEquals("NUMBER", CWorkingEntry.CWorkingPicType.NUMBER.text),
                () -> assertEquals("SIGNED NUMBER", CWorkingEntry.CWorkingPicType.SIGNED.text),
                () -> assertEquals("SIGNED DECIMAL", CWorkingEntry.CWorkingPicType.SIGNED_DECIMAL.text),
                () -> assertEquals("DECIMAL", CWorkingEntry.CWorkingPicType.DECIMAL.text),
                () -> assertEquals("ZONED NUMBER", CWorkingEntry.CWorkingPicType.EDITED.text));
    }
}
