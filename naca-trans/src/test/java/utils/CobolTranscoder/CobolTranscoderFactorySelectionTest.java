package utils.CobolTranscoder;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import generate.CJavaEntityFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class CobolTranscoderFactorySelectionTest
{
    private static final String FACTORY_PROPERTY = "naca.transpiler.factory";
    private final String originalValue = System.getProperty(FACTORY_PROPERTY);

    @AfterEach
    void restoreProperty()
    {
        if (originalValue == null)
        {
            System.clearProperty(FACTORY_PROPERTY);
        }
        else
        {
            System.setProperty(FACTORY_PROPERTY, originalValue);
        }
    }

    @Test
    void recursiveSt4FactoryIsTheDefault()
    {
        System.clearProperty(FACTORY_PROPERTY);

        assertInstanceOf(CJavaEntityFactory.class,
            CobolTranscoderEngine.newJavaEntityFactory(null, null));
    }

    @Test
    void retiredDirectFactorySwitchCannotChangeTheFactory()
    {
        System.setProperty(FACTORY_PROPERTY, "direct");

        assertInstanceOf(CJavaEntityFactory.class,
            CobolTranscoderEngine.newJavaEntityFactory(null, null));
    }

    @Test
    void legacySt4SwitchRemainsCompatibleWithTheNewDefault()
    {
        System.setProperty(FACTORY_PROPERTY, "st4");

        assertInstanceOf(CJavaEntityFactory.class,
            CobolTranscoderEngine.newJavaEntityFactory(null, null));
    }
}
