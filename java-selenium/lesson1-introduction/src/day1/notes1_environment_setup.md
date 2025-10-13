
# **Lesson Notes: Introduction to Java Programming**

### **1. Java and Eclipse Environment Setup**

The session introduced Java programming and the tools required to start coding. The instructor discussed what Java is and its characteristics as an **object-oriented programming language (OOP)**, emphasizing that it supports six core OOP concepts:

* Class
* Object
* Inheritance
* Polymorphism
* Abstraction
* Encapsulation

**Key Points:**

* Java is a *purely object-oriented* programming language—every program must be written inside a class.
* It is **platform-independent**, meaning code written on one operating system can run on another without modification.
* Java is **case-sensitive**, distinguishing between uppercase and lowercase letters.

---

### **2. Setting up Java Development Kit (JDK)**

The instructor explained three major components of Java:

| Component | Full Form                | Purpose                                                   |
| --------- | ------------------------ | --------------------------------------------------------- |
| **JDK**   | Java Development Kit     | Used for developing Java programs (includes JRE and JVM). |
| **JRE**   | Java Runtime Environment | Used to run existing Java applications.                   |
| **JVM**   | Java Virtual Machine     | Converts Java code into bytecode and executes it.         |

**Steps to Install JDK:**

1. Visit the [Oracle JDK download page](https://www.oracle.com/java/technologies/downloads/).
2. Choose the appropriate installer for your OS (Windows `.exe` or macOS `.dmg`).
3. Sign up or log in to Oracle’s website before downloading.
4. Run the installer and complete the installation.

**Setting the Java Path (Windows):**

* Go to *System Properties → Environment Variables → Path*.
* Add the JDK `bin` directory path (e.g., `C:\Program Files\Java\jdk-21\bin`).
* Verify installation via Command Prompt:

  ```
  java -version
  ```

  Successful installation displays the JDK version number.

---

### **3. Installing Eclipse IDE**

**Eclipse IDE** (Integrated Development Environment) is used to write, compile, and run Java programs.

**Installation Steps:**

1. Visit [eclipse.org/downloads](https://www.eclipse.org/downloads/).
2. Download the latest version (e.g., Eclipse IDE for Java Developers).
3. Run the installer and select **“Eclipse IDE for Java Developers”**.
4. Keep default installation settings and complete installation.
5. Launch Eclipse and select a **workspace folder** to store all your Java projects.

---

### **4. Configuring Java and Eclipse Environment**

After installation:

* Eclipse automatically detects the installed JDK.
* To verify the environment, open Eclipse and check:

    * **Window → Preferences → Java → Installed JREs**
* The workspace acts as your project storage.
* It is recommended to uncheck *“Always show Welcome screen”* after the first launch.

---

### **5. How to Write a Basic Java Program**

#### **Creating a Java Class**

Steps to create a new Java project:

1. Open Eclipse → *File → New → Java Project*
2. Name your project (e.g., `JavaProgramming`).
3. Right-click `src` → *New → Package* → name it (e.g., `day1`).
4. Right-click the package → *New → Class* → name it (e.g., `FirstJavaProgram`).

    * Class names should start with an uppercase letter.
    * Avoid spaces in names.

---

### **6. Writing the First Java Program**

A basic Java program structure:

```java
package day1;

public class FirstJavaProgram {
    public static void main(String[] args) {
        System.out.println("Welcome to Java!");
        System.out.println(10 + 20);
    }
}
```

**Explanation:**

* `public class FirstJavaProgram`: Declares the class.
* `public static void main(String[] args)`: Entry point of every Java program.
* `System.out.println()`: Used to print output to the console.

---

### **7. Compiling and Running Java Programs**

In Eclipse:

* **Run the program** by:

    * Clicking the green “Run” button, or
    * Right-clicking the file → *Run As → Java Application*
* Output appears in the **Console window** below the editor.

Example Output:

```
Welcome to Java!
30
```

---

### **8. Understanding Console Output**

* The **Console** shows results from `System.out.println()` statements.
* Strings (in quotes) print text as-is.
* Arithmetic operations inside `println()` are evaluated and the result is displayed.
* Example:

    * `System.out.println("10 + 20");` → prints **10 + 20**
    * `System.out.println(10 + 20);` → prints **30**

---

### **Summary**

By the end of the class, students learned:

* The fundamentals of Java and object-oriented programming.
* How to install and configure JDK and Eclipse IDE.
* The structure of a simple Java program.
* How to compile and execute Java code in Eclipse.
* How to view and interpret console output.

---

