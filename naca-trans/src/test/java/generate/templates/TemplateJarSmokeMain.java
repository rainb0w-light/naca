package generate.templates;

/** Executed with naca-trans main classes and resources loaded from the built JAR. */
public final class TemplateJarSmokeMain
{
    private TemplateJarSmokeMain()
    {
    }

    public static void main(String[] args)
    {
        JavaTemplateCatalog catalog =
            JavaTemplateCatalogFactory.create(JavaTemplateProfile.full());
        require(catalog.hasTemplate("javaProgramRoot"));
        require(catalog.hasTemplate("bmsMapsetRoot"));
        require(catalog.hasTemplate("recursiveCICSReturnEntity"));
        require(catalog.hasTemplate("recursiveSQLCommitEntity"));
        require(catalog.hasTemplate("recursiveFPacClassEntity"));
    }

    private static void require(boolean condition)
    {
        if (!condition)
        {
            throw new IllegalStateException("JAR template catalog is incomplete");
        }
    }
}
