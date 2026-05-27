package generate.java.st;

import generate.templates.TemplateLoader;
import org.stringtemplate.v4.ST;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ST4-based Java class structure generator.
 * Creates complete Java class with imports, variables, and procedureDivision method.
 */
public class CJavaClassST {
    public static class VariableDeclaration {
        String level;
        String name;
        String type;
        String length;
        String value;
        
        public VariableDeclaration(String level, String name, String type, String length, String value) {
            this.level = level;
            this.name = name;
            this.type = type;
            this.length = length;
            this.value = value;
        }
    }
    
    private String programName;
    private boolean isBatch;
    private List<String> imports;
    private List<String> variables;
    private String procedureBody;
    
    public CJavaClassST(String name, boolean isBatch) {
        this.programName = name;
        this.isBatch = isBatch;
        this.imports = new ArrayList<>();
        this.variables = new ArrayList<>();
    }
    
    public void addImport(String pkg) { 
        imports.add(pkg); 
    }
    
    public void addVariable(String decl) { 
        variables.add(decl); 
    }
    
    public void addVariable(VariableDeclaration varDecl) {
        ST varTemplate = TemplateLoader.getTemplate("variableDecl");
        varTemplate.add("level", varDecl.level);
        varTemplate.add("name", varDecl.name);
        varTemplate.add("type", varDecl.type);
        varTemplate.add("length", varDecl.length);
        if (varDecl.value != null) {
            varTemplate.add("value", varDecl.value);
        }
        variables.add(varTemplate.render());
    }
    
    public void setProcedureBody(String body) { 
        procedureBody = body; 
    }
    
    public String generate() {
        ST template = TemplateLoader.getTemplate("javaClass");
        template.add("name", programName);
        template.add("isBatch", isBatch);
        
        // Generate import statements
        List<String> importStatements = imports.stream()
            .map(pkg -> {
                ST importTemplate = TemplateLoader.getTemplate("importStatement");
                importTemplate.add("pkg", pkg);
                return importTemplate.render();
            })
            .collect(Collectors.toList());
        template.add("imports", importStatements);
        
        template.add("variables", variables);
        template.add("procedureBody", procedureBody != null ? procedureBody : "");
        return template.render();
    }
}