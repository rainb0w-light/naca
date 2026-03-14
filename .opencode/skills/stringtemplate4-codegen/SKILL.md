---
name: stringtemplate4-codegen
description: StringTemplate4 (ST4) code generation guide for refactoring string concatenation to templates. Use when migrating code generators, building compilers/transpilers, or creating multi-target language generators. Covers MVC architecture, PUSH model, template inheritance, regions, auto-indentation, and Naca project migration patterns.
license: MIT
compatibility: opencode
metadata:
  category: code-generation
  project: naca
  tags: template-engine,compiler,transpiler,antlr
---

# StringTemplate4 for Code Generation

## Overview

StringTemplate4 (ST4) is a template engine designed by Terence Parr (ANTLR author) specifically for **code generation**. Unlike JSP, FreeMarker, or Velocity, ST4 enforces **strict Model-View Separation** by deliberately prohibiting Turing-complete features.

**When to use this skill:**
- Refactoring code generation from string concatenation to templates
- Creating multi-target language generators
- Building compilers, transpilers, or code generators
- Migrating existing string-based code generation to ST4

---

## Part 1: The MVC Architecture Pattern

### 1.1 Core Philosophy: Templates Are Output Grammars, Not Programs

ST4 treats templates as **output grammars** — documents with "holes" where values are inserted. This is fundamentally different from template engines that allow embedded programming logic.

**What ST4 ALLOWS (View-only):**
- Reference attributes: `<name>`
- Include templates: `<header()>`
- Conditionals on presence: `<if(title)>...<endif>`
- Apply templates to lists: `<items:render()>`
- Access object properties: `<user.name>` (via getters)
- Format output: separators, indentation

**What ST4 FORBIDS (Model logic):**
- Assignments: No `<x = 1>`
- Arithmetic: No `<x + 1>`
- Value comparisons: No `<if(name == "parrt")>`
- Loops: Use template application instead
- Arbitrary method calls: Only property getters
- Variable mutation: All expressions are side-effect free

### 1.2 The PUSH Model vs PULL Model

This is the most critical architectural decision in ST4.

**PULL Model (Other Engines) — PROBLEMATIC:**
```
Template PULLS data on-demand:
Template: "$names$ has $count$ items"
           ↓         ↓
         Model    Model

Problem: Order matters! If designer moves $count$ before $names$,
and the model computes count by iterating names, you get NULL errors.
```

**PUSH Model (ST4) — SAFE:**
```java
// Controller computes ALL data BEFORE rendering
ST st = group.getInstanceOf("userList");
st.add("users", users);           // Push users
st.add("count", users.size());    // Push computed count
st.render();                      // THEN render

// Template can reference in ANY order — no dependencies!
```

**Why This Matters:**
- Eliminates an entire class of bugs (order-of-computation dependencies)
- Designers can rearrange templates freely without breakage
- Programmers don't need to do mental "topological sort"

### 1.3 The Four Canonical Operations

| Operation | Syntax | Purpose |
|-----------|--------|---------|
| **Attribute Reference** | `<name>` | Display value |
| **Template Include** | `<header()>` | Compose views |
| **Conditional Include** | `<if(title)>...<endif>` | Show/hide based on presence |
| **Template Application** | `<items:render()>` | Transform lists |

### 1.4 Strict MVC Mapping

```
┌─────────────────────────────────────────────────────────────────┐
│                        MODEL LAYER                              │
├─────────────────────────────────────────────────────────────────┤
│  Responsibilities:                                              │
│  - ALL computation and business logic                          │
│  - Data transformation (COBOL AST → Java-ready data)           │
│  - Type conversion, validation, lookups                        │
│                                                                │
│  Code Location: Java generator classes (CJava*ST.java)         │
│                                                                │
│  Example:                                                      │
│    String source = m_Value.ExportReference(getLine());         │
│    template.add("source", source);  // Push to view            │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ template.add()
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                        VIEW LAYER                               │
├─────────────────────────────────────────────────────────────────┤
│  Responsibilities:                                              │
│  - ONLY formatting and presentation                            │
│  - Whitespace, indentation, separators                         │
│  - Conditional presence (NOT value comparison)                 │
│                                                                │
│  Code Location: .stg template files                            │
│                                                                │
│  Example (verbs.stg):                                          │
│    move(source, dest) ::= "move(<source>).to(<dest>);"         │
└─────────────────────────────────────────────────────────────────┘
```

### 1.5 Migration Anti-Patterns to Avoid

**❌ WRONG: Logic in Template**
```stg
// BAD: Value comparison in template
move(source, dest, type) ::= <<
<if(type == "all")>moveAll(...)<else>move(...)<endif>
>>
```

**✅ CORRECT: Logic in Model, Template Selection in Controller**
```java
// GOOD: Controller decides template
String templateName = m_bFillAll ? "moveAll" : "move";
ST template = TemplateLoader.getVerbsTemplate(templateName);
template.add("source", source);
template.add("dest", dest);
WriteLine(template.render());
```

```stg
// GOOD: Simple, single-purpose templates
move(source, dest) ::= "move(<source>).to(<dest>);"
moveAll(source, dest) ::= "moveAll(<source>).to(<dest>);"
```

---

## Part 2: Template Inheritance (Key Differentiator)

### 2.1 Why Inheritance Matters for Code Generation

Code generators often need to target multiple language versions or variants:

```
                    ┌───────────────┐
                    │   Base Java   │
                    │   Templates   │
                    └───────┬───────┘
                            │
            ┌───────────────┼───────────────┐
            │               │               │
            ▼               ▼               ▼
    ┌───────────┐   ┌───────────┐   ┌───────────┐
    │  Java 8   │   │  Java 11  │   │  Java 17  │
    └───────────┘   └───────────┘   └───────────┘
```

### 2.2 Group File Inheritance

**Base Group (Java8.stg):**
```stg
group Java8;

class(name, fields) ::= <<
public class <name> {
<fields; separator="\n">
}
>>

// Java 8: No records, use class
dataClass(name, fields) ::= <<
public class <name> {
<fields:{f |   private final <f.type> <f.name>;}>
}
>>
```

**Derived Group (Java17.stg):**
```stg
group Java17;
import "Java8.stg"  // Inherit all Java8 templates

// Override: Use records for data classes
dataClass(name, fields) ::= <<
public record <name>(<fields:{f | <f.type> <f.name>}; separator=", ">) {}
>>
```

**Usage:**
```java
// Switch target by loading different group
STGroup java8 = new STGroupFile("templates/Java8.stg");
STGroup java17 = new STGroupFile("templates/Java17.stg");

// Same code, different output
ST st = java17.getInstanceOf("dataClass");
// Output: public record Person(String name) {}
```

### 2.3 Practical Example: COBOL to Multiple Targets

```
┌─────────────────────────────────────────────────────────────────┐
│                    templates/base.stg                           │
│  (Shared templates: expressions, conditions, basic statements) │
└─────────────────────────────────────────────────────────────────┘
                            │
         ┌──────────────────┼──────────────────┐
         │                  │                  │
         ▼                  ▼                  ▼
┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
│ templates/      │ │ templates/      │ │ templates/      │
│ java.stg        │ │ python.stg      │ │ csharp.stg      │
│                 │ │                 │ │                 │
│ import "base"   │ │ import "base"   │ │ import "base"   │
│                 │ │                 │ │                 │
│ Override:       │ │ Override:       │ │ Override:       │
│ classDecl()     │ │ classDecl()     │ │ classDecl()     │
│ methodDecl()    │ │ methodDecl()    │ │ methodDecl()    │
└─────────────────┘ └─────────────────┘ └─────────────────┘
```

---

## Part 3: Regions (Fine-Grained Customization)

### 3.1 What Are Regions?

Regions are **customization points** within templates — similar to Django's `{% block %}` tags. They allow subgroups to inject or replace specific parts of a template without rewriting the entire template.

### 3.2 Defining Regions

**Base Template:**
```stg
method(name, body) ::= <<
public void <name>() {
    <@preamble()>  // Region: can be overridden
    <body>
    <@postamble()> // Region: can be overridden
}
>>
```

**Override in Subgroup:**
```stg
group Debug;
import "Java.stg"

@method.preamble() ::= "logger.entering(\"<name>\");"
@method.postamble() ::= "logger.exiting(\"<name>\");"
```

**Output:**
```java
// With Debug group:
public void processData() {
    logger.entering("processData");
    // body
    logger.exiting("processData");
}
```

### 3.3 Use Cases

| Use Case | Region Override |
|----------|-----------------|
| **Debug Build** | Add assertions at method entry/exit |
| **Profiling** | Inject timing code around blocks |
| **Tracing** | Add enter/exit logging |
| **Testing** | Inject mock initialization |

---

## Part 4: Attribute Renderers (Custom Formatting)

### 4.1 What Are Renderers?

Renderers allow **type-specific formatting** without modifying templates.

### 4.2 Custom Renderer for Code Generation

```java
public class TypeRenderer implements AttributeRenderer<String> {
    
    private static final Map<String, String> TYPE_MAP = Map.of(
        "PIC X", "String",
        "PIC 9", "int",
        "PIC S9", "int",
        "PIC 9V99", "BigDecimal"
    );
    
    @Override
    public String toString(String value, String format, Locale locale) {
        if ("java".equals(format)) {
            return TYPE_MAP.getOrDefault(value, "Object");
        }
        return value;
    }
}
```

```stg
// Use in templates
field(name, type) ::= "<type; format=\"java\"> <name>;"
```

**Output:**
```
// template.add("type", "PIC 9") → int counter;
// template.add("type", "PIC X") → String description;
```

---

## Part 5: Expression Options (Output Control)

### 5.1 Separator (Most Common)

```stg
// Parameter lists
method(name, params) ::= "void <name>(<params; separator=\", \">) { }"

// Array initializers
array(type, elements) ::= "<type>[] arr = { <elements; separator=\", \"> };"

// Import statements
imports(files) ::= "<files:{f | import <f>;}; separator=\"\n\">"
```

### 5.2 Null Handling

```stg
// Replace null values with default
values(nums) ::= "[<nums; null=\"0\"; separator=\", \">]"

// Input: [1, null, 3, null, 5]
// Output: [1, 0, 3, 0, 5]
```

### 5.3 Line Wrapping

```stg
// Wrap with anchor (align to expression start)
methodChain(calls) ::= "builder<calls; wrap, anchor, separator=\"\n    .\">"
```

**Output:**
```java
builder
    .setFirstName("John")
    .setLastName("Doe")
    .build();
```

---

## Part 6: Auto-Indentation (Critical for Code Generation)

### 6.1 The Problem with Manual Indentation

```java
// WITHOUT ST4: Manual indentation tracking
WriteLine("public class " + name + " {");
m_Indent = "    ";  // Increase indent
WriteLine(m_Indent + "private String " + field + ";");
// ... error-prone manual management
```

### 6.2 ST4 Auto-Indentation

```stg
// Template defines structure, ST4 handles indentation
class(name, fields, methods) ::= <<
public class <name> {
<fields; separator="\n">
<methods>
}
>>

method(name, body) ::= <<
public void <name>() {
<body>
}
>>
```

**Output (automatically indented):**
```java
public class Person {
    private String name;
    public void greet() {
        System.out.println("Hello");
    }
}
```

---

## Part 7: Migration Patterns for Naca Project

### 7.1 Current State

```
✅ Completed:
   - 87 *ST.java classes using ST4
   - 7 .stg template files (1,369 lines)
   - TemplateLoader infrastructure

❌ Remaining:
   - ~144 non-ST classes using string concatenation
   - CJavaExprSum, CJavaCondOr, CJavaConcat, etc.
   - CJavaStructure, CJavaProcedure, CJavaBloc
```

### 7.2 Migration Pattern (5 Steps)

```
┌─────────────────────────────────────────────────────────────────┐
│ Step 1: Identify String Concatenation                           │
│   Find: String result = prefix + value + suffix;               │
│         result += anotherValue;                                 │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ Step 2: Create Template in .stg file                            │
│   ifThen(condition, body) ::= "if (<condition>) { <body> }"    │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ Step 3: Create ST Class                                         │
│   public class CJavaIfThenST extends CEntityIfThen {           │
│       protected void DoExport() {                               │
│           ST template = TemplateLoader.getVerbsTemplate(...);  │
│           template.add("condition", m_Condition.Export());      │
│           WriteLine(template.render());                         │
│       }                                                         │
│   }                                                             │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ Step 4: Update Factory                                          │
│   return new CJavaIfThenST(line, cat, out, ...);               │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ Step 5: Test                                                    │
│   ./gradlew test                                                │
└─────────────────────────────────────────────────────────────────┘
```

### 7.3 Common Migration Examples

#### Expression Migration

**Before (string concatenation):**
```java
// CJavaExprSum.java
public String Export() {
    String cs = (m_Type == ADD) ? "add(" : "subtract(";
    cs += m_Op1.Export() + ", " + m_Op2.Export() + ")";
    return cs;
}
```

**After (ST4):**
```java
// CJavaExprSumST.java
public String Export() {
    String templateName = (m_Type == ADD) ? "sum" : "subtract";
    ST template = TemplateLoader.getExpressionsTemplate(templateName);
    template.add("left", m_Op1.Export());
    template.add("right", m_Op2.Export());
    return template.render();
}
```

```stg
// expressions.stg
sum(left, right) ::= "add(<left>, <right>)"
subtract(left, right) ::= "subtract(<left>, <right>)"
```

---

## Part 8: Best Practices Summary

### DO ✅

- **Compute in Java, format in templates**
- **Use separators for lists**: `<params; separator=", ">`
- **Leverage template inheritance for variants**
- **Use regions for cross-cutting concerns**
- **Test presence, not values**: `<if(optionalValue)>...<endif>`

### DON'T ❌

- **Put logic in templates**: `<if(type == "all")>...<endif>` — WRONG
- **Compute in templates**: `<index + 1>` — WRONG
- **Mix string concatenation with templates**
- **Forget to handle null**: Use `<value; null="default">`

---

## Quick Reference

### Syntax Cheat Sheet

```
<attribute>                      // Simple reference
<obj.property>                   // Property access (via getter)
<list; separator=", ">           // List with separator
<list:{x | <x>}>                 // Apply anonymous template
<list:templateName()>            // Apply named template
<if(attr)>...<endif>             // Presence test
<if(attr)>...<else>...<endif>    // With else
<template()>                     // Include template
<attr; null="default">           // Null handling
<attr; format="pattern">         // Use renderer
<i>, <i0>                        // Iteration index (1/0-based)
<! comment !>                    // Comment
```

### File Organization

```
templates/
├── base.stg           # Shared templates
├── java/
│   ├── java.stg       # Java-specific
│   ├── verbs.stg      # COBOL verbs → Java
│   ├── expressions.stg# Expressions
│   ├── SQL/sql.stg    # SQL generation
│   └── CICS/cics.stg  # CICS commands
└── python/python.stg  # Future: Python target
```

---

## References

- **Academic Paper**: [Enforcing Strict Model-View Separation in Template Engines](https://www.cs.usfca.edu/~parrt/papers/mvc.templates.pdf) (WWW2004 best paper nominee)
- **Official Docs**: https://github.com/antlr/stringtemplate4/tree/master/doc
- **Project Migration Doc**: `STRINGTEMPLATE_MIGRATION_SUMMARY.md`