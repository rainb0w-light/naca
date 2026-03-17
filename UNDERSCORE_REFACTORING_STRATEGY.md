# Strategy for Handling _ Prefixed Variables with Setter Method Conflicts

## Problem Analysis

The core issue is that `_` prefixed fields often have setter methods with parameters of the same name, creating ambiguity when the `_` prefix is removed.

### Example Before Refactoring:
```java
protected String _conString;

public void setConString(String conString) {
    if (!_conString.equals(conString)) cleanConnection();
    _conString = conString;
}
```

### Problem After Simple Rename:
```java
protected String conString;  // Field renamed

public void setConString(String conString) {  // Parameter name unchanged
    if (!conString.equals(conString)) cleanConnection();  // Compares parameter to itself!
    conString = conString;  // Assigns parameter to itself!
}
```

## Solution Approaches

### Option 1: Use `this.` Prefix (Recommended)

This approach maintains the existing API while making the field access explicit.

#### Step-by-Step Process:

1. **Manual Preprocessing**: Add `this.` prefix to all field references in setter methods
2. **JDTLS Rename**: Rename the field from `_fieldName` to `fieldName`
3. **Verification**: Ensure no compilation errors and logic remains correct

#### Example Implementation:

**Before any changes:**
```java
protected String _conString;

public void setConString(String conString) {
    if (!_conString.equals(conString)) cleanConnection();
    _conString = conString;
}
```

**Step 1 - Add `this.` prefix manually:**
```java
protected String _conString;

public void setConString(String conString) {
    if (!this._conString.equals(conString)) cleanConnection();
    this._conString = conString;
}
```

**Step 2 - Use JDTLS rename on `_conString` → `conString`:**
```java
protected String conString;

public void setConString(String conString) {
    if (!this.conString.equals(conString)) cleanConnection();
    this.conString = conString;
}
```

**Result**: Correct, unambiguous, and maintains the same API.

### Option 2: Rename to More Descriptive Names

Instead of removing just the `_`, create more descriptive field names that avoid conflicts.

#### Example:
- `_conString` → `connectionString`
- `_driver` → `databaseDriver`  
- `_user` → `dbUser`
- `_password` → `dbPassword`

This approach eliminates the naming conflict entirely but requires updating the public API (getter/setter method names).

### Option 3: Keep _ Prefix for Protected Fields

Given that these are **protected** fields (not private), consider whether the `_` prefix serves a purpose in distinguishing protected vs private fields. If this is an intentional design pattern, you might want to keep it.

## Recommended Approach: Option 1 (`this.` prefix)

### Why Option 1 is Best:
1. **Maintains backward compatibility** - no API changes
2. **Minimal code changes** - only affects field access within the class
3. **Clear and explicit** - `this.` makes field access obvious
4. **Follows Java conventions** - standard practice for disambiguation
5. **Safe with JDTLS** - semantic rename works correctly

### Implementation Script for Manual Preprocessing

For each of the 24 files, run this manual process:

#### Step 1: Identify setter methods
Look for patterns like:
```regex
public void set[A-Z][a-zA-Z]*\([^)]*\)
```

#### Step 2: For each setter, find field assignments
Find lines containing `_fieldName =` or `_fieldName.`

#### Step 3: Add `this.` prefix
Change `_fieldName` to `this._fieldName` in setter methods only

#### Step 4: Apply JDTLS rename
Use your IDE's rename functionality to change `_fieldName` to `fieldName`

## Detailed File-by-File Strategy

### Files with Simple Setters (Most Common)
Files like `Db.java` have straightforward setter patterns. Apply Option 1 directly.

### Files with Complex Logic
Some files may have the `_` fields used in complex methods beyond simple setters. For these:

1. **Review all usages** of the field
2. **Add `this.` prefix** to all field references (not just setters)
3. **Apply JDTLS rename**

### Files Without Setters
If a file has `_` prefixed fields but no setter methods, you can safely use JDTLS rename directly without the `this.` preprocessing step.

## Verification Process

After refactoring each file:

1. **Compile check**: Ensure no compilation errors
2. **Logic verification**: Test that the field values are correctly set and retrieved
3. **API compatibility**: Ensure existing code using getters/setters still works

## Risk Assessment

- **Low risk**: Option 1 maintains exact same external API
- **Medium risk**: Option 2 requires updating all calling code
- **No risk**: Keeping `_` prefix (but doesn't meet refactoring goal)

## Final Recommendation

**Use Option 1 (`this.` prefix) for all 24 files** because:
- It achieves the goal of removing non-standard prefixes
- Maintains full backward compatibility  
- Requires minimal manual intervention
- Works seamlessly with JDTLS semantic rename
- Follows standard Java best practices

This approach will successfully refactor all `_` prefixed variables while maintaining code correctness and API compatibility.