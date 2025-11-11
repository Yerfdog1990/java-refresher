
---

## **Lesson Notes: Data Types in C**

### **1. Overview**

In C programming, **data types** define the type of data that a variable can hold — such as integer, character, floating-point, or double.
C is a **statically typed language**, meaning every variable’s data type must be declared before it is used, and it **cannot be changed later**.

Each data type determines:

* The **size** of memory allocated.
* The **range** of values it can hold.
* The **operations** that can be performed on it.

---

### **2. Basic Data Types in C**

| Data Type | Description                                  | Size (Typical 32-bit System) | Format Specifier |
| --------- | -------------------------------------------- | ---------------------------- | ---------------- |
| `int`     | Stores whole numbers (positive or negative). | 4 bytes                      | `%d`             |
| `char`    | Stores a single character.                   | 1 byte                       | `%c`             |
| `float`   | Stores single-precision decimal numbers.     | 4 bytes                      | `%f`             |
| `double`  | Stores double-precision decimal numbers.     | 8 bytes                      | `%lf`            |
| `void`    | Represents no value.                         | —                            | —                |

---

### **3. Example: Declaring and Printing Different Data Types**

```c
#include <stdio.h>

int main()
{
    // integer
    int age = 20;

    // floating-point
    float height = 5.7;

    // double-precision floating-point
    double pi = 3.14159;

    // character
    char grade = 'A';

    printf("Age: %d\n", age);
    printf("Height: %.1f\n", height);
    printf("Pi: %.5lf\n", pi);
    printf("Grade: %c\n", grade);

    return 0;
}
```

**Output:**

```
Age: 20
Height: 5.7
Pi: 3.14159
Grade: A
```

---

### **4. Integer Data Type (`int`)**

* Used to store **whole numbers** (no fractions).
* **Size:** 4 bytes
* **Range:** -2,147,483,648 to 2,147,483,647
* **Format specifier:** `%d`

```c
#include <stdio.h>

int main() {
    int var = 22;
    printf("var = %d", var);
    return 0;
}
```

**Output:**

```
var = 22
```

---

### **5. Character Data Type (`char`)**

* Stores a **single character** (e.g., `'A'`, `'b'`, `'5'`).
* **Size:** 1 byte
* **Range:** -128 to 127 (signed by default)
* **Format specifier:** `%c`

```c
#include <stdio.h>

int main() {
    char ch = 'A';
    printf("ch = %c", ch);
    return 0;
}
```

**Output:**

```
ch = A
```

---

### **6. Float Data Type (`float`)**

* Stores **decimal numbers** with single precision.
* **Size:** 4 bytes
* **Approximate range:** 3.4e-38 to 3.4e+38
* **Format specifier:** `%f`

```c
#include <stdio.h>

int main() {
    float val = 12.45;
    printf("val = %f", val);
    return 0;
}
```

**Output:**

```
val = 12.450000
```

---

### **7. Double Data Type (`double`)**

* Stores **decimal numbers** with double precision (higher accuracy).
* **Size:** 8 bytes
* **Approximate range:** 1.7e-308 to 1.7e+308
* **Format specifier:** `%lf`

```c
#include <stdio.h>

int main() {
    double val = 1.4521;
    printf("val = %lf", val);
    return 0;
}
```

**Output:**

```
val = 1.452100
```

---

### **8. Void Data Type (`void`)**

* Represents **no value or empty type**.
* Commonly used:

    * For functions that **do not return a value**.
    * For **generic pointers** (`void *`).

```c
#include <stdio.h>

// Function with void return type
void greet()
{
    printf("Hello, welcome!\n");
}

int main()
{
    greet();
    return 0;
}
```

**Output:**

```
Hello, welcome!
```

---

### **9. Checking the Size of Data Types**

C provides the built-in **`sizeof()`** operator to determine the size (in bytes) of data types or variables.

```c
#include <stdio.h>

int main()
{
    printf("The size of int: %d\n", sizeof(int));
    printf("The size of char: %d\n", sizeof(char));
    printf("The size of float: %d\n", sizeof(float));
    printf("The size of double: %d\n", sizeof(double));

    return 0;
}
```

**Output (Typical 32-bit System):**

```
The size of int: 4
The size of char: 1
The size of float: 4
The size of double: 8
```

> **Note:** Sizes may vary depending on the system architecture and compiler.

---

### **10. Type Modifiers in C**

C provides **modifiers** that alter the size or range of basic data types:

| Modifier   | Applied To  | Description                         |
| ---------- | ----------- | ----------------------------------- |
| `short`    | int         | Reduces size (usually 2 bytes)      |
| `long`     | int, double | Increases size and range            |
| `signed`   | int, char   | Allows negative and positive values |
| `unsigned` | int, char   | Allows only positive values         |

Example:

```c
unsigned int x = 50;
long double y = 20.123456789;
```

---

### **11. Literals in C**

**Literals** are fixed constant values assigned to variables in a program.
Examples:

```c
int num = 10;       // Integer literal
float rate = 5.5;   // Floating-point literal
char grade = 'A';   // Character literal
```

Literals **occupy memory** but **cannot be modified** during program execution.

---

### **12. Type Conversion**

Type conversion means converting a value from one data type to another.

#### **Types:**

1. **Implicit (Automatic) Conversion**
   Performed automatically by the compiler.

   ```c
   int a = 10;
   float b = a;   // int automatically converted to float
   ```
2. **Explicit (Type Casting)**
   Done manually by the programmer.

   ```c
   float x = 9.78;
   int y = (int) x;   // Explicit type casting
   printf("y = %d", y);
   ```

---

### ✅ **Key Takeaways**

* Data types define the **kind of data** a variable holds.
* The `sizeof()` operator helps determine memory usage.
* **Format specifiers** are used in `printf()` and `scanf()` for displaying or reading data.
* **Type conversion** allows safe manipulation between compatible data types.

---

Would you like me to add a **summary table** comparing all data types with size, range, and example values for quick revision?
