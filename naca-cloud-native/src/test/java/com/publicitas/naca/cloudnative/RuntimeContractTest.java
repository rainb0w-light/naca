package com.publicitas.naca.cloudnative;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import contract.CodegenRuntimeContract;
import contract.CodegenRuntimeContract.RuntimeOperation;
import java.lang.reflect.Method;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * runtimeContractCheck (Codegen-Runtime Contract, stage 2): the runtime side of
 * the boundary. For every operation declared in {@code runtime-operations.yaml}
 * it reflects on naca-rt and asserts the implementing class+method actually
 * exists with the declared parameter and return types. Lives in naca-cloud-native
 * because that is the module that can see naca-rt on its classpath.
 *
 * <p>Together with {@code TemplateRuntimeContractTest} (template requirements
 * resolve to declared operations) this closes the loop: a template can only emit
 * a call the contract declares, and the contract only declares calls the runtime
 * really implements. A runtime API change breaks the build here, not generated
 * code at the user's site.
 */
class RuntimeContractTest
{
    private final CodegenRuntimeContract contract = CodegenRuntimeContract.load();

    @Test
    @DisplayName("every declared contract operation has a real, signature-matching naca-rt implementation")
    void everyOperationHasARuntimeImplementation() throws Exception
    {
        for (RuntimeOperation op : contract.operations().values())
        {
            Class<?> runtimeClass = Class.forName(op.runtimeClass());
            Class<?>[] paramTypes = resolveTypes(op.paramTypes());
            Method method = resolveMethod(runtimeClass, op.runtimeMethod(), paramTypes);
            assertNotNull(method, "operation " + op.id() + " has no runtime method");
            assertEquals(resolveType(op.returns()), method.getReturnType(),
                "operation " + op.id() + " return type mismatch: contract declares "
                    + op.returns() + " but " + op.runtimeClass() + "." + op.runtimeMethod()
                    + " returns " + method.getReturnType().getName());
        }
    }

    /**
     * Resolves a method walking up the class hierarchy and including declared
     * (public and protected) methods — runtime operations such as
     * {@code BaseProgram.call(String)} are protected and inherited by generated
     * program subclasses, so {@code getMethod} (public only) would miss them.
     */
    private static Method resolveMethod(Class<?> clazz, String name, Class<?>[] paramTypes)
        throws NoSuchMethodException
    {
        for (Class<?> c = clazz; c != null; c = c.getSuperclass())
        {
            try
            {
                return c.getDeclaredMethod(name, paramTypes);
            }
            catch (NoSuchMethodException e)
            {
                // keep walking up the hierarchy
            }
        }
        throw new NoSuchMethodException(clazz.getName() + "." + name);
    }

    private static Class<?>[] resolveTypes(List<String> names) throws ClassNotFoundException
    {
        Class<?>[] types = new Class<?>[names.size()];
        for (int i = 0; i < names.size(); i++)
        {
            types[i] = resolveType(names.get(i));
        }
        return types;
    }

    private static Class<?> resolveType(String name) throws ClassNotFoundException
    {
        switch (name)
        {
            case "boolean": return boolean.class;
            case "int": return int.class;
            case "long": return long.class;
            case "double": return double.class;
            case "void": return void.class;
            default: return Class.forName(name);
        }
    }
}
