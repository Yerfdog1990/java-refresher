
---

## JSP Custom Tags

When Expression Language (EL) and standard JSP action tags are not sufficient to completely eliminate scriptlet code from a JSP page, **Custom Tags** can be used. JSP Custom Tags are **user-defined tags** that allow developers to move Java logic out of JSP pages and keep the presentation layer clean.

Custom tags help separate **business logic** from **view logic**, making JSP pages easier to read, maintain, and manage. They are especially useful for Web designers or frontend developers who may not be comfortable working directly with Java code. Another major advantage is **reusability**—once created, custom tags can be reused across multiple JSP pages.

---

## Structure of a JSP Custom Tag

A JSP custom tag can be defined in one of two forms:

1. **Empty Tag** – does not contain a body
2. **Body Tag** – contains content between opening and closing tags

The number and type of attributes supported by a custom tag depend on how its **Tag Handler class** is implemented.

---

### Syntax of an Empty Custom Tag

```jsp
<tagLibraryPrefix:customTagName 
    attribute1="value1"
    attribute2="value2" />
```

---

### Syntax of a Custom Body Tag

```jsp
<tagLibraryPrefix:customTagName 
    attribute1="value1"
    attribute2="value2">
    <!-- Body of the custom tag -->
</tagLibraryPrefix:customTagName>
```

---

## Why Use Custom Tags?

Creating custom tags is considered a **best practice** in JSP development. Frequently used operations such as formatting, validation, or utility functions should be implemented as custom tags. This improves readability, reduces duplication, and enforces clean MVC architecture.

In the next sections, we will learn how to create and use a custom tag in JSP.

---

## Creating a Custom Tag in JSP

To create a JSP custom tag, the following components are required:

1. **Tag Handler Class**
2. **Tag Library Descriptor (TLD) file**
3. **Usage of the custom tag in a JSP page**

---

## Tag Handler Class

A Tag Handler class defines the behavior of a custom tag. There are two ways to create it:

### 1. By Implementing Interfaces

You can directly implement one of the following interfaces:

* `SimpleTag`
* `Tag`
* `BodyTag`

These interfaces define lifecycle methods that are executed when the tag is processed.

### 2. By Extending Support Classes (Recommended)

You can extend one of the abstract support classes:

* `SimpleTagSupport`
* `TagSupport`
* `BodyTagSupport`

These classes already implement the required interfaces and provide default implementations, reducing boilerplate code and simplifying development.

---

## Tag Library Descriptor (TLD)

A **Tag Library Descriptor (TLD)** is an XML file that describes a tag library and the custom tags it contains. It is used by the web container to validate tags and by development tools for code assistance.

### Location Rules for TLD Files

* Must have a `.tld` extension
* Stored in:

    * `/WEB-INF/` directory or its subdirectories
    * `/META-INF/` directory of a JAR file

---

## Example: Creating a Custom Tag

In this example, we will create a custom tag that counts how many times a substring appears in a string.

---

### Tag Handler Class: `CountMatches.java`

```java
package com.studytonight.taghandler;

import java.io.IOException;
import javax.servlet.jsp.*;
import org.apache.commons.lang.StringUtils;

public class CountMatches extends TagSupport {

    private String inputstring;
    private String lookupstring;

    public String getInputstring() {
        return inputstring;
    }

    public void setInputstring(String inputstring) {
        this.inputstring = inputstring;
    }

    public String getLookupstring() {
        return lookupstring;
    }

    public void setLookupstring(String lookupstring) {
        this.lookupstring = lookupstring;
    }

    @Override
    public int doStartTag() throws JspException {
        try {
            JspWriter out = pageContext.getOut();
            out.println(StringUtils.countMatches(inputstring, lookupstring));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return SKIP_BODY;
    }
}
```

### Explanation

* The class extends `TagSupport`
* The `doStartTag()` method is overridden to define tag behavior
* `inputstring` and `lookupstring` act as tag attributes
* Getter and setter methods are required so the JSP container can pass attribute values to the tag
* The tag outputs the number of matching substrings

---

## Tag Library Descriptor: `CountMatchesDescriptor.tld`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<taglib>
  <tlibversion>1.0</tlibversion>
  <jspversion>1.1</jspversion>
  <shortname>cntmtchs</shortname>
  <info>Sample tag library for string operations</info>
  <uri>http://studytonight.com/jsp/taglib/countmatches</uri>

  <tag>
    <name>countmatches</name>
    <tagclass>com.studytonight.taghandler.CountMatches</tagclass>
    <info>Counts substring occurrences</info>

    <attribute>
      <name>inputstring</name>
      <required>true</required>
    </attribute>

    <attribute>
      <name>lookupstring</name>
      <required>true</required>
    </attribute>
  </tag>
</taglib>
```

### Explanation

* `<taglib>` defines the tag library metadata
* `<uri>` uniquely identifies the tag library
* Each `<tag>` element defines one custom tag
* `<attribute>` elements specify tag attributes and whether they are mandatory

---

## Using the Custom Tag in JSP

### `test.jsp`

```jsp
<%@ taglib prefix="mytag" uri="/WEB-INF/CountMatchesDescriptor.tld" %>

<html>
    <mytag:countmatches 
        inputstring="Studytonight" 
        lookupstring="t">
    </mytag:countmatches>
</html>
```

### Output

```
3
```

The output is `3` because the letter **"t"** appears three times in the word **"Studytonight"**.

---

## Summary

* JSP Custom Tags help eliminate scriptlets
* They promote clean separation of logic and presentation
* Custom tags are reusable and maintainable
* Creating a custom tag involves:

    * A Tag Handler class
    * A TLD file
    * Using the tag in a JSP page

Custom tags are a powerful feature of JSP and should be used whenever common logic needs to be reused across JSP pages.
